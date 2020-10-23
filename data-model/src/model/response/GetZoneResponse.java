package model.response;


import model.StoreDTO;
import model.SystemItemDTO;

import java.util.List;

public class GetZoneResponse {

    private List<StoreDTO> stores;
    private List<SystemItemDTO> items;

    public GetZoneResponse(List<StoreDTO> stores, List<SystemItemDTO> items) {
        this.stores = stores;
        this.items = items;
    }

    public List<StoreDTO> getStores() {
        return stores;
    }

    public List<SystemItemDTO> getItems() {
        return items;
    }
}
