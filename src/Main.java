import javafx.application.Application;
import javafx.stage.Stage;


import javafx.scene.Scene;
import javafx.scene.Group;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;


import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

 



public class Main extends Application{
    Properties client_properties = null;

     
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
        Label lbl_err = new Label("");

        Label login_lbl = new Label("Логин:  ");
        Label password_lbl = new Label("Пароль: ");
        login_lbl.setTooltip(new Tooltip("Email или номер телефона(+7...)"));
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
                    VerifyClientCodes verify_code = database.verifyClientInDB(login, password);
                    switch (verify_code){
                        case SUCCESS:
                            
                            client_properties.setProperty("login", login);
                            try(OutputStream out = Files.newOutputStream(Paths.get("sources/client_props.properties"))){
                                client_properties.store(out, "add info");
                            }
                            catch(Exception ex){System.out.println(ex);}

                            try{authorization_btn.getScene().setRoot(loadMainWindow());}
                            catch(Exception ex){System.out.println(ex);}
                            break;
                    
                        case NO_LOGIN:
                            lbl_err.setText("Такого логина еще нет, проверьте правильность или зарегестрируйтесь");
                            break;
                        
                        case PSW_INC:
                            lbl_err.setText("Неверный пароль, попробуйте снова");
                            login_field.clear();
                            password_field.clear();
                            break;
                        case DATABASE_CONN_ERR:
                            authorization_btn.getScene().setRoot(loadDataBaseErrorWindow());
                            break;
                    }
                }
            }
        });

        HBox buttons = new HBox(50, registration_btn, authorization_btn);

        FlowPane root = new FlowPane(Orientation.VERTICAL, 25, 25, head_label, auth_form, lbl_err, buttons);
        
        head_label.setAlignment(Pos.CENTER);
        auth_form.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        
        return root;

    }

    public FlowPane loadRegistrationWindow(){
        FlowPane head_label = new FlowPane(new Label("РЕГИСТРАЦИЯ"));
        Label lbl_err = new Label("");

        Label fio_lbl      = new Label("ФИО: ");
        Label nickname_lbl = new Label("Как обращаться: ");
        Label email_lbl    = new Label("Email: ");
        Label phone_lbl    = new Label("Номер телефона(+7...): ");
        Label birthday_lbl = new Label("Дата Рождения(ДД.ММ.ГГГГ): ");
        Label password_lbl = new Label("Пароль: ");
        
        VBox lbls_form = new VBox(39, fio_lbl, nickname_lbl, email_lbl, phone_lbl, birthday_lbl, password_lbl);


        TextField fio_field      = new TextField();
        TextField nickname_field = new TextField();
        TextField email_field    = new TextField();
        TextField phone_field    = new TextField();
        DatePicker birthday_field= new DatePicker();
        TextField password_field = new TextField();

        fio_field.setPrefColumnCount(20);
        nickname_field.setPrefColumnCount(20);
        email_field.setPrefColumnCount(20);
        phone_field.setPrefColumnCount(20);
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
            }
        });

        registration_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DB database = new DB();
                try{database.getConnection();}
                catch(Exception ex){System.out.println(ex);}
                
                if (database.db_conn == null){
                    authorization_btn.getScene().setRoot(loadDataBaseErrorWindow());
                }
                else{
                    String fio      = fio_field.getText();
                    String nickname = nickname_field.getText();
                    String email    = email_field.getText();
                    String phone    = phone_field.getText();
                    String password = password_field.getText();
                    String birthday;
                    try{birthday = birthday_field.getValue().toString();}
                    catch(Exception ex){lbl_err.setText("Заполните поле дата рождения корректно"); return;}

                    InsertClientCodes insert_code = database.insertNewClient(fio, nickname, email, phone, birthday, password);

                    switch (insert_code){
                        case SUCCESS:
                            
                            client_properties.setProperty("login", email);
                            try(OutputStream out = Files.newOutputStream(Paths.get("sources/client_props.properties"))){
                                client_properties.store(out, "add info");
                            }
                            catch(Exception ex){System.out.println(ex);}

                            try{registration_btn.getScene().setRoot(loadMainWindow());}
                            catch(Exception ex){System.out.println(ex);}
                            break;

                        case IN_BASE:
                            lbl_err.setText("Аккаунт с такими данными уже есть. Авторизуйтесь или используйте другие данные");
                            email_field.clear();
                            password_field.clear();
                            phone_field.clear();
                            break;
                    
                        case DATABASE_CONN_ERR:
                            authorization_btn.getScene().setRoot(loadDataBaseErrorWindow());
                            break;

                        case BIRTH_ERR:
                            lbl_err.setText("Неправильно написана Дата Рождения. Вам должно быть более 1 года и меньше 150 лет!");
                            break;

                        case EMAIL_ERR:
                            lbl_err.setText("Некорректно написан Email(не более 50 символов)");
                            email_field.clear();
                            break;

                        case PHONE_ERR:
                            lbl_err.setText("Некорректно написан телефон. Только номера из РФ(+7... и еще 10 цифр)");
                            phone_field.clear();
                            break;

                        case PSW_ERR:
                            lbl_err.setText("Пароль должен состоять не менее чем из 8 символов и не более чем из 20");
                            password_field.clear();
                            break;
                        case FILL_FIELD:
                            lbl_err.setText("Пожалуйста, заполните все поля для регистрации");
                            break;
                    }
                }
            }
        });

        HBox buttons = new HBox(150, authorization_btn, registration_btn);

        FlowPane root = new FlowPane(Orientation.VERTICAL, 25, 25, head_label, reg_form, lbl_err, buttons);
        
        head_label.setAlignment(Pos.CENTER);
        reg_form.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        
        return root;

    }

    public BorderPane loadMainWindow() throws FileNotFoundException{
        // String guest_name = "Илья";
        DB database = new DB();
        try{database.getConnection();}
        catch(Exception ex){System.out.println(ex);}
        String guest_name = database.getNickname(client_properties.getProperty("login", "No"));

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


        VBox central_box = new VBox(100, head_box, buttons_box);        
        VBox.setMargin(head_box, new Insets(50, 10, 10, 10));
        BorderPane.setAlignment(central_box, Pos.CENTER);

        Button exit_btn = new Button("Выход");
        BorderPane.setMargin(exit_btn, new Insets(50, 50, 0, 50));
        BorderPane.setAlignment(exit_btn, Pos.TOP_RIGHT);
        
        BorderPane root = new BorderPane();
        root.setCenter(central_box);
        root.setTop(exit_btn);

        btn_booking.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{btn_booking.getScene().setRoot(loadBookingWindow());}
                catch(FileNotFoundException e){System.out.println("No file");} 
            }
        });

        exit_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exit_btn.getScene().setRoot(loadAuthorizationWindow());
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
    public void start(Stage stage) throws FileNotFoundException, IOException{
        
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("sources/client_props.properties"))){
            props.load(in);
        }
        client_properties = props;
        String username = client_properties.getProperty("login", "No");
        if (username.equals("No")){stage.setScene(new Scene(loadAuthorizationWindow()));}
        else{stage.setScene(new Scene(loadMainWindow()));}
        
         
        stage.setTitle("VeronichkaNails_APP");
        stage.setWidth(1000);
        stage.setHeight(1000);
         
        stage.show();
    }
}
