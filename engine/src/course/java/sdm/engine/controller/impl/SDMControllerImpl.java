package course.java.sdm.engine.controller.impl;

import java.io.FileNotFoundException;
import java.util.UUID;

import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.service.SDMService;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
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
        return service.placeStaticOrder(request);
    }

    @Override
    public boolean isValidLocation (final int xCoordinate, final int yCoordinate) {
        return service.isValidLocation(xCoordinate, yCoordinate);
    }

    @Override
    public PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request) {
        return service.placeDynamicOrder(request);
    }

    @Override
    public void completeDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder) {
        service.completeDynamicOrder(dynamicOrderId, toConfirmNewDynamicOrder);
    }

    @Override
    public void saveOrdersHistoryToFile (String path) {
        service.saveOrdersHistoryToFile(path);
    }

    @Override
    public void loadOrdersHistoryFromFile (String path) {
        service.loadDataFromFile(path);
    }
}