import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.DatePicker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.time.*;
 
public class Help extends Application{
       

    public static void main(String[] args) throws FileNotFoundException{
        
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws FileNotFoundException{
        // Scene main_window_scene = create_main_window(stage);
        DatePicker field = new DatePicker();
        Button btn = new Button("Click");
        Label lbl = new Label(" ");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                LocalDate date = field.getValue();
                lbl.setText(date.toString());
            }
        });
        FlowPane root = new FlowPane(field, btn, lbl);
        // stage.setFullScreen(true);
        stage.setScene(new Scene(root));
        // stage.setScene(new Scene(loadMainWindow()));
         
        stage.setTitle("VeronichkaNails_APP");
        stage.setWidth(1000);
        stage.setHeight(1000);
         
        stage.show();
    }
     
    
}