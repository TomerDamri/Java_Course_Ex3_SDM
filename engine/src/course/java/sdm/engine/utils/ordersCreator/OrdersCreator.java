package course.java.sdm.engine.utils.ordersCreator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import course.java.sdm.engine.exceptions.ItemNotExistInStores;
import course.java.sdm.engine.mapper.GeneratedDataMapper;
import course.java.sdm.engine.model.*;
import model.request.ItemChosenDiscount;
import model.request.PlaceDynamicOrderRequest;
import model.request.StoreChosenDiscounts;
import model.request.StoreValidDiscounts;

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

        if (parentId == null) {
            TempOrder tempOrder = new TempOrder(newOrder.getId(),
                                                Collections.singletonMap(systemStore.getStore().getStoreDetails(), newOrder),
                                                customerId);
            tempStaticOrders.put(tempOrder.getOrderId(), tempOrder);
        }

        return newOrder;
    }

    public TempOrder getTempOrder (UUID orderId) {
        ORDERS_CREATOR_VALIDATOR.validateTempStaticOrderExist(orderId, tempStaticOrders);
        return tempStaticOrders.get(orderId);
    }

    public void deleteTempOrder (UUID orderId) {
        ORDERS_CREATOR_VALIDATOR.validateTempStaticOrderExist(orderId, tempStaticOrders);
        tempStaticOrders.remove(orderId);
    }

    /*
     * 1) chosenDiscountsForChosenStore = user chosen discounts (== Map<itemId, Map<Discount's name,
     * amount>>) 2) storeValidDiscounts = store discounts that valid for current order (
     */
    public void addDiscountsPerStoreToStaticOrder (SystemStore systemStore,
                                                   Order order,
                                                   StoreChosenDiscounts storeChosenDiscounts,
                                                   StoreValidDiscounts storeValidDiscounts) {
        Map<Integer, Double> itemIdToAmount = order.getPricedItems()
                                                   .entrySet()
                                                   .stream()
                                                   .collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue));
        Map<Integer, List<ItemChosenDiscount>> itemIdToChosenDiscountsDetails = storeChosenDiscounts.getItemIdToChosenDiscounts();

        for (Map.Entry<Integer, List<ItemChosenDiscount>> entry : itemIdToChosenDiscountsDetails.entrySet()) {
            Integer currItemId = entry.getKey();
            List<ItemChosenDiscount> chosenDiscountNameToAmount = entry.getValue();
            Map<String, Discount> discountsNameToValidDiscount = storeValidDiscounts.getItemIdToValidStoreDiscounts()
                                                                                    .get(currItemId)
                                                                                    .stream()
                                                                                    .collect(Collectors.toMap(Discount::getName,
                                                                                                              discount -> discount));
            Double itemAmountInOrder = itemIdToAmount.get(currItemId);

            addDiscountsPerItemToStaticOrder(systemStore,
                                             order,
                                             chosenDiscountNameToAmount,
                                             discountsNameToValidDiscount,
                                             itemAmountInOrder);
        }
    }

    private void addDiscountsPerItemToStaticOrder (SystemStore systemStore,
                                                   Order order,
                                                   List<ItemChosenDiscount> chosenDiscountNameToAmount,
                                                   Map<String, Discount> discountsNameToValidDiscount,
                                                   Double itemAmountInOrder) {
        for (ItemChosenDiscount itemChosenDiscount : chosenDiscountNameToAmount) {
            String discountName = itemChosenDiscount.getDiscountId();
            Integer chosenDiscountRealizationCount = itemChosenDiscount.getNumOfRealizations();
            Optional<Integer> chosenOfferId = itemChosenDiscount.getOrOfferId();

            ORDERS_CREATOR_VALIDATOR.validateExistenceOfChosenDiscount(discountsNameToValidDiscount, order.getId(), discountName);

            Discount chosenDiscount = discountsNameToValidDiscount.get(discountName);
            double itemQuantityInChosenDiscount = chosenDiscount.getIfYouBuy().getQuantity();

            for (int i = 0; chosenDiscountRealizationCount > i; i++) {
                ORDERS_CREATOR_VALIDATOR.validateNumOfRealizationForChosenDiscount(itemAmountInOrder,
                                                                                   itemQuantityInChosenDiscount,
                                                                                   discountName,
                                                                                   i,
                                                                                   chosenDiscountRealizationCount);

                // add to order
                handleDiscountOperator(chosenDiscount, systemStore, order, chosenOfferId);
                itemAmountInOrder = itemAmountInOrder - itemQuantityInChosenDiscount;
            }
        }
    }

    private void handleDiscountOperator (Discount chosenDiscount,
                                         SystemStore systemStore,
                                         Order order,
                                         Optional<Integer> chosenOfferIdOptional) {
        ThenYouGet.DiscountType operator = chosenDiscount.getThenYouGet().getOperator();
        Map<Integer, Offer> discountOffers = chosenDiscount.getThenYouGet().getOffers();
        String discountName = chosenDiscount.getName();
        List<Offer> offersToAdd = new LinkedList<>();

        switch (operator) {
        case OR:
            ORDERS_CREATOR_VALIDATOR.validateOffersForOneOfOperator(discountOffers, chosenOfferIdOptional, discountName);
            Offer chosenOffer = discountOffers.get(chosenOfferIdOptional.get());
            offersToAdd.add(chosenOffer);
            break;
        case AND:
            ORDERS_CREATOR_VALIDATOR.validateOffersForAllOrNothingOperator(discountOffers.size(), discountName);
            Collection<Offer> allDiscountOffers = discountOffers.values();
            offersToAdd.addAll(allDiscountOffers);
            break;
        default: // IRRELEVANT
            ORDERS_CREATOR_VALIDATOR.validateOffersForIrrelevantOperator(discountOffers.size(), discountName);
            Offer singleOffer = discountOffers.entrySet().iterator().next().getValue();
            offersToAdd.add(singleOffer);
            break;
        }

        offersToAdd.forEach(offer -> addOffersToStaticOrder(offer, systemStore, order, discountName));
    }

    private void addOffersToStaticOrder (Offer offer, SystemStore systemStore, Order order, String discountName) {
        int discountItemId = offer.getItemId();
        Map<Integer, StoreItem> itemIdToStoreItem = systemStore.getItemIdToStoreItem();

        // validate discount item is supplied in chosen store
        if (!itemIdToStoreItem.containsKey(discountItemId)) {
            throw new ItemNotExistInStores(discountName, discountItemId);
        }

        Item item = itemIdToStoreItem.get(discountItemId).getPricedItem().getItem();
        addOfferItemToOrder(systemStore, order, item, offer);
    }

    private void addOfferItemToOrder (SystemStore systemStore, Order newOrder, Item item, Offer offer) {
        ORDERS_CREATOR_VALIDATOR.validateAmount(item, offer.getQuantity());
        ORDERS_CREATOR_VALIDATOR.validateItemExistsInStore(item, systemStore);

        Map<Offer, Integer> orderOffers = newOrder.getOrderOffers();
        Integer numOfOfferRealizations = 1;
        if (orderOffers.containsKey(offer)) {
            numOfOfferRealizations += orderOffers.get(offer);
        }

        orderOffers.put(offer, numOfOfferRealizations);
    }

//    public Order createOrder (SystemStore systemStore,
//                              LocalDateTime orderDate,
//                              Location orderLocation,
//                              Map<PricedItem, Double> pricedItemToAmountMap,
//                              UUID parentId) {
//        ORDERS_CREATOR_VALIDATOR.validateLocation(orderLocation, systemStore);
//        Order newOrder = new Order(orderDate, orderLocation, parentId);
//        addItemsToOrder(systemStore, newOrder, pricedItemToAmountMap);
//        completeTheOrder(systemStore, newOrder);
//
//        return newOrder;
//    }

//    public DynamicOrder createDynamicOrder (PlaceDynamicOrderRequest request,
//                                            Location orderLocation,
//                                            List<SystemItem> systemItemsIncludedInOrder,
//                                            Set<SystemStore> storesIncludedInOrder) {
//        UUID dynamicOrderId = UUID.randomUUID();
//        Map<StoreDetails, Order> staticOrders = storesIncludedInOrder.stream()
//                                                                     .collect(Collectors.toMap(systemStore -> systemStore.getStore()
//                                                                                                                         .getStoreDetails(),
//                                                                                               createSubOrder(request.getOrderItemToAmount(),
//                                                                                                              request.getOrderDate(),
//                                                                                                              orderLocation,
//                                                                                                              systemItemsIncludedInOrder,
//                                                                                                              dynamicOrderId)));
//
//        DynamicOrder dynamicOrder = new DynamicOrder(dynamicOrderId, staticOrders);
//        return dynamicOrder;
//    }

    public TempOrder createDynamicOrderV2 (PlaceDynamicOrderRequest request,
                                           Location orderLocation,
                                           List<SystemItem> systemItemsIncludedInOrder,
                                           Set<SystemStore> storesIncludedInOrder,
                                           Integer customerID) {
        UUID dynamicOrderId = UUID.randomUUID();
        Map<StoreDetails, Order> staticOrders = storesIncludedInOrder.stream()
                                                                     .collect(Collectors.toMap(systemStore -> systemStore.getStore()
                                                                                                                         .getStoreDetails(),
                                                                                               createSubOrderV2(request.getOrderItemToAmount(),
                                                                                                                request.getOrderDate(),
                                                                                                                orderLocation,
                                                                                                                systemItemsIncludedInOrder,
                                                                                                                dynamicOrderId,
                                                                                                                customerID)));

        TempOrder tempOrder = new TempOrder(dynamicOrderId, staticOrders, customerID);
        tempStaticOrders.put(tempOrder.getOrderId(), tempOrder);

        return tempOrder;
    }

//    private Function<SystemStore, Order> createSubOrder (Map<Integer, Double> orderItemToAmount,
//                                                         LocalDateTime orderDate,
//                                                         Location orderLocation,
//                                                         List<SystemItem> systemItemsIncludedInOrder,
//                                                         UUID parentId) {
//        return systemStore -> {
//            Map<PricedItem, Double> pricedItems = getPricedItemFromDynamicOrderRequest(orderItemToAmount,
//                                                                                       systemItemsIncludedInOrder,
//                                                                                       systemStore);
//
//            return createOrder(systemStore, orderDate, orderLocation, pricedItems, parentId);
//        };
//    }

    private Function<SystemStore, Order> createSubOrderV2 (Map<Integer, Double> orderItemToAmount,
                                                           LocalDateTime orderDate,
                                                           Location orderLocation,
                                                           List<SystemItem> systemItemsIncludedInOrder,
                                                           UUID parentId,
                                                           Integer customerId) {
        return systemStore -> {
            Map<PricedItem, Double> pricedItems = getPricedItemFromDynamicOrderRequest(orderItemToAmount,
                                                                                       systemItemsIncludedInOrder,
                                                                                       systemStore);

            return createOrderV2(systemStore, orderDate, orderLocation, pricedItems, parentId, customerId);
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
        Item item = pricedItem.getItem();
        ORDERS_CREATOR_VALIDATOR.validateAmount(item, amount);
        ORDERS_CREATOR_VALIDATOR.validateItemExistsInStore(item, systemStore);

        double value = amount;
        Map<PricedItem, Double> pricedItems = newOrder.getPricedItems();

        if (pricedItems.containsKey(pricedItem)) {
            value += pricedItems.get(pricedItem);
        }

        pricedItems.put(pricedItem, value);
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
            amountOfItems += calculateNumOfOrderItemsToAdd(newOrder, pricedItem);
        }

        newOrder.setAmountOfItems(amountOfItems);
    }

    private int calculateNumOfOrderItemsToAdd (Order newOrder, PricedItem pricedItem) {
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
            allItemPrices += calculateTotalPriceForOrderItem(newOrder, pricedItem);
        }

        BigDecimal bd = new BigDecimal(allItemPrices).setScale(2, RoundingMode.HALF_UP);
        newOrder.setItemsPrice(bd.doubleValue());
    }

    private double calculateTotalPriceForOrderItem (Order newOrder, PricedItem pricedItem) {
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

    public void completeTheOrderV2 (SystemStore systemStore, Order newOrder) {
        setItemTypesV2(newOrder);
        setItemsAmountV2(systemStore, newOrder);
        setItemsPriceV2(newOrder);
        setDeliveryPrice(systemStore, newOrder);
        setTotalPrice(newOrder);
    }

    private void setItemTypesV2 (Order newOrder) {
        List<Integer> orderItemIds = newOrder.getPricedItems().keySet().stream().map(PricedItem::getId).collect(Collectors.toList());
        List<Integer> discountItemIds = newOrder.getOrderOffers().keySet().stream().map(Offer::getItemId).collect(Collectors.toList());

        Set<Integer> orderItemTypes = new HashSet<>(orderItemIds);
        orderItemTypes.addAll(discountItemIds);

        newOrder.setNumOfItemTypes(orderItemTypes.size());
    }

    private void setItemsAmountV2 (SystemStore systemStore, Order newOrder) {
        int amountOfItems = 0;
        for (PricedItem pricedItem : newOrder.getPricedItems().keySet()) {
            amountOfItems += calculateNumOfOrderItemsToAdd(newOrder, pricedItem);
        }

        for (Map.Entry<Offer, Integer> entry : newOrder.getOrderOffers().entrySet()) {
            Offer currOffer = entry.getKey();
            Integer numOfRealizations = entry.getValue();
            StoreItem storeItem = systemStore.getItemIdToStoreItem().get(currOffer.getItemId());

            amountOfItems += calculateNumOfDiscountsItemsToAdd(currOffer, numOfRealizations, storeItem);
        }

        newOrder.setAmountOfItems(amountOfItems);
    }

    private int calculateNumOfDiscountsItemsToAdd (Offer offer, Integer numOfRealizations, StoreItem storeItem) {
        int numOfItemsToAdd;

        if (storeItem.getPurchaseCategory().equals(Item.PurchaseCategory.WEIGHT)) {
            numOfItemsToAdd = 1;
        }
        else {
            numOfItemsToAdd = offer.getQuantity().intValue();
        }

        return numOfItemsToAdd * numOfRealizations;
    }

    private void setItemsPriceV2 (Order newOrder) {
        double allItemPrices = 0;
        for (PricedItem pricedItem : newOrder.getPricedItems().keySet()) {
            allItemPrices += calculateTotalPriceForOrderItem(newOrder, pricedItem);
        }

        for (Map.Entry<Offer, Integer> entry : newOrder.getOrderOffers().entrySet()) {
            Offer currOffer = entry.getKey();
            Integer numOfRealizations = entry.getValue();

            allItemPrices += calculateTotalPriceForDiscountItem(currOffer, numOfRealizations);
        }

        BigDecimal bd = new BigDecimal(allItemPrices).setScale(2, RoundingMode.HALF_UP);
        newOrder.setItemsPrice(bd.doubleValue());
    }

    private double calculateTotalPriceForDiscountItem (Offer offer, Integer numOfRealizations) {
        return offer.getForAdditional() * numOfRealizations;
    }
}
