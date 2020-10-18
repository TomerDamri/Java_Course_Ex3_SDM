package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.List;

public class SystemOrdersHistory implements Serializable {

    private List<ZoneOrdersHistory> zoneOrdersHistories;

    public SystemOrdersHistory () {
    }

    public SystemOrdersHistory (List<ZoneOrdersHistory> zoneOrdersHistories) {
        this.zoneOrdersHistories = zoneOrdersHistories;
    }

    public List<ZoneOrdersHistory> getZoneOrdersHistories () {
        return zoneOrdersHistories;
    }
}