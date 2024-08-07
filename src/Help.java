import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

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
        // Для простоты возвращаем фиксированное значение
        // В реальном приложении вы можете использовать TextInputDialog или другие средства ввода
        return 5; // Например, возвращаем 5 кнопок
    }
}