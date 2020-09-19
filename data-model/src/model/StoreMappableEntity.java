package model;

public class StoreMappableEntity extends MappableEntity {

    private final Integer ppk;

    public StoreMappableEntity(Integer id, LocationDTO location, String name, Integer numOfOrders, Integer ppk) {
        super(id, location, name, numOfOrders);
        this.ppk = ppk;
    }

    public Integer getPpk() {
        return ppk;
    }
}