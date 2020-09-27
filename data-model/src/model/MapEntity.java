package model;

public abstract class MapEntity {

    protected final Integer id;
    protected final String name;
    protected final LocationDTO location;
    protected final Integer numOfOrders;

    protected MapEntity (Integer id, LocationDTO location, String name, Integer numOfOrders) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.numOfOrders = numOfOrders;
    }

    public Integer getId () {
        return id;
    }

    public LocationDTO getLocation () {
        return location;
    }

    public String getName () {
        return name;
    }

    public Integer getNumOfOrders () {
        return numOfOrders;
    }
}
