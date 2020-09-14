package course.java.sdm.engine.controller.impl;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.service.SDMService;
import model.request.AddDiscountsToOrderRequest;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.request.StoreValidDiscounts;
import model.response.*;

public class SDMControllerImpl implements ISDMController {
    private SDMService service = new SDMService();

    @Override
    public void loadFile (String filePath) throws FileNotFoundException {
        service.loadData(filePath);
    }

    @Override
    public boolean isFileLoaded () {
        return service.isFileLoaded();
    }

    @Override
    public GetCustomersResponse getCustomers () {
        return service.getCustomers();
    }

    @Override
    public GetStoresResponse getStores () {
        return service.getStores();
    }

    @Override
    public GetItemsResponse getItems () {
        return service.getItems();
    }

    @Override
    public GetOrdersResponse getOrders () {
        return service.getOrders();
    }

    @Override
    public PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request) {
        // return service.placeStaticOrder(request);
        PlaceOrderResponse placeOrderResponse = service.placeStaticOrderV2(request);
        // Map<Integer, StoreValidDiscounts> orderDiscounts =
        // service.getOrderDiscounts(placeOrderResponse.getOrderId());
        return placeOrderResponse;
    }

    @Override
    public boolean isValidLocation (final int xCoordinate, final int yCoordinate) {
        return service.isValidLocation(xCoordinate, yCoordinate);
    }

    @Override
    public PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request) {
        // return service.placeDynamicOrder(request);
        return service.placeDynamicOrderV2(request);
    }

    @Override
    public void completeDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder) {
//        service.completeDynamicOrder(dynamicOrderId, toConfirmNewDynamicOrder);
    }

    @Override
    public void saveOrdersHistoryToFile (String path) {
        service.saveOrdersHistoryToFile(path);
    }

    @Override
    public void loadOrdersHistoryFromFile (String path) {
        service.loadOrdersHistoryFromFile(path);
    }

    public Map<Integer, StoreValidDiscounts> getDiscounts (UUID orderId) {
        return service.getOrderDiscounts(orderId);
    }

    @Override
    public void addDiscountsToOrder (AddDiscountsToOrderRequest request) {
        service.addDiscountsToOrder(request);
    }

    @Override
    public void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder) {
        service.completeTheOrder(orderId, toConfirmNewDynamicOrder);
    }
}