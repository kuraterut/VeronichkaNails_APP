import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.*;

import java.util.*;
public class Help extends Application {
    private int numButtons;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        numButtons = getUserInput();  // Получить количество кнопок от пользователя

        primaryStage.setTitle("Main Page");
        VBox vbox = new VBox();

        for (int i = 1; i <= numButtons; i++) {
            Button button = new Button("Button " + i);
            int pageNumber = i;
            button.setOnAction(e -> openPage(primaryStage, pageNumber));
            vbox.getChildren().add(button);
        }

        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openPage(Stage stage, int pageNumber) {
        Label label = new Label("Label " + pageNumber);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(label);

        Scene scene = new Scene(stackPane, 300, 250);
        stage.setScene(scene);
        stage.show();
    }

    private int getUserInput() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Input Required");
        alert.setHeaderText("Enter the number of buttons:");
        alert.setContentText("Number of buttons:");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL){
            return 0;
        }
        else if (option.get() == ButtonType.OK){
            return 1;
        }
        return 2;
    }
}