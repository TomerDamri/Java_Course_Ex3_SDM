package course.java.sdm.engine.model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class SDMDescriptor {

    private Map<UUID, SystemCustomer> idToSystemCustomerMap;
    private Map<UUID, StoresOwner> idToStoreOwnerMap;
    private Map<String, Zone> zoneNameToZone;
    private Map<Location, SystemStore> allSystemLocations;

    public SDMDescriptor () {
        this.idToStoreOwnerMap = new HashMap<>();
        this.idToSystemCustomerMap = new HashMap<>();
        this.zoneNameToZone = new TreeMap<>();
        this.allSystemLocations = new HashMap<>();
    }

    public Map<UUID, SystemCustomer> getSystemCustomers () {
        return idToSystemCustomerMap;
    }

    public Map<UUID, StoresOwner> getStoresOwners () {
        return idToStoreOwnerMap;
    }

    public Map<String, Zone> getZones () {
        return zoneNameToZone;
    }

    public Map<Location, SystemStore> getSystemLocations () {
        return allSystemLocations;
    }
}
