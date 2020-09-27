package course.java.sdm.engine.model;

import java.util.List;

public class DeleteItemResult {

    private final StoreItem removedStoreItem;
    private final List<Discount> removedDiscounts;

    public DeleteItemResult(StoreItem storeItem, List<Discount> removedDiscounts) {
        this.removedStoreItem = storeItem;
        this.removedDiscounts = removedDiscounts;
    }

    public StoreItem getRemovedStoreItem() {
        return removedStoreItem;
    }

    public List<Discount> getRemovedDiscounts() {
        return removedDiscounts;
    }
}
