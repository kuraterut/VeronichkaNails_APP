import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
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

 
public class Main extends Application{
     
    public static void main(String[] args) throws FileNotFoundException{
        
        launch(args);
    }

    public FlowPane loadDataBaseErrorWindow(){
        Label is_valid = new Label("Ошибка подключения к базе данных. Повторите попытку");
        final Button btn = new Button("Повторить");
        btn.setPrefWidth(200);
        btn.setPrefHeight(100);
        FlowPane root = new FlowPane(Orientation.VERTICAL, 25, 25, is_valid, btn);
        root.setAlignment(Pos.CENTER);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                btn.getScene().setRoot(loadAuthorizationWindow());
                // try{registration_btn.getScene().setRoot(loadRegistrationWindow());}
                // catch(FileNotFoundException e){System.out.println("No file");} 
            }
        });

        return root;
    }

    public FlowPane loadAuthorizationWindow(){
        FlowPane head_label = new FlowPane(new Label("АВТОРИЗАЦИЯ"));
        Label is_valid = new Label("");

        Label login_lbl = new Label("Логин:  ");
        Label password_lbl = new Label("Пароль: ");
        login_lbl.setTooltip(new Tooltip("Email или номер телефона"));
        VBox lbls_form = new VBox(40, login_lbl, password_lbl);

        TextField login_field = new TextField();
        login_field.setPrefColumnCount(20);
        PasswordField password_field = new PasswordField();
        password_field.setPrefColumnCount(20);
        VBox fields_form = new VBox(30, login_field, password_field);


        HBox auth_form = new HBox(25, lbls_form, fields_form);


        final Button registration_btn = new Button("Регистрация");
        registration_btn.setPrefWidth(170);
        registration_btn.setPrefHeight(25);
        
        final Button authorization_btn = new Button("Авторизоваться");
        authorization_btn.setPrefWidth(170);
        authorization_btn.setPrefHeight(25);

        registration_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                registration_btn.getScene().setRoot(loadRegistrationWindow());
                // try{registration_btn.getScene().setRoot(loadRegistrationWindow());}
                // catch(FileNotFoundException e){System.out.println("No file");} 
            }
        });

        authorization_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DB database = new DB();
                try{database.getConnection();}
                catch(Exception ex){System.out.println(ex);}
                
                if (database.db_conn == null){
                    authorization_btn.getScene().setRoot(loadDataBaseErrorWindow());
                }
                else{
                    String login    = login_field.getText();
                    String password = password_field.getText();
                    int verify_code = database.verifyClientInDB(login, password);
                    switch (verify_code){
                        case (0):
                            try{authorization_btn.getScene().setRoot(loadMainWindow());}
                            catch(Exception ex){System.out.println(ex);}
                            break;
                    
                        case (1):
                            is_valid.setText("Такого логина еще нет, проверьте правильность или зарегестрируйтесь");
                            break;
                        
                        case (2):
                            is_valid.setText("Неверный пароль, попробуйте снова");
                            login_field.clear();
                            password_field.clear();
                            break;
                        case (3):
                            authorization_btn.getScene().setRoot(loadDataBaseErrorWindow());
                            break;
                    }
                }
                
                // try{registration_btn.getScene().setRoot(loadRegistrationWindow());}
                // catch(FileNotFoundException e){System.out.println("No file");} 
            }
        });

        HBox buttons = new HBox(50, registration_btn, authorization_btn);

        FlowPane root = new FlowPane(Orientation.VERTICAL, 25, 25, head_label, auth_form, is_valid, buttons);
        
        head_label.setAlignment(Pos.CENTER);
        auth_form.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        
        return root;

    }

    public FlowPane loadRegistrationWindow(){
        FlowPane head_label = new FlowPane(new Label("РЕГИСТРАЦИЯ"));
        Label is_valid = new Label("");

        Label fio_lbl      = new Label("ФИО: ");
        Label nickname_lbl = new Label("Как обращаться: ");
        Label email_lbl    = new Label("Email: ");
        Label phone_lbl    = new Label("Номер телефона: ");
        Label birthday_lbl = new Label("Дата Рождения(ДД.ММ.ГГГГ): ");
        Label password_lbl = new Label("Пароль: ");
        
        VBox lbls_form = new VBox(39, fio_lbl, nickname_lbl, email_lbl, phone_lbl, birthday_lbl, password_lbl);


        TextField fio_field      = new TextField();
        TextField nickname_field = new TextField();
        TextField email_field    = new TextField();
        TextField phone_field    = new TextField();
        TextField birthday_field = new TextField();
        TextField password_field = new TextField();

        fio_field.setPrefColumnCount(20);
        nickname_field.setPrefColumnCount(20);
        email_field.setPrefColumnCount(20);
        phone_field.setPrefColumnCount(20);
        birthday_field.setPrefColumnCount(20);
        password_field.setPrefColumnCount(20);

        VBox fields_form = new VBox(30, fio_field, nickname_field, email_field, phone_field, birthday_field, password_field);


        HBox reg_form = new HBox(25, lbls_form, fields_form);


        final Button registration_btn = new Button("Зарегистрироваться");
        registration_btn.setPrefWidth(170);
        registration_btn.setPrefHeight(25);
        
        final Button authorization_btn = new Button("Авторизация");
        authorization_btn.setPrefWidth(170);
        authorization_btn.setPrefHeight(25);

        authorization_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                authorization_btn.getScene().setRoot(loadAuthorizationWindow());
                // try{registration_btn.getScene().setRoot(loadRegistrationWindow());}
                // catch(FileNotFoundException e){System.out.println("No file");} 
            }
        });

        HBox buttons = new HBox(150, authorization_btn, registration_btn);

        FlowPane root = new FlowPane(Orientation.VERTICAL, 25, 25, head_label, reg_form, is_valid, buttons);
        
        head_label.setAlignment(Pos.CENTER);
        reg_form.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        
        return root;

    }

    public VBox loadMainWindow() throws FileNotFoundException{
        String guest_name = "Илья";
        Label head_label = new Label("Приветствуем, " + guest_name);

        Image avatar_image = new Image(new FileInputStream("photos/standard.jpg"));
        ImageView avatar_imageView = new ImageView(avatar_image);
        avatar_imageView.setFitHeight(300);
        avatar_imageView.setFitWidth(300);
        avatar_imageView.setPreserveRatio(true);


        final Button btn_booking = new Button();
        btn_booking.setText("Запись");
        btn_booking.setPrefWidth(200);
        btn_booking.setPrefHeight(100);

        final Button btn_history = new Button();
        btn_history.setText("История посещений");
        btn_history.setPrefWidth(200);
        btn_history.setPrefHeight(100);

        final Button btn_promotions = new Button();
        btn_promotions.setText("Акции и скидки");
        btn_promotions.setPrefWidth(200);
        btn_promotions.setPrefHeight(100);
        


        VBox head_box = new VBox(20, head_label, avatar_imageView);
        head_box.setAlignment(Pos.CENTER);


        VBox buttons_box = new VBox(20, btn_booking, 
                                        btn_history, 
                                        btn_promotions);
        buttons_box.setAlignment(Pos.CENTER);


        VBox root = new VBox(100, head_box, buttons_box);        
        VBox.setMargin(head_box, new Insets(50, 10, 10, 10));

        btn_booking.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{btn_booking.getScene().setRoot(loadBookingWindow());}
                catch(FileNotFoundException e){System.out.println("No file");} 
            }
        });

        return root;
    }

    public VBox loadBookingWindow() throws FileNotFoundException{
        Label head_label = new Label("Это окно для записей");
        final Button back_btn = new Button("Назад");
        VBox root = new VBox(100, head_label, back_btn);

        back_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{back_btn.getScene().setRoot(loadMainWindow());}
                catch(FileNotFoundException e){System.out.println("No file");}
            }
        });
        return root;
    }


    @Override
    public void start(Stage stage) throws FileNotFoundException{
        // Scene main_window_scene = create_main_window(stage);
         
        // stage.setFullScreen(true);
        stage.setScene(new Scene(loadAuthorizationWindow()));
        // stage.setScene(new Scene(loadMainWindow()));
         
        stage.setTitle("VeronichkaNails_APP");
        stage.setWidth(1000);
        stage.setHeight(1000);
         
        stage.show();
    }
}

class DB {
    int connection_code; //0-не подключался; 1-успешно подключено; 2-ошибка подключения
    Connection db_conn;
    public DB(){
        this.connection_code = 0;
        this.db_conn = null;
    }

    public void getConnection() throws SQLException, IOException{
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("sources/database.properties"))){
            props.load(in);
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
        try {
            this.db_conn = DriverManager.getConnection(url, username, password);
            this.connection_code = 1;
            System.out.println("Connection to VeronichkaNailsApp DB succesfull!");
        } 
        catch(Exception ex){
            this.connection_code = 2;

            System.out.println("Connection to VeronichkaNailsApp failed...");
            System.out.println(ex);
            this.db_conn = null;
        }
    }

    public int verifyClientInDB(String login, String password){ // 0-успешно; 1-нет человека; 2-пароль не тот; 3-ошибка получения данных
        try{
            String sqlST = "SELECT * FROM CLIENTS WHERE Client_LOGIN = ? OR Client_PHONE = ?";
            PreparedStatement prep_statement = this.db_conn.prepareStatement(sqlST);
            prep_statement.setString(1, login);
            prep_statement.setString(2, login);
            ResultSet res = prep_statement.executeQuery();
            if(res.next() == false){
                return 1;
            }
            else{
                if(password == res.getString("Client_PSW")){
                    return 0;
                }
                else{
                    return 2;
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex);
            return 3;
        }
    }

}