import components.sdm.SDMController;
import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.application.Application;

import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Super Duper Market");

        FXMLLoader loader = new FXMLLoader();

        // load main fxml
        URL mainFXML = getClass().getResource("components/sdm/SDM.fxml");
        loader.setLocation(mainFXML);
        BorderPane root = loader.load();

        // wire up controller
        SDMController sdmController = loader.getController();
        ISDMController businessLogic = new SDMControllerImpl();
        sdmController.setPrimaryStage(primaryStage);
        sdmController.setBusinessLogic(businessLogic);


        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


