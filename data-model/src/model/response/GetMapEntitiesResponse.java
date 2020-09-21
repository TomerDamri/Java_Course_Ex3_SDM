package model.response;

import java.util.List;

import model.MapEntity;

public class GetMapEntitiesResponse {
    private final List<MapEntity> allSystemMappableEntities;

    public GetMapEntitiesResponse(List<MapEntity> allSystemMappableEntities) {
        this.allSystemMappableEntities = allSystemMappableEntities;
    }

    public List<MapEntity> getAllSystemMappableEntities () {
        return allSystemMappableEntities;
    }
}