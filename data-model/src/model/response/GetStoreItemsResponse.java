package model.response;

import java.util.List;

import model.PricedItemDTO;

public class GetStoreItemsResponse {

    private List<PricedItemDTO> storeItems;

    public GetStoreItemsResponse (List<PricedItemDTO> storeItems) {
        this.storeItems = storeItems;
    }

    public List<PricedItemDTO> getStoreItems () {
        return storeItems;
    }
}
