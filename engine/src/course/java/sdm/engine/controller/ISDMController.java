package course.java.sdm.engine.controller;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import course.java.sdm.engine.model.Discount;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.response.*;

public interface ISDMController {
    void loadFile (String filePath) throws FileNotFoundException;

    GetCustomersResponse getCustomers ();

    GetStoresResponse getStores ();

    GetItemsResponse getItems ();

    GetOrdersResponse getOrders ();

    PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request);

    Map<Integer, Map<Integer, List<Discount>>> getDiscounts(UUID orderId);

    boolean isFileLoaded ();

    boolean isValidLocation (final int xCoordinate, final int yCoordinate);

    PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request);

    void completeDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder);

    void saveOrdersHistoryToFile (String path);

    void loadOrdersHistoryFromFile (String path);
}