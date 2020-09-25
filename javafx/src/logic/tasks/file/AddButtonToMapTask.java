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

public class AddButtonToMapTask extends Task<Boolean> {

    private final ISDMController beController;
    private final Consumer<GridPane> addButtonConsumer;

    public AddButtonToMapTask (ISDMController beController, Consumer<GridPane> addButtonConsumer) {
        this.beController = beController;
        this.addButtonConsumer = addButtonConsumer;
    }

    @Override
    protected Boolean call () throws Exception {
        GetMapEntitiesResponse systemMappableEntities = beController.getSystemMappableEntities();
        List<MapEntity> mapEntities = systemMappableEntities.getAllSystemMappableEntities();

        // TODO: 21/09/2020 - get max X and max Y
        LocationDTO maxLocationOnMap = calculateMaxLocationOnMap(mapEntities);
        // TODO: 21/09/2020 - set the first line and column to appropriate number
        GridPane newMap = new GridPane();

        // for (int rowIndex = 0; rowIndex <= maxLocationOnMap.getxCoordinate(); rowIndex++) {
        // RowConstraints rc = new RowConstraints();
        // rc.setVgro); // allow row to grow
        // rc.setFillHeight(true); // ask nodes to fill height for row
        // // other settings as needed...
        // newMap.getRowConstraints().add(rc);
        // }
        // for (int colIndex = 0; colIndex <= maxLocationOnMap.getyCoordinate(); colIndex++) {
        // ColumnConstraints cc = new ColumnConstraints();
        // cc.setHgrow(Priority.ALWAYS); // allow column to grow
        // cc.setFillWidth(true); // ask nodes to fill space for column
        // // other settings as needed...
        // newMap.getColumnConstraints().add(cc);
        // }

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
            newMap.add(button, location.getxCoordinate() + 1, location.getyCoordinate() + 1);
        });

        Platform.runLater( () -> addButtonConsumer.accept(newMap));
        return Boolean.TRUE;
    }

//    private Button createEmptyButton () {
//        Button button = new Button();
//        button.setDisable(true);
//        button.prefHeight(50);
//        button.prefWidth(50);
//
//        return button;
//    }

    private Button createButtonForMapEntity (MapEntity mapEntity) {
        Button button = new Button(mapEntity.getName());
        button.setId(Integer.toString(mapEntity.getId()));
        button.setPrefWidth(120);
//        if(mapEntity)
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
