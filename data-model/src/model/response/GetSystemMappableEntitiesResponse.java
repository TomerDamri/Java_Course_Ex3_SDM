package model.response;

import java.util.List;

import model.MappableEntity;

public class GetSystemMappableEntitiesResponse {
    private final List<MappableEntity> allSystemMappableEntities;

    public GetSystemMappableEntitiesResponse (List<MappableEntity> allSystemMappableEntities) {
        this.allSystemMappableEntities = allSystemMappableEntities;
    }

    public List<MappableEntity> getAllSystemMappableEntities () {
        return allSystemMappableEntities;
    }
}