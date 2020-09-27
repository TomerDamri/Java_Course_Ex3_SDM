package logic;

import java.util.UUID;
import java.util.function.Consumer;

import components.app.AppController;
import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import javafx.concurrent.Task;
import javafx.scene.layout.GridPane;
import logic.tasks.file.CreateMapTask;
import logic.tasks.file.LoadFileTask;
import model.request.*;
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

    public void createMap (Consumer<GridPane> mapConsumer) {
        currentRunningTask = new CreateMapTask(beController, mapConsumer);
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

    public PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request) {
        return beController.placeDynamicOrder(request);
    }

    public GetDiscountsResponse getDiscounts (UUID orderId) {
        return beController.getDiscounts(orderId);
    }

    public PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request) {
        return beController.placeStaticOrder(request);
    }

    public FinalSummaryForOrder addDiscountsToOrder (AddDiscountsToOrderRequest request) {
        return beController.addDiscountsToOrder(request);
    }

    public void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder) {
        beController.completeTheOrder(orderId, toConfirmNewDynamicOrder);
    }

    public void addItemToStore (UpdateStoreRequest request) {
        beController.addItemToStore(request);
    }

    public void updatePriceOfSelectedItem (UpdateStoreRequest request) {
        beController.updatePriceOfSelectedItem(request);
    }

    public DeleteItemFromStoreResponse deleteItemFromStore (BaseUpdateStoreRequest request) {
        return beController.deleteItemFromStore(request);
    }

}
