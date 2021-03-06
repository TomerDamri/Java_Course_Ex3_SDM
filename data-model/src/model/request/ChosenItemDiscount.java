package model.request;

import java.util.Optional;

public class ChosenItemDiscount {
    private final String discountId;
    private Integer numOfRealizations;
    private Optional<Integer> orOfferId;

    public ChosenItemDiscount (String discountId, Integer numOfRealizations, Optional<Integer> orOfferId) {
        this.discountId = discountId;
        this.numOfRealizations = numOfRealizations;
        this.orOfferId = orOfferId;
    }

    public String getDiscountId () {
        return discountId;
    }

    public Integer getNumOfRealizations () {
        return numOfRealizations;
    }

    public Optional<Integer> getOrOfferId () {
        return orOfferId;
    }
}
