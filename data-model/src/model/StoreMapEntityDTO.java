package model;

public class StoreMapEntityDTO extends MapEntity {

    private final Integer ppk;

    public StoreMapEntityDTO(Integer id, LocationDTO location, String name, Integer numOfOrders, Integer ppk) {
        super(id, location, name, numOfOrders);
        this.ppk = ppk;
    }

    public Integer getPpk () {
        return ppk;
    }
}