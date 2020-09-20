package components.sdm;

import course.java.sdm.engine.controller.ISDMController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.StoreDTO;
import model.StoreItemDTO;
import model.StoreOrderDTO;
import model.SystemItemDTO;
import model.response.GetItemsResponse;
import model.response.GetStoresResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SDMController {

    @FXML
    private Text sdmTitle;

    @FXML
    private Button loadFileButton;

    @FXML
    private ComboBox<String> menuBox;

    @FXML
    private FlowPane buttonsContainer;

    @FXML
    private GridPane displayArea;

    private SimpleBooleanProperty isFileSelected;
    private SimpleStringProperty selectedFileProperty;

    private ISDMController businessLogic;
    private Stage primaryStage;
    final ObservableList<String> menuOptions =
            FXCollections.observableArrayList("Display Stores", "Display Items", "Display Customers", "Display Orders", "Place Order");

    public SDMController() {
        isFileSelected = new SimpleBooleanProperty(false);
        selectedFileProperty = new SimpleStringProperty();
    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setBusinessLogic(ISDMController businessLogic) {
        this.businessLogic = businessLogic;
    }

    @FXML
    private void initialize() {
        menuBox.getItems().addAll(menuOptions);
        menuBox.disableProperty().bind(isFileSelected.not());
//        menuBox.getItems().addAll(menuOptions);
//        buttonsContainer.getStyleClass().add("jfx-decorator-button
//        s-container");
        buttonsContainer.setMinSize(100, 100);
        AnchorPane.setLeftAnchor(displayArea, 5.0);
        sdmTitle.getStyleClass().add("components.sdm-title");

    }

    @FXML
    void loadFileButtonAction(ActionEvent event) throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("xml files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        selectedFileProperty.set(absolutePath);
        isFileSelected.set(true);
        businessLogic.loadFile(selectedFileProperty.getValue());
    }


    @FXML
    void menuBoxAction(ActionEvent event) {
        String selection = menuBox.getValue();
        switch (selection) {
            case "Display Stores":
                handleDisplayStores();
                break;
            case "Display Items":
                handleDisplayItems();
                break;
//            case "Display Customers":
//                handleDisplayCustomers();
//                break;
//            case "Display Orders":
//                handleDisplayOrders();
//                break;
//                case 6:
//                    handleSaveOrdersHistory();
//                    break;
//                case 7:
//                    handleLoadOrdersHistory();
//                    break;
            default:
                break;
        }

    }

    private void handleDisplayStores() {
        buttonsContainer.getChildren().clear();
        GetStoresResponse response = businessLogic.getStores();
        response.getStores().values().forEach(storeDTO -> {
            Button button = new Button(storeDTO.getName());
            button.getStyleClass().add("display-button");
            button.setId(Integer.toString(storeDTO.getId()));
            buttonsContainer.getChildren().add(button);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    displayArea.getChildren().clear();
                    StoreDTO store = (response.getStores().get(Integer.parseInt(button.getId())));
                    displayObject(store, displayArea);
                }
            });
        });
    }

    private void displayObject(Object object, GridPane gridPane) {
        Field[] fields = object.getClass().getDeclaredFields();
        int rowIndex = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            Label key = new Label(field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + ":");
            gridPane.add(key, 0, rowIndex);
//            ParameterizedType pt = (ParameterizedType) field.getGenericType();
//            Type concreteType = pt.getActualTypeArguments()[0];
            if (field.getGenericType().getTypeName().contains("Map")) {
                try {
                    gridPane.add(displayList(new ArrayList<>(((HashMap<Integer, Object>) field.get(object)).values())), 1, rowIndex);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
//                        if (field.getGenericType().equals()
            else if (field.getGenericType().getTypeName().contains("List")) {
                try {
                    displayList(((ArrayList<Object>) field.get(object)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    TextField value = new TextField(field.get(object).toString());
                    gridPane.add(value, 1, rowIndex);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            rowIndex++;

        }
    }

    private Accordion displayList(List<Object> objects) {
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
        } else if (objects != null && !objects.isEmpty() && objects.get(0).getClass().getName().contains("Order")) {
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
//    private void handleDisplayOrders() {
//        GetOrdersResponse response = businessLogic.getOrders();
//        List<Button> buttonList = new ArrayList<>();
//        response.getOrders().values().forEach(order -> {
//            Button button = new Button(order.getId());
//            button.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    DisplayDetails.clear();
//                    DisplayDetails.setText((response.getStores().get(Integer.parseInt(button.getText()))).toString());
//                }
//            });
//
//            buttonList.add(button);
//        });
//
//    }

    //    private void handleDisplayCustomers() {
//        GetStoresResponse response = businessLogic.getStores();
//        List<Button> buttonList = new ArrayList<>();
//        response.getStores().values().forEach(storeDTO -> {
//            Button button = new Button(Integer.toString(storeDTO.getId()));
//            button.setOnAction(new EventHandler<ActionEvent>() {
//                @Override
//                public void handle(ActionEvent event) {
//                    DisplayDetails.clear();
//                    DisplayDetails.setText((response.getStores().get(Integer.parseInt(button.getText()))).toString());
//                }
//            });
//
//            buttonList.add(button);
//        });
//
//    }
//
    private void handleDisplayItems() {
        buttonsContainer.getChildren().clear();
        GetItemsResponse response = businessLogic.getItems();
        response.getItems().values().forEach(itemDTO -> {
            Button button = new Button(itemDTO.getName());
            button.getStyleClass().add("display-button");
            button.setId(Integer.toString(itemDTO.getId()));
            buttonsContainer.getChildren().add(button);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    displayArea.getChildren().clear();
                    SystemItemDTO item = (response.getItems().get(Integer.parseInt(button.getId())));
                    displayObject(item, displayArea);
                }
            });
        });
    }

}