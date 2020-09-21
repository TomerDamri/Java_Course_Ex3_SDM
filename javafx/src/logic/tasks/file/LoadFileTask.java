package logic.tasks.file;

import course.java.sdm.engine.controller.ISDMController;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class LoadFileTask extends Task<Boolean> {
    private final long SLEEP_TIME = 1000;

    private String fileName;
    private final ISDMController beController;
    private final Consumer<String> fileErrorDelegate;

    public LoadFileTask(String fileName, ISDMController beController, Consumer<String> fileErrorDelegate) {
        this.fileName = fileName;
        this.beController = beController;
        this.fileErrorDelegate = fileErrorDelegate;
    }

    @Override
    protected Boolean call() {
        updateMessage("Loading File...");
        try {
            beController.loadFile(fileName);
            Thread.sleep(SLEEP_TIME);
            updateMessage("Done...");
            Thread.sleep(SLEEP_TIME);

        } catch (Exception ex) {
            Platform.runLater(() -> fileErrorDelegate.accept(ex.getMessage()));
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
