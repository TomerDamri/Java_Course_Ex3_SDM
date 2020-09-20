package model.response;

import model.request.ValidStoreDiscounts;

import java.util.Map;

public class GetDiscountsResponse {

    private Map<Integer, ValidStoreDiscounts> storeIdToValidDiscounts;

    public GetDiscountsResponse(Map<Integer, ValidStoreDiscounts> storeIdToValidDiscounts) {
        this.storeIdToValidDiscounts = storeIdToValidDiscounts;
    }
}