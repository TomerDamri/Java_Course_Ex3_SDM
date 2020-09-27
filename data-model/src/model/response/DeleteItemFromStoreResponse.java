package model.response;

import java.util.List;

public class DeleteItemFromStoreResponse {

    private final List<String> removedDiscounts;

    public DeleteItemFromStoreResponse (List<String> removedDiscounts) {
        this.removedDiscounts = removedDiscounts;
    }

    public List<String> getRemovedDiscounts () {
        return removedDiscounts;
    }
}