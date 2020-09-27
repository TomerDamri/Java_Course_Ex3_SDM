package logic.tasks.file;

import java.util.function.Consumer;

import course.java.sdm.engine.controller.ISDMController;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class LoadSystemDataFileTask extends Task<Boolean> {
    private final long SLEEP_TIME = 0;

    private String fileName;
    private final ISDMController beController;
    private final Consumer<String> fileErrorDelegate;

    public LoadSystemDataFileTask (String fileName, ISDMController beController, Consumer<String> fileErrorDelegate) {
        this.fileName = fileName;
        this.beController = beController;
        this.fileErrorDelegate = fileErrorDelegate;
    }

    @Override
    protected Boolean call () {
        updateMessage("Loading File...");
        try {
            beController.loadFile(fileName);
            Thread.sleep(SLEEP_TIME);
            updateMessage("Done...");
            Thread.sleep(SLEEP_TIME);

        }
        catch (Exception ex) {
            Platform.runLater( () -> fileErrorDelegate.accept(ex.getMessage()));
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
