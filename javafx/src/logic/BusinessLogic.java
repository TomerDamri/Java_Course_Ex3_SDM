package logic;

import components.sdm.SDMController;
import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import javafx.concurrent.Task;
import logic.tasks.file.LoadFileTask;
import model.request.PlaceOrderRequest;
import model.response.*;

import java.util.function.Consumer;

public class BusinessLogic {
    private final SDMController feController;
    private final ISDMController beController = new SDMControllerImpl();
    private Task<Boolean> currentRunningTask;

    public BusinessLogic(SDMController controller) {
        this.feController = controller;
    }

    public void loadFile(String filePath, Consumer<String> fileErrorDelegate, Runnable onFinish) {
        currentRunningTask = new LoadFileTask(filePath, beController, fileErrorDelegate);
        feController.bindTaskToUIComponents(currentRunningTask, onFinish);
        new Thread(currentRunningTask).start();
    }

    public GetCustomersResponse getCustomers() {
        return beController.getCustomers();
    }

    public GetStoresResponse getStores() {
        return beController.getStores();
    }

    public GetItemsResponse getItems() {
        return beController.getItems();
    }

    public GetOrdersResponse getOrders() {
        return beController.getOrders();
    }

    PlaceOrderResponse placeStaticOrder(PlaceOrderRequest request){
        return beController.placeStaticOrder(request);
    }
//
//    GetDiscountsResponse getDiscounts(UUID orderId);
//
//    void addDiscountsToOrder(AddDiscountsToOrderRequest request);
//
//    void completeTheOrder(UUID orderId, boolean toConfirmNewDynamicOrder);
//
//    boolean isFileLoaded();
//
//    boolean isValidLocation(final int xCoordinate, final int yCoordinate);
//
//    GetSystemMappableEntitiesResponse getSystemMappableEntities();
//
//    PlaceDynamicOrderResponse placeDynamicOrder(PlaceDynamicOrderRequest request);
//
//    void completeDynamicOrder(UUID dynamicOrderId, boolean toConfirmNewDynamicOrder);
//
//    void saveOrdersHistoryToFile(String path);
//
//    void loadOrdersHistoryFromFile(String path);


}
