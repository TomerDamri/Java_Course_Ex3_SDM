package course.java.sdm.engine.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZoneOrdersHistory {
    private String zoneName;
    private Map<UUID, List<SystemOrder>> systemOrders;

    public ZoneOrdersHistory () {
    }

    public ZoneOrdersHistory (String zoneName, Map<UUID, List<SystemOrder>> systemOrders) {
        this.zoneName = zoneName;
        this.systemOrders = systemOrders;
    }

    public Map<UUID, List<SystemOrder>> getSystemOrders () {
        return systemOrders;
    }

    public String getZoneName () {
        return zoneName;
    }
}
