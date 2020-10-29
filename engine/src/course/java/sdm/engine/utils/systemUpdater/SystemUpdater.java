package course.java.sdm.engine.utils.systemUpdater;

import java.util.*;

import course.java.sdm.engine.mapper.GeneratedDataMapper;
import course.java.sdm.engine.model.*;
import model.request.StoreRank;

public class SystemUpdater {

    private final static SystemUpdaterValidator SYSTEM_UPDATER_VALIDATOR = new SystemUpdaterValidator();
    private static SystemUpdater singletonSystemUpdater = null;

    private SystemUpdater () {
    }

    public static SystemUpdater getSystemUpdater () {
        if (singletonSystemUpdater == null) {
            singletonSystemUpdater = new SystemUpdater();
        }

        return singletonSystemUpdater;
    }

    public void addItemToStore (Integer itemId,
                                Integer storeId,
                                Integer itemPrice,
                                Map<Integer, SystemStore> systemStores,
                                Map<Integer, SystemItem> systemItems) {

        validateExistenceInSystem(itemId, storeId, systemStores, systemItems);

        SystemStore systemStore = systemStores.get(storeId);
        SystemItem systemItem = systemItems.get(itemId);

        SYSTEM_UPDATER_VALIDATOR.validateItemNotExistInStore(itemId, systemStore, systemItem);

        addNewItemToStore(itemId, itemPrice, systemStore, systemItem);
        updateSystemItemAfterAddingToStore(itemId, storeId, itemPrice, systemStores, systemItem);
    }

    public void updateItemPrice (Integer itemId,
                                 Integer storeId,
                                 Integer newItemPrice,
                                 Map<Integer, SystemStore> systemStores,
                                 Map<Integer, SystemItem> systemItems) {
        validateExistenceInSystem(itemId, storeId, systemStores, systemItems);

        SystemStore systemStore = systemStores.get(storeId);
        SystemItem systemItem = systemItems.get(itemId);

        SYSTEM_UPDATER_VALIDATOR.validateItemExistInStore(itemId, systemStore, systemItem);
        // update store item price
        StoreItem storeItem = systemStore.getItemIdToStoreItem().get(itemId);
        int prevPrice = storeItem.getPrice();
        storeItem.setPrice(newItemPrice);
        // update systemItem avg price
        updateSystemItemAfterUpdatingStoreItemPrice(newItemPrice, systemStores, systemItem, prevPrice);
    }

    public DeleteItemResult deleteItemFromStore (Integer itemId,
                                                 Integer storeId,
                                                 Map<Integer, SystemStore> systemStores,
                                                 Map<Integer, SystemItem> systemItems) {
        validateExistenceInSystem(itemId, storeId, systemStores, systemItems);

        SystemStore systemStore = systemStores.get(storeId);
        SystemItem systemItem = systemItems.get(itemId);

        SYSTEM_UPDATER_VALIDATOR.validateItemExistInStore(itemId, systemStore, systemItem);
        SYSTEM_UPDATER_VALIDATOR.validateItemSellsInOtherStores(systemStore, systemItem);
        SYSTEM_UPDATER_VALIDATOR.validateStoreSellsOtherItems(systemStore, systemItem);

        // remove item from store
        StoreItem removedStoreItem = systemStore.getItemIdToStoreItem().remove(itemId);
        // remove related discounts
        List<Discount> removedDiscounts = deleteRelatedDiscounts(itemId, systemStore);
        // update system item
        updateSystemItemAfterDeletingItemFromStore(systemStores, systemItem, removedStoreItem);

        return new DeleteItemResult(removedStoreItem, removedDiscounts);
    }

    public void updateSystemAfterLoadingZoneFile (SDMDescriptor sdmDescriptor, Zone newZone, StoresOwner storesOwner) {
        addZoneLocationsToSystem(newZone, sdmDescriptor.getSystemLocations());
        addZoneToSystem(newZone, sdmDescriptor.getZones());
        addSystemStoresToStoresOwner(storesOwner, newZone.getZoneName(), newZone.getSystemStores());
    }

    private void addZoneLocationsToSystem (Zone newZone, Map<Location, SystemStore> systemLocations) {
        // adding all zone stores locations
        newZone.getSystemStores().values().forEach(systemStore -> systemLocations.put(systemStore.getLocation(), systemStore));
    }

    private void addZoneToSystem (Zone newZone, Map<String, Zone> systemZones) {
        // adding zone to system
        systemZones.put(newZone.getZoneName(), newZone);
    }

    private void addSystemStoresToStoresOwner (StoresOwner storesOwner, String newZoneName, Map<Integer, SystemStore> zoneStores) {
        // adding zone stores to stores owner
        storesOwner.getZoneToOwnedStores().put(newZoneName, zoneStores);
    }

    private void validateExistenceInSystem (Integer itemId,
                                            Integer storeId,
                                            Map<Integer, SystemStore> systemStores,
                                            Map<Integer, SystemItem> systemItems) {
        SYSTEM_UPDATER_VALIDATOR.validateItemExistInSystem(itemId, systemItems);
        SYSTEM_UPDATER_VALIDATOR.validateStoreExistInSystem(storeId, systemStores);
    }

    private void updateSystemItemAfterAddingToStore (Integer itemId,
                                                     Integer storeId,
                                                     Integer itemPrice,
                                                     Map<Integer, SystemStore> systemStores,
                                                     SystemItem systemItem) {
        updateStoreCountAfterAddingItemToStore(systemItem);
        updateAvgPriceAfterAddingItemToStore(itemPrice, systemItem);
        updateStoreSellsInCheapestPriceAfterAddingItemToStore(itemId, storeId, itemPrice, systemStores, systemItem);
    }

    private void updateStoreSellsInCheapestPriceAfterAddingItemToStore (Integer itemId,
                                                                        Integer storeId,
                                                                        Integer itemPrice,
                                                                        Map<Integer, SystemStore> systemStores,
                                                                        SystemItem systemItem) {
        SystemStore storeSellsInCheapestPrice = systemStores.get(systemItem.getStoreSellsInCheapestPrice());
        int cheapestPrice = storeSellsInCheapestPrice.getItemIdToStoreItem().get(itemId).getPrice();

        if (cheapestPrice > itemPrice) {
            systemItem.setStoreSellsInCheapestPrice(storeId);
        }
    }

    private void updateAvgPriceAfterAddingItemToStore (Integer itemPrice, SystemItem systemItem) {
        int storesCount = systemItem.getStoresCount();
        int prevStoreCount = storesCount - 1;
        double prevAvg = systemItem.getAvgPrice();
        double newAvgPrice = (prevAvg * prevStoreCount + itemPrice) / storesCount;

        systemItem.setAvgPrice(newAvgPrice);
    }

    private void updateStoreCountAfterAddingItemToStore (SystemItem systemItem) {
        int prevStoreCount = systemItem.getStoresCount();
        systemItem.setStoresCount(prevStoreCount + 1);
    }

    private void addNewItemToStore (Integer itemId, Integer itemPrice, SystemStore systemStore, SystemItem systemItem) {
        StoreItem newStoreItem = new StoreItem(systemItem.getItem(), itemPrice);
        systemStore.getItemIdToStoreItem().put(itemId, newStoreItem);
    }

    private void updateSystemItemAfterUpdatingStoreItemPrice (Integer newItemPrice,
                                                              Map<Integer, SystemStore> systemStores,
                                                              SystemItem systemItem,
                                                              int prevPrice) {
        updateAvgPriceAfterUpdatingStoreItemPrice(newItemPrice, systemItem, prevPrice);
        updateStoreSellsInCheapestPriceAfterUpdatingStoreItemPrice(systemStores, systemItem);
    }

    private void updateStoreSellsInCheapestPriceAfterUpdatingStoreItemPrice (Map<Integer, SystemStore> systemStores,
                                                                             SystemItem systemItem) {
        Integer itemId = systemItem.getId();
        Comparator<SystemStore> storeSellsInCheapestPriceComparator = (store1,
                                                                       store2) -> Integer.compare(store1.getItemIdToStoreItem()
                                                                                                        .get(itemId)
                                                                                                        .getPrice(),
                                                                                                  store2.getItemIdToStoreItem()
                                                                                                        .get(itemId)
                                                                                                        .getPrice());

        int newStoreSellsInCheapestPriceId = systemStores.values()
                                                         .stream()
                                                         .filter(store -> store.getItemIdToStoreItem().containsKey(itemId))
                                                         .min(storeSellsInCheapestPriceComparator)
                                                         .get()
                                                         .getId();

        systemItem.setStoreSellsInCheapestPrice(newStoreSellsInCheapestPriceId);
    }

    private void updateAvgPriceAfterUpdatingStoreItemPrice (Integer newItemPrice, SystemItem systemItem, int prevPrice) {
        int storesCount = systemItem.getStoresCount();
        double prevAvg = systemItem.getAvgPrice();
        double newAvgPrice = (prevAvg * storesCount - prevPrice + newItemPrice) / storesCount;

        systemItem.setAvgPrice(newAvgPrice);
    }

    private void updateSystemItemAfterDeletingItemFromStore (Map<Integer, SystemStore> systemStores,
                                                             SystemItem systemItem,
                                                             StoreItem removedStoreItem) {
        updateStoreCountAfterDeletion(systemItem);
        // update avg price
        updateAvgPriceAfterDeletion(systemItem, removedStoreItem);
        // update store sells in cheapest price
        updateStoreSellsInCheapestPriceAfterUpdatingStoreItemPrice(systemStores, systemItem);
    }

    private int updateStoreCountAfterDeletion (SystemItem systemItem) {
        int prevStoresCount = systemItem.getStoresCount();
        systemItem.setStoresCount(prevStoresCount - 1);
        return prevStoresCount;
    }

    private void updateAvgPriceAfterDeletion (SystemItem systemItem, StoreItem removedStoreItem) {
        double prevAvgPrice = systemItem.getAvgPrice();
        int storesCount = systemItem.getStoresCount();
        int prevStoresCount = storesCount - 1;
        double newAvgPrice = (prevAvgPrice * prevStoresCount - removedStoreItem.getPrice()) / storesCount;

        systemItem.setAvgPrice(newAvgPrice);
    }

    private List<Discount> deleteRelatedDiscounts (Integer itemId, SystemStore systemStore) {
        List<Discount> removedDiscounts = null;
        Map<Integer, List<Discount>> storeDiscounts = systemStore.getStore().getStoreDiscounts();
        if (storeDiscounts != null && storeDiscounts.containsKey(itemId)) {
            removedDiscounts = storeDiscounts.remove(itemId);
        }

        Map<Integer, Set<Discount>> discountsToRemove = new HashMap<>();

        for (Map.Entry<Integer, List<Discount>> entry : storeDiscounts.entrySet()) {
            Integer key = entry.getKey();
            List<Discount> discountList = entry.getValue();
            Set<Discount> discountsToRemoveForItem = new HashSet<>();

            for (Discount discount : discountList) {
                for (Offer offer : discount.getThenYouGet().getOffers().values()) {
                    if (offer.getItemId() == itemId) {
                        discountsToRemoveForItem.add(discount);
                    }
                }
            }

            if (!discountsToRemoveForItem.isEmpty()) {
                discountsToRemove.put(key, discountsToRemoveForItem);
            }
        }

        for (Map.Entry<Integer, Set<Discount>> entry : discountsToRemove.entrySet()) {
            Integer ifYouButItemId = entry.getKey();
            Set<Discount> storeDiscountPerItemToRemove = entry.getValue();
            List<Discount> storeDiscountPerItem = storeDiscounts.get(ifYouButItemId);

            for (Discount discount : storeDiscountPerItemToRemove) {
                if (storeDiscountPerItem.contains(discount)) {
                    if (removedDiscounts == null) {
                        removedDiscounts = new LinkedList<>();
                    }

                    removedDiscounts.add(discount);
                    storeDiscountPerItem.remove(discount);
                    if (storeDiscountPerItem.size() == 0) {
                        storeDiscounts.remove(ifYouButItemId);
                    }
                }
            }
        }

        return removedDiscounts;
    }

    public void updateSystemAfterLoadingOrdersHistoryFromFile (Map<UUID, List<SystemOrder>> ordersFromHistoryFile,
                                                               Map<UUID, SystemCustomer> systemCustomers,
                                                               Zone zone) {
        Map<UUID, List<SystemOrder>> systemOrdersBeforeUpdate = zone.getSystemOrders();

        updateSystemOrdersAccordingToHistoryFile(ordersFromHistoryFile, zone, systemOrdersBeforeUpdate, systemCustomers);
    }

    public void rankOrderStores (List<StoreRank> storeRanks,
                                 UUID orderId,
                                 List<SystemOrder> subOrders,
                                 SystemCustomer customer,
                                 Zone zone) {
        storeRanks.forEach(storeRank -> {
            SystemOrder relatedSubOrder = getSubOrderRelatedToSpecificStore(orderId, subOrders, storeRank);
            SystemStore relatedStore = getStoreByID(zone, storeRank.getStoreId());

            CustomerFeedback customerFeedback = new CustomerFeedback(relatedStore.getId(),
                                                                     customer.getName(),
                                                                     relatedSubOrder.getOrderDate(),
                                                                     storeRank.getRank(),
                                                                     storeRank.getTextualFeedback());
            relatedStore.getCustomersFeedback().add(customerFeedback);
        });
    }

    private SystemOrder getSubOrderRelatedToSpecificStore (UUID orderId, List<SystemOrder> subOrders, StoreRank storeRank) {
        Integer storeId = storeRank.getStoreId();
        Optional<SystemOrder> storeOrderOpt = subOrders.stream().filter(subOrder -> subOrder.getStoreId().equals(storeId)).findFirst();
        if (!storeOrderOpt.isPresent()) {
            throw new RuntimeException(String.format("There is no store with id: '%s' in order with id: '%s'", storeId, orderId));
        }

        return storeOrderOpt.get();
    }

    private SystemStore getStoreByID (Zone zone, Integer storeId) {
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();
        if (!systemStores.containsKey(storeId)) {
            throw new RuntimeException(String.format("There is no store with id: '%s' in '%s' zone", storeId, zone.getZoneName()));
        }

        return systemStores.get(storeId);
    }

    private void updateSystemOrdersAccordingToHistoryFile (Map<UUID, List<SystemOrder>> ordersFromHistoryFile,
                                                           Zone zone,
                                                           Map<UUID, List<SystemOrder>> systemOrdersBeforeUpdate,
                                                           Map<UUID, SystemCustomer> systemCustomers) {
        ordersFromHistoryFile.entrySet().stream().filter(entry -> !systemOrdersBeforeUpdate.containsKey(entry.getKey())).forEach(entry -> {
            UUID orderId = entry.getKey();
            List<SystemOrder> subOrdersToAdd = entry.getValue();

            subOrdersToAdd.forEach(order -> updateSystemAfterLoadingHistoryOrder(zone, order, systemCustomers));
            updateSystemCustomerAfterLoadingDynamicOrder(zone, systemCustomers, orderId, subOrdersToAdd);
        });
    }

    private void updateSystemCustomerAfterLoadingDynamicOrder (Zone zone,
                                                               Map<UUID, SystemCustomer> systemCustomers,
                                                               UUID orderId,
                                                               List<SystemOrder> subOrdersToAdd) {
        SystemOrder firstSubOrder = subOrdersToAdd.iterator().next();
        SystemCustomer systemCustomer = getCustomerById(firstSubOrder.getCustomerId(), systemCustomers);
        if (subOrdersToAdd.size() > 1 || !firstSubOrder.getId().equals(orderId)) {
            updateSystemCustomerAfterDynamicOrder(orderId, systemCustomer, zone.getZoneName());
        }
    }

    private void updateSystemAfterLoadingHistoryOrder (Zone zone, SystemOrder systemOrder, Map<UUID, SystemCustomer> systemCustomers) {
        SystemStore systemStore = getStoreById(zone, systemOrder.getStoreId());
        Order order = getOrder(systemStore, systemOrder);
    }

    private Order getOrder (SystemStore systemStore, SystemOrder systemOrder) {
        Order order = systemOrder.getOrder();
        SYSTEM_UPDATER_VALIDATOR.validateAllOrderItemsExistInStore(systemStore, order);

        return order;
    }

    private SystemCustomer getCustomerById (UUID customerId, Map<UUID, SystemCustomer> systemCustomers) {
        SYSTEM_UPDATER_VALIDATOR.validateCustomerExistInSystem(systemCustomers, customerId);

        return systemCustomers.get(customerId);
    }

    private SystemStore getStoreById (Zone zone, Integer storeId) {
        Map<Integer, SystemStore> systemStores = zone.getSystemStores();

        SYSTEM_UPDATER_VALIDATOR.validateStoreExistInSystem(storeId, systemStores);

        return systemStores.get(storeId);
    }

    private void addNewOrderToSystemOrders (SystemStore systemStore, Order newOrder, Zone zone, UUID customerId) {
        List<SystemOrder> orders;
        SystemOrder newSystemOrder = new SystemOrder(newOrder, zone.getZoneName(), systemStore.getName(), systemStore.getId(), customerId);
        UUID id = (newOrder.getParentId() != null) ? newOrder.getParentId() : newOrder.getId();
        Map<UUID, List<SystemOrder>> systemOrders = zone.getSystemOrders();

        if (systemOrders.containsKey(id)) {
            orders = new ArrayList<>(systemOrders.get(id));
            orders.add(newSystemOrder);
        }
        else {
            orders = Collections.singletonList(newSystemOrder);
        }

        systemOrders.put(id, orders);
    }

    private void updateSystemStore (SystemStore systemStore, Order newOrder) {
        updateDeliveriesPayment(systemStore, newOrder);
    }

    private void updateDeliveriesPayment (SystemStore systemStore, Order newOrder) {
        systemStore.setTotalDeliveriesPayment(GeneratedDataMapper.round(systemStore.getTotalDeliveriesPayment()
                    + newOrder.getDeliveryPrice(), 2));
    }

    public void updateSystemAfterStaticOrderV2 (SystemStore systemStore, Order newOrder, Zone zone, SystemCustomer systemCustomer) {
        updateSystemStore(systemStore, newOrder);
        // add order to store
        systemStore.getOrders().add(newOrder);
        // add order to order collection in descriptor
        addNewOrderToSystemOrders(systemStore, newOrder, zone, systemCustomer.getId());
        // update counter of all store items that was included in order
        updateStoreAfterOrderCompletionV2(systemStore, newOrder);
        // update counter of all system items that was included in order
        updateSystemItemsAfterOrderCompletionV2(zone.getSystemItems(), newOrder);
        // update total delivery price and total items price for system customer (update numOfOrder in case
        // the order is not part of dynamic order)
        updateSystemCustomerAfterOrderCompletion(newOrder, systemCustomer, zone.getZoneName());
    }

    private void updateSystemCustomerAfterOrderCompletion (Order newOrder, SystemCustomer systemCustomer, String zoneName) {
        double prevTotalDeliveryPrice = systemCustomer.getTotalDeliveryPrice();
        double prevTotalItemsPrice = systemCustomer.getTotalItemsPrice();

        systemCustomer.setTotalItemsPrice(prevTotalDeliveryPrice + newOrder.getDeliveryPrice());
        systemCustomer.setTotalDeliveryPrice(prevTotalItemsPrice + newOrder.getItemsPrice());

        updateSystemCustomerAfterStaticOrder(newOrder, systemCustomer, zoneName);
    }

    private void updateSystemCustomerAfterStaticOrder (Order newOrder, SystemCustomer systemCustomer, String zoneName) {
        // if we are in "place static order" flow
        if (newOrder.getParentId() == null) {
            updateSystemCustomerAfterDynamicOrder(newOrder.getId(), systemCustomer, zoneName);
        }
    }

    // will update numOfOrders property for chosen system customer in case the temp order is dynamic
    // order
    public void updateSystemCustomerAfterDynamicOrder (UUID orderId,
                                                       SystemCustomer systemCustomer,
                                                       Map<StoreDetails, Order> staticOrders,
                                                       String zoneName) {
        Order firstSubOrder = staticOrders.values().iterator().next();
        if (staticOrders.size() > 1 || !firstSubOrder.getId().equals(orderId)) {
            updateSystemCustomerAfterDynamicOrder(orderId, systemCustomer, zoneName);
        }
    }

    private void updateSystemCustomerAfterDynamicOrder (UUID orderId, SystemCustomer systemCustomer, String zoneName) {
        int prevNumOfOrders = systemCustomer.getNumOfOrders();
        systemCustomer.setNumOfOrders(prevNumOfOrders + 1);
        updateZoneNameToCustomersOrders(orderId, systemCustomer, zoneName);
    }

    private void updateZoneNameToCustomersOrders(UUID orderId, SystemCustomer systemCustomer, String zoneName) {
        Map<String, List<UUID>> zoneNameToCustomerOrders = systemCustomer.getZoneNameToExecutedOrdersId();
        List<UUID> ordersIds = zoneNameToCustomerOrders.containsKey(zoneName) ? zoneNameToCustomerOrders.get(zoneName) : new ArrayList<>();
        ordersIds.add(orderId);
        zoneNameToCustomerOrders.put(zoneName, ordersIds);
    }

    private void updateStoreAfterOrderCompletionV2 (SystemStore systemStore, Order newOrder) {
        Map<Integer, StoreItem> storeItems = systemStore.getItemIdToStoreItem();
        Map<PricedItem, Double> orderItems = newOrder.getPricedItems();
        Map<Offer, Integer> orderOffers = newOrder.getSelectedOfferToNumOfRealization();

        // for order items
        for (PricedItem pricedItem : orderItems.keySet()) {
            int itemId = pricedItem.getId();

            if (storeItems.containsKey(itemId)) {
                StoreItem storeItem = storeItems.get(itemId);
                double prevNumOfPurchases = storeItem.getPurchasesCount();
                storeItem.setPurchasesCount(prevNumOfPurchases + orderItems.get(pricedItem));
            }
        }

        // for discount items
        for (Map.Entry<Offer, Integer> entry : orderOffers.entrySet()) {
            Offer currOffer = entry.getKey();
            Integer numOfRealizations = entry.getValue();
            int offerItemId = currOffer.getItemId();

            if (storeItems.containsKey(offerItemId)) {
                StoreItem storeItem = storeItems.get(offerItemId);
                double prevDiscountNumOfPurchases = storeItem.getDiscountPurchasesCount();
                storeItem.setDiscountPurchasesCount(prevDiscountNumOfPurchases + numOfRealizations * currOffer.getQuantity());
            }
        }

    }

    private void updateSystemItemsAfterOrderCompletionV2 (Map<Integer, SystemItem> allSystemItems, Order newOrder) {
        // for order items
        Map<PricedItem, Double> orderItemsToAmount = newOrder.getPricedItems();
        for (Map.Entry<PricedItem, Double> entry : orderItemsToAmount.entrySet()) {
            PricedItem pricedItem = entry.getKey();
            Double amount = entry.getValue();
            int itemId = pricedItem.getId();
            SystemItem systemItem = allSystemItems.get(itemId);
            Double prevOrdersCount = systemItem.getOrdersCount();

            systemItem.setOrdersCount(prevOrdersCount + amount);
        }

        // for discount items
        Map<Offer, Integer> orderOffersToNumOfRealizations = newOrder.getSelectedOfferToNumOfRealization();
        for (Map.Entry<Offer, Integer> entry : orderOffersToNumOfRealizations.entrySet()) {
            Offer currOffer = entry.getKey();
            Integer numOfRealizations = entry.getValue();
            SystemItem systemItem = allSystemItems.get(currOffer.getItemId());
            double prevDiscountOrdersCount = systemItem.getDiscountOrdersCount();

            systemItem.setDiscountOrdersCount(prevDiscountOrdersCount + currOffer.getQuantity() * numOfRealizations);
        }
    }
}