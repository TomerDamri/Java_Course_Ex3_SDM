package course.java.sdm.engine.utils.systemUpdater;

import java.util.*;
import java.util.stream.Collectors;

import course.java.sdm.engine.exceptions.ItemNotExist;
import course.java.sdm.engine.model.*;

public class SystemUpdaterValidator {
    public void validateAllOrderItemsExistInStore (SystemStore systemStore, Order order) {
        Set<Integer> orderItems = order.getPricedItems().keySet().stream().map(PricedItem::getId).collect(Collectors.toSet());
        Set<Integer> orderDiscountsItems = order.getSelectedOfferToNumOfRealization()
                                                .keySet()
                                                .stream()
                                                .map(Offer::getItemId)
                                                .collect(Collectors.toSet());

        Set<Integer> allOrderItemsIds = new HashSet<>();
        allOrderItemsIds.addAll(orderItems);
        allOrderItemsIds.addAll(orderDiscountsItems);

        Set<Integer> storeItemsIds = systemStore.getItemIdToStoreItem().keySet();

        if (!storeItemsIds.containsAll(allOrderItemsIds)) {
            List<Integer> invalidItemsIdsFromOrder = orderItems.stream()
                                                               .filter(orderItemId -> !storeItemsIds.contains(orderItemId))
                                                               .collect(Collectors.toList());
            throw new RuntimeException(String.format("The following items ids: %s don't exist in '%s' store",
                                                     invalidItemsIdsFromOrder,
                                                     systemStore.getName()));
        }
    }

    public void validateCustomerExistInSystem (Map<UUID, SystemCustomer> systemCustomers, UUID customerId) {
        if (!systemCustomers.containsKey(customerId)) {
            throw new RuntimeException(String.format("There is no customer in system with id %s", customerId));
        }
    }

    public void validateItemNotExistInStore (Integer itemId, SystemStore systemStore, SystemItem systemItem) {
        if (isItemExistInStore(itemId, systemStore)) {
            throw new RuntimeException(String.format("The store '%s' already sells '%s' item",
                                                     systemStore.getName(),
                                                     systemItem.getName()));
        }
    }

    public void validateItemExistInStore (Integer itemId, SystemStore systemStore, SystemItem systemItem) {
        if (!isItemExistInStore(itemId, systemStore)) {
            throw new RuntimeException(String.format("The store '%s' don't sell '%s' item", systemStore.getName(), systemItem.getName()));
        }
    }

    private boolean isItemExistInStore (Integer itemId, SystemStore systemStore) {
        return systemStore.getItemIdToStoreItem().containsKey(itemId);
    }

    public void validateStoreExistInSystem (Integer storeId, Map<Integer, SystemStore> systemStores) {
        if (!systemStores.containsKey(storeId)) {
            throw new RuntimeException(String.format("There is no such store in the system with id :%s", storeId));
        }
    }

    public void validateItemExistInSystem (Integer itemId, Map<Integer, SystemItem> systemItems) {
        if (!systemItems.containsKey(itemId)) {
            throw new ItemNotExist(itemId);
        }
    }

    public void validateStoreSellsOtherItems (SystemStore systemStore, SystemItem systemItem) {
        if (systemStore.getItemIdToStoreItem().size() == 1) {
            throw new RuntimeException(String.format("The store: '%s' only sells '%s' item, you can't remove it.",
                                                     systemStore.getName(),
                                                     systemItem.getName()));
        }
    }

    public void validateItemSellsInOtherStores (SystemStore systemStore, SystemItem systemItem) {
        if (systemItem.getStoresCount() == 1) {
            throw new RuntimeException(String.format("Only the store: '%s' sells '%s' item, you can't remove it.",
                                                     systemStore.getName(),
                                                     systemItem.getName()));
        }
    }
}