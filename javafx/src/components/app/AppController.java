package components.app;

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
import logic.BusinessLogic;
import model.*;
import model.request.PlaceDynamicOrderRequest;
import model.response.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AppController {

    private PlaceOrderController placeOrderComponentController;
    private SDMController sdmComponentController;
    private MapController mapComponentController;
    private BusinessLogic businessLogic;

    private BorderPane placeOrderPane;

    private BorderPane mainBorderPane;

    private ScrollPane mapScrollPane;

    private GridPane mapGridPane;

    private ScrollPane displayInfoScrollPane;

    public void setMainBorderPane(BorderPane mainBorderPane) {
        this.mainBorderPane = mainBorderPane;
    }

    public void setDisplayInfoScrollPane(javafx.scene.control.ScrollPane displayInfoScrollPane) {
        this.displayInfoScrollPane = displayInfoScrollPane;
    }

    public void setPlaceOrderPane(BorderPane placeOrderPane) {
        this.placeOrderPane = placeOrderPane;
    }

    public void setMapScrollPane(javafx.scene.control.ScrollPane mapScrollPane) {
        this.mapScrollPane = mapScrollPane;
    }

    public void setMapGridPane(GridPane mapGridPane) {
        this.mapGridPane = mapGridPane;
    }

    public void setCenterToDisplayInfoScrollPane() {
        mainBorderPane.setCenter(displayInfoScrollPane);
    }

    @FXML
    public void initialize() {
        if (placeOrderComponentController != null && sdmComponentController != null && mapComponentController != null) {
            placeOrderComponentController.setMainController(this);
            sdmComponentController.setMainController(this);
            mapComponentController.setMainController(this);
        }
    }

    public void setBusinessLogic(BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    public void setSdmComponentController(SDMController sdmComponentController) {
        this.sdmComponentController = sdmComponentController;
        sdmComponentController.setMainController(this);
    }

    public void setPlaceOrderComponentController(PlaceOrderController placeOrderComponentController) {
        this.placeOrderComponentController = placeOrderComponentController;
        placeOrderComponentController.setMainController(this);
    }

    public void setMapComponentController(MapController mapComponentController) {
        this.mapComponentController = mapComponentController;
        mapComponentController.setMainController(this);
    }

    public void bindTaskToUIComponents(Task<Boolean> aTask, Runnable onFinish) {
        sdmComponentController.bindTaskToUIComponents(aTask, onFinish);
    }

    public void loadFile(String filePath, Consumer<String> fileErrorDelegate, Runnable onFinish) {
        businessLogic.loadFile(filePath, fileErrorDelegate, onFinish);
    }

    public void createMap() {
        Consumer<GridPane> mapConsumer = gridPane -> {
            mapGridPane.getChildren().addAll(gridPane.getChildren());
            mapScrollPane.setContent(mapGridPane);
        };
        mainBorderPane.setCenter(mapScrollPane);
        businessLogic.createMap(mapConsumer);
    }

    public GetCustomersResponse getCustomers() {
        return businessLogic.getCustomers();
    }

    public GetStoresResponse getStores() {
        return businessLogic.getStores();
    }

    public GetOrdersResponse getOrders() {
        return businessLogic.getOrders();
    }

    public GetItemsResponse getItems() {
        return businessLogic.getItems();
    }

    public void handlePlaceOrder() {
        resetPlaceOrderComponent();
        mainBorderPane.setCenter(placeOrderPane);
        setCustomersList();
    }

    private void setCustomersList() {
        GetCustomersResponse customersResponse = businessLogic.getCustomers();
        List<CustomerDTO> customers = new ArrayList<>(customersResponse.getSystemCustomers().values());
        ObservableList<String> customersObservableList = FXCollections.observableArrayList();
        customers.forEach(customerDTO -> {
            customersObservableList.add(customerDTO.toString());
        });
        placeOrderComponentController.setCustomersList(customersObservableList);
    }

    private void resetPlaceOrderComponent() {
        placeOrderComponentController.setCustomersList(null);
        placeOrderComponentController.setStoresList(null);
        placeOrderComponentController.setIsCustomerSelected(false);
        placeOrderComponentController.setIsDatePicked(false);
        placeOrderComponentController.setIsStoreSelected(false);
        placeOrderComponentController.setIsStaticOrder(false);
        placeOrderComponentController.setIsOrderTypeSelected(false);
        placeOrderComponentController.setSelectedCustomer(0);
        placeOrderComponentController.setSelectedDate(null);
    }

    public void setStoresList() {
        GetStoresResponse storesResponse = businessLogic.getStores();
        List<StoreDTO> stores = new ArrayList<>(storesResponse.getStores().values());
        ObservableList<String> storesObservableList = FXCollections.observableArrayList();
        stores.forEach(storeDTO -> {
            storesObservableList.add(storeDTO.toString());
        });
        placeOrderComponentController.setStoresList(storesObservableList);
    }

    public void setItemsList() {
        GetItemsResponse itemsResponse = businessLogic.getItems();
        List<SystemItemDTO> items = new ArrayList<>(itemsResponse.getItems().values());
        ObservableList<ItemDTO> storesObservableList = FXCollections.observableArrayList();
        items.forEach(systemItemDTO -> {
            storesObservableList.add(new ItemDTO(systemItemDTO.getId(), systemItemDTO.getName(), systemItemDTO.getPurchaseCategory()));
        });
        placeOrderComponentController.setItemsList(storesObservableList);
    }

    public void setPricedItemsList(int selectedStoreId) {
        GetStoresResponse storesResponse = businessLogic.getStores();
        StoreDTO selectedStore = storesResponse.getStores().get(selectedStoreId);
        ObservableList<PricedItemDTO> pricedItemsObservableList = FXCollections.observableArrayList();
        selectedStore.getItems().values().forEach(storeItemDTO -> {
            pricedItemsObservableList.add(new PricedItemDTO(storeItemDTO.getId(), storeItemDTO.getName(), storeItemDTO.getPurchaseCategory(), storeItemDTO.getPrice()));
        });
        placeOrderComponentController.setPricedItemsList(pricedItemsObservableList);

    }

    public PlaceDynamicOrderResponse placeDynamicOrder(PlaceDynamicOrderRequest placeDynamicOrderRequest) {
        return businessLogic.placeDynamicOrder(placeDynamicOrderRequest);
    }
}
