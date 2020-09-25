package components.app;

import components.mapComponent.MapController;
import components.placeOrderComponent.PlaceOrderController;
import components.sdmComponent.SDMController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class AppController {

    private PlaceOrderController placeOrderComponentController;
    private BorderPane sdmComponent;
    private SDMController sdmComponentController;
    private MapController mapComponentController;

    @FXML
    public void initialize () {
        if (placeOrderComponentController != null && sdmComponentController != null && mapComponentController != null) {
            placeOrderComponentController.setMainController(this);
            sdmComponentController.setMainController(this);
            mapComponentController.setMainController(this);
        }
    }

    public void setSdmComponentController (SDMController sdmComponentController) {
        this.sdmComponentController = sdmComponentController;
        sdmComponentController.setMainController(this);
    }

    public void setPlaceOrderComponentController (PlaceOrderController placeOrderComponentController) {
        this.placeOrderComponentController = placeOrderComponentController;
        placeOrderComponentController.setMainController(this);
    }

    public void setMapComponentController (MapController mapComponentController) {
        this.mapComponentController = mapComponentController;
        mapComponentController.setMainController(this);
    }

    public void bindTaskToUIComponents (Task<Boolean> aTask, Runnable onFinish) {
    sdmComponentController.bindTaskToUIComponents(aTask, onFinish);
    }
}
