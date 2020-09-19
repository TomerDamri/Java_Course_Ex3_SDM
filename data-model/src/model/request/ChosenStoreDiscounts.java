package model.request;

import java.util.List;
import java.util.Map;

public class ChosenStoreDiscounts {
    // Map<itemId,
    private Map<Integer, List<ChosenItemDiscount>> itemIdToChosenDiscounts;

    public ChosenStoreDiscounts (Map<Integer, List<ChosenItemDiscount>> itemIdToChosenDiscounts) {
        this.itemIdToChosenDiscounts = itemIdToChosenDiscounts;
    }

    public Map<Integer, List<ChosenItemDiscount>> getItemIdToChosenDiscounts () {
        return itemIdToChosenDiscounts;
    }
}
