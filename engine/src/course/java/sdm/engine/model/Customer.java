package course.java.sdm.engine.model;

public class Customer {

    private int id;
    private String name;
    private course.java.sdm.engine.model.Location location;

    public Customer (int id, String name, course.java.sdm.engine.model.Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public int getId () {
        return id;
    }

    public String getName () {
        return name;
    }

    public course.java.sdm.engine.model.Location getLocation () {
        return location;
    }
}
