import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

 
public class Main extends Application{
     
    public static void main(String[] args) {
         
        launch(args);
    }
     
    @Override
    public void start(Stage stage) throws FileNotFoundException {
        String guest_name = "Илья";


        Label main_window_head_label = new Label("Приветствуем, " + guest_name);

        Image main_window_avatar_image = new Image(new FileInputStream("photos/standard.jpg"));
        ImageView main_window_avatar_imageView = new ImageView(main_window_avatar_image);
        main_window_avatar_imageView.setFitHeight(300);
        main_window_avatar_imageView.setFitWidth(300);
        main_window_avatar_imageView.setPreserveRatio(true);


        Button main_window_btn_booking = new Button();
        main_window_btn_booking.setText("Запись");
        main_window_btn_booking.setPrefWidth(200);
        main_window_btn_booking.setPrefHeight(100);

        Button main_window_btn_history = new Button();
        main_window_btn_history.setText("История посещений");
        main_window_btn_history.setPrefWidth(200);
        main_window_btn_history.setPrefHeight(100);

        Button main_window_btn_promotions = new Button();
        main_window_btn_promotions.setText("Акции и скидки");
        main_window_btn_promotions.setPrefWidth(200);
        main_window_btn_promotions.setPrefHeight(100);
        


        VBox main_window_head = new VBox(20, main_window_head_label, main_window_avatar_imageView);
        main_window_head.setAlignment(Pos.CENTER);


        VBox main_window_buttons = new VBox(20, main_window_btn_booking, 
                                                main_window_btn_history, 
                                                main_window_btn_promotions);
        main_window_buttons.setAlignment(Pos.CENTER);


        VBox main_window_root = new VBox(100, main_window_head, main_window_buttons);        
        VBox.setMargin(main_window_head, new Insets(50, 10, 10, 10));
        

        Scene main_window_scene = new Scene(main_window_root);

                 

        // btn1.setOnAction(new EventHandler<ActionEvent>() {
             
        //     @Override
        //     public void handle(ActionEvent event) {
        //         stage.setScene(scene2);
        //     }
        // });

        // btn2.setOnAction(new EventHandler<ActionEvent>() {
             
        //     @Override
        //     public void handle(ActionEvent event) {
        //         stage.setScene(scene1);
        //     }
        // });

         
        stage.setScene(main_window_scene);
         
        stage.setTitle("VeronichkaNails_APP");
        stage.setWidth(1000);
        stage.setHeight(1000);
        // stage.setFullScreen(true);
         
        stage.show();
    }
}