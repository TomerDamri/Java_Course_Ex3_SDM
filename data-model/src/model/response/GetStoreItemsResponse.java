package model.response;

import java.util.List;

import model.PricedItemDTO;
import model.StoreItemDTO;

public class GetStoreItemsResponse {

    private List<PricedItemDTO> storeItems;

    public GetStoreItemsResponse (List<PricedItemDTO> storeItems) {
        this.storeItems = storeItems;
    }

    public List<PricedItemDTO> getStoreItems () {
        return storeItems;
    }
}
