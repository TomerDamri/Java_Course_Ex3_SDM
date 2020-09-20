package model;

public abstract class MappableEntity {

    protected final Integer id;
    protected final String name;
    protected final LocationDTO location;
    protected final Integer numOfOrders;

    protected MappableEntity (Integer id, LocationDTO location, String name, Integer numOfOrders) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.numOfOrders = numOfOrders;
    }

    protected Integer getId () {
        return id;
    }

    protected LocationDTO getLocation () {
        return location;
    }

    protected String getName () {
        return name;
    }

    protected Integer getNumOfOrders () {
        return numOfOrders;
    }
}
