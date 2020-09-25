package components.placeOrderComponent;

import components.app.AppController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.DoubleStringConverter;
import model.ItemDTO;
import model.PricedItemDTO;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.response.PlaceDynamicOrderResponse;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.function.Consumer;

public class PlaceOrderController {

    private AppController mainController;
    private PlaceDynamicOrderRequest placeDynamicOrderRequest;
    private PlaceOrderRequest placeOrderRequest;

    @FXML
    private BorderPane placeOrderPane;

    @FXML
    private ComboBox<String> customersBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> orderTypeBox;

    @FXML
    private ComboBox<String> storesBox;

    @FXML
    private ScrollPane itemsScrollPane;

    @FXML
    private Button createOrderButton;

    private Consumer<PlaceOrderRequest> placeOrderRequestConsumer;

    private Consumer<PlaceDynamicOrderRequest> placeDynamicOrderRequestConsumer;

    private TableView<PricedItemDTO> staticOrderItemsView = new TableView<>();

    private TableView<ItemDTO> dynamicOrderItemsView = new TableView<>();

    private SimpleBooleanProperty isCustomerSelected;
    private SimpleBooleanProperty isDatePicked;
    private SimpleBooleanProperty isStoreSelected;
    private SimpleBooleanProperty isStaticOrder;
    private SimpleBooleanProperty isOrderTypeSelected;
    private SimpleIntegerProperty selectedCustomer;
    private LocalDate selectedDate;
    final ObservableList<String> orderTypes = FXCollections.observableArrayList("From Chosen Store", "Cheapest Shopping Cart");

    public void setIsCustomerSelected(boolean isCustomerSelected) {
        this.isCustomerSelected.set(isCustomerSelected);
    }

    public void setIsDatePicked(boolean isDatePicked) {
        this.isDatePicked.set(isDatePicked);
    }

    public void setIsStoreSelected(boolean isStoreSelected) {
        this.isStoreSelected.set(isStoreSelected);
    }

    public void setIsStaticOrder(boolean isStaticOrder) {
        this.isStaticOrder.set(isStaticOrder);
    }

    public void setIsOrderTypeSelected(boolean isOrderTypeSelected) {
        this.isOrderTypeSelected.set(isOrderTypeSelected);
    }

    public void setSelectedCustomer(int selectedCustomer) {
        this.selectedCustomer.set(selectedCustomer);
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void setCustomersList(ObservableList<String> customers) {
        customersBox.setItems(customers);
    }

    public void setStoresList(ObservableList<String> stores) {
        storesBox.setItems(stores);
    }

    public void setItemsList(ObservableList<ItemDTO> items) {
        dynamicOrderItemsView.setItems(items);
    }

    public void setPricedItemsList(ObservableList<PricedItemDTO> pricedItems) {
        staticOrderItemsView.setItems(pricedItems);
    }

    public PlaceOrderController() {

        isStaticOrder = new SimpleBooleanProperty(false);
        isCustomerSelected = new SimpleBooleanProperty(false);
        isDatePicked = new SimpleBooleanProperty(false);
        isOrderTypeSelected = new SimpleBooleanProperty(false);
        isStoreSelected = new SimpleBooleanProperty(false);
        selectedCustomer = new SimpleIntegerProperty();

        initItemsTable();
        initPricedItemsTable();
    }

    private void initPricedItemsTable() {

        TableColumn<PricedItemDTO, String> idColumn = new TableColumn<>("Id");
        idColumn.setMinWidth(20);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<PricedItemDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(20);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<PricedItemDTO, String> categoryColumn = new TableColumn<>("Purchase Category");
        categoryColumn.setMinWidth(20);
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseCategory"));

        TableColumn<PricedItemDTO, String> priceColumn = new TableColumn<>("Price");
        priceColumn.setMinWidth(20);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<PricedItemDTO, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setMinWidth(20);
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        amountColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<PricedItemDTO, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<PricedItemDTO, Double> event) {
                if (placeOrderRequest.getOrderItemToAmount() == null) {
                    placeOrderRequest.setOrderItemToAmount(new HashMap<>());
                }
                placeOrderRequest.getOrderItemToAmount().put(event.getRowValue().getId(), event.getNewValue());
            }
        });

        staticOrderItemsView.getColumns().addAll(idColumn, nameColumn, categoryColumn, priceColumn, amountColumn);
        staticOrderItemsView.setEditable(true);
    }

    private void initItemsTable() {
        TableColumn<ItemDTO, String> idColumn = new TableColumn<>("Id");
        idColumn.setMinWidth(20);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ItemDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(20);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ItemDTO, String> categoryColumn = new TableColumn<>("Purchase Category");
        categoryColumn.setMinWidth(20);
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseCategory"));


        TableColumn<ItemDTO, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setMinWidth(20);
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        amountColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ItemDTO, Double>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<ItemDTO, Double> event) {
                if (placeDynamicOrderRequest.getOrderItemToAmount() == null) {
                    placeDynamicOrderRequest.setOrderItemToAmount(new HashMap<>());
                }
                placeDynamicOrderRequest.getOrderItemToAmount().put(event.getRowValue().getId(), event.getNewValue());
            }
        });

        dynamicOrderItemsView.getColumns().addAll(idColumn, nameColumn, categoryColumn, amountColumn);
        dynamicOrderItemsView.setEditable(true);


    }

    @FXML
    private void initialize() {
        datePicker.disableProperty().bind(isCustomerSelected.not());
        orderTypeBox.disableProperty().bind(isDatePicked.not());
        storesBox.visibleProperty().bind(isStaticOrder);
        orderTypeBox.setItems(orderTypes);
    }

    @FXML
    void createOrderButtonAction(ActionEvent event) {
        if (isStaticOrder.get()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Place Order Confirmation");
            alert.setHeaderText("Press OK to confirm Order Creation");
            alert.setContentText(placeOrderRequest.toString());
            alert.showAndWait();
        } else {
            PlaceDynamicOrderResponse response = mainController.placeDynamicOrder(placeDynamicOrderRequest);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Place Order offer");
            alert.setHeaderText("Press Ok to confirm");
            alert.setContentText(response.toString());
            alert.showAndWait();
        }
    }

    @FXML
    void customersBoxAction(ActionEvent event) {
        isCustomerSelected.set(true);
        if (customersBox.getValue() != null) {
            selectedCustomer.setValue(Integer.valueOf(customersBox.getValue().substring(4, 5)));
        }
    }

    @FXML
    void datePickerAction(ActionEvent event) {
        isDatePicked.set(true);
        selectedDate = datePicker.getValue();
    }

    @FXML
    void orderTypeBoxAction(ActionEvent event) {
        isOrderTypeSelected.set(true);
        if (orderTypeBox.getValue().equals("From Chosen Store")) {
            isStaticOrder.set(true);
            mainController.setStoresList();
            placeOrderRequest = new PlaceOrderRequest(selectedCustomer.getValue(), selectedDate);
        } else {
            isStaticOrder.set(false);
            mainController.setItemsList();
            itemsScrollPane.setContent(dynamicOrderItemsView);
            placeDynamicOrderRequest = new PlaceDynamicOrderRequest(selectedCustomer.getValue(), selectedDate);
        }
    }

    @FXML
    void storesBoxAction(ActionEvent event) {
        isStoreSelected.set(true);
        int storeId = Integer.parseInt(storesBox.getValue().substring(4, 5));
        mainController.setPricedItemsList(storeId);
        itemsScrollPane.setContent(staticOrderItemsView);
        placeOrderRequest.setStoreId(storeId);
    }

}
