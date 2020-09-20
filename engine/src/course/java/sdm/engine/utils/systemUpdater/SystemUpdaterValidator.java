package course.java.sdm.engine.utils.systemUpdater;

import java.util.Map;

import course.java.sdm.engine.exceptions.ItemNotExist;
import course.java.sdm.engine.model.SystemItem;
import course.java.sdm.engine.model.SystemStore;

public class SystemUpdaterValidator {

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