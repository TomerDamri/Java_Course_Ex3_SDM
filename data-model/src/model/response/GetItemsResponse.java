package model.response;

import java.util.Map;

import model.SystemItemDTO;

public class GetItemsResponse {
    private final Map<Integer, SystemItemDTO> items;

    public GetItemsResponse (Map<Integer, SystemItemDTO> items) {
        this.items = items;
    }

    public Map<Integer, SystemItemDTO> getItems () {
        return items;
    }
}
