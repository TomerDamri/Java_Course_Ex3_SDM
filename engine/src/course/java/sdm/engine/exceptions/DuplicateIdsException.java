package course.java.sdm.engine.exceptions;

public class DuplicateIdsException extends RuntimeException {
    public DuplicateIdsException (String duplicatedEntity, Throwable cause) {
        super(String.format("There are 2 %ss  with the same id", duplicatedEntity), cause);
    }
}