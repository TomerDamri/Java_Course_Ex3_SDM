package course.java.sdm.engine.utils.systemUpdater;

import java.util.*;

import course.java.sdm.engine.mapper.GeneratedDataMapper;
import course.java.sdm.engine.model.*;

public class SystemUpdater {

    private static SystemUpdater singletonSystemUpdater = null;
    private final static SystemUpdaterValidator SYSTEM_UPDATER_VALIDATOR = new SystemUpdaterValidator();

    private SystemUpdater () {
    }

    public static SystemUpdater getSystemUpdater () {
        if (singletonSystemUpdater == null) {
            singletonSystemUpdater = new SystemUpdater();
        }

        return singletonSystemUpdater;
    }

    public void updateSystemAfterLoadingOrdersHistoryFromFile (Map<UUID, List<SystemOrder>> ordersFromHistoryFile,
                                                               Map<UUID, DynamicOrder> historyDynamicOrders,
                                                               Descriptor descriptor) {
        Map<UUID, List<SystemOrder>> systemOrdersBeforeUpdate = descriptor.getSystemOrders();
        Map<UUID, DynamicOrder> dynamicOrdersBeforeUpdate = descriptor.getDynamicOrders();
        Map<Integer, SystemStore> systemStores = descriptor.getSystemStores();

        updateSystemOrdersAccordingToHistoryFile(ordersFromHistoryFile, descriptor, systemOrdersBeforeUpdate, systemStores);
        updateDynamicOrdersAccordingToHistoryFile(historyDynamicOrders, dynamicOrdersBeforeUpdate);
    }

    private void updateSystemOrdersAccordingToHistoryFile (Map<UUID, List<SystemOrder>> ordersFromHistoryFile,
                                                           Descriptor descriptor,
                                                           Map<UUID, List<SystemOrder>> systemOrdersBeforeUpdate,
                                                           Map<Integer, SystemStore> systemStores) {
        ordersFromHistoryFile.entrySet()
                             .stream()
                             .filter(entry -> !systemOrdersBeforeUpdate.containsKey(entry.getKey()))
                             .forEach(entry -> entry.getValue()
                                                    .forEach(order -> updateSystemAfterLoadingHistoryOrder(descriptor,
                                                                                                           systemStores,
                                                                                                           order)));
    }

    private void updateSystemAfterLoadingHistoryOrder (Descriptor descriptor,
                                                       Map<Integer, SystemStore> systemStores,
                                                       SystemOrder systemOrder) {
        Integer storeId = systemOrder.getStoreId();
        Order order = systemOrder.getOrder();
        SystemStore systemStore = systemStores.get(storeId);

        updateSystemAfterStaticOrder(systemStore, order, descriptor);
    }

    private void updateDynamicOrdersAccordingToHistoryFile (Map<UUID, DynamicOrder> historyDynamicOrders,
                                                            Map<UUID, DynamicOrder> dynamicOrdersBeforeUpdate) {
        historyDynamicOrders.entrySet()
                            .stream()
                            .filter(entry -> !dynamicOrdersBeforeUpdate.containsKey(entry.getKey()))
                            .forEach(entry -> dynamicOrdersBeforeUpdate.put(entry.getKey(), entry.getValue()));
    }

    public void updateSystemAfterDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder, Descriptor descriptor) {
        Map<UUID, DynamicOrder> dynamicOrders = descriptor.getDynamicOrders();
        SYSTEM_UPDATER_VALIDATOR.validateDynamicOrderExist(dynamicOrderId, dynamicOrders);

        DynamicOrder dynamicOrder = dynamicOrders.get(dynamicOrderId);
        SYSTEM_UPDATER_VALIDATOR.validateDynamicOrderNotConfirmedYet(dynamicOrderId, dynamicOrder);

        if (toConfirmNewDynamicOrder) {
            dynamicOrder.setConfirmed(toConfirmNewDynamicOrder);
            dynamicOrder.getStaticOrders().forEach( (storeDetails, order) -> {
                int storeId = storeDetails.getId();
                SystemStore systemStore = descriptor.getSystemStores().get(storeId);
                updateSystemAfterStaticOrder(systemStore, order, descriptor);
            });
        }
        else {
            dynamicOrders.remove(dynamicOrderId);
        }
    }

    public void updateSystemAfterStaticOrder (SystemStore systemStore, Order newOrder, Descriptor descriptor) {
        updateSystemStore(systemStore, newOrder);
        // add order to store
        systemStore.getOrders().add(newOrder);
        // add order to order collection in descriptor
        addNewOrderToSystemOrders(systemStore, newOrder, descriptor);
        // update counter of all store items that was included in order
        updateStoreAfterOrderCompletion(systemStore, newOrder);
        // update counter of all system items that was included in order
        updateSystemItemsAfterOrderCompletion(descriptor.getSystemItems(), newOrder);
    }

    private void addNewOrderToSystemOrders (SystemStore systemStore, Order newOrder, Descriptor descriptor) {
        List<SystemOrder> orders;
        SystemOrder newSystemOrder = new SystemOrder(newOrder, systemStore.getName(), systemStore.getId());
        UUID id = (newOrder.getParentId() != null) ? newOrder.getParentId() : newOrder.getId();
        Map<UUID, List<SystemOrder>> systemOrders = descriptor.getSystemOrders();

        if (systemOrders.containsKey(id)) {
            orders = new ArrayList<>(systemOrders.get(id));
            orders.add(newSystemOrder);
        }
        else {
            orders = Arrays.asList(newSystemOrder);
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

    private void updateStoreAfterOrderCompletion (SystemStore systemStore, Order newOrder) {
        Map<Integer, StoreItem> storeItems = systemStore.getItemIdToStoreItem();
        Map<PricedItem, Double> allOrderItemsMap = newOrder.getPricedItems();
        Set<PricedItem> orderItems = allOrderItemsMap.keySet();

        for (PricedItem pricedItem : orderItems) {
            int itemId = pricedItem.getId();

            if (storeItems.containsKey(itemId)) {
                StoreItem storeItem = storeItems.get(itemId);
                int prevNumOfPurchases = storeItem.getPurchasesCount();
                storeItem.setPurchasesCount(prevNumOfPurchases + allOrderItemsMap.get(pricedItem).intValue());
            }
        }
    }

    private void updateSystemItemsAfterOrderCompletion (Map<Integer, SystemItem> allSystemItems, Order newOrder) {
        Set<PricedItem> orderItems = newOrder.getPricedItems().keySet();
        for (PricedItem pricedItem : orderItems) {
            int itemId = pricedItem.getId();
            SystemItem systemItem = allSystemItems.get(itemId);
            int numOfItemsToCount = pricedItem.getPurchaseCategory().equals(Item.PurchaseCategory.QUANTITY)
                        ? newOrder.getPricedItems().get(pricedItem).intValue()
                        : 1;
            systemItem.setOrdersCount(systemItem.getOrdersCount() + numOfItemsToCount);
        }
    }
}