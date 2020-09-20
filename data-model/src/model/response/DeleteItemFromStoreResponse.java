package model.response;

import java.util.List;

import model.DiscountDTO;

public class DeleteItemFromStoreResponse {

    private final List<DiscountDTO> removedDiscounts;

    public DeleteItemFromStoreResponse (List<DiscountDTO> removedDiscounts) {
        this.removedDiscounts = removedDiscounts;
    }

    public List<DiscountDTO> getRemovedDiscounts () {
        return removedDiscounts;
    }
}