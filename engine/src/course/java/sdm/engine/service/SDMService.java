package course.java.sdm.engine.service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import course.java.sdm.engine.exceptions.FileNotLoadedException;
import course.java.sdm.engine.mapper.DTOMapper;
import course.java.sdm.engine.model.*;
import course.java.sdm.engine.utils.fileManager.FileManager;
import course.java.sdm.engine.utils.ordersCreator.OrdersCreator;
import course.java.sdm.engine.utils.systemUpdater.SystemUpdater;
import examples.jaxb.schema.generated.SuperDuperMarketDescriptor;
import model.DynamicOrderEntityDTO;
import model.request.*;
import model.response.*;

public class SDMService {

    private final FileManager fileManager = FileManager.getFileManager();
    private final OrdersCreator ordersCreator = OrdersCreator.getOrdersExecutor();
    private final SystemUpdater systemUpdater = SystemUpdater.getSystemUpdater();
    private Map<UUID, Map<Integer, StoreValidDiscounts>> orderIdToValidDiscounts = new HashMap<>();
    private final static DTOMapper mapper = new DTOMapper();
    private Descriptor descriptor;

    public void loadData (String xmlDataFileStr) throws FileNotFoundException {
        SuperDuperMarketDescriptor superDuperMarketDescriptor = fileManager.generateDataFromXmlFile(xmlDataFileStr);
        this.descriptor = fileManager.loadDataFromGeneratedData(superDuperMarketDescriptor);
    }

    public boolean isFileLoaded () {
        return descriptor != null;
    }

    public GetStoresResponse getStores () {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }
        return mapper.toGetStoresResponse(descriptor.getSystemStores());
    }

    public GetCustomersResponse getCustomers () {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }
        return mapper.toGetCustomersResponse(descriptor.getSystemCustomers());
    }

    public GetItemsResponse getItems () {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }
        return mapper.toGetItemsResponse(descriptor.getSystemItems());
    }

    public GetOrdersResponse getOrders () {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }
        return mapper.toGetOrdersResponse(descriptor.getSystemOrders());
    }

    public Map<Integer, StoreValidDiscounts> getOrderDiscounts (UUID orderId) {

        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);
        Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();
        Map<Integer, StoreValidDiscounts> returnDiscounts = new TreeMap<>();

        for (Map.Entry<StoreDetails, Order> SO_Entry : staticOrders.entrySet()) {
            Order currOrder = SO_Entry.getValue();
            StoreDetails currStoreDetails = SO_Entry.getKey();

            Map<PricedItem, Double> pricedItems = currOrder.getPricedItems();
            int storeId = currStoreDetails.getId();
            StoreValidDiscounts storeValidDiscounts = getStoreDiscounts(pricedItems, storeId);

            if (storeValidDiscounts != null) {
                returnDiscounts.put(storeId, storeValidDiscounts);
            }
        }

        orderIdToValidDiscounts.put(orderId, returnDiscounts);
        return returnDiscounts;
    }

    private StoreValidDiscounts getStoreDiscounts (Map<PricedItem, Double> pricedItems, int storeId) {
        Map<Integer, List<Discount>> storeValidDiscounts = new TreeMap<>();
        SystemStore systemStore = descriptor.getSystemStores().get(storeId);
        Map<Integer, List<Discount>> storeDiscounts = systemStore.getStore().getStoreDiscounts();

        for (Map.Entry<PricedItem, Double> PI_Entry : pricedItems.entrySet()) {
            int itemId = PI_Entry.getKey().getId();

            if (storeDiscounts.containsKey(itemId)) {
                addItemDiscounts(storeValidDiscounts, PI_Entry.getValue(), storeDiscounts, itemId);
            }
        }

        return !storeValidDiscounts.isEmpty() ? new StoreValidDiscounts(storeValidDiscounts) : null;
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

    public void addDiscountsToOrder (AddDiscountsToOrderRequest request) {
        UUID orderId = request.getOrderID();
        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);
        Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();

        staticOrders.forEach(( (storeDetails, order) -> {
            int storeId = storeDetails.getId();
            UUID currStaticOrderId = order.getId();

            SystemStore systemStore = descriptor.getSystemStores().get(storeId);
            StoreChosenDiscounts storeChosenDiscounts = request.getStoreIdToChosenDiscounts().get(storeId);
            StoreValidDiscounts validDiscounts = orderIdToValidDiscounts.get(currStaticOrderId).get(storeId);

            ordersCreator.addDiscountsPerStoreToStaticOrder(systemStore, order, storeChosenDiscounts, validDiscounts);

            ordersCreator.completeTheOrderV2(systemStore, order);
        }));

    }

    // public PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request) {
    // if (descriptor == null) {
    // throw new FileNotLoadedException();
    // }
    //
    // Map<PricedItem, Double> pricedItems = getPricedItemFromStaticRequest(request);
    // Order newOrder = addNewStaticOrder(request, pricedItems);
    //
    // return new PlaceOrderResponse(newOrder.getId());
    // }

    public PlaceOrderResponse placeStaticOrderV2 (PlaceOrderRequest request) {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }

        int customerId = request.getCustomerId();
        if (!descriptor.getSystemCustomers().containsKey(customerId)) {
            throw new RuntimeException(String.format("There is no customer with id: %s in the system", customerId));
        }

        SystemCustomer customer = descriptor.getSystemCustomers().get(customerId);

        Map<PricedItem, Double> pricedItems = getPricedItemFromStaticRequest(request);
        SystemStore systemStore = descriptor.getSystemStores().get(request.getStoreId());
        LocalDateTime orderDate = request.getOrderDate();
        Location orderLocation = customer.getLocation();

        Order newOrder = ordersCreator.createOrderV2(systemStore, orderDate, orderLocation, pricedItems, null, customerId);

        return new PlaceOrderResponse(newOrder.getId());
    }

    public void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder) {
        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);

        if (toConfirmNewDynamicOrder) {
            Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();

            staticOrders.forEach( (storeDetails, order) -> {
                SystemStore systemStore = descriptor.getSystemStores().get(storeDetails.getId());

                systemUpdater.updateSystemAfterStaticOrderV2(systemStore, order, descriptor);
            });
        }

        ordersCreator.deleteTempOrder(orderId);
    }

    public boolean isValidLocation (final int xCoordinate, final int yCoordinate) {
        Location userLocation = new Location(xCoordinate, yCoordinate);
        List<Location> allStoresLocations = descriptor.getSystemStores()
                                                      .values()
                                                      .stream()
                                                      .map(SystemStore::getLocation)
                                                      .collect(Collectors.toList());

        return !allStoresLocations.contains(userLocation);
    }

    // public PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request) {
    // if (descriptor == null) {
    // throw new FileNotLoadedException();
    // }
    //
    // Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());
    // List<SystemItem> systemItemsIncludedInOrder =
    // getItemsFromDynamicOrderRequest(request.getOrderItemToAmount());
    // Set<SystemStore> storesIncludedInOrder = getIncludedStoresInOrder(systemItemsIncludedInOrder);
    // DynamicOrder dynamicOrder = ordersCreator.createDynamicOrder(request,
    // orderLocation,
    // systemItemsIncludedInOrder,
    // storesIncludedInOrder);
    // descriptor.getDynamicOrders().put(dynamicOrder.getOrderId(), dynamicOrder);
    //
    // return createPlaceDynamicOrderResponse(dynamicOrder);
    // }

    public PlaceDynamicOrderResponse placeDynamicOrderV2 (PlaceDynamicOrderRequest request) {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }

        Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());
        List<SystemItem> systemItemsIncludedInOrder = getItemsFromDynamicOrderRequest(request.getOrderItemToAmount());
        Set<SystemStore> storesIncludedInOrder = getIncludedStoresInOrder(systemItemsIncludedInOrder);
        TempOrder tempDynamicOrder = ordersCreator.createDynamicOrderV2(request,
                                                                        orderLocation,
                                                                        systemItemsIncludedInOrder,
                                                                        storesIncludedInOrder,
                                                                        request.getCustomerId());

        return createPlaceDynamicOrderResponseV2(tempDynamicOrder);
    }

    // public void completeDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder) {
    // systemUpdater.updateSystemAfterDynamicOrder(dynamicOrderId, toConfirmNewDynamicOrder,
    // descriptor);
    // }

    public void saveOrdersHistoryToFile (String path) {
        fileManager.saveOrdersHistoryToFile(descriptor, path);
    }

    public void loadOrdersHistoryFromFile (String path) {
        SystemOrdersHistory systemOrdersHistory = fileManager.loadDataFromFile(path);
        Map<UUID, List<SystemOrder>> historySystemOrders = systemOrdersHistory.getSystemOrders();

        systemUpdater.updateSystemAfterLoadingOrdersHistoryFromFile(historySystemOrders, descriptor);
    }

    // private Order addNewStaticOrder (PlaceOrderRequest request, Map<PricedItem, Double> pricedItems)
    // {
    // SystemStore systemStore = descriptor.getSystemStores().get(request.getStoreId());
    // LocalDateTime orderDate = request.getOrderDate();
    // Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());
    //
    // Order newOrder = ordersCreator.createOrder(systemStore, orderDate, orderLocation, pricedItems,
    // null);
    // systemUpdater.updateSystemAfterStaticOrder(systemStore, newOrder, descriptor);
    //
    // return newOrder;
    // }

    // private PlaceDynamicOrderResponse createPlaceDynamicOrderResponse (DynamicOrder dynamicOrder) {
    // List<DynamicOrderEntityDTO> dynamicOrderEntityDTOS = dynamicOrder.getStaticOrders()
    // .entrySet()
    // .stream()
    // .map(entry -> createDynamicOrderEntity(entry))
    // .collect(Collectors.toList());
    //
    // return new PlaceDynamicOrderResponse(dynamicOrder.getOrderId(), dynamicOrderEntityDTOS);
    // }

    private PlaceDynamicOrderResponse createPlaceDynamicOrderResponseV2 (TempOrder tempDynamicOrder) {
        List<DynamicOrderEntityDTO> dynamicOrderEntityDTOS = tempDynamicOrder.getStaticOrders()
                                                                             .entrySet()
                                                                             .stream()
                                                                             .map(this::createDynamicOrderEntity)
                                                                             .collect(Collectors.toList());

        return new PlaceDynamicOrderResponse(tempDynamicOrder.getOrderId(), dynamicOrderEntityDTOS);
    }

    private DynamicOrderEntityDTO createDynamicOrderEntity (Map.Entry<StoreDetails, Order> entry) {
        StoreDetails storeDetails = entry.getKey();
        Order order = entry.getValue();

        return new DynamicOrderEntityDTO(storeDetails.getId(),
                                         storeDetails.getName(),
                                         storeDetails.getLocation().getX(),
                                         storeDetails.getLocation().getY(),
                                         order.getDistanceFromCustomerLocation(),
                                         storeDetails.getDeliveryPpk(),
                                         order.getNumOfItemTypes(),
                                         order.getDeliveryPrice(),
                                         order.getItemsPrice());
    }

    private Map<PricedItem, Double> getPricedItemFromStaticRequest (PlaceOrderRequest request) {
        return request.getOrderItemToAmount()
                      .keySet()
                      .stream()
                      .map(itemId -> descriptor.getSystemStores()
                                               .get(request.getStoreId())
                                               .getItemIdToStoreItem()
                                               .get(itemId)
                                               .getPricedItem())
                      .collect(Collectors.toMap(pricedItem -> pricedItem,
                                                pricedItem -> request.getOrderItemToAmount().get(pricedItem.getId())));
    }

    private List<SystemItem> getItemsFromDynamicOrderRequest (final Map<Integer, Double> orderItemToAmount) {
        return orderItemToAmount.keySet().stream().map(itemId -> descriptor.getSystemItems().get(itemId)).collect(Collectors.toList());
    }

    private Set<SystemStore> getIncludedStoresInOrder (List<SystemItem> itemsToAmount) {
        return itemsToAmount.stream()
                            .map(systemItem -> descriptor.getSystemStores().get(systemItem.getStoreSellsInCheapestPrice()))
                            .collect(Collectors.toSet());
    }
}