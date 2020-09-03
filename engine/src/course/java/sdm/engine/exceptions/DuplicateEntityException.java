package course.java.sdm.engine.exceptions;

import course.java.sdm.engine.model.Location;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException (String duplicatedEntity, Throwable cause) {
        super(String.format("There are 2 %ss with the same id", duplicatedEntity), cause);
    }

    public DuplicateEntityException (Location duplicatedLocation) {
        super(String.format("The location %s already exist in the system", duplicatedLocation.toString()));
    }
}