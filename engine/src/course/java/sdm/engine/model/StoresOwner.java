package course.java.sdm.engine.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StoresOwner {
    private UUID id;
    private String name;
    private Map<String, List<SystemStore>> zoneToOwnedStores;

    public StoresOwner(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.zoneToOwnedStores = new HashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, List<SystemStore>> getZoneToOwnedStores() {
        return zoneToOwnedStores;
    }
}
