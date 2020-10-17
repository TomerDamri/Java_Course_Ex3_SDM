package course.java.sdm.engine.model;

import java.util.*;

public class SDMDescriptor {

    private Map<UUID, SystemCustomer> idToSystemCustomerMap;
    private Map<UUID, StoresOwner> idToStoreOwnerMap;
    private List<Zone> zones;

    public SDMDescriptor() {
        this.idToStoreOwnerMap = new HashMap<>();
        this.idToSystemCustomerMap = new HashMap<>();
        this.zones = new LinkedList<>();
    }

    public Map<UUID, SystemCustomer> getSystemCustomers() {
        return idToSystemCustomerMap;
    }

    public Map<UUID, StoresOwner> getStoresOwners() {
        return idToStoreOwnerMap;
    }

    public List<Zone> getZones() {
        return zones;
    }
}
