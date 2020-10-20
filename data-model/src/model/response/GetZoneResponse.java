package model.response;

import java.util.Map;

import model.StoreDTO;
import model.SystemItemDTO;

public class GetZoneResponse {

    private Map<Integer, StoreDTO> stores;
    private Map<Integer, SystemItemDTO> items;

    public GetZoneResponse (Map<Integer, StoreDTO> stores, Map<Integer, SystemItemDTO> items) {
        this.stores = stores;
        this.items = items;
    }

    public Map<Integer, StoreDTO> getStores () {
        return stores;
    }

    public Map<Integer, SystemItemDTO> getItems () {
        return items;
    }
}
