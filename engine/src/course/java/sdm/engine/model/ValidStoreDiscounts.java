package course.java.sdm.engine.model;

import java.util.List;
import java.util.Map;

public class ValidStoreDiscounts {
    private Map<Integer, List<Discount>> itemIdToValidStoreDiscounts;

    public ValidStoreDiscounts (Map<Integer, List<Discount>> itemIdToValidStoreDiscounts) {
        this.itemIdToValidStoreDiscounts = itemIdToValidStoreDiscounts;
    }

    public Map<Integer, List<Discount>> getItemIdToValidStoreDiscounts () {
        return itemIdToValidStoreDiscounts;
    }
}