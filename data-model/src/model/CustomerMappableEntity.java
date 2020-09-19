package model;

public class CustomerMappableEntity extends MappableEntity {
    public CustomerMappableEntity (Integer id, LocationDTO location, String name, Integer numOfOrders) {
        super(id, location, name, numOfOrders);
    }
}