package components.sdmComponent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

import components.app.AppController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import model.*;
import model.response.GetCustomersResponse;
import model.response.GetItemsResponse;
import model.response.GetOrdersResponse;
import model.response.GetStoresResponse;

public class SDMController {

    private AppController mainController;

    public BorderPane getMainBorderPane () {
        return mainBorderPane;
    }

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Text sdmTitle;

    @FXML
    private Button loadFileButton;

    @FXML
    private ComboBox<String> menuBox;

    @FXML
    private ScrollPane displayInfoScrollPane;

    @FXML
    private FlowPane buttonsContainer;

    @FXML
    private GridPane displayArea;

    @FXML
    private Text loadFileIndicator;

    @FXML
    private Button saveOrdersHistoryButton;

    @FXML
    private Text enterPath;

    @FXML
    private TextField saveHistoryTextField;

    @FXML
    private Button confirmSavingOrdersHistoryButton;

    @FXML
    private Button cancelSavingOrdersHistoryButton;

    @FXML
    private Button loadOrdersHistoryButton;

    private SimpleBooleanProperty isFileSelected;
    private SimpleBooleanProperty isFileBeingLoaded;
    private SimpleStringProperty selectedFileProperty;
    private SimpleBooleanProperty isSaveOrdersButtonSelected;

    final ObservableList<String> menuOptions = FXCollections.observableArrayList("Display Stores",
                                                                                 "Display Items",
                                                                                 "Display Customers",
                                                                                 "Display Orders",
                                                                                 "Place Order",
                                                                                 "Display Map",
                                                                                 "Edit Items In Store");

    public SDMController () {
        isFileSelected = new SimpleBooleanProperty(false);
        selectedFileProperty = new SimpleStringProperty();
        isFileBeingLoaded = new SimpleBooleanProperty(false);
        isSaveOrdersButtonSelected = new SimpleBooleanProperty(false);
    }

    public void setMainController (AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize () {
        menuBox.getItems().addAll(menuOptions);
        menuBox.disableProperty().bind(isFileSelected.not());
        loadFileIndicator.visibleProperty().bind(isFileBeingLoaded);
        AnchorPane.setLeftAnchor(displayArea, 5.0);
        loadOrdersHistoryButton.disableProperty().bind(isFileSelected.not());
        loadOrdersHistoryButton.visibleProperty().bind(isFileSelected);
        saveOrdersHistoryButton.disableProperty().bind(isFileSelected.not());
        saveOrdersHistoryButton.visibleProperty().bind(isFileSelected);

        saveHistoryTextField.visibleProperty().bind(isSaveOrdersButtonSelected);
        confirmSavingOrdersHistoryButton.visibleProperty().bind(isSaveOrdersButtonSelected);
        cancelSavingOrdersHistoryButton.visibleProperty().bind(isSaveOrdersButtonSelected);
        enterPath.visibleProperty().bind(isSaveOrdersButtonSelected);
        mainBorderPane.setRight(null);

    }

    public void bindTaskToUIComponents (Task<Boolean> aTask, Runnable onFinish) {
        loadFileIndicator.textProperty().bind(aTask.messageProperty());
        aTask.valueProperty().addListener( (observable, oldValue, newValue) -> onTaskFinished(Optional.ofNullable(onFinish)));
    }

    public ScrollPane getDisplayInfoScrollPane () {
        return displayInfoScrollPane;
    }

    public void onTaskFinished (Optional<Runnable> onFinish) {
        loadFileIndicator.textProperty().unbind();
        onFinish.ifPresent(Runnable::run);
    }

    @FXML
    void loadFileButtonAction (ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        selectedFileProperty.set(absolutePath);
        isFileBeingLoaded.set(true);
        Consumer<String> fileErrorConsumer = status -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File Status information");
            alert.setHeaderText("Error Loading File:");
            alert.setContentText(status);
            alert.showAndWait();
            isFileSelected.set(false);
            isFileBeingLoaded.set(false);
        };
        mainController.loadFile(selectedFileProperty.getValue(), fileErrorConsumer, () -> {
            isFileSelected.set(true);
            isFileBeingLoaded.set(false);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("File Status information");
            alert.setHeaderText("XML File Loaded Successfully");
            alert.showAndWait();
        });
    }

    @FXML
    void menuBoxAction (ActionEvent event) {
        saveHistoryTextField.setText(null);
        isSaveOrdersButtonSelected.set(false);
        displayArea.getChildren().clear();
        buttonsContainer.getChildren().clear();

        String selection = menuBox.getValue();
        switch (selection) {
        case "Display Stores":
            handleDisplayStores();
            break;
        case "Display Items":
            handleDisplayItems();
            break;
        case "Display Customers":
            handleDisplayCustomers();
            break;
        case "Display Orders":
            handleDisplayOrders();
            break;
        case "Place Order":
            mainBorderPane.setRight(null);
            handlePlaceOrder();
            break;
        case "Display Map":
            mainBorderPane.setRight(null);
            handleDisplayMap();
            break;
        case "Edit Items In Store":
            mainBorderPane.setRight(null);
            handleEditItemsInStore();
            break;
        default:
            break;
        }

    }

    @FXML
    void handleSaveOrderHistory (ActionEvent event) {
        mainBorderPane.setCenter(null);
        mainBorderPane.setRight(null);
        isSaveOrdersButtonSelected.set(true);
    }

    @FXML
    void handleConfirmSavingOrderHistory (ActionEvent event) {
        String textFieldValue = saveHistoryTextField.getText();
        Consumer<String> errorConsumer = (contentText) -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid path");
            alert.setContentText(contentText);
            alert.showAndWait();
        };

        mainController.saveOrdersHistoryFile(textFieldValue, errorConsumer, () -> isSaveOrdersButtonSelected.set(false));
    }

    @FXML
    void handleCancelSavingOrderHistory (ActionEvent event) {
        saveHistoryTextField.setText(null);
        isSaveOrdersButtonSelected.set(false);
    }

    @FXML
    void handleLoadOrderHistory (ActionEvent event) {
        mainBorderPane.setCenter(null);
        mainBorderPane.setRight(null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select orders history file to load");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        Consumer<String> fileErrorConsumer = status -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed to load order history file");
            alert.setHeaderText("Error Occurred While Loading Orders History File:");
            alert.setContentText(status);
            alert.showAndWait();
        };
        mainController.loadOrdersHistoryFile(absolutePath, fileErrorConsumer);
    }

    private void handleEditItemsInStore () {
        mainController.handleEditItemsInStore();
    }

    private void handlePlaceOrder () {
        mainController.handlePlaceOrder();
    }

    private void handleDisplayMap () {
        mainController.createMap();
    }

    private void handleDisplayCustomers () {
        GetCustomersResponse response = mainController.getCustomers();
        mainController.setCenterToDisplayInfoScrollPane();
        buttonsContainer.getChildren().clear();

        response.getSystemCustomers().values().forEach(customer -> {
            Button button = new Button(customer.getName());
            button.getStyleClass().add("display-button");
            button.setId(Integer.toString(customer.getId()));
            buttonsContainer.getChildren().add(button);
            button.setOnAction(event -> {
                displayArea.getChildren().clear();
                CustomerDTO customer1 = (response.getSystemCustomers().get(Integer.parseInt(button.getId())));
                displayObject(customer1, displayArea);
                mainBorderPane.setRight(displayArea);
            });
        });

    }

    private void handleDisplayStores () {
        GetStoresResponse response = mainController.getStores();
        mainController.setCenterToDisplayInfoScrollPane();
        buttonsContainer.getChildren().clear();
        response.getStores().values().forEach(storeDTO -> {
            Button button = new Button(storeDTO.getName());
            button.getStyleClass().add("display-button");
            button.setId(Integer.toString(storeDTO.getId()));
            buttonsContainer.getChildren().add(button);
            button.setOnAction(event -> {
                displayArea.getChildren().clear();
                StoreDTO store = (response.getStores().get(Integer.parseInt(button.getId())));
                displayObject(store, displayArea);
                mainBorderPane.setRight(displayArea);
            });
        });
    }

    private void handleDisplayOrders () {
        GetOrdersResponse response = mainController.getOrders();
        mainController.setCenterToDisplayInfoScrollPane();
        buttonsContainer.getChildren().clear();
        if (response.getOrders().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Display Orders");
            alert.setHeaderText("There are no orders to display");
            alert.setContentText("No orders have been placed yet");
            alert.showAndWait();
        }

        response.getOrders().keySet().forEach(orderId -> {
            Button button = new Button(orderId.toString());
            button.getStyleClass().add("display-button");
            button.setId(orderId.toString());
            buttonsContainer.getChildren().add(button);
            button.setOnAction(event -> {
                displayArea.getChildren().clear();
                List<OrderDTO> order = (response.getOrders().get(UUID.fromString(button.getId())));
                displayArea.add(new Label(String.format("OrderId: %s", button.getId())), 0, 0);
                final int[] rowIndex = { 1 };
                order.forEach(staticOrder -> {
                    Accordion accordion = new Accordion();
                    GridPane gridObjects = new GridPane();
                    gridObjects.setHgap(10);
                    gridObjects.setVgap(10);
                    TitledPane titledPane = new TitledPane(staticOrder.getStoreName(), gridObjects);
                    accordion.getPanes().add(titledPane);
                    displayArea.add(accordion, 0, rowIndex[0]);
                    displayObject(staticOrder, gridObjects);
                    rowIndex[0]++;
                });
                mainBorderPane.setRight(displayArea);
            });
        });

    }

    private void displayObject (Object object, GridPane gridPane) {
        Field[] fields = object.getClass().getDeclaredFields();
        int rowIndex = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            Label key = new Label(field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + ":");
            gridPane.add(key, 0, rowIndex);
            if (field.getGenericType().getTypeName().contains("Map")) {
                try {
                    gridPane.add(displayList(new ArrayList<>(((HashMap<Integer, Object>) field.get(object)).values())), 1, rowIndex);
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            else if (field.getGenericType().getTypeName().contains("List")) {
                try {
                    gridPane.add(displayList(((ArrayList<Object>) field.get(object))), 1, rowIndex);
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Object realObject = field.get(object);
                    String textFieldValue = (realObject != null) ? realObject.toString() : null;
                    TextField value = new TextField(textFieldValue);
                    gridPane.add(value, 1, rowIndex);
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            rowIndex++;

        }
    }

    private Accordion displayList (List<Object> objects) {
        Accordion accordion = new Accordion();

        if (objects != null && !objects.isEmpty() && objects.get(0).getClass().getName().contains("StoreItem")) {
            objects.forEach(object -> {
                GridPane gridObjects = new GridPane();
                gridObjects.setHgap(10);
                gridObjects.setVgap(10);
                TitledPane titledPane = new TitledPane(((StoreItemDTO) object).getName(), gridObjects);
                accordion.getPanes().add(titledPane);
                displayObject(object, gridObjects);
            });
        }
        else if (objects != null && !objects.isEmpty() && objects.get(0).getClass().getName().contains("Order")) {
            objects.forEach(object -> {
                GridPane gridObjects = new GridPane();
                gridObjects.setHgap(10);
                gridObjects.setVgap(10);
                TitledPane titledPane = new TitledPane(((StoreOrderDTO) object).getId().toString(), gridObjects);
                accordion.getPanes().add(titledPane);
                displayObject(object, gridObjects);
            });
        }
        else if (objects != null && !objects.isEmpty() && objects.get(0).getClass().getName().contains("Discount")) {
            objects.forEach(object -> {
                GridPane gridObjects = new GridPane();
                gridObjects.setHgap(10);
                gridObjects.setVgap(10);
                TitledPane titledPane = new TitledPane(((DiscountDTO) object).getDiscountName(), gridObjects);
                accordion.getPanes().add(titledPane);
                displayObject(object, gridObjects);
            });
        }
        else if (objects != null && !objects.isEmpty() && objects.get(0).getClass().getName().contains("Offer")) {
            objects.forEach(object -> {
                GridPane gridObjects = new GridPane();
                gridObjects.setHgap(10);
                gridObjects.setVgap(10);
                TitledPane titledPane = new TitledPane(((OfferDTO) object).getOfferItemName(), gridObjects);
                accordion.getPanes().add(titledPane);
                displayObject(object, gridObjects);
            });
        }

        return accordion;
    }

    private void handleDisplayItems () {
        GetItemsResponse response = mainController.getItems();
        mainController.setCenterToDisplayInfoScrollPane();
        buttonsContainer.getChildren().clear();

        response.getItems().values().forEach(itemDTO -> {
            Button button = new Button(itemDTO.getName());
            button.getStyleClass().add("display-button");
            button.setId(Integer.toString(itemDTO.getId()));
            buttonsContainer.getChildren().add(button);
            button.setOnAction(event -> {
                displayArea.getChildren().clear();
                SystemItemDTO item = (response.getItems().get(Integer.parseInt(button.getId())));
                displayObject(item, displayArea);
                mainBorderPane.setRight(displayArea);
            });
        });
    }
}