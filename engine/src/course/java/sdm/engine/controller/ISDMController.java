package course.java.sdm.engine.controller;

import model.request.*;
import model.response.*;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.UUID;

public interface ISDMController {
    //    void loadFile (String filePath) throws FileNotFoundException;
    void loadFile(Part part) throws IOException;


    GetCustomersResponse getCustomers();

    GetStoresResponse getStores();

    GetItemsResponse getItems();

    GetOrdersResponse getOrders();

    PlaceOrderResponse placeStaticOrder(PlaceOrderRequest request);

    GetDiscountsResponse getDiscounts(UUID orderId);

    FinalSummaryForOrder addDiscountsToOrder(AddDiscountsToOrderRequest request);

    void completeTheOrder(UUID orderId, boolean toConfirmNewDynamicOrder);

    boolean isFileLoaded();

    boolean isValidLocation(final int xCoordinate, final int yCoordinate);

    GetMapEntitiesResponse getSystemMappableEntities();

    PlaceDynamicOrderResponse placeDynamicOrder(PlaceDynamicOrderRequest request);

    void completeDynamicOrder(UUID dynamicOrderId, boolean toConfirmNewDynamicOrder);

    void saveOrdersHistoryToFile(String path);

    void loadOrdersHistoryFromFile(String path);

    void addItemToStore(UpdateStoreRequest request);

    DeleteItemFromStoreResponse deleteItemFromStore(BaseUpdateStoreRequest request);

    void updatePriceOfSelectedItem(UpdateStoreRequest request);
}