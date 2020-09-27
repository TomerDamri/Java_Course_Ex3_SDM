package components.editItemsComponent;

import java.util.Map;

import components.app.AppController;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.StoreDTO;
import model.SystemItemDTO;
import model.request.BaseUpdateStoreRequest;
import model.request.UpdateStoreRequest;
import model.response.DeleteItemFromStoreResponse;

public class EditItemsController {

    public void setMainController (AppController mainController) {
        this.mainController = mainController;
    }

    private AppController mainController;

    @FXML
    private VBox editItemsVBox;

    @FXML
    private ComboBox<String> storesComboBox;

    @FXML
    private ComboBox<String> editItemsOptionsComboBox;

    @FXML
    private ComboBox<String> itemsComboBox;

    @FXML
    private Label newPriceLabel;

    @FXML
    private TextField newPriceTextField;
    @FXML
    private Button submitEditItemButton;

    private SimpleBooleanProperty isStoreSelected;
    private SimpleBooleanProperty isActionSelected;
    private SimpleBooleanProperty isUpdatePriceAction;
    private SimpleBooleanProperty isAddItemAction;
    private SimpleBooleanProperty isDeleteItemAction;
    private SimpleBooleanProperty isItemSelected;
    private SimpleBooleanProperty isNewPriceNeeded;
    private SimpleBooleanProperty isPriceSelected;
    private Map<Integer, StoreDTO> stores;
    private Map<Integer, SystemItemDTO> items;

    private int selectedStoreId;
    private int selectedItemId;

    private String selectedStoreName;
    private String selectedItemName;

    public EditItemsController () {
        isStoreSelected = new SimpleBooleanProperty(false);
        isActionSelected = new SimpleBooleanProperty(false);
        isUpdatePriceAction = new SimpleBooleanProperty(false);
        isAddItemAction = new SimpleBooleanProperty(false);
        isDeleteItemAction = new SimpleBooleanProperty(false);
        isItemSelected = new SimpleBooleanProperty(false);
        isNewPriceNeeded = new SimpleBooleanProperty(false);
        isPriceSelected = new SimpleBooleanProperty(false);

    }

    @FXML
    public void initialize () {
        newPriceLabel.visibleProperty().bind(isNewPriceNeeded);
        newPriceTextField.visibleProperty().bind(isNewPriceNeeded);
        editItemsOptionsComboBox.disableProperty().bind(isStoreSelected.not());
        itemsComboBox.visibleProperty().bind(isActionSelected);
        submitEditItemButton.visibleProperty().bind(isItemSelected);
        newPriceTextField.setOnAction( (event) -> {
            isPriceSelected.setValue(true);
        });
    }

    @FXML
    void editItemsOptionsComboBoxAction (ActionEvent event) {
        if (editItemsOptionsComboBox.getValue() != null) {
            isActionSelected.set(true);
            String selection = editItemsOptionsComboBox.getValue();
            switch (selection) {
            case "Add Item To Store":
                isNewPriceNeeded.set(true);
                isAddItemAction.set(true);
                handleAddItem();
                break;
            case "Delete Item From Store":
                isDeleteItemAction.set(true);
                handleDeleteItem();
                break;
            case "Update Item Price In Store":
                isNewPriceNeeded.set(true);
                isUpdatePriceAction.set(true);
                handleUpdateItemPrice();
                break;
            default:
                break;

            }
        }
    }

    @FXML
    void itemsComboBoxAction (ActionEvent event) {
        if (itemsComboBox.getValue() != null) {
            isItemSelected.set(true);
            selectedItemId = Integer.parseInt(itemsComboBox.getValue().substring(4, 5));
            selectedItemName = items.get(selectedItemId).getName();
        }
    }

    private void addItem () {
        // todo consider change price to double
        try {
            int price = Integer.parseInt(newPriceTextField.getText());
            UpdateStoreRequest request = new UpdateStoreRequest(selectedStoreId, selectedItemId, price);
            mainController.addItemToStore(request);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(String.format("%s was successfully added to %s", selectedItemName, selectedStoreName));
            alert.showAndWait();
        }
        catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Price");
            alert.setContentText("The price should be an integer");
            alert.showAndWait();
        }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        finally {
            resetEditItemsComponent();
        }
    }

    private void deleteItem () {
        try {
            BaseUpdateStoreRequest request = new BaseUpdateStoreRequest(selectedStoreId, selectedItemId);
            DeleteItemFromStoreResponse response = mainController.deleteItemFromStore(request);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (response.getRemovedDiscounts() != null && !response.getRemovedDiscounts().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder("Deleting ").append(selectedItemName)
                                                                            .append(" caused deletion of the following discounts:\n");
                response.getRemovedDiscounts().forEach(discountDTO -> stringBuilder.append(discountDTO.getDiscountName()).append("\n"));
                alert.setContentText(stringBuilder.toString());
            }
            alert.setHeaderText(String.format("%s was successfully deleted from %s", selectedItemName, selectedStoreName));
            alert.showAndWait();
        }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        finally {
            resetEditItemsComponent();
        }

    }

    private void updateItemPrice () {
        try {
            int price = Integer.parseInt(newPriceTextField.getText());
            UpdateStoreRequest request = new UpdateStoreRequest(selectedStoreId, selectedItemId, price);
            mainController.updatePriceOfSelectedItem(request);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(String.format("%s price was successfully updated to %s", selectedItemName, price));
            alert.showAndWait();
        }
        catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Price");
            alert.setContentText("The price should be an integer");
            alert.showAndWait();
        }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
        finally {
            resetEditItemsComponent();
        }

    }

    @FXML
    void storesComboBoxAction (ActionEvent event) {
        if (storesComboBox.getValue() != null) {
            isStoreSelected.set(true);
            selectedStoreId = Integer.parseInt(storesComboBox.getValue().substring(4, 5));
            selectedStoreName = stores.get(selectedStoreId).getName();
        }
    }

    @FXML
    void submitEditItemButtonAction (ActionEvent event) {
        if (isDeleteItemAction.get()) {
            deleteItem();
        }
        else if (newPriceTextField.getText() != null) {
            if (isAddItemAction.get()) {
                addItem();
            }
            else if (isUpdatePriceAction.get()) {
                updateItemPrice();
            }
        }

    }

    private void handleUpdateItemPrice () {
        itemsComboBox.getItems().addAll(mainController.getItemsInStoreObservableList(selectedStoreId));

    }

    private void handleDeleteItem () {
        itemsComboBox.getItems().addAll(mainController.getItemsInStoreObservableList(selectedStoreId));
    }

    private void handleAddItem () {
        itemsComboBox.setItems(mainController.getItemsNotInStoreObservableList(selectedStoreId));
    }

    public void resetEditItemsComponent () {

        isStoreSelected.set(false);
        isActionSelected.set(false);
        isUpdatePriceAction.set(false);
        isAddItemAction.set(false);
        isDeleteItemAction.set(false);
        isItemSelected.set(false);
        isNewPriceNeeded.set(false);
        itemsComboBox.getItems().clear();
        newPriceTextField.setText("");

        selectedStoreId = 0;
        selectedItemId = 0;
        selectedStoreName = null;
        selectedItemName = null;
        stores = null;
        items = null;
        startEditItems();

    }

    public void initComboBoxes () {
        storesComboBox.getItems().clear();
        editItemsOptionsComboBox.getItems().clear();
        storesComboBox.getItems().addAll(mainController.getStoresList());
        editItemsOptionsComboBox.getItems()
                                .addAll(FXCollections.observableArrayList("Add Item To Store",
                                                                          "Delete Item From Store",
                                                                          "Update Item Price In Store"));
    }

    public void startEditItems () {
        stores = mainController.getStores().getStores();
        items = mainController.getItems().getItems();
        initComboBoxes();

    }
}