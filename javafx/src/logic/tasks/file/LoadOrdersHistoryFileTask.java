package logic.tasks.file;

import java.util.function.Consumer;

import course.java.sdm.engine.controller.ISDMController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public class LoadOrdersHistoryFileTask extends Task<Boolean> {

    private String fileName;
    private final ISDMController beController;
    private final Consumer<String> fileErrorDelegate;
    private final Consumer<Boolean> onFinishLoadingFile = (isSuccess) -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Loading orders history");
        String onFinishMessage = isSuccess ? "The orders history file: '%s' loaded successfully"
                    : "The loading of the orders history file: '%s' failed";
        alert.setContentText(String.format(onFinishMessage, fileName));
        alert.showAndWait();
    };

    public LoadOrdersHistoryFileTask (String fileName, ISDMController beController, Consumer<String> fileErrorDelegate) {
        this.fileName = fileName;
        this.beController = beController;
        this.fileErrorDelegate = fileErrorDelegate;

        this.setOnSucceeded(event -> onFinishLoadingFile.accept(this.getValue()));
        this.setOnFailed(event -> onFinishLoadingFile.accept(this.getValue()));
    }

    @Override
    protected Boolean call () {
        try {
            beController.loadOrdersHistoryFromFile(fileName);
        }
        catch (Exception ex) {
            Platform.runLater( () -> fileErrorDelegate.accept(ex.getMessage()));
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
