package model.response;

import model.StoreDTO;

import java.util.Map;

public class GetStoresResponse {
    private final Map<Integer, StoreDTO> stores;

    public GetStoresResponse(Map<Integer, StoreDTO> stores) {
        this.stores = stores;
    }

    public Map<Integer, StoreDTO> getStores() {
        return stores;
    }
}
