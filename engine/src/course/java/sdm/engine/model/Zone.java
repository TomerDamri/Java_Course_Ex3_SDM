package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import course.java.sdm.engine.model.notifications.publisher.NotificationsPublisher;

public class Zone extends NotificationsPublisher implements Serializable {

    private final String zoneName;
    private final UUID zoneOwnerId;
    private final String zoneOwnerName;
    private Map<Integer, SystemStore> systemStores;
    private Map<Integer, SystemItem> systemItems;
    private Map<UUID, List<SystemOrder>> systemOrders;

    public Zone (String zoneName,
                 UUID zoneOwnerId,
                 String zoneOwnerName,
                 Map<Integer, SystemStore> systemStores,
                 Map<Integer, SystemItem> systemItems) {
        this.zoneName = zoneName;
        this.zoneOwnerId = zoneOwnerId;
        this.zoneOwnerName = zoneOwnerName;
        this.systemStores = systemStores;
        this.systemItems = systemItems;
        this.systemOrders = new TreeMap<>();
    }

    public Map<Integer, SystemStore> getSystemStores () {
        return systemStores;
    }

    public Map<Integer, SystemItem> getSystemItems () {
        return systemItems;
    }

    public Map<UUID, List<SystemOrder>> getSystemOrders () {
        return systemOrders;
    }

    public String getZoneName () {
        return zoneName;
    }

    public UUID getZoneOwnerId () {
        return zoneOwnerId;
    }

    public String getZoneOwnerName () {
        return zoneOwnerName;
    }

    public List<SystemOrder> getOrderById (UUID orderId) {
        Map<UUID, List<SystemOrder>> systemOrders = getSystemOrders();
        if (!systemOrders.containsKey(orderId)) {
            throw new RuntimeException(String.format("There is no order with ID: '%s' in the system with name '%s'", orderId, zoneName));
        }

        return systemOrders.get(orderId);
    }
}
