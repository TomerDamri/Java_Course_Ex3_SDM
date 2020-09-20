package model;

public class StoreMappableEntityDTO extends MappableEntity {

    private final Integer ppk;

    public StoreMappableEntityDTO (Integer id, LocationDTO location, String name, Integer numOfOrders, Integer ppk) {
        super(id, location, name, numOfOrders);
        this.ppk = ppk;
    }

    public Integer getPpk () {
        return ppk;
    }
}