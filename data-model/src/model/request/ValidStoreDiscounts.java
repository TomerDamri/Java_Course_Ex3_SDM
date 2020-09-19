package model.request;

import java.util.List;
import java.util.Map;

import course.java.sdm.engine.model.Discount;

public class ValidStoreDiscounts {
    private Map<Integer, List<Discount>> itemIdToValidStoreDiscounts;

    public ValidStoreDiscounts (Map<Integer, List<Discount>> itemIdToValidStoreDiscounts) {
        this.itemIdToValidStoreDiscounts = itemIdToValidStoreDiscounts;
    }

    public Map<Integer, List<Discount>> getItemIdToValidStoreDiscounts () {
        return itemIdToValidStoreDiscounts;
    }
}
