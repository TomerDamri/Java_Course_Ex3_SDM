package course.java.sdm.engine.controller;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Part;

import model.User;
import model.request.*;
import model.response.*;

public interface ISDMController {

    // TODO: 29/10/2020
    // load / add new data to system
    void loadFile (Part part, UUID storesOwnerID) throws IOException;

    void addStoreToZone (AddStoreToZoneRequest request);

    void saveOrdersHistoryToFile (String path);

    void loadSystemHistoryToFile (String path);

    // load / add new data to system

    // TODO: 29/10/2020
    // Place order

    PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request);

    PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request);

    GetDiscountsResponse getDiscounts (UUID orderId);

    FinalSummaryForOrder addDiscountsToOrder (AddDiscountsToOrderRequest request);

    void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder);

    // Place order

    // TODO: 29/10/2020
    // add/delete/update item in store

    void addItemToStore (UpdateStoreRequest request);

    DeleteItemFromStoreResponse deleteItemFromStore (BaseUpdateStoreRequest request);

    void updatePriceOfSelectedItem (UpdateStoreRequest request);

    // add/delete/update item in store

    // TODO: 29/10/2020
    // users

    UUID addUserToSystem (String username, User.UserType userType);

    public void removeUser (String username);

    public Set<User> getUsers ();

    public boolean isUserExists (String username);

    // users

    // TODO: 29/10/2020
    // bank account
    public GetUserTransactionsResponse getUserTransactions (GetUserTransactionsRequest request);

    public void deposit (DepositRequest request);

    public GetUserBalanceResponse getUserBalance (GetUserBalanceRequest request);

    // bank account

    // TODO: 29/10/2020
    // feedbacks

    public void rankOrderStores (RankOrderStoresRequest request);

    public GetFeedbackForStoreOwnerResponse getFeedbackForStoreOwner (GetFeedbackForStoreOwnerRequest request);

    // feedbacks

    // TODO: 29/10/2020
    // getters
    GetCustomersResponse getCustomers ();

    GetCustomerOrdersResponse getCustomerOrders (GetCustomerOrdersRequest request);

    public GetZonesResponse getZones ();

    public GetZoneResponse getZone (String zoneName);

    public GetStoreItemsResponse getStoreItems (GetStoreItemsRequest request);

    boolean isFileLoaded ();

    boolean isValidLocation (final int xCoordinate, final int yCoordinate);

    // getters
}