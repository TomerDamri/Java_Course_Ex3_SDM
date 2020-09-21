package components.mapComponent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import components.app.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.LocationDTO;
import model.MapEntity;
import model.StoreMapEntityDTO;

public class MapController {

    public static final String STORE_IMAGE_PATH = "resources/store.jpg";
    public static final String CUSTOMER_IMAGE_PATH = "resources/customer.jpg";

    private AppController mainController;

    @FXML
    private ScrollPane mapPane;

    @FXML
    private GridPane locationsGridPane;

    public GridPane getLocationsGridPane() {
        return locationsGridPane;
    }

    public void setLocationsOnMap (List<MapEntity> mapEntities) {
        // TODO: 21/09/2020 - get max X and max Y?
        LocationDTO maxLocationOnMap = calculateMaxLocationOnMap(mapEntities);
        // TODO: 21/09/2020 - set the first line and column to appropriate number

        Map<LocationDTO, MapEntity> locationToMapEntity = mapEntities.stream()
                                                                     .collect(Collectors.toMap(MapEntity::getLocation,
                                                                                               mapEntity -> mapEntity));

        // for (int rowIndex = 0; rowIndex <= maxLocationOnMap.getxCoordinate(); rowIndex++) {
        // RowConstraints rc = new RowConstraints();
        // rc.setVgrow(Priority.ALWAYS); // allow row to grow
        // rc.setFillHeight(true); // ask nodes to fill height for row
        // // other settings as needed...
        // mapGridPane.getRowConstraints().add(rc);
        // }
        // for (int colIndex = 0; colIndex <= maxLocationOnMap.getyCoordinate(); colIndex++) {
        // ColumnConstraints cc = new ColumnConstraints();
        // cc.setHgrow(Priority.ALWAYS); // allow column to grow
        // cc.setFillWidth(true); // ask nodes to fill space for column
        // // other settings as needed...
        // mapGridPane.getColumnConstraints().add(cc);
        // }

        for (int i = 0; i <= maxLocationOnMap.getxCoordinate(); i++) {
            for (int j = 0; j < maxLocationOnMap.getyCoordinate(); i++) {
                LocationDTO currLocation = new LocationDTO(i, j);
                Button button = (locationToMapEntity.containsKey(currLocation))
                            ? createButtonForMapEntity(locationToMapEntity, currLocation)
                            : createEmptyButton();

                locationsGridPane.add(button, i + 1, j + 1);
            }
        }

        // mapEntities.forEach(mapEntity -> {
        // Button button = new Button(mapEntity.getName());
        // button.setId(Integer.toString(mapEntity.getId()));
        // // ImageView view = getButtonImageView(mapEntity);
        // // button.setGraphic(view);
        //
        // LocationDTO location = mapEntity.getLocation();
        // mapGridPane.add(button, location.getxCoordinate() + 1, location.getyCoordinate() + 1);
        //
        // button.setOnAction(event -> {
        // Alert alert = new Alert(Alert.AlertType.INFORMATION);
        // alert.setTitle("Map entity information");
        // alert.setHeaderText(button.getText());
        // alert.setContentText(mapEntity.toString());
        // alert.showAndWait();
        // });
        //
        // });

    }

    private Button createEmptyButton () {
        Button button = new Button();
        button.setDisable(true);
        button.prefHeight(50);
        button.prefWidth(50);

        return button;
    }

    private Button createButtonForMapEntity (Map<LocationDTO, MapEntity> locationToMapEntity, LocationDTO currLocation) {
        MapEntity mapEntity = locationToMapEntity.get(currLocation);
        Button button = new Button(mapEntity.getName());
        button.setId(Integer.toString(mapEntity.getId()));
        // ImageView view = getButtonImageView(mapEntity);
        // button.setGraphic(view);

        button.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Map entity information");
            alert.setHeaderText(button.getText());
            alert.setContentText(mapEntity.toString());
            alert.showAndWait();
        });

        return button;
    }

    public void setMainController (AppController mainController) {
        this.mainController = mainController;
    }

    private LocationDTO calculateMaxLocationOnMap (List<MapEntity> mapEntities) {
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

    private ImageView getButtonImageView (MapEntity mapEntity) {
        String imagePath = mapEntity.getClass().getSimpleName().equals(StoreMapEntityDTO.class.getSimpleName()) ? STORE_IMAGE_PATH
                    : CUSTOMER_IMAGE_PATH;
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        ImageView view = new ImageView(image);
        return view;
    }

    public Pane getMap () {
        locationsGridPane.setGridLinesVisible(true);
        return locationsGridPane;
    }
}
