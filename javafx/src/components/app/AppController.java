package components.app;

import components.mapComponent.MapController;
import components.placeOrderComponent.PlaceOrderController;
import components.sdmComponent.SDMController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class AppController {

    @FXML
    private ScrollPane headerComponent;
    @FXML private PlaceOrderController placeOrderComponentController;
    @FXML private BorderPane sdmComponent;
    @FXML private SDMController sdmComponentController;
    @FXML private MapController mapComponentController;

    @FXML
    public void initialize() {
        if (placeOrderComponentController != null && sdmComponentController != null && mapComponentController != null) {
            placeOrderComponentController.setMainController(this);
            sdmComponentController.setMainController(this);
            mapComponentController.setMainController(this);
        }
    }

    public void setSdmComponentController(SDMController sdmComponentController) {
        this.sdmComponentController = sdmComponentController;
        sdmComponentController.setMainController(this);
    }

    public void setPlaceOrderComponentController(PlaceOrderController placeOrderComponentController) {
        this.placeOrderComponentController = placeOrderComponentController;
        placeOrderComponentController.setMainController(this);
    }

    public void setMapComponentController(MapController mapComponentController) {
        this.mapComponentController = mapComponentController;
        mapComponentController.setMainController(this);
    }



}
