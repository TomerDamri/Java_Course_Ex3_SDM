package course.java.sdm.engine.utils.ordersCreator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import course.java.sdm.engine.mapper.GeneratedDataMapper;
import course.java.sdm.engine.model.*;
import model.request.PlaceDynamicOrderRequest;

public class OrdersCreator {

    private Map<UUID, TempOrder> tempStaticOrders = new TreeMap<>();

    private static OrdersCreator singletonOrderExecutor = null;
    private final static OrdersCreatorValidator ORDERS_CREATOR_VALIDATOR = new OrdersCreatorValidator();

    private OrdersCreator () {
    }

    public static OrdersCreator getOrdersExecutor () {
        if (singletonOrderExecutor == null) {
            singletonOrderExecutor = new OrdersCreator();
        }

        return singletonOrderExecutor;
    }

    public Order createOrderV2 (SystemStore systemStore,
                                LocalDateTime orderDate,
                                Location orderLocation,
                                Map<PricedItem, Double> pricedItemToAmountMap,
                                UUID parentId,
                                Integer customerId) {
        ORDERS_CREATOR_VALIDATOR.validateLocation(orderLocation, systemStore);
        Order newOrder = new Order(orderDate, orderLocation, parentId);
        addItemsToOrder(systemStore, newOrder, pricedItemToAmountMap);
        completeTheOrder(systemStore, newOrder);

        TempOrder tempOrder = new TempOrder(newOrder.getId(),
                                            Collections.singletonMap(systemStore.getStore().getStoreDetails(), newOrder),
                                            customerId);
        tempStaticOrders.put(tempOrder.getOrderId(), tempOrder);

        return newOrder;
    }

    public TempOrder getTempOrder (UUID orderId) {
        ORDERS_CREATOR_VALIDATOR.validateTempStaticOrderExist(orderId, tempStaticOrders);
        return tempStaticOrders.get(orderId);
        // ORDERS_CREATOR_VALIDATOR.validateTempOrderNotConfirmedYet(orderId, tempOrder);
    }

    public Order createOrder (SystemStore systemStore,
                              LocalDateTime orderDate,
                              Location orderLocation,
                              Map<PricedItem, Double> pricedItemToAmountMap,
                              UUID parentId) {
        ORDERS_CREATOR_VALIDATOR.validateLocation(orderLocation, systemStore);
        Order newOrder = new Order(orderDate, orderLocation, parentId);
        addItemsToOrder(systemStore, newOrder, pricedItemToAmountMap);
        completeTheOrder(systemStore, newOrder);

        return newOrder;
    }

    public DynamicOrder createDynamicOrder (PlaceDynamicOrderRequest request,
                                            Location orderLocation,
                                            List<SystemItem> systemItemsIncludedInOrder,
                                            Set<SystemStore> storesIncludedInOrder) {
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
        return dynamicOrder;
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

            return createOrder(systemStore, orderDate, orderLocation, pricedItems, parentId);
        };
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

    private Function<SystemItem, PricedItem> createPricedItem (SystemStore systemStore) {
        return systemItem -> {
            int itemPriceInStore = systemStore.getItemIdToStoreItem().get(systemItem.getId()).getPrice();
            return new PricedItem(systemItem.getItem(), itemPriceInStore);
        };
    }

    private void addItemsToOrder (SystemStore systemStore, Order newOrder, Map<PricedItem, Double> pricedItemToAmountMap) {
        for (Map.Entry<PricedItem, Double> entry : pricedItemToAmountMap.entrySet()) {
            addItem(systemStore, newOrder, entry.getKey(), entry.getValue());
        }
    }

    private void addItem (SystemStore systemStore, Order newOrder, PricedItem pricedItem, Double amount) {
        ORDERS_CREATOR_VALIDATOR.validateAmount(pricedItem, amount);
        ORDERS_CREATOR_VALIDATOR.validateItemExistsInStore(pricedItem.getItem(), systemStore);

        double value = amount;
        if (newOrder.getPricedItems().containsKey(pricedItem)) {
            value += newOrder.getPricedItems().get(pricedItem);
        }

        newOrder.getPricedItems().put(pricedItem, value);
    }

    private void completeTheOrder (SystemStore systemStore, Order newOrder) {
        setItemTypes(newOrder);
        setItemsAmount(newOrder);
        setItemsPrice(newOrder);
        setDeliveryPrice(systemStore, newOrder);
        setTotalPrice(newOrder);
    }

    private void setItemTypes (Order newOrder) {
        newOrder.setNumOfItemTypes(newOrder.getPricedItems().size());
    }

    private void setItemsAmount (Order newOrder) {
        int amountOfItems = 0;
        for (PricedItem pricedItem : newOrder.getPricedItems().keySet()) {
            amountOfItems += calculateNumOfItemsToAdd(newOrder, pricedItem);
        }

        newOrder.setAmountOfItems(amountOfItems);
    }

    private int calculateNumOfItemsToAdd (Order newOrder, PricedItem pricedItem) {
        int numOfItemsToAdd;
        if (pricedItem.getPurchaseCategory().equals(Item.PurchaseCategory.WEIGHT)) {
            numOfItemsToAdd = 1;
        }
        else {
            numOfItemsToAdd = newOrder.getPricedItems().get(pricedItem).intValue();
        }
        return numOfItemsToAdd;
    }

    private void setItemsPrice (Order newOrder) {
        double allItemPrices = 0;
        for (PricedItem pricedItem : newOrder.getPricedItems().keySet()) {
            allItemPrices += calculateTotalPriceForItem(newOrder, pricedItem);
        }

        BigDecimal bd = new BigDecimal(allItemPrices).setScale(2, RoundingMode.HALF_UP);
        newOrder.setItemsPrice(bd.doubleValue());
    }

    private double calculateTotalPriceForItem (Order newOrder, PricedItem pricedItem) {
        return pricedItem.getPrice() * newOrder.getPricedItems().get(pricedItem);
    }

    private void setDeliveryPrice (SystemStore systemStore, Order newOrder) {
        Location orderLocation = newOrder.getOrderLocation();
        Location storeLocation = systemStore.getLocation();
        newOrder.setDistanceFromCustomerLocation(GeneratedDataMapper.round(calculateDeliveryDistance(orderLocation.getX()
                    - storeLocation.getX(), orderLocation.getY() - storeLocation.getY()), 2));
        double deliveryPrice = newOrder.getDistanceFromCustomerLocation() * systemStore.getDeliveryPpk();
        deliveryPrice = GeneratedDataMapper.round(deliveryPrice, 2);
        newOrder.setDeliveryPrice(deliveryPrice);
    }

    private double calculateDeliveryDistance (int x, int y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    private void setTotalPrice (Order newOrder) {
        newOrder.setTotalPrice(GeneratedDataMapper.round(newOrder.getItemsPrice() + newOrder.getDeliveryPrice(), 2));
    }
}
