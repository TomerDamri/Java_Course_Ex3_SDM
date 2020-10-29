package model.response;

import java.util.List;

import model.request.ValidStoreDiscountsDTO;

public class GetDiscountsResponse {

    private List<ValidStoreDiscountsDTO> storeIdToValidDiscounts;

    public GetDiscountsResponse (List<ValidStoreDiscountsDTO> storeIdToValidDiscounts) {
        this.storeIdToValidDiscounts = storeIdToValidDiscounts;
    }

    public List<ValidStoreDiscountsDTO> getStoreIdToValidDiscounts () {
        return storeIdToValidDiscounts;
    }
}