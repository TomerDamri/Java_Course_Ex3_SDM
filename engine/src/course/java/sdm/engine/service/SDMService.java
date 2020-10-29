package course.java.sdm.engine.service;

import course.java.sdm.engine.exceptions.FileNotLoadedException;
import course.java.sdm.engine.mapper.DTOMapper;
import course.java.sdm.engine.model.*;
import course.java.sdm.engine.users.UserManager;
import course.java.sdm.engine.utils.SDMUtils;
import course.java.sdm.engine.utils.accountManager.AccountManager;
import course.java.sdm.engine.utils.fileManager.FileManager;
import course.java.sdm.engine.utils.ordersCreator.OrdersCreator;
import course.java.sdm.engine.utils.systemUpdater.SystemUpdater;
import examples.jaxb.schema.generated.SuperDuperMarketDescriptor;
import model.TransactionDTO;
import model.User;
import model.request.*;
import model.response.*;

import javax.servlet.http.Part;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SDMService {

    private final static DTOMapper dtoMapper = DTOMapper.getDTOMapper();
    private final FileManager fileManager = FileManager.getFileManager();
    private final OrdersCreator ordersCreator = OrdersCreator.getOrdersExecutor();
    private final SystemUpdater systemUpdater = SystemUpdater.getSystemUpdater();
    private final UserManager userManager = UserManager.getUserManager();
    private final AccountManager accountManager = AccountManager.getAccountManager();

    private SDMDescriptor sdmDescriptor = new SDMDescriptor();
    private Map<UUID, Map<Integer, ValidStoreDiscounts>> orderIdToValidDiscounts = new HashMap<>();

    public GetStoreItemsResponse getStoreItems (GetStoreItemsRequest request) {
        Zone zone = getZoneByName(request.getZoneName());
        SystemStore systemStore = SDMUtils.getStoreByID(zone, request.getStoreId());

        return dtoMapper.toGetStoreItems(systemStore);
    }

    /* Feedback */
    public void rankOrderStores (RankOrderStoresRequest request) {
        SystemCustomer customer = getCustomerById(request.getCustomerId());
        Zone zone = getZoneByName(request.getZoneName());
        UUID orderId = request.getOrderId();
        List<SystemOrder> subOrders = getOrderByID(zone, orderId);

        systemUpdater.rankOrderStores(request.getOrderStoreRanks(), orderId, subOrders, customer, zone);
    }

    public GetFeedbackForStoreOwnerResponse getFeedbackForStoreOwner (GetFeedbackForStoreOwnerRequest request) {
        validateFileLoadedToSystem();
        StoresOwner storesOwner = getStoresOwner(request.getStoreOwnerID());
        Map<String, List<CustomerFeedback>> zoneIdToCustomerFeedbacks = new HashMap<>();

        storesOwner.getZoneToOwnedStores()
                   .forEach( (key, value) -> value.values()
                                                  .forEach(systemStore -> addFeedbacks(zoneIdToCustomerFeedbacks, key, systemStore)));

        return dtoMapper.createGetFeedbackForStoreOwnerResponse(zoneIdToCustomerFeedbacks);
    }

    private void addFeedbacks (Map<String, List<CustomerFeedback>> zoneIdToCustomerFeedbacks, String zoneName, SystemStore systemStore) {
        List<CustomerFeedback> zoneFeedbacks = zoneIdToCustomerFeedbacks.containsKey(zoneName) ? zoneIdToCustomerFeedbacks.get(zoneName)
                    : new ArrayList<>();
        zoneFeedbacks.addAll(systemStore.getCustomersFeedback());

        zoneIdToCustomerFeedbacks.put(zoneName, zoneFeedbacks);
    }

    /* Feedback */

    private SystemCustomer getCustomerById (UUID customerId) {
        Map<UUID, SystemCustomer> systemCustomers = sdmDescriptor.getSystemCustomers();
        if (systemCustomers.containsKey(customerId)) {
            throw new RuntimeException(String.format("There is no customer in the system with id '%s'", customerId));
        }

        return systemCustomers.get(customerId);
    }

    /* Feedback */

    /* Users */

    public UUID addUserToSystem (String username, User.UserType userType) {
        UUID userId = userManager.addUser(sdmDescriptor, username, userType);
        accountManager.createAccountForUser(userId);

        return userId;
    }

    public void removeUser (String username) {
        SystemUser removedUser = userManager.removeUser(sdmDescriptor, username);
        accountManager.removeAccountForUser(removedUser.getId());
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

    /* UserAccounts */

    public void deposit (DepositRequest request) {
        accountManager.deposit(request.getUserId(), request.getAmount());
    }

    public GetUserBalanceResponse getUserBalance (GetUserBalanceRequest request) {
        UUID userId = request.getUserId();

        Double userBalance = accountManager.getUserBalance(userId);
        return dtoMapper.toGetUserBalanceResponse(userId, userBalance);
    }

    public GetUserTransactionsResponse getUserTransactions (GetUserTransactionsRequest request) {
        UUID userId = request.getUserId();

        List<TransactionDTO> userTransactions = accountManager.getUserTransactions(userId);
        return dtoMapper.toGetUserTransactionsResponse(userTransactions);
    }
    /* UserAccounts */

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
            UUID customerId = tempOrder.getCustomerId();
            SystemCustomer systemCustomer = sdmDescriptor.getSystemCustomers().get(customerId);
            Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();

            staticOrders.forEach( (storeDetails, order) -> {
                SystemStore systemStore = zone.getSystemStores().get(storeDetails.getId());

                systemUpdater.updateSystemAfterStaticOrderV2(systemStore, order, zone, systemCustomer);
                accountManager.transfer(customerId, order.getTotalPrice(), systemStore.getStoreOwnerId(), order.getOrderDate());
            });

            systemUpdater.updateSystemCustomerAfterDynamicOrder(orderId, systemCustomer, staticOrders, zone.getZoneName());
        }

        ordersCreator.deleteTempOrder(orderId);
    }

    /* Placing orders - static/dynamic */

    public void saveOrdersHistoryToFile (String path) {
        validateFileLoadedToSystem();

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

    public void loadData (Part part, UUID storesOwnerID) throws IOException {
        StoresOwner storesOwner = getStoresOwner(storesOwnerID);
        SuperDuperMarketDescriptor superDuperMarketDescriptor = fileManager.generateDataFromXmlFile(part);
        Zone newZone = fileManager.loadDataFromGeneratedData(superDuperMarketDescriptor, storesOwner, sdmDescriptor);
        systemUpdater.updateSystemAfterLoadingZoneFile(sdmDescriptor, newZone, storesOwner);
    }

    public boolean isFileLoaded () {
        Map<String, Zone> zones = sdmDescriptor.getZones();

        return zones != null && !zones.isEmpty();
    }

    public boolean isValidLocation (final int xCoordinate, final int yCoordinate) {
        Location userLocation = new Location(xCoordinate, yCoordinate);

        return isValidLocation(userLocation);
    }

    public GetZoneResponse getZone (String zoneName) {
        validateFileLoadedToSystem();

        Map<String, Zone> zones = sdmDescriptor.getZones();
        if (!zones.containsKey(zoneName)) {
            throw new RuntimeException(String.format("No zone with name %s exist in the system", zoneName));
        }

        return dtoMapper.toGetZoneResponse(zones.get(zoneName));
    }

    private void validateFileLoadedToSystem () {
        if (!isFileLoaded()) {
            throw new FileNotLoadedException();
        }
    }

    public GetCustomersResponse getCustomers () {
        validateFileLoadedToSystem();

        Map<UUID, SystemCustomer> systemCustomers = sdmDescriptor.getSystemCustomers();

        return dtoMapper.toGetCustomersResponse(systemCustomers);
    }

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
     */

    public GetCustomerOrdersResponse getCustomerOrders (GetCustomerOrdersRequest request) {
        validateFileLoadedToSystem();

        SystemCustomer customer = getCustomerById(request.getCustomerId());
        Map<UUID, List<SystemOrder>> customerOrders = new HashMap<>();

        customer.getZoneNameToExecutedOrdersId().forEach( (zoneName, executedOrdersIds) -> {
            Zone zone = getZoneByName(zoneName);

            zone.getSystemOrders()
                .entrySet()
                .stream()
                .filter(entry -> executedOrdersIds.contains(entry.getKey()))
                .forEach(entry -> customerOrders.put(entry.getKey(), entry.getValue()));
        });

        return dtoMapper.toGetCustomerOrdersResponse(customerOrders);
    }

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
//todo - throws null pointer exception?
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

    private List<SystemOrder> getOrderByID (Zone zone, UUID orderId) {
        Map<UUID, List<SystemOrder>> zoneOrders = zone.getSystemOrders();
        if (!zoneOrders.containsKey(orderId)) {
            throw new RuntimeException(String.format("There is no order with id: '%s' in '%s' zone", orderId, zone.getZoneName()));
        }

        return zoneOrders.get(orderId);
    }

    private Zone getZoneByName (String zoneName) {
        validateFileLoadedToSystem();

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