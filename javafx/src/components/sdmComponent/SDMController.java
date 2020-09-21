package components.sdmComponent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

import components.app.AppController;
import components.placeOrderComponent.PlaceOrderController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logic.BusinessLogic;
import model.*;
import model.response.GetCustomersResponse;
import model.response.GetItemsResponse;
import model.response.GetOrdersResponse;
import model.response.GetStoresResponse;

public class SDMController {

    private AppController mainController;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Text sdmTitle;

    @FXML
    private Button loadFileButton;

    @FXML
    private ComboBox<String> menuBox;

    @FXML
    private ScrollPane centerScrollPane;

    @FXML
    private FlowPane buttonsContainer;

    @FXML
    private GridPane displayArea;

    @FXML
    private Text loadFileIndicator;

    private BorderPane placeOrderPane;

    private ScrollPane mapScrollPane;

    private GridPane mapGridPane;
    //
    // private HBox placeOrderBox;
    //
    // private ComboBox<String> customerBox;
    //
    // private DatePicker datePicker;
    //
    // private ComboBox<String> orderTypeBox;

    private PlaceOrderController placeOrderController;

    private ScrollPane itemsScrollPane = new ScrollPane();

    private TableView<PricedItemDTO> staticOrderItemsView = new TableView<>();

    private TableView<ItemDTO> dynamicOrderItemsView = new TableView<>();

    private SimpleBooleanProperty isFileSelected;
    private SimpleBooleanProperty isFileBeingLoaded;

    private SimpleStringProperty selectedFileProperty;

    private BusinessLogic businessLogic;
    private Stage primaryStage;
    final ObservableList<String> menuOptions = FXCollections.observableArrayList("Display Stores",
                                                                                 "Display Items",
                                                                                 "Display Customers",
                                                                                 "Display Orders",
                                                                                 "Place Order",
                                                                                 "Display Map");

    public SDMController () {
        isFileSelected = new SimpleBooleanProperty(false);
        selectedFileProperty = new SimpleStringProperty();
        isFileBeingLoaded = new SimpleBooleanProperty(false);

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
        categoryColumn.setMinWidth(20);
        // categoryColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseCategory"));

        dynamicOrderItemsView.getColumns().addAll(idColumn, nameColumn, categoryColumn);

        itemsScrollPane.setContent(dynamicOrderItemsView);

    }

    public void setMainController (AppController mainController) {
        this.mainController = mainController;
    }

    public void setBusinessLogic (BusinessLogic businessLogic) {
        this.businessLogic = businessLogic;
    }

    public void setPlaceOrderPane (BorderPane placeOrderPane) {
        this.placeOrderPane = placeOrderPane;
    }

    public void setMapScrollPane(ScrollPane mapScrollPane) {
        this.mapScrollPane = mapScrollPane;
    }

    public void setAppController (AppController appController) {
        this.mainController = appController;
    }

    public void setPrimaryStage (Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize () {
        menuBox.getItems().addAll(menuOptions);
        menuBox.disableProperty().bind(isFileSelected.not());
        loadFileIndicator.visibleProperty().bind(isFileBeingLoaded);
        AnchorPane.setLeftAnchor(displayArea, 5.0);

    }

    public void bindTaskToUIComponents (Task<Boolean> aTask, Runnable onFinish) {
        loadFileIndicator.textProperty().bind(aTask.messageProperty());
        aTask.valueProperty().addListener( (observable, oldValue, newValue) -> onTaskFinished(Optional.ofNullable(onFinish)));
    }

    public void onTaskFinished (Optional<Runnable> onFinish) {
        loadFileIndicator.textProperty().unbind();
        onFinish.ifPresent(Runnable::run);
    }

    public void setMapGridPane(GridPane mapGridPane) {
        this.mapGridPane = mapGridPane;
    }

    @FXML
    void loadFileButtonAction (ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
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
        businessLogic.loadFile(selectedFileProperty.getValue(), fileErrorConsumer, () -> {
            isFileSelected.set(true);
            isFileBeingLoaded.set(false);
        });
    }

    @FXML
    void menuBoxAction (ActionEvent event) {
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
            handlePlaceOrder();
            break;
        case "Display Map":
            handleDisplayMap();
            break;
        default:
            break;
        }

    }

    private void setCenterToButtonsContainer () {
        mainBorderPane.setCenter(centerScrollPane);
    }

    private void setCenterToPlaceOrder () {
        mainBorderPane.setCenter(centerScrollPane);
    }

    private void handlePlaceOrder () {
        // Consumer<PlaceOrderRequest> placeStaticOrderConsumer = new Consumer<PlaceOrderRequest>() {
        // @Override
        // public void accept(PlaceOrderRequest request) {
        // businessLogic.placeStaticOrder(request);
        // }
        // };
        mainBorderPane.setCenter(placeOrderPane);

    }

    private void handleDisplayMap () {
        Consumer<GridPane> addButtonToMapConsumer = gridPane -> mapGridPane.getChildren().addAll(gridPane.getChildren());
        businessLogic.createMap(addButtonToMapConsumer);
        mainBorderPane.setCenter(mapScrollPane);
    }

    private void handleDisplayCustomers () {
        GetCustomersResponse response = businessLogic.getCustomers();
        response.getSystemCustomers().values().forEach(customer -> {
            Button button = new Button(customer.getName());
            button.getStyleClass().add("display-button");
            button.setId(Integer.toString(customer.getId()));
            buttonsContainer.getChildren().add(button);
            button.setOnAction(event -> {
                displayArea.getChildren().clear();
                CustomerDTO customer1 = (response.getSystemCustomers().get(Integer.parseInt(button.getId())));
                displayObject(customer1, displayArea);
            });
        });

    }

    private void handleDisplayStores () {
        setCenterToButtonsContainer();
        GetStoresResponse response = businessLogic.getStores();
        response.getStores().values().forEach(storeDTO -> {
            Button button = new Button(storeDTO.getName());
            button.getStyleClass().add("display-button");
            button.setId(Integer.toString(storeDTO.getId()));
            buttonsContainer.getChildren().add(button);
            button.setOnAction(event -> {
                displayArea.getChildren().clear();
                StoreDTO store = (response.getStores().get(Integer.parseInt(button.getId())));
                displayObject(store, displayArea);
            });
        });
    }

    private void handleDisplayOrders () {
        setCenterToButtonsContainer();
        GetOrdersResponse response = businessLogic.getOrders();
        response.getOrders().keySet().forEach(orderId -> {
            Button button = new Button(orderId.toString());
            button.getStyleClass().add("display-button");
            button.setId(orderId.toString());
            buttonsContainer.getChildren().add(button);
            button.setOnAction(event -> {
                displayArea.getChildren().clear();
                List<OrderDTO> order = (response.getOrders().get(UUID.fromString(button.getId())));
                displayArea.add(new Label("Id"), 0, 0);
                displayArea.add(new TextField(button.getId()), 1, 0);
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
                    displayList(((ArrayList<Object>) field.get(object)));
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    TextField value = new TextField(field.get(object).toString());
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
            //
        }
        return accordion;
    }
    // private void handleDisplayOrders() {
    // GetOrdersResponse response = businessLogic.getOrders();
    // List<Button> buttonList = new ArrayList<>();
    // response.getOrders().values().forEach(order -> {
    // Button button = new Button(order.getId());
    // button.setOnAction(new EventHandler<ActionEvent>() {
    // @Override
    // public void handle(ActionEvent event) {
    // DisplayDetails.clear();
    // DisplayDetails.setText((response.getStores().get(Integer.parseInt(button.getText()))).toString());
    // }
    // });
    //
    // buttonList.add(button);
    // });
    //
    // }

    // private void handleDisplayCustomers() {
    // GetStoresResponse response = businessLogic.getStores();
    // List<Button> buttonList = new ArrayList<>();
    // response.getStores().values().forEach(storeDTO -> {
    // Button button = new Button(Integer.toString(storeDTO.getId()));
    // button.setOnAction(new EventHandler<ActionEvent>() {
    // @Override
    // public void handle(ActionEvent event) {
    // DisplayDetails.clear();
    // DisplayDetails.setText((response.getStores().get(Integer.parseInt(button.getText()))).toString());
    // }
    // });
    //
    // buttonList.add(button);
    // });
    //
    // }
    //
    private void handleDisplayItems () {
        buttonsContainer.getChildren().clear();
        GetItemsResponse response = businessLogic.getItems();
        response.getItems().values().forEach(itemDTO -> {
            Button button = new Button(itemDTO.getName());
            button.getStyleClass().add("display-button");
            button.setId(Integer.toString(itemDTO.getId()));
            buttonsContainer.getChildren().add(button);
            button.setOnAction(event -> {
                displayArea.getChildren().clear();
                SystemItemDTO item = (response.getItems().get(Integer.parseInt(button.getId())));
                displayObject(item, displayArea);
            });
        });
    }
}