package model.response;

import java.util.Map;

import model.request.ValidStoreDiscountsDTO;

public class GetDiscountsResponse {

    private Map<Integer, ValidStoreDiscountsDTO> storeIdToValidDiscounts;

    public GetDiscountsResponse (Map<Integer, ValidStoreDiscountsDTO> storeIdToValidDiscounts) {
        this.storeIdToValidDiscounts = storeIdToValidDiscounts;
    }

    public Map<Integer, ValidStoreDiscountsDTO> getStoreIdToValidDiscounts () {
        return storeIdToValidDiscounts;
    }
}