package model.request;

import java.util.List;
import java.util.Map;

import course.java.sdm.engine.model.Discount;

public class StoreValidDiscounts {
    private Map<Integer, List<Discount>> itemIdToValidStoreDiscounts;

    public StoreValidDiscounts (Map<Integer, List<Discount>> itemIdToValidStoreDiscounts) {
        this.itemIdToValidStoreDiscounts = itemIdToValidStoreDiscounts;
    }

    public Map<Integer, List<Discount>> getItemIdToValidStoreDiscounts () {
        return itemIdToValidStoreDiscounts;
    }
}
