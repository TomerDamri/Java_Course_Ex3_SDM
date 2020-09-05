package course.java.sdm.engine.service;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
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
        UUID dynamicOrderId = UUID.randomUUID();
        Map<StoreDetails, Order> staticOrders = storesIncludedInOrder.stream()
                                                                     .collect(Collectors.toMap(systemStore -> systemStore.getStore()
                                                                                                                         .getStoreDetails(),
                                                                                               createSubOrder(request.getOrderItemToAmount(),
                                                                                                              request.getOrderDate(),
                                                                                                              orderLocation,
                                                                                                              systemItemsIncludedInOrder,
                                                                                                              dynamicOrderId)));

        DynamicOrder dynamicOrder = new DynamicOrder(dynamicOrderId, staticOrders);
        descriptor.getDynamicOrders().put(dynamicOrder.getOrderId(), dynamicOrder);

        return createPlaceDynamicOrderResponse(dynamicOrder);
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

    private Function<SystemStore, Order> createSubOrder (Map<Integer, Double> orderItemToAmount,
                                                         LocalDateTime orderDate,
                                                         Location orderLocation,
                                                         List<SystemItem> systemItemsIncludedInOrder,
                                                         UUID parentId) {
        return systemStore -> {
            Map<PricedItem, Double> pricedItems = getPricedItemFromDynamicOrderRequest(orderItemToAmount,
                                                                                       systemItemsIncludedInOrder,
                                                                                       systemStore);

            return ordersCreator.createOrder(systemStore, orderDate, orderLocation, pricedItems, parentId);
        };
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

    private Map<PricedItem, Double> getPricedItemFromDynamicOrderRequest (final Map<Integer, Double> orderItemToAmount,
                                                                          List<SystemItem> systemItemsIncludedInOrder,
                                                                          SystemStore systemStore) {
        return systemItemsIncludedInOrder.stream()
                                         .filter(systemItem -> systemItem.getStoreSellsInCheapestPrice().equals(systemStore.getId()))
                                         .map(createPricedItem(systemStore))
                                         .collect(Collectors.toMap(pricedItem -> pricedItem,
                                                                   pricedItem -> orderItemToAmount.get(pricedItem.getId())));
    }

    private List<SystemItem> getItemsFromDynamicOrderRequest (final Map<Integer, Double> orderItemToAmount) {
        return orderItemToAmount.keySet().stream().map(itemId -> descriptor.getSystemItems().get(itemId)).collect(Collectors.toList());
    }

    private Set<SystemStore> getIncludedStoresInOrder (List<SystemItem> itemsToAmount) {
        return itemsToAmount.stream()
                            .map(systemItem -> descriptor.getSystemStores().get(systemItem.getStoreSellsInCheapestPrice()))
                            .collect(Collectors.toSet());
    }

    private Function<SystemItem, PricedItem> createPricedItem (SystemStore systemStore) {
        return systemItem -> {
            int itemPriceInStore = systemStore.getItemIdToStoreItem().get(systemItem.getId()).getPrice();
            return new PricedItem(systemItem.getItem(), itemPriceInStore);
        };
    }
}