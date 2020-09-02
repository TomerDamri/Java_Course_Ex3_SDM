package course.java.sdm.engine.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException (String message) {
        super(message);
    }
}
