package course.java.sdm.engine.controller;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Part;

import model.User;
import model.request.*;
import model.response.*;

public interface ISDMController {
    // void loadFile (String filePath) throws FileNotFoundException;
    void loadFile (Part part, UUID storesOwnerID) throws IOException;

    GetCustomersResponse getCustomers ();

    // GetStoresResponse getStores ();

    // GetItemsResponse getItems ();

    // GetOrdersResponse getOrders ();

    PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request);

    GetDiscountsResponse getDiscounts (UUID orderId);

    FinalSummaryForOrder addDiscountsToOrder (AddDiscountsToOrderRequest request);

    void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder);

    boolean isFileLoaded ();

    boolean isValidLocation (final int xCoordinate, final int yCoordinate);

    // GetMapEntitiesResponse getSystemMappableEntities ();

    PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request);

    void completeDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder);

    void saveOrdersHistoryToFile (String path);

    void loadSystemHistoryToFile (String path);

    void addItemToStore (UpdateStoreRequest request);

    DeleteItemFromStoreResponse deleteItemFromStore (BaseUpdateStoreRequest request);

    void updatePriceOfSelectedItem (UpdateStoreRequest request);

    UUID addUserToSystem (String username, User.UserType userType);

    public void removeUser (String username);

    public Set<User> getUsers ();

    public boolean isUserExists (String username);

    public GetZonesResponse getZones ();

    public GetZoneResponse getZone (String zoneName);

    public void deposit (DepositRequest request);

    public GetUserBalanceResponse getUserBalance (GetUserBalanceRequest request);

    public GetUserTransactionsResponse getUserTransactions(GetUserTransactionsRequest request);
}