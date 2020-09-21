package model;

public class CustomerMapEntityDTO extends MapEntity {
    public CustomerMapEntityDTO(Integer id, LocationDTO location, String name, Integer numOfOrders) {
        super(id, location, name, numOfOrders);
    }
}