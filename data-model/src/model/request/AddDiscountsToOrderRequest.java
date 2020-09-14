package model.request;

import java.util.Map;
import java.util.UUID;

public class AddDiscountsToOrderRequest {
    private UUID orderID;
    //<storeId,
    private Map<Integer, StoreChosenDiscounts> storeIdToChosenDiscounts;

    public AddDiscountsToOrderRequest(UUID orderID, Map<Integer, StoreChosenDiscounts> storeIdToChosenDiscounts) {
        this.orderID = orderID;
        this.storeIdToChosenDiscounts = storeIdToChosenDiscounts;
    }

    public UUID getOrderID() {
        return orderID;
    }

    public Map<Integer, StoreChosenDiscounts> getStoreIdToChosenDiscounts() {
        return storeIdToChosenDiscounts;
    }
}
