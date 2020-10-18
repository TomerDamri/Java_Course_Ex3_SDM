package course.java.sdm.engine.controller.impl;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Part;

import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.service.SDMService;
import course.java.sdm.engine.users.User;
import model.request.*;
import model.response.*;

public class SDMControllerImpl implements ISDMController {
    private SDMService service = new SDMService();

    @Override
    public void loadFile (Part part, UUID storesOwnerID) throws IOException {
        service.loadData(part, storesOwnerID);
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
        return service.placeStaticOrderV2(request);
    }

    @Override
    public boolean isValidLocation (final int xCoordinate, final int yCoordinate) {
        return service.isValidLocation(xCoordinate, yCoordinate);
    }

    @Override
    public GetMapEntitiesResponse getSystemMappableEntities () {
        return service.getSystemMappableEntities();
    }

    @Override
    public PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest request) {
        return service.placeDynamicOrderV2(request);
    }

    @Override
    public void completeDynamicOrder (UUID dynamicOrderId, boolean toConfirmNewDynamicOrder) {
        // service.completeDynamicOrder(dynamicOrderId, toConfirmNewDynamicOrder);
    }

    @Override
    public void saveOrdersHistoryToFile (String path) {
        service.saveOrdersHistoryToFile(path);
    }

    @Override
    public void loadSystemHistoryToFile (String path) {
        service.loadSystemHistoryToFile(path);
    }

    @Override
    public GetDiscountsResponse getDiscounts (UUID orderId) {
        return service.getOrderDiscounts(orderId);
    }

    @Override
    public FinalSummaryForOrder addDiscountsToOrder (AddDiscountsToOrderRequest request) {
        return service.addDiscountsToOrder(request);
    }

    @Override
    public void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder) {
        service.completeTheOrder(orderId, toConfirmNewDynamicOrder);
    }

    @Override
    public void addItemToStore (UpdateStoreRequest request) {
        service.addItemToStore(request);
    }

    @Override
    public void updatePriceOfSelectedItem (UpdateStoreRequest request) {
        service.updateItemPrice(request);
    }

    @Override
    public DeleteItemFromStoreResponse deleteItemFromStore (BaseUpdateStoreRequest request) {
        return service.deleteItemFromStore(request);
    }

    @Override
    public UUID addUserToSystem (String username, User.UserType userType) {
        return service.addUserToSystem(username, userType);
    }

    @Override
    public void removeUser (String username) {
        service.removeUser(username);
    }

    @Override
    public Set<User> getUsers () {
        return service.getUsers();
    }

    @Override
    public boolean isUserExists (String username) {
        return service.isUserExists(username);
    }

}