package components.mapComponent;

import components.app.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import model.LocationDTO;
import model.MapEntity;
import model.StoreMapEntityDTO;

import java.util.List;

public class MapController {

    public static final String STORE_IMAGE_PATH = "resources/store.jpg";
    public static final String CUSTOMER_IMAGE_PATH = "resources/customer.jpg";

    private AppController mainController;
    @FXML
    private GridPane mapGridPane;

    public void setLocationsOnMap (List<MapEntity> mapEntities) {
        // TODO: 21/09/2020 - get max X and max Y?
        LocationDTO maxLocationOnMap = calculateMaxLocationOnMap(mapEntities);
        // TODO: 21/09/2020 - set the first line and column to appropriate number
        mapEntities.forEach(mapEntity -> {
            Button button = new Button(mapEntity.getName());
            button.setId(Integer.toString(mapEntity.getId()));
            // ImageView view = getButtonImageView(mapEntity);
            // button.setGraphic(view);

            LocationDTO location = mapEntity.getLocation();
            mapGridPane.add(button, location.getxCoordinate() + 1, location.getyCoordinate() + 1);

            button.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Map entity information");
                alert.setHeaderText(button.getText());
                alert.setContentText(mapEntity.toString());
                alert.showAndWait();
            });

        });

    }

    public void setMainController(AppController mainController) {
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
        mapGridPane.setGridLinesVisible(true);
        return mapGridPane;
    }
}

