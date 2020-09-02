package course.java.sdm.engine.exceptions;

public class IllegalFileExtensionException extends IllegalArgumentException {

    public IllegalFileExtensionException (String filePath, String expectedExtension) {
        super(String.format("The extension of The file: %s should be %s.", filePath, expectedExtension));
    }
}
