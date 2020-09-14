package model.request;

import java.util.List;
import java.util.Map;

public class StoreChosenDiscounts {
    // Map<itemId,
    private Map<Integer, List<ItemChosenDiscount>> itemIdToChosenDiscounts;

    public StoreChosenDiscounts (Map<Integer, List<ItemChosenDiscount>> itemIdToChosenDiscounts) {
        this.itemIdToChosenDiscounts = itemIdToChosenDiscounts;
    }

    public Map<Integer, List<ItemChosenDiscount>> getItemIdToChosenDiscounts () {
        return itemIdToChosenDiscounts;
    }
}
