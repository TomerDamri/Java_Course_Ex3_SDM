package model.response;

import java.util.List;
import java.util.UUID;

import model.DynamicOrderEntityDTO;

public class PlaceDynamicOrderResponse {

    private UUID id;
    private final List<DynamicOrderEntityDTO> dynamicOrderEntityDTO;

    public PlaceDynamicOrderResponse (UUID id, List<DynamicOrderEntityDTO> dynamicOrderEntityDTO) {
        this.id = id;
        this.dynamicOrderEntityDTO = dynamicOrderEntityDTO;
    }

    public UUID getId () {
        return id;
    }

    public List<DynamicOrderEntityDTO> getDynamicOrderEntity () {
        return dynamicOrderEntityDTO;
    }
}