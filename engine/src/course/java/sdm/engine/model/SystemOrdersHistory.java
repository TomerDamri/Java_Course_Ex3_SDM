package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SystemOrdersHistory implements Serializable {

    private Map<UUID, List<SystemOrder>> systemOrders;

    public SystemOrdersHistory () {
    }

    public SystemOrdersHistory (Map<UUID, List<SystemOrder>> systemOrders) {
        this.systemOrders = systemOrders;
    }

    public Map<UUID, List<SystemOrder>> getSystemOrders () {
        return systemOrders;
    }
}