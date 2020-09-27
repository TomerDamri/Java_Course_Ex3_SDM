package components.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import components.editItemsComponent.EditItemsController;
import components.mapComponent.MapController;
import components.placeOrderComponent.PlaceOrderController;
import components.sdmComponent.SDMController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import logic.BusinessLogic;
import model.*;
import model.request.*;
import model.response.*;

public class AppController {

    private SDMController sdmComponentController;
    private PlaceOrderController placeOrderComponentController;
    private EditItemsController editItemsController;

    private MapController mapComponentController;
    private BusinessLogic businessLogic;

    private BorderPane placeOrderPane;

    private BorderPane mainBorderPane;

    private ScrollPane mapScrollPane;

    private GridPane mapGridPane;

    private ScrollPane displayInfoScrollPane;
    private VBox editItemsComponent;

    public void setMainBorderPane (BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    public void setDisplayInfoScrollPane (javafx.scene.control.ScrollPane displayInfoScrollPane) {
        this.displayInfoScrollPane = displayInfoScrollPane;
    }

    public void setPlaceOrderPane (BorderPane placeOrderPane) {
        this.placeOrderPane = placeOrderPane;
    }

    public void setMapScrollPane (javafx.scene.control.ScrollPane mapScrollPane) {
        this.mapScrollPane = mapScrollPane;
    }

    public void setMapGridPane (GridPane mapGridPane) {
        this.mapGridPane = mapGridPane;
    }

    public void setCenterToDisplayInfoScrollPane () {
        mainBorderPane.setCenter(displayInfoScrollPane);
    }

    @FXML
    public void initialize () {
        if (placeOrderComponentController != null && sdmComponentController != null && mapComponentController != null) {
            placeOrderComponentController.setMainController(this);
            sdmComponentController.setMainController(this);
            mapComponentController.setMainController(this);
        }
    }

    public void setBusinessLogic (BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    public void setSdmComponentController (SDMController sdmComponentController) {
        this.sdmComponentController = sdmComponentController;
        sdmComponentController.setMainController(this);
    }

    public void setPlaceOrderComponentController (PlaceOrderController placeOrderComponentController) {
        this.placeOrderComponentController = placeOrderComponentController;
        placeOrderComponentController.setMainController(this);
    }

    public void setEditItemsController (EditItemsController editItemsController) {
        this.editItemsController = editItemsController;
    }

    public void setMapComponentController (MapController mapComponentController) {
        this.mapComponentController = mapComponentController;
        mapComponentController.setMainController(this);
    }

    public void bindTaskToUIComponents (Task<Boolean> aTask, Runnable onFinish) {
        sdmComponentController.bindTaskToUIComponents(aTask, onFinish);
    }

    public void loadFile (String filePath, Consumer<String> fileErrorDelegate, Runnable onFinish) {
        businessLogic.loadFile(filePath, fileErrorDelegate, onFinish);
        mainBorderPane.setCenter(null);
    }

    public void loadOrdersHistoryFile (String filePath, Consumer<String> fileErrorDelegate) {
        businessLogic.loadOrdersHistoryFile(filePath, fileErrorDelegate);
    }

    public void saveOrdersHistoryFile (String filePath, Consumer<String> errorDelegate, Runnable onFinish) {
        businessLogic.saveOrdersHistoryFile(filePath, errorDelegate, onFinish);
    }

    public void createMap () {
        Consumer<GridPane> mapConsumer = gridPane -> {
            mapGridPane.getChildren().addAll(gridPane.getChildren());
            mapScrollPane.setContent(mapGridPane);
        };
        mainBorderPane.setCenter(mapScrollPane);
        businessLogic.createMap(mapConsumer);
    }

    public GetCustomersResponse getCustomers () {
        return businessLogic.getCustomers();
    }

    public GetStoresResponse getStores () {
        return businessLogic.getStores();
    }

    public GetOrdersResponse getOrders () {
        return businessLogic.getOrders();
    }

    public GetItemsResponse getItems () {
        return businessLogic.getItems();
    }

    public void handlePlaceOrder () {
        placeOrderComponentController.resetPlaceOrderComponent();
        mainBorderPane.setCenter(placeOrderPane);
        setCustomersList();
    }

    private void setCustomersList () {
        GetCustomersResponse customersResponse = businessLogic.getCustomers();
        List<CustomerDTO> customers = new ArrayList<>(customersResponse.getSystemCustomers().values());
        ObservableList<String> customersObservableList = FXCollections.observableArrayList();
        customers.forEach(customerDTO -> {
            customersObservableList.add(customerDTO.toString());
        });
        placeOrderComponentController.setCustomersList(customersObservableList);
    }

    public ObservableList<String> getStoresList () {
        GetStoresResponse storesResponse = businessLogic.getStores();
        List<StoreDTO> stores = new ArrayList<>(storesResponse.getStores().values());
        ObservableList<String> storesObservableList = FXCollections.observableArrayList();
        stores.forEach(storeDTO -> {
            storesObservableList.add(storeDTO.toString());
        });
        return storesObservableList;
    }

    public ObservableList<String> getItemsInStoreObservableList (int storeId) {
        GetStoresResponse storesResponse = businessLogic.getStores();
        StoreDTO store = storesResponse.getStores().get(storeId);
        ObservableList<String> itemsInStoreObservableList = FXCollections.observableArrayList();
        store.getItems().values().forEach(item -> {
            itemsInStoreObservableList.add(String.format("Id: %s Name: %s", item.getId(), item.getName()));
        });

        return itemsInStoreObservableList;
    }

    public ObservableList<String> getItemsNotInStoreObservableList (int storeId) {
        GetStoresResponse storesResponse = businessLogic.getStores();
        StoreDTO store = storesResponse.getStores().get(storeId);
        Set<Integer> itemsInStore = store.getItems().keySet();
        GetItemsResponse getItemsResponse = businessLogic.getItems();
        List<SystemItemDTO> itemsInSystems = new ArrayList<>(getItemsResponse.getItems().values());
        List<SystemItemDTO> filtered = itemsInSystems.stream()
                                                     .filter(itemDTO -> !itemsInStore.contains(itemDTO.getId()))
                                                     .collect(Collectors.toList());
        ObservableList<String> itemsNotInStoreObservableList = FXCollections.observableArrayList();
        filtered.forEach(item -> {
            itemsNotInStoreObservableList.add(String.format("Id: %s Name: %s", item.getId(), item.getName()));

        });
        return itemsNotInStoreObservableList;

    }

    public void setStoresList () {
        placeOrderComponentController.setStoresList(getStoresList());
    }

    public void setItemsList () {
        GetItemsResponse itemsResponse = businessLogic.getItems();
        List<SystemItemDTO> items = new ArrayList<>(itemsResponse.getItems().values());
        ObservableList<ItemDTO> storesObservableList = FXCollections.observableArrayList();
        items.forEach(systemItemDTO -> {
            storesObservableList.add(new ItemDTO(systemItemDTO.getId(), systemItemDTO.getName(), systemItemDTO.getPurchaseCategory()));
        });
        placeOrderComponentController.setItemsList(storesObservableList);
    }

    public void setPricedItemsList (int selectedStoreId) {
        GetStoresResponse storesResponse = businessLogic.getStores();
        StoreDTO selectedStore = storesResponse.getStores().get(selectedStoreId);
        ObservableList<PricedItemDTO> pricedItemsObservableList = FXCollections.observableArrayList();
        selectedStore.getItems().values().forEach(storeItemDTO -> {
            pricedItemsObservableList.add(new PricedItemDTO(storeItemDTO.getId(),
                                                            storeItemDTO.getName(),
                                                            storeItemDTO.getPurchaseCategory(),
                                                            storeItemDTO.getPrice()));
        });
        placeOrderComponentController.setPricedItemsList(pricedItemsObservableList);

    }

    public PlaceDynamicOrderResponse placeDynamicOrder (PlaceDynamicOrderRequest placeDynamicOrderRequest) {
        return businessLogic.placeDynamicOrder(placeDynamicOrderRequest);
    }

    public static double round (double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public PlaceOrderResponse placeStaticOrder (PlaceOrderRequest request) {
        return businessLogic.placeStaticOrder(request);
    }

    public GetDiscountsResponse getDiscounts (UUID orderId) {
        return businessLogic.getDiscounts(orderId);
    }

    public FinalSummaryForOrder addDiscountsToOrder (AddDiscountsToOrderRequest request) {
        return businessLogic.addDiscountsToOrder(request);
    }

    public void completeTheOrder (UUID orderId, boolean toConfirmNewDynamicOrder) {
        businessLogic.completeTheOrder(orderId, toConfirmNewDynamicOrder);
    }

    public void addItemToStore (UpdateStoreRequest request) {
        businessLogic.addItemToStore(request);
    }

    public void updatePriceOfSelectedItem (UpdateStoreRequest request) {
        businessLogic.updatePriceOfSelectedItem(request);
    }

    public DeleteItemFromStoreResponse deleteItemFromStore (BaseUpdateStoreRequest request) {
        return businessLogic.deleteItemFromStore(request);
    }

    public void handleEditItemsInStore () {
        editItemsController.resetEditItemsComponent();
        mainBorderPane.setCenter(editItemsComponent);
        editItemsController.startEditItems();
    }

    public void setEditItemsComponent (VBox editItemsComponent) {
        this.editItemsComponent = editItemsComponent;
    }
}
