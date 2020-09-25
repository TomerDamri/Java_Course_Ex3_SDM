package logic;

import java.util.function.Consumer;

import components.app.AppController;
import components.sdmComponent.SDMController;
import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import logic.tasks.file.AddButtonToMapTask;
import logic.tasks.file.LoadFileTask;
import model.request.PlaceOrderRequest;
import model.response.*;

public class BusinessLogic {
    private final AppController feController;
    private final ISDMController beController = new SDMControllerImpl();
    private Task<Boolean> currentRunningTask;

    public BusinessLogic (AppController controller) {
        this.feController = controller;
    }

    public void loadFile (String filePath, Consumer<String> fileErrorDelegate, Runnable onFinish) {
        currentRunningTask = new LoadFileTask(filePath, beController, fileErrorDelegate);
        feController.bindTaskToUIComponents(currentRunningTask, onFinish);
        new Thread(currentRunningTask).start();
    }

    public void createMap (Consumer<GridPane> addButtonConsumer) {
        currentRunningTask = new AddButtonToMapTask(beController, addButtonConsumer);
        new Thread(currentRunningTask).start();
    }

    public GetCustomersResponse getCustomers () {
        return beController.getCustomers();
    }

    public GetStoresResponse getStores () {
        return beController.getStores();
    }

    public GetItemsResponse getItems () {
        return beController.getItems();
    }

    public GetOrdersResponse getOrders () {
        return beController.getOrders();
    }

    public PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request) {
        return beController.placeStaticOrder(request);
    }

    //
    // GetDiscountsResponse getDiscounts(UUID orderId);
    //
    // void addDiscountsToOrder(AddDiscountsToOrderRequest request);
    //
    // void completeTheOrder(UUID orderId, boolean toConfirmNewDynamicOrder);
    //
    // boolean isFileLoaded();
    //
    // boolean isValidLocation(final int xCoordinate, final int yCoordinate);
    //
    //
    // PlaceDynamicOrderResponse placeDynamicOrder(PlaceDynamicOrderRequest request);
    //
    // void completeDynamicOrder(UUID dynamicOrderId, boolean toConfirmNewDynamicOrder);
    //
    // void saveOrdersHistoryToFile(String path);
    //
    // void loadOrdersHistoryFromFile(String path);

}
