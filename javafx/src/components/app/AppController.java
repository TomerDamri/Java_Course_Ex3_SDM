package components.app;

import java.util.function.Consumer;

import components.mapComponent.MapController;
import components.placeOrderComponent.PlaceOrderController;
import components.sdmComponent.SDMController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import logic.BusinessLogic;
import model.response.GetCustomersResponse;
import model.response.GetItemsResponse;
import model.response.GetOrdersResponse;
import model.response.GetStoresResponse;

public class AppController {

    private PlaceOrderController placeOrderComponentController;
    private ScrollPane sdmComponent;
    private SDMController sdmComponentController;
    private MapController mapComponentController;
    private BusinessLogic businessLogic;

    private BorderPane placeOrderPane;

    public void setMainBorderPane (BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    private BorderPane mainBorderPane;

    private ScrollPane mapScrollPane;

    private GridPane mapGridPane;

    private ScrollPane displayInfoScrollPane;

    public void setDisplayInfoScrollPane (javafx.scene.control.ScrollPane displayInfoScrollPane) {
        this.displayInfoScrollPane = displayInfoScrollPane;
    }

    public void setPlaceOrderPane (BorderPane placeOrderPane) {
        this.placeOrderPane = placeOrderPane;
    }

    public void setMapScrollPane (javafx.scene.control.ScrollPane mapScrollPane) {
        this.mapScrollPane = mapScrollPane;
    }

    public void setMapGridPane (GridPane mapGridPane) {
        this.mapGridPane = mapGridPane;
    }

    public void setCenterToDisplayInfoScrollPane () {
        mainBorderPane.setCenter(displayInfoScrollPane);
    }

    public void setCenterToPlaceOrderPane () {
        mainBorderPane.setCenter(placeOrderPane);
    }

    @FXML
    public void initialize () {
        if (placeOrderComponentController != null && sdmComponentController != null && mapComponentController != null) {
            placeOrderComponentController.setMainController(this);
            sdmComponentController.setMainController(this);
            mapComponentController.setMainController(this);
        }
    }

    public void setBusinessLogic (BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
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

    public void loadFile (String filePath, Consumer<String> fileErrorDelegate, Runnable onFinish) {
        businessLogic.loadFile(filePath, fileErrorDelegate, onFinish);
    }

    public void createMap () {
        Consumer<GridPane> addButtonConsumer = gridPane -> mapGridPane.getChildren().addAll(gridPane.getChildren());
        businessLogic.createMap(addButtonConsumer);
        mainBorderPane.setCenter(mapScrollPane);
    }

    public GetCustomersResponse getCustomers () {
        return businessLogic.getCustomers();
    }

    public GetStoresResponse getStores () {
        return businessLogic.getStores();
    }

    public GetOrdersResponse getOrders () {
        return businessLogic.getOrders();
    }

    public GetItemsResponse getItems () {
        return businessLogic.getItems();
    }
}
