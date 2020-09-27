package logic.tasks.file;

import java.util.function.Consumer;

import course.java.sdm.engine.controller.ISDMController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

public class SaveOrdersHistoryFileTask extends Task<Boolean> {

    private String filePath;
    private final ISDMController beController;
    private final Consumer<String> errorConsumer;

    private final Runnable savingCompletedRunnable = () -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Saving orders history");
        alert.setContentText(String.format("The orders history file: '%s' saved successfully", filePath));
        alert.showAndWait();
    };

    public SaveOrdersHistoryFileTask (String filePath, ISDMController beController, Consumer<String> errorConsumer, Runnable onFinish) {
        this.filePath = filePath;
        this.beController = beController;
        this.errorConsumer = errorConsumer;

        EventHandler<WorkerStateEvent> workerStateEventEventHandler = event -> {
            onFinish.run();
            savingCompletedRunnable.run();
        };
        this.setOnSucceeded(workerStateEventEventHandler);
        this.setOnFailed(workerStateEventEventHandler);
        this.setOnCancelled(workerStateEventEventHandler);
    }

    @Override
    protected Boolean call () {
        try {
            if (filePath == null) {
                Platform.runLater( () -> errorConsumer.accept("You should enter path before saving orders history"));
                return Boolean.FALSE;
            }

            beController.saveOrdersHistoryToFile(filePath);
            return Boolean.TRUE;
        }
        catch (Exception ex) {
            Platform.runLater( () -> errorConsumer.accept(ex.getMessage()));
            return Boolean.FALSE;
        }

    }

}
