package components.app;

import java.net.URL;

import components.mapComponent.MapController;
import components.placeOrderComponent.PlaceOrderController;
import components.sdmComponent.SDMController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import logic.BusinessLogic;

public class Main extends Application {

    @Override
    public void start (Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/components/sdmComponent/SDM.fxml");
        fxmlLoader.setLocation(url);
        BorderPane sdmComponent = fxmlLoader.load(url.openStream());
        SDMController sdmController = fxmlLoader.getController();

        // load place order component and controller from fxml
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/components/placeOrderComponent/placeOrder.fxml");
        fxmlLoader.setLocation(url);
        BorderPane placeOrderComponent = fxmlLoader.load(url.openStream());
        PlaceOrderController placeOrderController = fxmlLoader.getController();

        // load place order component and controller from fxml
        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/components/mapComponent/Map.fxml");
        fxmlLoader.setLocation(url);
        ScrollPane mapComponent = fxmlLoader.load(url.openStream());
        MapController mapController = fxmlLoader.getController();

        // load sdm component and controller from fxml

        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("/components/app/app.fxml");
        fxmlLoader.setLocation(url);
        ScrollPane root = fxmlLoader.load(url.openStream());
        root.setContent(sdmComponent);
        AppController appController = fxmlLoader.getController();

        setAppControllerConfig(sdmController, placeOrderComponent, placeOrderController, mapComponent, mapController, appController);

        sdmController.setAppController(appController);
        mapController.setMainController(appController);
        placeOrderController.setMainController(appController);

        // set stage
        primaryStage.setTitle("Super Duper Market");
        Scene scene = new Scene(root, 780, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setAppControllerConfig(SDMController sdmController, BorderPane placeOrderComponent, PlaceOrderController placeOrderController, ScrollPane mapComponent, MapController mapController, AppController appController) {
        BusinessLogic businessLogic = new BusinessLogic(appController);
        appController.setBusinessLogic(businessLogic);

        appController.setMapComponentController(mapController);
        appController.setPlaceOrderComponentController(placeOrderController);
        appController.setSdmComponentController(sdmController);
        appController.setPlaceOrderPane(placeOrderComponent);
        appController.setMapScrollPane(mapComponent);
        appController.setMapGridPane(mapController.getLocationsGridPane());
        appController.setMainBorderPane(sdmController.getMainBorderPane());
    }

    public static void main (String[] args) {
        launch(args);
    }
}
