package components.placeOrderComponent;

import components.app.AppController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.BusinessLogic;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;

import java.util.function.Consumer;

public class PlaceOrderController {

    private AppController mainController;

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
    private Button createOrderButton;

    private Consumer<PlaceOrderRequest> placeOrderRequestConsumer;

    private Consumer<PlaceDynamicOrderRequest> placeDynamicOrderRequestConsumer;

    private SimpleBooleanProperty iCustomerSelected;
    private SimpleBooleanProperty isDatePicked;
    private SimpleBooleanProperty isStoreSelected;
    private SimpleBooleanProperty isStaticOrder;
    private SimpleBooleanProperty isOrderTypeSelected;



    private BusinessLogic businessLogic;
    private Stage primaryStage;

    final ObservableList<String> orderTypes =
            FXCollections.observableArrayList("From Chosen Store", "Cheapest Shopping Cart");

    public PlaceOrderController() {
        isStaticOrder = new SimpleBooleanProperty(false);
        iCustomerSelected = new SimpleBooleanProperty(false);
        isDatePicked = new SimpleBooleanProperty(false);
        isStoreSelected = new SimpleBooleanProperty(false);
        this.placeOrderRequestConsumer = placeOrderRequestConsumer;
        this.placeDynamicOrderRequestConsumer = placeDynamicOrderRequestConsumer;
    }

    @FXML
    private void initialize() {
        datePicker.disableProperty().bind(iCustomerSelected);
        orderTypeBox.disableProperty().bind(isDatePicked.not());
        storesBox.disableProperty().bind(isStaticOrder);
        storesBox.visibleProperty().bind(isStaticOrder);
        orderTypeBox.setItems(orderTypes);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }
    public BorderPane getPlaceOrderPane() {
        return placeOrderPane;
    }

    public void setCustomersList(ObservableList<String> customers) {
        customersBox.setItems(customers);
    }

    public void setStoresList(ObservableList<String> stores) {
        customersBox.setItems(stores);
    }

//    public void setBusinessLogic(BusinessLogic businessLogic) {
//        this.businessLogic = businessLogic;
//    }
//
//    public void setPrimaryStage(Stage primaryStage) {
//        this.primaryStage = primaryStage;
//    }



    @FXML
    void createOrderButtonAction(ActionEvent event) {
//        if(isStaticOrder.get()){
//            this.placeOrderRequestConsumer.accept(new PlaceOrderRequest(customersBox.getValue(), storesBox.getValue(), datePicker.getValue(),  null));
//        }
//        else{
//            this.placeDynamicOrderRequestConsumer.accept(new PlaceDynamicOrderRequest(customersBox.getValue(), storesBox.getValue(), datePicker.getValue(),  null));));
//        }

    }

    @FXML
    void customersBoxAction(ActionEvent event) {
        iCustomerSelected.set(true);

    }

    @FXML
    void datePickerAction(ActionEvent event) {
        isDatePicked.set(true);

    }

    @FXML
    void orderTypeBoxAction(ActionEvent event) {
        isOrderTypeSelected.set(true);
        isStaticOrder.set(orderTypeBox.getValue().equals("From Chosen Store"));
    }

    @FXML
    void storesBoxAction(ActionEvent event) {
        isStoreSelected.set(true);

    }


}



