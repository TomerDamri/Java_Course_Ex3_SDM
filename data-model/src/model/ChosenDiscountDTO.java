package model;

public class ChosenDiscountDTO {
    private Integer storeId;
    private Integer itemId;
    private String discountName;
    private Integer numOfRealizations;
    private Integer orOfferId;

    public ChosenDiscountDTO (Integer storeId, Integer itemId, String discountName, Integer numOfRealizations, Integer orOfferId) {
        this.storeId = storeId;
        this.itemId = itemId;
        this.discountName = discountName;
        this.numOfRealizations = numOfRealizations;
        this.orOfferId = orOfferId;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public Integer getItemId () {
        return itemId;
    }

    public String getDiscountName () {
        return discountName;
    }

    public Integer getNumOfRealizations () {
        return numOfRealizations;
    }

    public Integer getOrOfferId () {
        return orOfferId;
    }
}
