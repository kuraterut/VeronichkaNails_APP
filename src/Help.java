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

import javafx.application.Application;
import javafx.stage.Stage;


import javafx.scene.Scene;
import javafx.scene.Group;

import javafx.scene.control.Alert.AlertType;

import javafx.scene.Node;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import javafx.scene.layout.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import javafx.scene.shape.Circle;


import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;


import javafx.animation.TranslateTransition;
import javafx.animation.FadeTransition;


import javafx.util.Duration;

import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.net.*;
import java.awt.*;



public class Help extends Application {

    @Override
    public void start(Stage primaryStage) {
        
        Scene scene = new Scene(loadRegistrationWindow(), 1000, 1000);
        primaryStage.setTitle("Auth");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public VBox loadAuthorizationWindow(){



        VBox root                       = new VBox(25);
        VBox fields_form                = new VBox(30);
        VBox lbls_form                  = new VBox(40);
        HBox auth_form                  = new HBox(25);
        HBox buttons                    = new HBox(50);
        
        Label head_label                = new Label("АВТОРИЗАЦИЯ");
        Label lbl_err                   = new Label("");
        Label login_lbl                 = new Label("Логин:  ");
        Label password_lbl              = new Label("Пароль: ");
        


        TextField login_field           = new TextField();
        PasswordField password_field    = new PasswordField();
        
        Button registration_btn         = new Button("Регистрация");
        Button authorization_btn        = new Button("Авторизоваться");

        
        auth_form.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);

        login_lbl.setTooltip(new Tooltip("Email или номер телефона(+7...)"));
        login_field.setPrefColumnCount(20);
        password_field.setPrefColumnCount(20);

        registration_btn.setPrefWidth(170);
        registration_btn.setPrefHeight(25);
        
        authorization_btn.setPrefWidth(170);
        authorization_btn.setPrefHeight(25);
        

        GridPane table_forms = new GridPane();

        table_forms.setAlignment(Pos.CENTER);
        table_forms.setVgap(15);
        table_forms.setHgap(50);
    
        table_forms.add(login_lbl, 0, 0);
        table_forms.add(password_lbl, 0, 1);
        table_forms.add(login_field, 1, 0);
        table_forms.add(password_field, 1, 1);

        
        // lbls_form.getChildren().addAll(login_lbl, password_lbl);
        // fields_form.getChildren().addAll(login_field, password_field);
        // auth_form.getChildren().addAll(lbls_form, fields_form);
        buttons.getChildren().addAll(registration_btn, authorization_btn);
        // root.getChildren().addAll(head_label, auth_form, lbl_err, buttons);
        root.getChildren().addAll(head_label, table_forms, buttons);
        
        return root;

    }


    public VBox loadRegistrationWindow(){
        VBox root                   = new VBox(25);
        VBox lbls_form              = new VBox(39);
        VBox fields_form            = new VBox(30);
        HBox reg_form               = new HBox(25);
        HBox buttons                = new HBox(150);

        Label head_label            = new Label("РЕГИСТРАЦИЯ");
        Label lbl_err               = new Label("");
        Label fio_lbl               = new Label("ФИО: ");
        Label nickname_lbl          = new Label("Как обращаться: ");
        Label email_lbl             = new Label("Email: ");
        Label phone_lbl             = new Label("Номер телефона(+7...): ");
        Label birthday_lbl          = new Label("Дата Рождения(ДД.ММ.ГГГГ): ");
        Label password_lbl          = new Label("Пароль: ");
        
        TextField fio_field         = new TextField();
        TextField nickname_field    = new TextField();
        TextField email_field       = new TextField();
        TextField phone_field       = new TextField();
        DatePicker birthday_field   = new DatePicker();
        TextField password_field    = new TextField();

        Button registration_btn     = new Button("Зарегистрироваться");
        Button authorization_btn    = new Button("Авторизация");
        

        fio_field.setPrefColumnCount(20);
        nickname_field.setPrefColumnCount(20);
        email_field.setPrefColumnCount(20);
        phone_field.setPrefColumnCount(20);
        password_field.setPrefColumnCount(20);

        registration_btn.setPrefWidth(170);
        registration_btn.setPrefHeight(25);
        
        authorization_btn.setPrefWidth(170);
        authorization_btn.setPrefHeight(25);

        head_label.setAlignment(Pos.CENTER);
        reg_form.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        
        GridPane table = new GridPane();
        table.setAlignment(Pos.CENTER);
        table.setVgap(30);
        table.setHgap(50);

        table.add(fio_lbl, 0, 0);
        table.add(nickname_lbl, 0, 1);
        table.add(email_lbl, 0, 2);
        table.add(phone_lbl, 0, 3);
        table.add(birthday_lbl, 0, 4);
        table.add(password_lbl, 0, 5);

        table.add(fio_field, 1, 0);
        table.add(nickname_field, 1, 1);
        table.add(email_field, 1, 2);
        table.add(phone_field, 1, 3);
        table.add(birthday_field, 1, 4);
        table.add(password_field, 1, 5);

        buttons.getChildren().addAll(authorization_btn, registration_btn);
        // fields_form.getChildren().addAll(fio_field, nickname_field, email_field, phone_field, birthday_field, password_field);
        // lbls_form.getChildren().addAll(fio_lbl, nickname_lbl, email_lbl, phone_lbl, birthday_lbl, password_lbl);
        // reg_form.getChildren().addAll(lbls_form, fields_form);
        root.getChildren().addAll(head_label, table, lbl_err, buttons);
        return root;
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
