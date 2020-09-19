package model.request;

import java.util.Map;
import java.util.UUID;

public class AddDiscountsToOrderRequest {
    private UUID orderID;
    // <storeId,
    private Map<Integer, ChosenStoreDiscounts> storeIdToChosenDiscounts;

    public AddDiscountsToOrderRequest (UUID orderID, Map<Integer, ChosenStoreDiscounts> storeIdToChosenDiscounts) {
        this.orderID = orderID;
        this.storeIdToChosenDiscounts = storeIdToChosenDiscounts;
    }

    public UUID getOrderID () {
        return orderID;
    }

    public Map<Integer, ChosenStoreDiscounts> getStoreIdToChosenDiscounts () {
        return storeIdToChosenDiscounts;
    }
}
