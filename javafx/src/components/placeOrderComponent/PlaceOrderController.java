package components.placeOrderComponent;

import static components.app.AppController.round;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

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
import model.*;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.response.GetCustomersResponse;
import model.response.GetItemsResponse;
import model.response.GetStoresResponse;
import model.response.PlaceDynamicOrderResponse;

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

    public void setIsCustomerSelected (boolean isCustomerSelected) {
        this.isCustomerSelected.set(isCustomerSelected);
    }

    public void setIsDatePicked (boolean isDatePicked) {
        this.isDatePicked.set(isDatePicked);
    }

    public void setIsStoreSelected (boolean isStoreSelected) {
        this.isStoreSelected.set(isStoreSelected);
    }

    public void setIsStaticOrder (boolean isStaticOrder) {
        this.isStaticOrder.set(isStaticOrder);
    }

    public void setIsOrderTypeSelected (boolean isOrderTypeSelected) {
        this.isOrderTypeSelected.set(isOrderTypeSelected);
    }

    public void setSelectedCustomer (int selectedCustomer) {
        this.selectedCustomer.set(selectedCustomer);
    }

    public void setSelectedDate (LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setMainController (AppController mainController) {
        this.mainController = mainController;
    }

    public void setCustomersList (ObservableList<String> customers) {
        customersBox.setItems(customers);
    }

    public void setStoresList (ObservableList<String> stores) {
        storesBox.setItems(stores);
    }

    public void setItemsList (ObservableList<ItemDTO> items) {
        dynamicOrderItemsView.setItems(items);
    }

    public void setPricedItemsList (ObservableList<PricedItemDTO> pricedItems) {
        staticOrderItemsView.setItems(pricedItems);
    }

    public PlaceOrderController () {

        isStaticOrder = new SimpleBooleanProperty(false);
        isCustomerSelected = new SimpleBooleanProperty(false);
        isDatePicked = new SimpleBooleanProperty(false);
        isOrderTypeSelected = new SimpleBooleanProperty(false);
        isStoreSelected = new SimpleBooleanProperty(false);
        selectedCustomer = new SimpleIntegerProperty();

        initItemsTable();
        initPricedItemsTable();
    }

    private void initPricedItemsTable () {

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

        TableColumn<PricedItemDTO, String> amountColumn = new TableColumn<>("Amount");
        amountColumn.setMinWidth(20);
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setOnEditCommit(addAmountToPricedItemEventHandler());

        staticOrderItemsView.getColumns().addAll(idColumn, nameColumn, categoryColumn, priceColumn, amountColumn);
        staticOrderItemsView.setEditable(true);
    }

    private EventHandler<TableColumn.CellEditEvent<PricedItemDTO, String>> addAmountToPricedItemEventHandler () {
        return new EventHandler<TableColumn.CellEditEvent<PricedItemDTO, String>>() {
            @Override
            public void handle (TableColumn.CellEditEvent<PricedItemDTO, String> event) {
                if (placeOrderRequest.getOrderItemToAmount() == null) {
                    placeOrderRequest.setOrderItemToAmount(new HashMap<>());
                }
                PricedItemDTO pricedItemDTO = event.getRowValue();
                String amountStr = event.getNewValue();

                try {
                    validateAmount(pricedItemDTO.getPurchaseCategory(), amountStr, pricedItemDTO.getId());
                    placeOrderRequest.getOrderItemToAmount().put(pricedItemDTO.getId(), Double.parseDouble(amountStr));
                }
                catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Amount");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        };
    }

    private EventHandler<TableColumn.CellEditEvent<ItemDTO, String>> addAmountToRowEventHandler () {
        return new EventHandler<TableColumn.CellEditEvent<ItemDTO, String>>() {
            @Override
            public void handle (TableColumn.CellEditEvent<ItemDTO, String> event) {
                if (placeDynamicOrderRequest.getOrderItemToAmount() == null) {
                    placeDynamicOrderRequest.setOrderItemToAmount(new HashMap<>());
                }
                // add validation according to the amount value (if not, display appropriate alert)
                ItemDTO itemDTO = event.getRowValue();
                String amountStr = event.getNewValue();

                try {
                    validateAmount(itemDTO.getPurchaseCategory(), amountStr, itemDTO.getId());
                    placeDynamicOrderRequest.getOrderItemToAmount().put(itemDTO.getId(), Double.parseDouble(amountStr));
                }
                catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Amount");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                }
            }
        };
    }

    public void validateAmount (String itemPurchaseCategory, String amountStr, Integer itemId) {
        Double amount = tryParseAmountStrToDouble(amountStr);
        validatePositiveAmount(amount);
        validateAmountToPurchaseCategory(itemPurchaseCategory, amount, itemId);
    }

    private Double tryParseAmountStrToDouble (String amountStr) {
        try {
            return Double.parseDouble(amountStr);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid amount.\nAmount should be a positive real number");
        }
    }

    private void validateAmountToPurchaseCategory (String itemPurchaseCategory, Double amount, Integer itemId) {
        if (itemPurchaseCategory.equals(PricedItemDTO.QUANTITY)) {
            if (amount.intValue() < amount) {
                throw new IllegalArgumentException(String.format("Invalid amount.\nPurchase category for item id %s is quantity and the amount should be an integer.",
                                                                 itemId));
            }
        }
    }

    private void validatePositiveAmount (Double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Invalid amount.\nAmount should be positive real number");
        }
    }

    private void initItemsTable () {
        TableColumn<ItemDTO, String> idColumn = new TableColumn<>("Id");
        idColumn.setMinWidth(20);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<ItemDTO, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setMinWidth(20);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ItemDTO, String> categoryColumn = new TableColumn<>("Purchase Category");
        categoryColumn.setMinWidth(20);
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseCategory"));

        TableColumn<ItemDTO, String> amountColumn = new TableColumn<>("Amount");
        amountColumn.setMinWidth(20);
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        amountColumn.setOnEditCommit(addAmountToRowEventHandler());

        dynamicOrderItemsView.getColumns().addAll(idColumn, nameColumn, categoryColumn, amountColumn);
        dynamicOrderItemsView.setEditable(true);

    }

    @FXML
    private void initialize () {
        datePicker.disableProperty().bind(isCustomerSelected.not());
        orderTypeBox.disableProperty().bind(isDatePicked.not());
        storesBox.visibleProperty().bind(isStaticOrder);
        orderTypeBox.setItems(orderTypes);
    }

    @FXML
    void createOrderButtonAction (ActionEvent event) {
        String title, header, message;
        if (isStaticOrder.get()) {
            title = "Place Order Confirmation";
            header = "Press Ok to confirm";
            message = getStaticOrderSummary();
        }
        else {
            PlaceDynamicOrderResponse response = mainController.placeDynamicOrder(placeDynamicOrderRequest);
            title = "Place Order offer";
            header = "Press Ok to confirm";
            message = getSDynamicOrderSummary(response);
        }

        Alert MidSummaryForOrder = createMidSummaryForOrder(title, header, message);
        MidSummaryForOrder.showAndWait();
    }

    private Alert createMidSummaryForOrder (String title, String header, String message) {
        Alert alert;
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        TextArea area = new TextArea(message);
        area.setWrapText(true);
        area.setEditable(false);
        alert.getDialogPane().setContent(area);
        alert.setResizable(true);
        return alert;
    }

    private String getStaticOrderSummary () {
        GetItemsResponse getItemsResponse = mainController.getItems();
        GetStoresResponse getStoresResponse = mainController.getStores();
        GetCustomersResponse getCustomersResponse = mainController.getCustomers();

        Map<Integer, SystemItemDTO> items = getItemsResponse.getItems();
        CustomerDTO selectedCustomer = getCustomersResponse.getSystemCustomers().get(placeOrderRequest.getCustomerId());
        StoreDTO selectedStore = getStoresResponse.getStores().get(placeOrderRequest.getStoreId());
        Map<Integer, Double> orderItemToAmount = placeOrderRequest.getOrderItemToAmount();

        StringBuilder builder = new StringBuilder("Order summary:\nOrder items:");
        Iterator<Integer> iterator = orderItemToAmount.keySet().iterator();
        double totalItemsPrice = 0;
        while (iterator.hasNext()) {
            Integer itemId = iterator.next();
            SystemItemDTO item = items.get(itemId);
            double amount = orderItemToAmount.get(itemId);
            int itemPrice = selectedStore.getItems().get(itemId).getPrice();
            double totalItemPrice = round(amount * itemPrice, 2);
            builder.append(String.format("\n{Item id: %s,\nItem name: %s,\nPurchase category: %s,\nPrice: %s,\nAmount: %s,\nTotal item price: %s}",
                                         itemId,
                                         item.getName(),
                                         item.getPurchaseCategory(),
                                         itemPrice,
                                         amount,
                                         round(itemPrice * amount, 2)));
            if (iterator.hasNext()) {
                builder.append(",");
            }
            totalItemsPrice += totalItemPrice;
        }
        builder.append("\n\n");

        addTotalPricesToSummary(selectedCustomer, selectedStore, builder, totalItemsPrice);

        return builder.toString();
    }

    private void addTotalPricesToSummary (CustomerDTO selectedCustomer,
                                          StoreDTO selectedStore,
                                          StringBuilder builder,
                                          double totalItemsPrice) {
        LocationDTO storeLocation = selectedStore.getLocation();
        LocationDTO customerLocation = selectedCustomer.getLocation();

        double distance = calculateDistance((storeLocation.getxCoordinate()),
                                            customerLocation.getxCoordinate(),
                                            storeLocation.getyCoordinate(),
                                            customerLocation.getyCoordinate());

        int ppk = selectedStore.getDeliveryPpk();
        double deliveryPrice = round(distance * ppk, 2);
        builder.append(String.format("Distance form store: %s\nStore PPK: %s\nDelivery price: %s,\nOrder total price: %s ",
                                     distance,
                                     ppk,
                                     deliveryPrice,
                                     deliveryPrice + totalItemsPrice));
    }

    private double calculateDistance (int x1, int x2, int y1, int y2) {
        return round(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)), 2);
    }

    private String getSDynamicOrderSummary (PlaceDynamicOrderResponse response) {
        StringBuilder builder = new StringBuilder("The order offer:");
        Iterator<DynamicOrderEntityDTO> iterator = response.getDynamicOrderEntity().iterator();
        while (iterator.hasNext()) {
            DynamicOrderEntityDTO orderEntity = iterator.next();
            builder.append("\n{" + orderEntity + "}");
            if (iterator.hasNext()) {
                builder.append(",\n");
            }
        }
        builder.append("\n");

        return builder.toString();
    }

    @FXML
    void customersBoxAction (ActionEvent event) {
        isCustomerSelected.set(true);
        if (customersBox.getValue() != null) {
            selectedCustomer.setValue(Integer.valueOf(customersBox.getValue().substring(4, 5)));
        }
    }

    @FXML
    void datePickerAction (ActionEvent event) {
        isDatePicked.set(true);
        selectedDate = datePicker.getValue();
    }

    @FXML
    void orderTypeBoxAction (ActionEvent event) {
        isOrderTypeSelected.set(true);
        if (orderTypeBox.getValue().equals("From Chosen Store")) {
            isStaticOrder.set(true);
            mainController.setStoresList();
            placeOrderRequest = new PlaceOrderRequest(selectedCustomer.getValue(), selectedDate);
        }
        else {
            isStaticOrder.set(false);
            mainController.setItemsList();
            itemsScrollPane.setContent(dynamicOrderItemsView);
            placeDynamicOrderRequest = new PlaceDynamicOrderRequest(selectedCustomer.getValue(), selectedDate);
        }
    }

    @FXML
    void storesBoxAction (ActionEvent event) {
        isStoreSelected.set(true);
        int storeId = Integer.parseInt(storesBox.getValue().substring(4, 5));
        mainController.setPricedItemsList(storeId);
        itemsScrollPane.setContent(staticOrderItemsView);
        placeOrderRequest.setStoreId(storeId);
    }

}
