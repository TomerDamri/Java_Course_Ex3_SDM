package model;

public class CustomerMappableEntityDTO extends MappableEntity {
    public CustomerMappableEntityDTO (Integer id, LocationDTO location, String name, Integer numOfOrders) {
        super(id, location, name, numOfOrders);
    }
}