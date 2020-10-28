package model.request;

import java.util.List;

import model.ValidItemDiscountsDTO;

public class ValidStoreDiscountsDTO {
    private String storeName;
    private Integer storeId;
    private List<ValidItemDiscountsDTO> validItemsDiscounts;

    public ValidStoreDiscountsDTO (String storeName, Integer storeId, List<ValidItemDiscountsDTO> validItemsDiscounts) {
        this.storeName = storeName;
        this.storeId = storeId;
        this.validItemsDiscounts = validItemsDiscounts;
    }

    public String getStoreName () {
        return storeName;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public List<ValidItemDiscountsDTO> getValidItemsDiscounts () {
        return validItemsDiscounts;
    }
}
