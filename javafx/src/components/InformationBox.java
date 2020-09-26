package components;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InformationBox {

    public void display (String title, String message) {
        Stage informationBox = new Stage();

        informationBox.initModality(Modality.APPLICATION_MODAL);
        informationBox.setTitle(title);
        informationBox.setMinWidth(250);

        Label label = new Label();
        label.setText(message);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> informationBox.close());

        ScrollBar s1 = new ScrollBar();
        s1.setOrientation(Orientation.VERTICAL);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton, s1);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        informationBox.setScene(scene);
        informationBox.showAndWait();
    }
}
