package course.java.sdm.engine.controller;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.UUID;

import model.request.AddDiscountsToOrderRequest;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.request.StoreValidDiscounts;
import model.response.*;

public interface ISDMController {
    void loadFile (String filePath) throws FileNotFoundException;

    GetCustomersResponse getCustomers ();

    GetStoresResponse getStores ();

    GetItemsResponse getItems ();

    GetOrdersResponse getOrders ();

    PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request);

    Map<Integer, StoreValidDiscounts> getDiscounts (UUID orderId);

    void addDiscountsToOrder (AddDiscountsToOrderRequest request);

    void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder);

    boolean isFileLoaded ();

    boolean isValidLocation (final int xCoordinate, final int yCoordinate);

    GetSystemMappableEntitiesResponse getSystemMappableEntities();

    PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request);

    void completeDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder);

    void saveOrdersHistoryToFile (String path);

    void loadOrdersHistoryFromFile (String path);
}