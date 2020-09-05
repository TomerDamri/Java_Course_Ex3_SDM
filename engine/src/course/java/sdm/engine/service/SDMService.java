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
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.response.*;

public class SDMService {

    private final FileManager fileManager = FileManager.getFileManager();
    private final OrdersCreator ordersCreator = OrdersCreator.getOrdersExecutor();
    private final SystemUpdater systemUpdater = SystemUpdater.getSystemUpdater();
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

    public Map<Integer, Map<Integer, List<Discount>>> getDiscounts (UUID orderId) {
        TempOrder tempOrder = ordersCreator.getTempOrder(orderId);
        Map<StoreDetails, Order> staticOrders = tempOrder.getStaticOrders();
        Map<Integer, Map<Integer, List<Discount>>> returnDiscounts = new TreeMap<>();

        for (Map.Entry<StoreDetails, Order> SO_Entry : staticOrders.entrySet()) {
            Map<PricedItem, Double> pricedItems = SO_Entry.getValue().getPricedItems();
            int storeId = SO_Entry.getKey().getId();
            Map<Integer, List<Discount>> storeValidDiscounts = createStoreValidDiscounts(pricedItems, storeId);

            if (storeValidDiscounts != null) {
                returnDiscounts.put(storeId, storeValidDiscounts);
            }
        }

        return returnDiscounts;
    }

    private Map<Integer, List<Discount>> createStoreValidDiscounts (Map<PricedItem, Double> pricedItems, int storeId) {
        Map<Integer, List<Discount>> storeValidDiscounts = null;

        for (Map.Entry<PricedItem, Double> PI_Entry : pricedItems.entrySet()) {
            Map<Integer, List<Discount>> storeDiscounts = descriptor.getSystemStores().get(storeId).getStore().getStoreDiscounts();
            int itemId = PI_Entry.getKey().getId();

            if (storeDiscounts.containsKey(itemId)) {
                storeValidDiscounts = putItemDiscountsInStoreValidDiscounts(storeValidDiscounts, PI_Entry, storeDiscounts, itemId);
            }
        }
        return storeValidDiscounts;
    }

    private Map<Integer, List<Discount>> putItemDiscountsInStoreValidDiscounts (Map<Integer, List<Discount>> storeValidDiscounts,
                                                                                Map.Entry<PricedItem, Double> PI_Entry,
                                                                                Map<Integer, List<Discount>> storeDiscounts,
                                                                                int itemId) {
        Double itemQuantity = PI_Entry.getValue();
        List<Discount> itemDiscounts = storeDiscounts.get(itemId);
        List<Discount> validDiscounts = itemDiscounts.stream()
                                                     .filter(discount -> discount.getIfYouBuy().getQuantity() <= itemQuantity)
                                                     .collect(Collectors.toList());

        if (!validDiscounts.isEmpty()) {
            if (storeValidDiscounts == null) {
                storeValidDiscounts = new TreeMap<>();
            }

            storeValidDiscounts.put(itemId, validDiscounts);
        }
        return storeValidDiscounts;
    }

    public PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request) {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }

        Map<PricedItem, Double> pricedItems = getPricedItemFromStaticRequest(request);
        Order newOrder = addNewStaticOrder(request, pricedItems);

        return new PlaceOrderResponse(newOrder.getId());
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

    public PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request) {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }

        Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());
        List<SystemItem> systemItemsIncludedInOrder = getItemsFromDynamicOrderRequest(request.getOrderItemToAmount());
        Set<SystemStore> storesIncludedInOrder = getIncludedStoresInOrder(systemItemsIncludedInOrder);
        DynamicOrder dynamicOrder = ordersCreator.createDynamicOrder(request,
                                                                     orderLocation,
                                                                     systemItemsIncludedInOrder,
                                                                     storesIncludedInOrder);
        descriptor.getDynamicOrders().put(dynamicOrder.getOrderId(), dynamicOrder);

        return createPlaceDynamicOrderResponse(dynamicOrder);
    }

    public PlaceOrderResponse placeStaticOrderV2 (PlaceOrderRequest request) {
        if (descriptor == null) {
            throw new FileNotLoadedException();
        }

        SystemStore systemStore = descriptor.getSystemStores().get(request.getStoreId());
        LocalDateTime orderDate = request.getOrderDate();
        Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());
        Map<PricedItem, Double> pricedItems = getPricedItemFromStaticRequest(request);
        int customerId = request.getCustomerId();

        Order newOrder = ordersCreator.createOrderV2(systemStore, orderDate, orderLocation, pricedItems, null, customerId);

        return new PlaceOrderResponse(newOrder.getId());
    }

    public void completeDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder) {
        systemUpdater.updateSystemAfterDynamicOrder(dynamicOrderId, toConfirmNewDynamicOrder, descriptor);
    }

    public void saveOrdersHistoryToFile (String path) {
        fileManager.saveOrdersHistoryToFile(descriptor, path);
    }

    public void loadDataFromFile (String path) {
        SystemOrdersHistory systemOrdersHistory = fileManager.loadDataFromFile(path);
        Map<UUID, List<SystemOrder>> historySystemOrders = systemOrdersHistory.getSystemOrders();
        Map<UUID, DynamicOrder> historyDynamicOrders = systemOrdersHistory.getDynamicOrders();

        systemUpdater.updateSystemAfterLoadingOrdersHistoryFromFile(historySystemOrders, historyDynamicOrders, descriptor);
    }

    private Order addNewStaticOrder (PlaceOrderRequest request, Map<PricedItem, Double> pricedItems) {
        SystemStore systemStore = descriptor.getSystemStores().get(request.getStoreId());
        LocalDateTime orderDate = request.getOrderDate();
        Location orderLocation = new Location(request.getxCoordinate(), request.getyCoordinate());

        Order newOrder = ordersCreator.createOrder(systemStore, orderDate, orderLocation, pricedItems, null);
        systemUpdater.updateSystemAfterStaticOrder(systemStore, newOrder, descriptor);

        return newOrder;
    }

    private PlaceDynamicOrderResponse createPlaceDynamicOrderResponse (DynamicOrder dynamicOrder) {
        List<DynamicOrderEntityDTO> dynamicOrderEntityDTOS = dynamicOrder.getStaticOrders()
                                                                         .entrySet()
                                                                         .stream()
                                                                         .map(entry -> createDynamicOrderEntity(entry))
                                                                         .collect(Collectors.toList());

        return new PlaceDynamicOrderResponse(dynamicOrder.getOrderId(), dynamicOrderEntityDTOS);
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