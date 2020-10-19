package course.java.sdm.engine.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.Part;

import course.java.sdm.engine.exceptions.FileNotLoadedException;
import course.java.sdm.engine.mapper.DTOMapper;
import course.java.sdm.engine.model.*;
import course.java.sdm.engine.users.User;
import course.java.sdm.engine.users.UserManager;
import course.java.sdm.engine.utils.fileManager.FileManager;
import course.java.sdm.engine.utils.ordersCreator.OrdersCreator;
import course.java.sdm.engine.utils.systemUpdater.SystemUpdater;
import examples.jaxb.schema.generated.SuperDuperMarketDescriptor;
import model.DynamicOrderEntityDTO;
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

    private Zone zone;

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

    public void loadData (Part part, UUID storesOwnerID) throws IOException {
        StoresOwner storesOwner = getStoresOwner(storesOwnerID);
        SuperDuperMarketDescriptor superDuperMarketDescriptor = fileManager.generateDataFromXmlFile(part);
        Zone newZone = fileManager.loadDataFromGeneratedData(superDuperMarketDescriptor, storesOwner, sdmDescriptor);
        systemUpdater.updateSystemAfterLoadingZoneFile(sdmDescriptor, newZone, storesOwner);
    }

    public boolean isFileLoaded () {
        return zone != null;
    }

    public GetStoresResponse getStores () {
        if (zone == null) {
            throw new FileNotLoadedException();
        }
        return dtoMapper.toGetStoresResponse(zone.getSystemStores());
    }

    public GetCustomersResponse getCustomers () {
        // if (zone == null) {
        // throw new FileNotLoadedException();
        // }
        //
        // Map<Integer, SystemCustomer> systemCustomers = zone.getSystemCustomers();
        //
        // return dtoMapper.toGetCustomersResponse(systemCustomers);
        return null;
    }

    // public GetMapEntitiesResponse getSystemMappableEntities () {
    // // if (zone == null) {
    // // throw new FileNotLoadedException();
    // // }
    // //
    // // return dtoMapper.toGetSystemMappableEntitiesResponse(zone.getMappableEntities().values());
    // return null;
    // }

    public GetItemsResponse getItems () {
        if (zone == null) {
            throw new FileNotLoadedException();
        }
        return dtoMapper.toGetItemsResponse(zone.getSystemItems());
    }

    public GetOrdersResponse getOrders () {
        if (zone == null) {
            throw new FileNotLoadedException();
        }
        return dtoMapper.toGetOrdersResponse(zone.getSystemOrders());
    }

    public GetDiscountsResponse getOrderDiscounts (UUID orderId) {

        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);
        Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();
        Map<Integer, ValidStoreDiscounts> returnDiscounts = new TreeMap<>();

        for (Map.Entry<StoreDetails, Order> SO_Entry : staticOrders.entrySet()) {
            Order currOrder = SO_Entry.getValue();
            StoreDetails currStoreDetails = SO_Entry.getKey();

            Map<PricedItem, Double> pricedItems = currOrder.getPricedItems();
            int storeId = currStoreDetails.getId();
            ValidStoreDiscounts validStoreDiscounts = getStoreDiscounts(pricedItems, storeId);

            if (validStoreDiscounts != null) {
                returnDiscounts.put(storeId, validStoreDiscounts);
            }
        }

        orderIdToValidDiscounts.put(orderId, returnDiscounts);
        return dtoMapper.createGetDiscountsResponse(returnDiscounts, zone.getSystemStores());
    }

    private ValidStoreDiscounts getStoreDiscounts (Map<PricedItem, Double> pricedItems, int storeId) {
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

    public FinalSummaryForOrder addDiscountsToOrder (AddDiscountsToOrderRequest request) {
        UUID orderId = request.getOrderID();
        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);
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

    public PlaceOrderResponse placeStaticOrderV2 (PlaceOrderRequest request) {
        if (zone == null) {
            throw new FileNotLoadedException();
        }
        SystemCustomer customer = getSystemCustomer(request.getCustomerId());

        Map<PricedItem, Double> pricedItems = getPricedItemFromStaticRequest(request);
        SystemStore systemStore = zone.getSystemStores().get(request.getStoreId());
        LocalDate orderDate = request.getOrderDate();
        Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());

        Order newOrder = ordersCreator.createOrderV2(systemStore, orderDate, orderLocation, pricedItems, null, customer.getId());

        return new PlaceOrderResponse(newOrder.getId());
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

    public void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder) {
        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);

        if (toConfirmNewDynamicOrder) {
            SystemCustomer systemCustomer = sdmDescriptor.getSystemCustomers().get(tempOrder.getCustomerId());
            Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();

            staticOrders.forEach( (storeDetails, order) -> {
                SystemStore systemStore = zone.getSystemStores().get(storeDetails.getId());

                systemUpdater.updateSystemAfterStaticOrderV2(systemStore, order, zone, systemCustomer);
            });

            updateSystemCustomer(orderId, systemCustomer, staticOrders);
        }

        ordersCreator.deleteTempOrder(orderId);
    }

    // will update numOfOrders property for chosen system customer in case the temp order is dynamic
    // order
    private void updateSystemCustomer (UUID orderId, SystemCustomer systemCustomer, Map<StoreDetails, Order> staticOrders) {
        Order firstSubOrder = staticOrders.values().iterator().next();
        if (staticOrders.size() > 1 || !firstSubOrder.getId().equals(orderId)) {
            int prevNumOfOrders = systemCustomer.getNumOfOrders();
            systemCustomer.setNumOfOrders(prevNumOfOrders + 1);
        }
    }

    public boolean isValidLocation (final int xCoordinate, final int yCoordinate) {
        Location userLocation = new Location(xCoordinate, yCoordinate);

        return isValidLocation(userLocation);
    }

    public boolean isValidLocation (Location userLocation) {
        return !sdmDescriptor.getSystemLocations().containsKey(userLocation);
    }

    public PlaceDynamicOrderResponse placeDynamicOrderV2 (PlaceDynamicOrderRequest request) {
        if (zone == null) {
            throw new FileNotLoadedException();
        }

        UUID customerId = request.getCustomerId();
        Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());
        List<SystemItem> systemItemsIncludedInOrder = getItemsFromDynamicOrderRequest(request.getOrderItemToAmount());
        Set<SystemStore> storesIncludedInOrder = getIncludedStoresInOrder(systemItemsIncludedInOrder);
        TempOrder tempDynamicOrder = ordersCreator.createDynamicOrderV2(request,
                                                                        orderLocation,
                                                                        systemItemsIncludedInOrder,
                                                                        storesIncludedInOrder,
                                                                        customerId);

        return createPlaceDynamicOrderResponseV2(tempDynamicOrder);
    }

    public void saveOrdersHistoryToFile (String path) {
        fileManager.saveOrdersHistoryToFile(sdmDescriptor, path);
    }

    public void loadSystemHistoryToFile (String path) {
        SystemOrdersHistory systemOrdersHistory = fileManager.loadDataFromFile(path);
        Map<String, Zone> zones = sdmDescriptor.getZones();

        systemOrdersHistory.getZoneOrdersHistories().forEach(zoneOrdersHistory -> {
            String zoneName = zoneOrdersHistory.getZoneName();
            if (zones.containsKey(zoneName)) {
                throw new RuntimeException(String.format("There is no zone with name: '%s' in the system", zoneName));
            }

            Zone zone = zones.get(zoneName);
            Map<UUID, List<SystemOrder>> zoneHistoryOrders = zoneOrdersHistory.getSystemOrders();
            systemUpdater.updateSystemAfterLoadingOrdersHistoryFromFile(zoneHistoryOrders, sdmDescriptor.getSystemCustomers(), zone);
        });
    }

    private PlaceDynamicOrderResponse createPlaceDynamicOrderResponseV2 (TempOrder tempDynamicOrder) {
        List<DynamicOrderEntityDTO> dynamicOrderEntityDTOS = tempDynamicOrder.getStaticOrders()
                                                                             .entrySet()
                                                                             .stream()
                                                                             .map(entry -> dtoMapper.toDynamicOrderEntityDTO(entry.getKey(),
                                                                                                                             entry.getValue()))
                                                                             .collect(Collectors.toList());

        return new PlaceDynamicOrderResponse(tempDynamicOrder.getOrderId(), dynamicOrderEntityDTOS);
    }

    private Map<PricedItem, Double> getPricedItemFromStaticRequest (PlaceOrderRequest request) {
        return request.getOrderItemToAmount()
                      .keySet()
                      .stream()
                      .map(itemId -> zone.getSystemStores().get(request.getStoreId()).getItemIdToStoreItem().get(itemId).getPricedItem())
                      .collect(Collectors.toMap(pricedItem -> pricedItem,
                                                pricedItem -> request.getOrderItemToAmount().get(pricedItem.getId())));
    }

    private List<SystemItem> getItemsFromDynamicOrderRequest (final Map<Integer, Double> orderItemToAmount) {
        return orderItemToAmount.keySet().stream().map(itemId -> zone.getSystemItems().get(itemId)).collect(Collectors.toList());
    }

    private Set<SystemStore> getIncludedStoresInOrder (List<SystemItem> itemsToAmount) {
        return itemsToAmount.stream()
                            .map(systemItem -> zone.getSystemStores().get(systemItem.getStoreSellsInCheapestPrice()))
                            .collect(Collectors.toSet());
    }

    public void addItemToStore (UpdateStoreRequest request) {
        Integer itemId = request.getItemId();
        Integer storeId = request.getStoreId();
        Integer itemPrice = request.getItemPrice();
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();
        Map<Integer, SystemItem> systemItems = zone.getSystemItems();

        systemUpdater.addItemToStore(itemId, storeId, itemPrice, systemStores, systemItems);
    }

    public DeleteItemFromStoreResponse deleteItemFromStore (BaseUpdateStoreRequest request) {
        Integer itemId = request.getItemId();
        Integer storeId = request.getStoreId();
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();
        Map<Integer, SystemItem> systemItems = zone.getSystemItems();

        DeleteItemResult deleteItemResult = systemUpdater.deleteItemFromStore(itemId, storeId, systemStores, systemItems);
        List<Discount> removedDiscounts = deleteItemResult.getRemovedDiscounts();

        return dtoMapper.createDeleteItemFromStoreResponse(removedDiscounts);
    }

    public void updateItemPrice (UpdateStoreRequest request) {
        Integer itemId = request.getItemId();
        Integer storeId = request.getStoreId();
        Integer itemPrice = request.getItemPrice();
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();
        Map<Integer, SystemItem> systemItems = zone.getSystemItems();

        systemUpdater.updateItemPrice(itemId, storeId, itemPrice, systemStores, systemItems);
    }

}