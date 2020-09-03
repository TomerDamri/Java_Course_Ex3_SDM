package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Descriptor implements Serializable {

    private static Integer numOfOrders = 1;
    private static Integer numOfDynamicOrders = 1;
    private Map<Integer, SystemStore> systemStores;
    private Map<Integer, SystemItem> systemItems;
    Map<Integer, SystemCustomer> systemCustomers;
    private Map<UUID, List<SystemOrder>> systemOrders;
    private Map<UUID, DynamicOrder> dynamicOrders;

    public static Integer generateStaticOrderId () {
        Integer num = numOfOrders;
        numOfOrders++;
        return num;
    }

    public static Integer generateDynamicOrderId () {
        Integer num = numOfDynamicOrders;
        numOfDynamicOrders++;
        return num;
    }

    public Descriptor (Map<Integer, SystemStore> systemStores,
                       Map<Integer, SystemItem> systemItems,
                       Map<Integer, SystemCustomer> systemCustomers) {
        this.systemStores = systemStores;
        this.systemItems = systemItems;
        this.systemCustomers = systemCustomers;
        this.systemOrders = new TreeMap<>();
        this.dynamicOrders = new TreeMap<>();
    }

    public Map<Integer, SystemCustomer> getSystemCustomers () {
        return systemCustomers;
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

    public Map<UUID, DynamicOrder> getDynamicOrders () {
        return dynamicOrders;
    }
}
