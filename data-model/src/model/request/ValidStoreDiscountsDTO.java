package model.request;

import model.DiscountDTO;

import java.util.List;
import java.util.Map;

public class ValidStoreDiscountsDTO {

    private Map<Integer, List<DiscountDTO>> itemIdToValidStoreDiscounts;

    public ValidStoreDiscountsDTO (Map<Integer, List<DiscountDTO>> itemIdToValidStoreDiscounts) {
        this.itemIdToValidStoreDiscounts = itemIdToValidStoreDiscounts;
    }

    public Map<Integer, List<DiscountDTO>> getItemIdToValidStoreDiscounts () {
        return itemIdToValidStoreDiscounts;
    }
}
