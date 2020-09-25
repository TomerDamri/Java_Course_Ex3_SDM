package course.java.sdm.engine.utils.ordersCreator;

import course.java.sdm.engine.exceptions.ItemNotExist;
import course.java.sdm.engine.mapper.GeneratedDataMapper;
import course.java.sdm.engine.model.*;
import model.request.ChosenItemDiscount;
import model.request.ChosenStoreDiscounts;
import model.request.PlaceDynamicOrderRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrdersCreator {

    private Map<UUID, TempOrder> tempOrders = new TreeMap<>();

    private static OrdersCreator singletonOrderExecutor = null;
    private final static OrdersCreatorValidator ORDERS_CREATOR_VALIDATOR = new OrdersCreatorValidator();

    private OrdersCreator() {
    }

    public static OrdersCreator getOrdersExecutor() {
        if (singletonOrderExecutor == null) {
            singletonOrderExecutor = new OrdersCreator();
        }

        return singletonOrderExecutor;
    }

    public Order createOrderV2(SystemStore systemStore,
                               LocalDate orderDate,
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
            tempOrders.put(tempOrder.getOrderId(), tempOrder);
        }

        return newOrder;
    }

    public TempOrder getTempOrder(UUID orderId) {
        ORDERS_CREATOR_VALIDATOR.validateTempStaticOrderExist(orderId, tempOrders);
        return tempOrders.get(orderId);
    }

    public void deleteTempOrder(UUID orderId) {
        ORDERS_CREATOR_VALIDATOR.validateTempStaticOrderExist(orderId, tempOrders);
        tempOrders.remove(orderId);
    }

    /*
     * 1) chosenDiscountsForChosenStore = user chosen discounts (== Map<itemId, Map<Discount's name,
     * amount>>) 2) storeValidDiscounts = store discounts that valid for current order (
     */
    public void addDiscountsPerStore(SystemStore systemStore,
                                     Order order,
                                     ChosenStoreDiscounts chosenStoreDiscounts,
                                     ValidStoreDiscounts validStoreDiscounts) {
        Map<Integer, Double> itemIdToAmount = order.getPricedItems()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getId(), Map.Entry::getValue));
        Map<Integer, List<ChosenItemDiscount>> itemIdToChosenItemDiscounts = chosenStoreDiscounts.getItemIdToChosenDiscounts();

        for (Map.Entry<Integer, List<ChosenItemDiscount>> entry : itemIdToChosenItemDiscounts.entrySet()) {
            Integer currItemId = entry.getKey();
            List<ChosenItemDiscount> chosenItemDiscounts = entry.getValue();
            Map<String, Discount> discountsNameToValidDiscount = validStoreDiscounts.getItemIdToValidStoreDiscounts()
                    .get(currItemId)
                    .stream()
                    .collect(Collectors.toMap(Discount::getName,
                            discount -> discount));
            Double itemAmountInOrder = itemIdToAmount.get(currItemId);

            addDiscountsPerItem(systemStore, order, chosenItemDiscounts, discountsNameToValidDiscount, itemAmountInOrder);
        }
    }

    private void addDiscountsPerItem(SystemStore systemStore,
                                     Order order,
                                     List<ChosenItemDiscount> chosenDiscountNameToAmount,
                                     Map<String, Discount> discountsNameToValidDiscount,
                                     Double itemAmountInOrder) {
        for (ChosenItemDiscount chosenItemDiscount : chosenDiscountNameToAmount) {
            String discountName = chosenItemDiscount.getDiscountId();
            Integer numOfRealizations = chosenItemDiscount.getNumOfRealizations();
            Optional<Integer> chosenOfferId = chosenItemDiscount.getOrOfferId();
            Discount chosenDiscount = getChosenDiscount(order, discountsNameToValidDiscount, discountName);
            double itemQuantityInChosenDiscount = chosenDiscount.getIfYouBuy().getQuantity();

            ORDERS_CREATOR_VALIDATOR.validateNumOfRealizationForChosenDiscount(itemAmountInOrder,
                    itemQuantityInChosenDiscount,
                    discountName,
                    numOfRealizations);

            List<Offer> offersToAdd = getOffersToAdd(chosenDiscount, chosenOfferId);
            offersToAdd.forEach(offer -> addOffersToOrder(offer, systemStore, order, discountName, numOfRealizations));
        }
    }

    private Discount getChosenDiscount(Order order, Map<String, Discount> discountsNameToValidDiscount, String discountName) {
        ORDERS_CREATOR_VALIDATOR.validateExistenceOfChosenDiscount(discountsNameToValidDiscount, order.getId(), discountName);
        return discountsNameToValidDiscount.get(discountName);
    }

    private List<Offer> getOffersToAdd(Discount chosenDiscount, Optional<Integer> chosenOfferIdOptional) {
        ThenYouGet.DiscountType operator = chosenDiscount.getThenYouGet().getOperator();
        Map<Integer, Offer> discountOffers = chosenDiscount.getThenYouGet().getOffers();
        String discountName = chosenDiscount.getName();
        List<Offer> offersToAdd = new LinkedList<>();

        switch (operator) {
            case OR:
                ORDERS_CREATOR_VALIDATOR.validateOffersForOneOfOperator(discountOffers, chosenOfferIdOptional, discountName);
                Offer chosenOffer = getChosenOffer(chosenOfferIdOptional, discountOffers, discountName);
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

        return offersToAdd;
    }

    private Offer getChosenOffer(Optional<Integer> chosenOfferIdOptional, Map<Integer, Offer> discountOffers, String discountName) {
        if (!chosenOfferIdOptional.isPresent()) {
            throw new RuntimeException(String.format("You need to send chosen offer id for discount '%s'", discountName));
        }

        return discountOffers.get(chosenOfferIdOptional.get());
    }

    private void addOffersToOrder(Offer offer, SystemStore systemStore, Order order, String discountName, Integer numOfRealizations) {
        int discountItemId = offer.getItemId();
        Map<Integer, StoreItem> itemIdToStoreItem = systemStore.getItemIdToStoreItem();

        // validate discount item is supplied in chosen store
        if (!itemIdToStoreItem.containsKey(discountItemId)) {
            throw new ItemNotExist(discountName, discountItemId);
        }

        Item item = itemIdToStoreItem.get(discountItemId).getPricedItem().getItem();
        addOfferItemToOrder(systemStore, order, item, offer, numOfRealizations);
    }

    private void addOfferItemToOrder(SystemStore systemStore, Order newOrder, Item item, Offer offer, int numOfOfferRealizations) {
        ORDERS_CREATOR_VALIDATOR.validateAmount(item, offer.getQuantity());
        ORDERS_CREATOR_VALIDATOR.validateItemExistsInStore(item, systemStore);

        Map<Offer, Integer> orderOffers = newOrder.getSelectedOfferToNumOfRealization();

        if (orderOffers.containsKey(offer)) {
            Integer prevNumOfRealization = orderOffers.get(offer);
            numOfOfferRealizations += prevNumOfRealization;
        }

        orderOffers.put(offer, numOfOfferRealizations);
    }

    public TempOrder createDynamicOrderV2(PlaceDynamicOrderRequest request,
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
        tempOrders.put(tempOrder.getOrderId(), tempOrder);

        return tempOrder;
    }

    private Function<SystemStore, Order> createSubOrderV2(Map<Integer, Double> orderItemToAmount,
                                                          LocalDate orderDate,
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

    private Map<PricedItem, Double> getPricedItemFromDynamicOrderRequest(final Map<Integer, Double> orderItemToAmount,
                                                                         List<SystemItem> systemItemsIncludedInOrder,
                                                                         SystemStore systemStore) {
        return systemItemsIncludedInOrder.stream()
                .filter(systemItem -> systemItem.getStoreSellsInCheapestPrice().equals(systemStore.getId()))
                .map(createPricedItem(systemStore))
                .collect(Collectors.toMap(pricedItem -> pricedItem,
                        pricedItem -> orderItemToAmount.get(pricedItem.getId())));
    }

    private Function<SystemItem, PricedItem> createPricedItem(SystemStore systemStore) {
        return systemItem -> {
            int itemPriceInStore = systemStore.getItemIdToStoreItem().get(systemItem.getId()).getPrice();
            return new PricedItem(systemItem.getItem(), itemPriceInStore);
        };
    }

    private void addItemsToOrder(SystemStore systemStore, Order newOrder, Map<PricedItem, Double> pricedItemToAmountMap) {
        for (Map.Entry<PricedItem, Double> entry : pricedItemToAmountMap.entrySet()) {
            addItem(systemStore, newOrder, entry.getKey(), entry.getValue());
        }
    }

    private void addItem(SystemStore systemStore, Order newOrder, PricedItem pricedItem, Double amount) {
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

    private void setItemTypes(Order newOrder) {
        newOrder.setNumOfItemTypes(newOrder.getPricedItems().size());
    }

    private void setItemsAmount(Order newOrder) {
        int amountOfItems = 0;
        for (PricedItem pricedItem : newOrder.getPricedItems().keySet()) {
            amountOfItems += calculateNumOfOrderItemsToAdd(newOrder, pricedItem);
        }

        newOrder.setAmountOfItems(amountOfItems);
    }

    private int calculateNumOfOrderItemsToAdd(Order newOrder, PricedItem pricedItem) {
        int numOfItemsToAdd;
        if (pricedItem.getPurchaseCategory().equals(Item.PurchaseCategory.WEIGHT)) {
            numOfItemsToAdd = 1;
        } else {
            numOfItemsToAdd = newOrder.getPricedItems().get(pricedItem).intValue();
        }
        return numOfItemsToAdd;
    }

    private double calculateTotalPriceForOrderItem(Order newOrder, PricedItem pricedItem) {
        return pricedItem.getPrice() * newOrder.getPricedItems().get(pricedItem);
    }

    private void setDeliveryPrice(SystemStore systemStore, Order newOrder) {
        Location orderLocation = newOrder.getOrderLocation();
        Location storeLocation = systemStore.getLocation();
        newOrder.setDistanceFromCustomerLocation(GeneratedDataMapper.round(calculateDeliveryDistance(orderLocation.getX()
                - storeLocation.getX(), orderLocation.getY() - storeLocation.getY()), 2));
        double deliveryPrice = newOrder.getDistanceFromCustomerLocation() * systemStore.getDeliveryPpk();
        deliveryPrice = GeneratedDataMapper.round(deliveryPrice, 2);
        newOrder.setDeliveryPrice(deliveryPrice);
    }

    private double calculateDeliveryDistance(int x, int y) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    private void setTotalPrice(Order newOrder) {
        newOrder.setTotalPrice(GeneratedDataMapper.round(newOrder.getItemsPrice() + newOrder.getDeliveryPrice(), 2));
    }

    public void completeTheOrderV2(SystemStore systemStore, Order newOrder) {
        setItemTypesV2(newOrder);
        setItemsAmountV2(systemStore, newOrder);
        setItemsPriceV2(newOrder);
        setDeliveryPrice(systemStore, newOrder);
        setTotalPrice(newOrder);
    }

    private void setItemTypesV2(Order newOrder) {
        List<Integer> orderItemIds = newOrder.getPricedItems().keySet().stream().map(PricedItem::getId).collect(Collectors.toList());
        List<Integer> discountItemIds = newOrder.getSelectedOfferToNumOfRealization()
                .keySet()
                .stream()
                .map(Offer::getItemId)
                .collect(Collectors.toList());

        Set<Integer> orderItemTypes = new HashSet<>(orderItemIds);
        orderItemTypes.addAll(discountItemIds);

        newOrder.setNumOfItemTypes(orderItemTypes.size());
    }

    private void setItemsAmountV2(SystemStore systemStore, Order newOrder) {
        int amountOfItems = 0;
        for (PricedItem pricedItem : newOrder.getPricedItems().keySet()) {
            amountOfItems += calculateNumOfOrderItemsToAdd(newOrder, pricedItem);
        }

        for (Map.Entry<Offer, Integer> entry : newOrder.getSelectedOfferToNumOfRealization().entrySet()) {
            Offer currOffer = entry.getKey();
            Integer numOfRealizations = entry.getValue();
            StoreItem storeItem = systemStore.getItemIdToStoreItem().get(currOffer.getItemId());

            amountOfItems += calculateNumOfDiscountsItemsToAdd(currOffer, numOfRealizations, storeItem);
        }

        newOrder.setAmountOfItems(amountOfItems);
    }

    private int calculateNumOfDiscountsItemsToAdd(Offer offer, Integer numOfRealizations, StoreItem storeItem) {
        int numOfItemsToAdd;

        if (storeItem.getPurchaseCategory().equals(Item.PurchaseCategory.WEIGHT)) {
            numOfItemsToAdd = 1;
        } else {
            numOfItemsToAdd = offer.getQuantity().intValue();
        }

        return numOfItemsToAdd * numOfRealizations;
    }

    private void setItemsPriceV2(Order newOrder) {
        double allItemPrices = 0;
        for (PricedItem pricedItem : newOrder.getPricedItems().keySet()) {
            allItemPrices += calculateTotalPriceForOrderItem(newOrder, pricedItem);
        }

        for (Map.Entry<Offer, Integer> entry : newOrder.getSelectedOfferToNumOfRealization().entrySet()) {
            Offer currOffer = entry.getKey();
            Integer numOfRealizations = entry.getValue();

            allItemPrices += calculateTotalPriceForDiscountItem(currOffer, numOfRealizations);
        }

        BigDecimal bd = new BigDecimal(allItemPrices).setScale(2, RoundingMode.HALF_UP);
        newOrder.setItemsPrice(bd.doubleValue());
    }

    private double calculateTotalPriceForDiscountItem(Offer offer, Integer numOfRealizations) {
        return offer.getForAdditional() * offer.getQuantity() * numOfRealizations;
    }
}
