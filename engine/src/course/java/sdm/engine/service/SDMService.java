package course.java.sdm.engine.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.Part;

import course.java.sdm.engine.exceptions.FileNotLoadedException;
import course.java.sdm.engine.mapper.DTOMapper;
import course.java.sdm.engine.model.*;
import course.java.sdm.engine.users.UserManager;
import course.java.sdm.engine.utils.fileManager.FileManager;
import course.java.sdm.engine.utils.ordersCreator.OrdersCreator;
import course.java.sdm.engine.utils.systemUpdater.SystemUpdater;
import examples.jaxb.schema.generated.SuperDuperMarketDescriptor;
import model.User;
import model.request.*;
import model.response.*;

public class SDMService {

    private final static DTOMapper dtoMapper = new DTOMapper();
    private final FileManager fileManager = FileManager.getFileManager();
    private final OrdersCreator ordersCreator = OrdersCreator.getOrdersExecutor();
    private final SystemUpdater systemUpdater = SystemUpdater.getSystemUpdater();
    private final UserManager userManager = UserManager.getUserManager();

    private SDMDescriptor sdmDescriptor = new SDMDescriptor();
    private Map<UUID, Map<Integer, ValidStoreDiscounts>> orderIdToValidDiscounts = new HashMap<>();

    /* Users */

    public UUID addUserToSystem (String username, User.UserType userType) {
        return userManager.addUser(sdmDescriptor, username, userType);
    }

    public void removeUser (String username) {
        userManager.removeUser(sdmDescriptor, username);
    }

    public synchronized Set<User> getUsers () {
        Collection<SystemUser> users = userManager.getUsers();
        return dtoMapper.toGetUsersResponse(users);
    }

    public GetZonesResponse getZones () {
        Collection<Zone> zones = sdmDescriptor.getZones().values();
        if (zones.isEmpty()) {
            throw new RuntimeException("No zone exists in the system");
        }

        return dtoMapper.toGetZonesResponse(zones);
    }

    public boolean isUserExists (String username) {
        return userManager.isUserExists(username);
    }

    /* Users */

    public void loadData (Part part, UUID storesOwnerID) throws IOException {
        StoresOwner storesOwner = getStoresOwner(storesOwnerID);
        SuperDuperMarketDescriptor superDuperMarketDescriptor = fileManager.generateDataFromXmlFile(part);
        Zone newZone = fileManager.loadDataFromGeneratedData(superDuperMarketDescriptor, storesOwner, sdmDescriptor);
        systemUpdater.updateSystemAfterLoadingZoneFile(sdmDescriptor, newZone, storesOwner);
    }

    public boolean isFileLoaded () {
        return sdmDescriptor.getZones() != null && !sdmDescriptor.getZones().isEmpty();
    }

    public boolean isValidLocation (final int xCoordinate, final int yCoordinate) {
        Location userLocation = new Location(xCoordinate, yCoordinate);

        return isValidLocation(userLocation);
    }

    public GetZoneResponse getZone (String zoneName) {
        if (isFileLoaded()) {
            throw new FileNotLoadedException();
        }

        Map<String, Zone> zones = sdmDescriptor.getZones();
        if (!zones.containsKey(zoneName)) {
            throw new RuntimeException(String.format("No zone with name %s exist in the system", zoneName));
        }

        return dtoMapper.toGetZoneResponse(zones.get(zoneName));
    }

    public GetCustomersResponse getCustomers () {
        if (isFileLoaded()) {
            throw new FileNotLoadedException();
        }

        Map<UUID, SystemCustomer> systemCustomers = sdmDescriptor.getSystemCustomers();

        return dtoMapper.toGetCustomersResponse(systemCustomers);
    }

    /* Placing orders - static/dynamic */

    public PlaceOrderResponse placeStaticOrderV2 (PlaceOrderRequest request) {
        Zone zone = getZoneByName(request.getZoneName());
        SystemCustomer customer = getSystemCustomer(request.getCustomerId());
        Map<PricedItem, Double> pricedItems = getPricedItemFromStaticRequest(zone, request);
        SystemStore systemStore = zone.getSystemStores().get(request.getStoreId());
        LocalDate orderDate = request.getOrderDate();
        Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());

        Order newOrder = ordersCreator.createOrderV2(systemStore,
                                                     orderDate,
                                                     orderLocation,
                                                     pricedItems,
                                                     null,
                                                     customer.getId(),
                                                     zone.getZoneName());

        return new PlaceOrderResponse(newOrder.getId());
    }

    public PlaceDynamicOrderResponse placeDynamicOrderV2 (PlaceDynamicOrderRequest request) {
        Zone zone = getZoneByName(request.getZoneName());
        UUID customerId = request.getCustomerId();
        Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());
        List<SystemItem> systemItemsIncludedInOrder = getItemsFromDynamicOrderRequest(zone, request.getOrderItemToAmount());
        Set<SystemStore> storesIncludedInOrder = getIncludedStoresInOrder(zone, systemItemsIncludedInOrder);
        TempOrder tempDynamicOrder = ordersCreator.createDynamicOrderV2(request,
                                                                        orderLocation,
                                                                        systemItemsIncludedInOrder,
                                                                        storesIncludedInOrder,
                                                                        customerId,
                                                                        zone.getZoneName());

        return dtoMapper.createPlaceDynamicOrderResponseV2(tempDynamicOrder);
    }

    public GetDiscountsResponse getOrderDiscounts (UUID orderId) {
        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);
        Zone zone = getZoneByName((tempOrder.getZoneName()));

        Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();
        Map<Integer, ValidStoreDiscounts> returnDiscounts = new TreeMap<>();

        for (Map.Entry<StoreDetails, Order> SO_Entry : staticOrders.entrySet()) {
            Order currOrder = SO_Entry.getValue();
            StoreDetails currStoreDetails = SO_Entry.getKey();

            Map<PricedItem, Double> pricedItems = currOrder.getPricedItems();
            int storeId = currStoreDetails.getId();
            ValidStoreDiscounts validStoreDiscounts = getStoreDiscounts(pricedItems, storeId, zone);

            if (validStoreDiscounts != null) {
                returnDiscounts.put(storeId, validStoreDiscounts);
            }
        }

        orderIdToValidDiscounts.put(orderId, returnDiscounts);
        return dtoMapper.createGetDiscountsResponse(returnDiscounts, zone.getSystemStores());
    }

    public FinalSummaryForOrder addDiscountsToOrder (AddDiscountsToOrderRequest request) {
        UUID orderId = request.getOrderID();
        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);
        Zone zone = getZoneByName(tempOrder.getZoneName());
        Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();
        Map<Integer, ChosenStoreDiscounts> storeIdToChosenDiscounts = request.getStoreIdToChosenDiscounts();
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();

        staticOrders.forEach(( (storeDetails, order) -> {
            int storeId = storeDetails.getId();
            SystemStore systemStore = systemStores.get(storeId);
            UUID currStaticOrderId = order.getId();

            if (storeIdToChosenDiscounts != null && storeIdToChosenDiscounts.containsKey(storeId)) {
                ChosenStoreDiscounts chosenStoreDiscounts = storeIdToChosenDiscounts.get(storeId);
                ValidStoreDiscounts validDiscounts = getValidStoreDiscounts(orderId, storeId, currStaticOrderId, systemStore);

                ordersCreator.addDiscountsPerStore(systemStore, order, chosenStoreDiscounts, validDiscounts);
            }

            ordersCreator.completeTheOrderV2(systemStore, order);
        }));

        return dtoMapper.createFinalSummaryForOrder(staticOrders, systemStores);

    }

    public void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder) {
        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);
        Zone zone = getZoneByName(tempOrder.getZoneName());

        if (toConfirmNewDynamicOrder) {
            SystemCustomer systemCustomer = sdmDescriptor.getSystemCustomers().get(tempOrder.getCustomerId());
            Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();

            staticOrders.forEach( (storeDetails, order) -> {
                SystemStore systemStore = zone.getSystemStores().get(storeDetails.getId());

                systemUpdater.updateSystemAfterStaticOrderV2(systemStore, order, zone, systemCustomer);
            });

            systemUpdater.updateSystemCustomerAfterDynamicOrder(orderId, systemCustomer, staticOrders, zone.getZoneName());
        }

        ordersCreator.deleteTempOrder(orderId);
    }

    /* Placing orders - static/dynamic */

    public void saveOrdersHistoryToFile (String path) {
        if (isFileLoaded()) {
            throw new FileNotLoadedException();
        }

        fileManager.saveOrdersHistoryToFile(sdmDescriptor, path);
    }

    public void loadSystemHistoryToFile (String path) {
        SystemOrdersHistory systemOrdersHistory = fileManager.loadDataFromFile(path);
        Map<String, Zone> zones = sdmDescriptor.getZones();

        systemOrdersHistory.getZoneOrdersHistories().forEach(zoneOrdersHistory -> {
            String zoneName = zoneOrdersHistory.getZoneName();
            if (!zones.containsKey(zoneName)) {
                throw new RuntimeException(String.format("There is no zone with name: '%s' in the system", zoneName));
            }

            Zone zone = zones.get(zoneName);
            Map<UUID, List<SystemOrder>> zoneHistoryOrders = zoneOrdersHistory.getSystemOrders();
            systemUpdater.updateSystemAfterLoadingOrdersHistoryFromFile(zoneHistoryOrders, sdmDescriptor.getSystemCustomers(), zone);
        });
    }

    /* Update store */

    public void addItemToStore (UpdateStoreRequest request) {
        Zone zone = getZoneByName(request.getZoneName());
        Integer itemId = request.getItemId();
        Integer storeId = request.getStoreId();
        Integer itemPrice = request.getItemPrice();
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();
        Map<Integer, SystemItem> systemItems = zone.getSystemItems();

        systemUpdater.addItemToStore(itemId, storeId, itemPrice, systemStores, systemItems);
    }

    public DeleteItemFromStoreResponse deleteItemFromStore (BaseUpdateStoreRequest request) {
        Zone zone = getZoneByName(request.getZoneName());
        Integer itemId = request.getItemId();
        Integer storeId = request.getStoreId();
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();
        Map<Integer, SystemItem> systemItems = zone.getSystemItems();

        DeleteItemResult deleteItemResult = systemUpdater.deleteItemFromStore(itemId, storeId, systemStores, systemItems);
        List<Discount> removedDiscounts = deleteItemResult.getRemovedDiscounts();

        return dtoMapper.createDeleteItemFromStoreResponse(removedDiscounts);
    }

    public void updateItemPrice (UpdateStoreRequest request) {
        Zone zone = getZoneByName(request.getZoneName());
        Integer itemId = request.getItemId();
        Integer storeId = request.getStoreId();
        Integer itemPrice = request.getItemPrice();
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();
        Map<Integer, SystemItem> systemItems = zone.getSystemItems();

        systemUpdater.updateItemPrice(itemId, storeId, itemPrice, systemStores, systemItems);
    }

    /* Update store */

    // public GetMapEntitiesResponse getSystemMappableEntities () {
    // // if (zone == null) {
    // // throw new FileNotLoadedException();
    // // }
    // //
    // // return dtoMapper.toGetSystemMappableEntitiesResponse(zone.getMappableEntities().values());
    // return null;
    // }

    /*
     * public GetItemsResponse getItems () { if (isFileLoaded()) { throw new FileNotLoadedException(); }
     * 
     * return dtoMapper.toGetItemsResponse(zone.getSystemItems()); }
     * 
     * public GetOrdersResponse getOrders () { if (zone == null) { throw new FileNotLoadedException(); }
     * return dtoMapper.toGetOrdersResponse(zone.getSystemOrders()); }
     */

    // public GetStoresResponse getStores () {
    // if (isFileLoaded()) {
    // throw new FileNotLoadedException();
    // }
    //
    // return dtoMapper.toGetStoresResponse(zone.getSystemStores());
    // }

    private boolean isValidLocation (Location userLocation) {
        return !sdmDescriptor.getSystemLocations().containsKey(userLocation);
    }

    private Map<PricedItem, Double> getPricedItemFromStaticRequest (Zone zone, PlaceOrderRequest request) {
        return request.getOrderItemToAmount()
                      .keySet()
                      .stream()
                      .map(itemId -> zone.getSystemStores().get(request.getStoreId()).getItemIdToStoreItem().get(itemId).getPricedItem())
                      .collect(Collectors.toMap(pricedItem -> pricedItem,
                                                pricedItem -> request.getOrderItemToAmount().get(pricedItem.getId())));
    }

    private List<SystemItem> getItemsFromDynamicOrderRequest (Zone zone, final Map<Integer, Double> orderItemToAmount) {
        return orderItemToAmount.keySet().stream().map(itemId -> zone.getSystemItems().get(itemId)).collect(Collectors.toList());
    }

    private Set<SystemStore> getIncludedStoresInOrder (Zone zone, List<SystemItem> itemsToAmount) {
        return itemsToAmount.stream()
                            .map(systemItem -> zone.getSystemStores().get(systemItem.getStoreSellsInCheapestPrice()))
                            .collect(Collectors.toSet());
    }

    private SystemCustomer getSystemCustomer (UUID customerId) {
        Map<UUID, SystemCustomer> systemCustomers = sdmDescriptor.getSystemCustomers();
        if (!systemCustomers.containsKey(customerId)) {
            throw new RuntimeException(String.format("There is no customer with id: %s in the system", customerId));
        }

        return systemCustomers.get(customerId);
    }

    private StoresOwner getStoresOwner (UUID storesOwnerId) {
        Map<UUID, StoresOwner> storesOwners = sdmDescriptor.getStoresOwners();
        if (!storesOwners.containsKey(storesOwnerId)) {
            throw new RuntimeException(String.format("There is no stores owner with id: %s in the system", storesOwnerId));
        }

        return storesOwners.get(storesOwnerId);
    }

    private Zone getZoneByName (String zoneName) {
        if (isFileLoaded()) {
            throw new FileNotLoadedException();
        }

        Map<String, Zone> zones = sdmDescriptor.getZones();
        if (!zones.containsKey(zoneName)) {
            throw new RuntimeException(String.format("There is no zone in the system with name '%s'", zoneName));
        }

        return zones.get(zoneName);
    }

    private ValidStoreDiscounts getStoreDiscounts (Map<PricedItem, Double> pricedItems, int storeId, Zone zone) {
        Map<Integer, List<Discount>> storeValidDiscounts = new TreeMap<>();
        SystemStore systemStore = zone.getSystemStores().get(storeId);
        Map<Integer, List<Discount>> storeDiscounts = systemStore.getStore().getStoreDiscounts();

        for (Map.Entry<PricedItem, Double> PI_Entry : pricedItems.entrySet()) {
            int itemId = PI_Entry.getKey().getId();

            if (storeDiscounts.containsKey(itemId)) {
                addItemDiscounts(storeValidDiscounts, PI_Entry.getValue(), storeDiscounts, itemId);
            }
        }

        return !storeValidDiscounts.isEmpty() ? new ValidStoreDiscounts(storeValidDiscounts) : null;
    }

    private void addItemDiscounts (Map<Integer, List<Discount>> storeValidDiscounts,
                                   Double itemQuantity,
                                   Map<Integer, List<Discount>> storeDiscounts,
                                   int itemId) {
        List<Discount> itemDiscounts = storeDiscounts.get(itemId);
        List<Discount> validDiscounts = itemDiscounts.stream()
                                                     .filter(discount -> discount.getIfYouBuy().getQuantity() <= itemQuantity)
                                                     .collect(Collectors.toList());

        if (!validDiscounts.isEmpty()) {
            storeValidDiscounts.put(itemId, validDiscounts);
        }
    }

    private ValidStoreDiscounts getValidStoreDiscounts (UUID orderId, int storeId, UUID currStaticOrderId, SystemStore systemStore) {
        UUID id = (orderId != null) ? orderId : currStaticOrderId;
        ValidStoreDiscounts validDiscounts = orderIdToValidDiscounts.get(id).get(storeId);
        if (validDiscounts.getItemIdToValidStoreDiscounts().size() <= 0) {
            throw new RuntimeException(String.format("There is no valid discount from store %s for order with id: %",
                                                     systemStore.getName(),
                                                     orderId));
        }
        return validDiscounts;
    }

}