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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.DoubleStringConverter;
import model.DiscountDTO;
import model.ItemDTO;
import model.OfferDTO;
import model.PricedItemDTO;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.response.GetDiscountsResponse;
import model.response.PlaceDynamicOrderResponse;
import model.response.PlaceOrderResponse;

import java.time.LocalDate;
import java.util.*;

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
    private ScrollPane itemsAndDiscountsScrollPane;

    @FXML
    private Button createOrderButton;

    private TableView<PricedItemDTO> staticOrderItemsView = new TableView<>();

    private TableView<ItemDTO> dynamicOrderItemsView = new TableView<>();

    private GridPane selectDiscountsPane = new GridPane();

    private UUID orderId;

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
        initSelectDiscountsPane();
    }

    private void initSelectDiscountsPane() {

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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (isStaticOrder.get()) {
            alert.setTitle("Place Order Confirmation");
            alert.setHeaderText("Press OK to confirm order and continue to select discounts");
            alert.setContentText(placeOrderRequest.toString());
        } else {
            PlaceDynamicOrderResponse response = mainController.placeDynamicOrder(placeDynamicOrderRequest);
            orderId = response.getId();
            alert.setTitle("Place Order offer");
            alert.setHeaderText("Press OK to confirm order and continue to select discounts");
            alert.setContentText(response.toString());
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                handleSelectDiscounts();
            } else if (result.get() == ButtonType.CANCEL) {
                handleCancelOrder();

            }
        }
    }

    private void handleCancelOrder() {
        mainController.resetPlaceOrderComponent();
        mainController.completeTheOrder(orderId, false);
    }

    private void handleSelectDiscounts() {
        if (isStaticOrder.get()) {
            PlaceOrderResponse response = mainController.placeStaticOrder(placeOrderRequest);
            orderId = response.getOrderId();
        }
        GetDiscountsResponse response = mainController.getDiscounts(orderId);
        handleDisplayDiscounts(response);
    }

    private void handleDisplayDiscounts(GetDiscountsResponse response) {
        GridPane itemsGridPane = new GridPane();
        GridPane storeDiscountsGridPane = new GridPane();
        GridPane discountDetailsGridPane = new GridPane();
        if (response.getStoreIdToValidDiscounts() != null && !response.getStoreIdToValidDiscounts().isEmpty()) {
            int storeRowIndex = 0;
            for (Integer storeId : response.getStoreIdToValidDiscounts().keySet()) {
                Accordion storeAccordion = new Accordion();
                int itemRowIndex = 0;
                for (Integer itemId : response.getStoreIdToValidDiscounts().get(storeId).getItemIdToValidStoreDiscounts().keySet()) {
                    Accordion itemAccordion = new Accordion();
                    List<DiscountDTO> discounts = response.getStoreIdToValidDiscounts().get(storeId).getItemIdToValidStoreDiscounts().get(itemId);
                    populateDiscounts(discounts, discountDetailsGridPane);
                    TitledPane discountsTitledPane = new TitledPane(Integer.toString(itemId), discountDetailsGridPane);
                    itemAccordion.getPanes().add(discountsTitledPane);
                    itemsGridPane.add(itemAccordion, 0, itemRowIndex);
                    itemRowIndex++;
                }
                TitledPane itemsTitledPane = new TitledPane(Integer.toString(storeId), itemsGridPane);
                storeAccordion.getPanes().add(itemsTitledPane);
                storeDiscountsGridPane.add(storeAccordion, 0, storeRowIndex);
                storeRowIndex++;
            }
            Button submitDiscountButton = new Button("Submit Discounts");
            submitDiscountButton.setOnAction(event -> {
                //todo: check discounts legal in be and display order summery
            });
            VBox discountsVBox = new VBox(storeDiscountsGridPane, submitDiscountButton);
            itemsAndDiscountsScrollPane.setContent(discountsVBox);
        } else {
            alertNoDiscountsAndConfirmOrder();
            //todo alert that there are no discounts and confirm
        }
    }

    private void alertNoDiscountsAndConfirmOrder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Place Order Confirmation");
        alert.setHeaderText("There are no available discounts for your order\n Press OK to confirm order creation");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                handleConfirmOrder();
            } else if (result.get() == ButtonType.CANCEL) {
                handleCancelOrder();
            }
        }
    }

    private void handleConfirmOrder() {
        mainController.completeTheOrder(orderId, true);
    }

    private void populateDiscounts(List<DiscountDTO> discounts, GridPane discountDetailsGridPane) {

        for (DiscountDTO discount : discounts) {
            Accordion discountAccordion = new Accordion();
            discountDetailsGridPane.add(new Label("If You Buy:"), 0, 0);
            discountDetailsGridPane.add(new TextField(String.format("%s %s", discount.getIfYouBuyQuantity(), discount.getIfYouBuyItemId())), 1, 0);
            discountDetailsGridPane.add(new Label("Then You Get:"), 0, 1);

            if (discount.getOperator().equals(DiscountDTO.DiscountType.IRRELEVANT)) {
                OfferDTO offer = (new ArrayList<>(discount.getOffers().values())).get(0);
                discountDetailsGridPane.add(new TextField(String.format("%s %s", offer.getQuantity(), offer.getItemId())), 1, 1);
            } else if (discount.getOperator().equals(DiscountDTO.DiscountType.ALL_OR_NOTHING)) {
                List<OfferDTO> offers = new ArrayList<>(discount.getOffers().values());
                StringBuilder stringBuilder = new StringBuilder();
                double additionalPrice = 0;
                for (OfferDTO offer : offers) {
                    stringBuilder.append(offer.getQuantity());
                    stringBuilder.append(" ");
                    stringBuilder.append(offer.getItemId());
                    stringBuilder.append("and ");
                    additionalPrice += offer.getQuantity() * offer.getForAdditional();
                }

                stringBuilder.append("for additional");
                stringBuilder.append(additionalPrice);
                discountDetailsGridPane.add(new TextField(stringBuilder.toString()), 1, 1);

            } else if (discount.getOperator().equals(DiscountDTO.DiscountType.ONE_OF)) {
                List<OfferDTO> offers = new ArrayList<>(discount.getOffers().values());
                ComboBox<String> offerOptionsBox = new ComboBox<>();
                ObservableList<String> offerOptionsList = FXCollections.observableArrayList();
                offers.forEach(offer -> {
                    offerOptionsList.add(String.format("%s %s for additional %s ", offer.getQuantity(), offer.getItemId(), offer.getForAdditional()));
                });
                offerOptionsBox.setItems(offerOptionsList);
                discountDetailsGridPane.add(offerOptionsBox, 1, 1);
            }

            discountDetailsGridPane.add(new Label("Quantity"), 0, 2);
            TextField quantityField = new TextField();
            quantityField.setPromptText("Enter number of desired discount realizations");
            discountDetailsGridPane.add(quantityField, 1, 2);
            TitledPane discountTitledPane = new TitledPane(discount.getName(), discountDetailsGridPane);
            discountAccordion.getPanes().add(discountTitledPane);
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
            itemsAndDiscountsScrollPane.setContent(dynamicOrderItemsView);
            placeDynamicOrderRequest = new PlaceDynamicOrderRequest(selectedCustomer.getValue(), selectedDate);
        }
    }

    @FXML
    void storesBoxAction(ActionEvent event) {
        if (storesBox.getValue() != null) {
            isStoreSelected.set(true);
            int storeId = Integer.parseInt(storesBox.getValue().substring(4, 5));
            mainController.setPricedItemsList(storeId);
            itemsAndDiscountsScrollPane.setContent(staticOrderItemsView);
            placeOrderRequest.setStoreId(storeId);
        }
    }
}
