package model.response;

import java.util.List;

import model.StoreDTO;
import model.SystemItemDTO;

public class GetZoneResponse {

    private List<StoreDTO> stores;
    private List<SystemItemDTO> items;

    public GetZoneResponse (List<StoreDTO> stores, List<SystemItemDTO> items) {
        this.stores = stores;
        this.items = items;
    }

    public List<StoreDTO> getStores () {
        return stores;
    }

    public List<SystemItemDTO> getItems () {
        return items;
    }
}
