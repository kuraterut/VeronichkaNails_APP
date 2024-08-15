import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Help extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button openLinkButton = new Button("Открыть StackOverflow");
        openLinkButton.setOnAction(event -> new Thread(() -> openLink("https://ru.stackoverflow.com/")).start());

        StackPane root = new StackPane();
        root.getChildren().add(openLinkButton);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setTitle("Open Link Example");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openLink(String url) {
        String[] command = { "xdg-open", url };
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
