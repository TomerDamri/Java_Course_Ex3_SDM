package logic.tasks.file;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import course.java.sdm.engine.controller.ISDMController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import model.LocationDTO;
import model.MapEntity;
import model.response.GetMapEntitiesResponse;

public class CreateMapTask extends Task<Boolean> {

    private final ISDMController beController;
    private final Consumer<GridPane> mapConsumer;

    public CreateMapTask (ISDMController beController, Consumer<GridPane> mapConsumer) {
        this.beController = beController;
        this.mapConsumer = mapConsumer;
    }

    @Override
    protected Boolean call () {
        GetMapEntitiesResponse systemMappableEntities = beController.getSystemMappableEntities();
        List<MapEntity> mapEntities = systemMappableEntities.getAllSystemMappableEntities();

        LocationDTO maxLocationOnMap = calculateMaxLocationOnMap(mapEntities);
        GridPane newMap = new GridPane();

        for (int i = 1; i <= maxLocationOnMap.getxCoordinate(); i++) {
            String buttonText = Integer.toString(i);
            Button hButton = new Button(buttonText);
            hButton.setPrefWidth(120);
            newMap.add(hButton, i, 0);
        }

        for (int i = 1; i <= maxLocationOnMap.getyCoordinate(); i++) {
            String buttonText = Integer.toString(i);
            Button vButton = new Button(buttonText);
            vButton.setPrefWidth(120);
            newMap.add(vButton, 0, i);
        }

        mapEntities.forEach(mapEntity -> {
            Button button = createButtonForMapEntity(mapEntity);
            LocationDTO location = mapEntity.getLocation();
            newMap.add(button, location.getxCoordinate(), location.getyCoordinate());
        });

        Platform.runLater( () -> mapConsumer.accept(newMap));
        return Boolean.TRUE;
    }

    private Button createButtonForMapEntity (MapEntity mapEntity) {
        Button button = new Button(mapEntity.getName());
        button.setId(Integer.toString(mapEntity.getId()));
        button.setPrefWidth(120);

        button.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Map entity information");
            alert.setHeaderText(button.getText());
            alert.setContentText(mapEntity.toString());
            alert.showAndWait();
        });

        return button;
    }

    private LocationDTO calculateMaxLocationOnMap (Collection<MapEntity> mapEntities) {
        Integer maxX = null;
        Integer maxY = null;

        for (MapEntity mapEntity : mapEntities) {
            LocationDTO currLocation = mapEntity.getLocation();
            if (maxX == null || currLocation.getxCoordinate() > maxX) {
                maxX = currLocation.getxCoordinate();
            }

            if (maxY == null || currLocation.getyCoordinate() > maxY) {
                maxY = currLocation.getyCoordinate();
            }
        }

        return new LocationDTO(maxX, maxY);
    }

}
