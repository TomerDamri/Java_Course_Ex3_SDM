package course.java.sdm.engine.model;

import java.util.UUID;

public class Customer {

    private UUID id;
    private String name;

    public Customer (UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId () {
        return id;
    }

    public String getName () {
        return name;
    }

}
