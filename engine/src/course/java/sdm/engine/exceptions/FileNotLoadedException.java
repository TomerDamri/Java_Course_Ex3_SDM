package course.java.sdm.engine.exceptions;

public class FileNotLoadedException extends RuntimeException {
    public FileNotLoadedException () {
        super("Can not process your request, data file is not yet uploaded");
    }

    public FileNotLoadedException (String message) {
        super(message);
    }
}