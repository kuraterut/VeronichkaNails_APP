import javafx.application.Application;
import javafx.stage.Stage;


import javafx.scene.Scene;
import javafx.scene.Group;

import javafx.scene.control.Alert.AlertType;

import javafx.scene.Node;

import javafx.scene.input.MouseEvent;    

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
import javafx.geometry.VPos;
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




 



public class Main extends Application{
    Properties client_properties = null;
    private static final double MENU_WIDTH = 150; // Ширина меню
    boolean isMenuVisible = true; // Флаг видимости меню
    ClientInfo client_info;
    GridPane employees_table;
    DB database;
    String about_url = "https://ru.stackoverflow.com/";
    int galery_images_num;
     
    public static void main(String[] args) throws FileNotFoundException{
        launch(args);
    }

    public VBox loadDataBaseErrorWindow(){
        VBox root           = new VBox();

        Label is_valid      = new Label();
        
        Button back_btn     = new Button();


        root.setSpacing(25);

        is_valid.setText("Ошибка подключения к базе данных. Повторите попытку");
        back_btn.setText("Повторить");
        
        back_btn.setPrefWidth(200);
        back_btn.setPrefHeight(100);

        root.setAlignment(Pos.CENTER);

        back_btn.setOnAction(event->HelpFuncs.loadAuthorizationWindowFunc(back_btn, this));


        root.getChildren().addAll(is_valid, back_btn);        
        return root;
    }

    public VBox loadAuthorizationWindow(){
        VBox root                       = new VBox();
        HBox buttons                    = new HBox();
        GridPane table                  = new GridPane();
        
        Label head_lbl                  = new Label();
        Label lbl_err                   = new Label();
        Label login_lbl                 = new Label();
        Label password_lbl              = new Label();
        
        TextField login_field           = new TextField();
        PasswordField password_field    = new PasswordField();
        
        Button registration_btn         = new Button();
        Button authorization_btn        = new Button();


        root.setSpacing(25);
        buttons.setSpacing(50);

        lbl_err.setText("");
        head_lbl.setText("АВТОРИЗАЦИЯ");
        login_lbl.setText("Логин");
        password_lbl.setText("Пароль");
        registration_btn.setText("Регистрация");
        authorization_btn.setText("Авторизация");

        root.setAlignment(Pos.CENTER);
        table.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        
        table.setVgap(15);
        table.setHgap(50);
    
        login_lbl.setTooltip(new Tooltip("Email или номер телефона"));
        
        login_field.setPrefColumnCount(20);
        password_field.setPrefColumnCount(20);

        registration_btn.setPrefWidth(170);
        authorization_btn.setPrefWidth(170);
        
        registration_btn.setPrefHeight(25);
        authorization_btn.setPrefHeight(25);

        registration_btn.setOnAction(event->HelpFuncs.loadRegistrationWindowFunc(registration_btn, this));
        authorization_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String login    = login_field.getText();
                String password = password_field.getText();

                VerifyClientCodes verify_code = database.verifyClientInDB(-1, login, password);
                switch (verify_code){
                    case SUCCESS:
                        lbl_err.setText("");
                        
                        client_properties.setProperty("login", login);
                        client_info = database.getClientInfoByLogin(login);
                        database.createClientAvatar(client_info.client_id);
                        
                        try{
                            OutputStream out = Files.newOutputStream(Paths.get("sources/client_props.properties"));
                            client_properties.store(out, "add info");
                        }
                        catch(Exception ex){System.out.println(ex);}

                        authorization_btn.getScene().setRoot(loadMainWindow());
                        break;
                
                    case NO_LOGIN:
                        lbl_err.setText("Такого логина еще нет, проверьте правильность или зарегестрируйтесь");
                        break;
                    
                    case PSW_INC:
                        lbl_err.setText("Неверный пароль, попробуйте снова");
                        password_field.clear();
                        break;

                    case DATABASE_CONN_ERR:
                        authorization_btn.getScene().setRoot(loadDataBaseErrorWindow());
                        break;
                
                }
            }
        });


        table.add(login_lbl, 0, 0);
        table.add(password_lbl, 0, 1);
        table.add(login_field, 1, 0);
        table.add(password_field, 1, 1);
        
        root.getChildren().addAll(head_lbl, table, lbl_err, buttons);
        buttons.getChildren().addAll(registration_btn, authorization_btn);
        return root;

    }

    public VBox loadRegistrationWindow(){
        VBox root                   = new VBox();
        HBox buttons                = new HBox();
        GridPane table              = new GridPane();

        Label head_lbl              = new Label();
        Label lbl_err               = new Label();
        Label fio_lbl               = new Label();
        Label nickname_lbl          = new Label();
        Label email_lbl             = new Label();
        Label phone_lbl             = new Label();
        Label birthday_lbl          = new Label();
        Label password_lbl          = new Label();
        
        TextField fio_field         = new TextField();
        TextField nickname_field    = new TextField();
        TextField email_field       = new TextField();
        TextField phone_field       = new TextField();
        DatePicker birthday_field   = new DatePicker();
        TextField password_field    = new TextField();

        Button registration_btn     = new Button();
        Button authorization_btn    = new Button();
        

        lbl_err.setText("");
        fio_lbl.setText("ФИО: ");
        head_lbl.setText("РЕГИСТРАЦИЯ");
        email_lbl.setText("Email: ");
        phone_lbl.setText("Номер телефона: ");
        nickname_lbl.setText("Как обращаться: ");
        birthday_lbl.setText("Дата Рождения(ДД.ММ.ГГГГ): ");
        password_lbl.setText("Пароль: ");
        registration_btn.setText("Зарегистрироваться");
        authorization_btn.setText("Авторизация");


        fio_field.setPrefColumnCount(20);
        email_field.setPrefColumnCount(20);
        phone_field.setPrefColumnCount(20);
        nickname_field.setPrefColumnCount(20);
        password_field.setPrefColumnCount(20);

        registration_btn.setPrefWidth(170);
        authorization_btn.setPrefWidth(170);
        
        registration_btn.setPrefHeight(25);
        authorization_btn.setPrefHeight(25);

        table.setVgap(15);
        table.setHgap(50);

        root.setSpacing(25);
        buttons.setSpacing(150);

        authorization_btn.setOnAction(event->HelpFuncs.loadAuthorizationWindowFunc(authorization_btn, this));
        registration_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fio      = fio_field.getText();
                String nickname = nickname_field.getText();
                String email    = email_field.getText();
                String phone    = phone_field.getText();
                String password = password_field.getText();
                String birthday;
                
                try{birthday = birthday_field.getValue().toString();}
                catch(Exception ex){lbl_err.setText("Заполните поле дата рождения корректно"); return;}

                ClientInfo new_client = new ClientInfo();
                new_client.client_id = -1;
                new_client.client_name = fio;
                new_client.client_nickname = nickname;
                new_client.client_email = email;
                new_client.client_phone = phone;
                new_client.client_psw = password;
                new_client.client_birthday = birthday;
                new_client.client_visits = 0;


                InsertClientCodes insert_code = database.insertNewClient(new_client);

                switch (insert_code){
                    case SUCCESS:
                        
                        client_properties.setProperty("login", email);
                        client_info = database.getClientInfoByLogin(email);

                        database.createClientAvatar(client_info.client_id);

                        try(OutputStream out = Files.newOutputStream(Paths.get("sources/client_props.properties"))){
                            client_properties.store(out, "add info");
                        }
                        catch(Exception ex){System.out.println(ex);}

                        registration_btn.getScene().setRoot(loadMainWindow());
                        
                        break;

                    case IN_BASE:
                        lbl_err.setText("Аккаунт с такими данными уже есть. Авторизуйтесь или используйте другие данные");
                        break;
                
                    case DATABASE_CONN_ERR:
                        authorization_btn.getScene().setRoot(loadDataBaseErrorWindow());
                        break;

                    case BIRTH_ERR:
                        lbl_err.setText("Неправильно написана Дата Рождения. Вам должно быть более 1 года и меньше 150 лет!");
                        break;

                    case EMAIL_ERR:
                        lbl_err.setText("Некорректно написан Email(не более 50 символов)");
                        break;

                    case PHONE_ERR:
                        lbl_err.setText("Некорректно написан телефон. Только номера из РФ");
                        break;

                    case PSW_ERR:
                        lbl_err.setText("Пароль должен состоять не менее чем из 8 символов и не более чем из 20");
                        break;

                    case FILL_FIELD:
                        lbl_err.setText("Пожалуйста, заполните все поля для регистрации");
                        break;
                
                }
            }
        });


        root.setAlignment(Pos.CENTER);
        table.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        head_lbl.setAlignment(Pos.CENTER);
        
        table.addColumn(0,  fio_lbl, 
                            nickname_lbl,  
                            email_lbl, 
                            phone_lbl, 
                            birthday_lbl, 
                            password_lbl);

        table.addColumn(1,  fio_field, 
                            nickname_field,  
                            email_field, 
                            phone_field, 
                            birthday_field, 
                            password_field);

        root.getChildren().addAll(head_lbl, table, lbl_err, buttons);
        buttons.getChildren().addAll(authorization_btn, registration_btn);
        return root;
    }


    public VBox createBookingInfoTable(ArrayList<BookingInfo> info){
        if (info == null){
            Label err = new Label();
            
            VBox root = new VBox();
            

            err.setText("Ошибка получения данных");
            
            root.setAlignment(Pos.CENTER);


            root.getChildren().addAll(err);        
            return root;

        }
        else{
            VBox root               = new VBox();
            GridPane table          = new GridPane();
            
            Label no_booking        = new Label();
            Label head_booking_lbl  = new Label();
            Label table_head_lbl0   = new Label();
            Label table_head_lbl1   = new Label();
            Label table_head_lbl2   = new Label();
            Label table_head_lbl3   = new Label();
            Label table_head_lbl4   = new Label();
            Label table_head_lbl5   = new Label();

            BookingInfo cur_book_info;
            ServiceInfo cur_service_info;
            EmployeeInfo cur_employee_info;
                     
            root.setAlignment(Pos.CENTER);
            table.setAlignment(Pos.CENTER);

            table.setVgap(15);
            table.setHgap(15);

            root.setSpacing(20);

            no_booking.setText("Нет текущих записей");
            table_head_lbl0.setText("№");
            table_head_lbl1.setText("Услуга");
            table_head_lbl2.setText("Цена");
            table_head_lbl3.setText("Мастер");
            table_head_lbl4.setText("Когда");
            table_head_lbl5.setText("На сколько");
            head_booking_lbl.setText("Текущие записи");

            table.add(table_head_lbl0, 0, 0);
            table.add(table_head_lbl1, 1, 0);
            table.add(table_head_lbl2, 2, 0);
            table.add(table_head_lbl3, 3, 0);
            table.add(table_head_lbl4, 4, 0);
            table.add(table_head_lbl5, 5, 0);
            
            boolean is_empty = true;
            int num_row = 1;

            for (int i = 0; i < info.size(); i++){
                if (info.get(i).booking_status == 1){
                    Button more = new Button("Подробнее");
                    is_empty = false;

                    cur_book_info = info.get(i);
                    cur_service_info = database.getServiceInfoById(cur_book_info.service_id);
                    cur_employee_info = database.getEmployeeInfoById(cur_book_info.employee_id);
                    
                    Label table_mid_lbl0 = new Label();
                    Label table_mid_lbl1 = new Label();
                    Label table_mid_lbl2 = new Label();
                    Label table_mid_lbl3 = new Label();
                    Label table_mid_lbl4 = new Label();
                    Label table_mid_lbl5 = new Label();

                    table_mid_lbl0.setText(String.format("%d", num_row));
                    table_mid_lbl1.setText(cur_service_info.service_name);
                    table_mid_lbl2.setText(String.format("%.2f", cur_service_info.service_price));
                    table_mid_lbl3.setText(cur_employee_info.employee_name.replaceFirst(" ", "\n"));
                    table_mid_lbl4.setText(HelpFuncs.parseDateTime(cur_book_info.booking_datetime));
                    table_mid_lbl5.setText(HelpFuncs.parseTime(cur_service_info.service_time));

                    table.addRow(num_row, table_mid_lbl0, 
                                          table_mid_lbl1, 
                                          table_mid_lbl2, 
                                          table_mid_lbl3, 
                                          table_mid_lbl4, 
                                          table_mid_lbl5, 
                                          more);

                    int num = i;
                    more.setOnAction(event -> HelpFuncs.loadBookingPageWindowFunc(more, this, num, 1));

                    num_row++;
                }
            }

            if (!is_empty){
                root.getChildren().addAll(head_booking_lbl, table);
            }
            else{
                root.getChildren().addAll(head_booking_lbl, no_booking);
            }

            return root;
        }
    }

    

    

    public VBox loadBookingPageWindow(int num_page, int need_btn_cancel) {
        BookingInfo booking_info        = database.getBookingInfoByClientId(client_info.client_id).get(num_page);
        ServiceInfo service_info        = database.getServiceInfoById(booking_info.service_id);
        EmployeeInfo employee_info      = database.getEmployeeInfoById(booking_info.employee_id);
        
        VBox root                       = new VBox();
        HBox buttons                    = new HBox();
        GridPane table_booking          = new GridPane();
        GridPane table_employee         = new GridPane();
        
        Label head_booking_info         = new Label();
        Label head_employee_info        = new Label();
        Label table_booking_head_lbl0   = new Label();
        Label table_booking_head_lbl1   = new Label();
        Label table_booking_head_lbl2   = new Label();
        Label table_booking_head_lbl3   = new Label();
        Label table_booking_head_lbl4   = new Label();
        Label table_booking_head_lbl5   = new Label();
        Label table_booking_mid_lbl0    = new Label();
        Label table_booking_mid_lbl1    = new Label();
        Label table_booking_mid_lbl2    = new Label();
        Label table_booking_mid_lbl3    = new Label();
        Label table_booking_mid_lbl4    = new Label();
        Label table_booking_mid_lbl5    = new Label();
        Label table_employee_head_lbl0  = new Label();
        Label table_employee_head_lbl1  = new Label();
        Label table_employee_head_lbl2  = new Label();
        Label table_employee_head_lbl3  = new Label();
        Label table_employee_mid_lbl1   = new Label();
        Label table_employee_mid_lbl2   = new Label();
        Label table_employee_mid_lbl3   = new Label();

        Button cancel_btn               = new Button("Отменить запись");
        Button escape_btn               = new Button("Назад");
        Button transfer_btn             = new Button("Перенести запись");
        
        ImageView avatar_imageView = new ImageView(HelpFuncs.getEmployeeAvatarById(employee_info.employee_id));
        
        Circle circle = new Circle(avatar_imageView.getImage().getHeight()/20);
        
        
        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);
        
        root.setSpacing(50);
        buttons.setSpacing(150);

        root.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        table_booking.setAlignment(Pos.CENTER);
        table_employee.setAlignment(Pos.CENTER);

        table_booking.setHgap(20);
        table_employee.setHgap(20);
        
        table_booking.setVgap(20);
        table_employee.setVgap(20);

        cancel_btn.setText("Отменить запись");
        escape_btn.setText("Назад");
        transfer_btn.setText("Перенести запись");

        head_booking_info.setText("Информация о записи:");
        head_employee_info.setText("Информация о Мастере");

        table_booking_head_lbl0.setText("№");
        table_booking_head_lbl1.setText("Услуга");
        table_booking_head_lbl2.setText("Цена");
        table_booking_head_lbl3.setText("Мастер");
        table_booking_head_lbl4.setText("Когда");
        table_booking_head_lbl5.setText("На сколько");
        
        table_booking_mid_lbl0.setText(String.format("%d", num_page+1));
        table_booking_mid_lbl1.setText(service_info.service_name);
        table_booking_mid_lbl2.setText(String.format("%.2f", service_info.service_price));
        table_booking_mid_lbl3.setText(employee_info.employee_name.replaceFirst(" ", "\n"));
        table_booking_mid_lbl4.setText(HelpFuncs.parseDateTime(booking_info.booking_datetime));
        table_booking_mid_lbl5.setText(HelpFuncs.parseTime(service_info.service_time));

        table_employee_head_lbl0.setText("Фото мастера");
        table_employee_head_lbl1.setText("Имя мастера");
        table_employee_head_lbl2.setText("Услуги");
        table_employee_head_lbl3.setText("Стаж");

        table_employee_mid_lbl1.setText(employee_info.employee_name);
        table_employee_mid_lbl2.setText(HelpFuncs.parseEmployeeServicesSet(employee_info.employee_services_id_set, database));
        table_employee_mid_lbl3.setText(employee_info.employee_exp);
        

        table_employee.addRow(0, table_employee_head_lbl0, 
                                 table_employee_head_lbl1, 
                                 table_employee_head_lbl2, 
                                 table_employee_head_lbl3);
        
        table_employee.addRow(1, avatar_imageView,
                                 table_employee_mid_lbl1,
                                 table_employee_mid_lbl2,
                                 table_employee_mid_lbl3);


        table_booking.addRow(0, table_booking_head_lbl0, 
                                table_booking_head_lbl1, 
                                table_booking_head_lbl2, 
                                table_booking_head_lbl3, 
                                table_booking_head_lbl4, 
                                table_booking_head_lbl5);

        table_booking.addRow(1, table_booking_mid_lbl0, 
                                table_booking_mid_lbl1, 
                                table_booking_mid_lbl2, 
                                table_booking_mid_lbl3, 
                                table_booking_mid_lbl4, 
                                table_booking_mid_lbl5);

        
        
        if (need_btn_cancel == 1){
            Main cur = this;
            transfer_btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int confirmation_code = HelpFuncs.confirmReBookingDialog(booking_info, database);  
                    if(confirmation_code == 1){
                        int deleting_code = database.cancelBookingById(booking_info.booking_id);
            
                        if (deleting_code == 1){
                            HelpFuncs.loadChooseEmployeeWindowFunc(transfer_btn, cur, booking_info.service_id);
                        }
                        else{
                            createInfoOfDeletingBookingDialog(deleting_code);
                        }
                    }
                }
            });
            escape_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(escape_btn, this));


            buttons.getChildren().addAll(cancel_btn, transfer_btn, escape_btn);
        }
        else{
            escape_btn.setOnAction(event->HelpFuncs.loadHistoryWindowFunc(escape_btn, this));

            
            buttons.getChildren().addAll(escape_btn);
        }

        cancel_btn.setOnAction(event -> cancelBookingBtnAction(booking_info.booking_id, cancel_btn));


        root.getChildren().addAll(head_booking_info, table_booking, head_employee_info, table_employee, buttons);
        return root;
    }

    public void cancelBookingBtnAction(int booking_id, Button btn){
        int alert_code = HelpFuncs.confirmDeleteBookingDialog(booking_id, database);
        
        if(alert_code == 1){
            int deleting_code = database.cancelBookingById(booking_id);
            
            if (deleting_code == 1){
                HelpFuncs.loadMainWindowFunc(btn, this);
            }
            else{
                createInfoOfDeletingBookingDialog(deleting_code);
            }
        }
    }

    public void createInfoOfDeletingBookingDialog(int code){
        Alert alert = new Alert(AlertType.ERROR);
        
        alert.setTitle("Отмена записи");
        alert.setHeaderText("Результат отмены:");
        
        if (code == 1){    
            alert.setContentText("Успешно отменено");
        }
        else {
            alert.setContentText("Не удалось отменить, Пожалуйста свяжитесь с администратором или попробуте еще раз");
        }

        alert.showAndWait();
    }

    
    public BorderPane loadMainWindow(){
        database.updateBookingTableByClientId(client_info.client_id);
        database.createClientAvatar(client_info.client_id);
        isMenuVisible               = true;
        Image menu_icon_img         = null;
        try{menu_icon_img           = new Image(new FileInputStream("photos/Menu-ICON.png"));}
        catch(Exception ex){System.out.println(ex);}

        String login                = client_properties.getProperty("login", "No");

        LocalDateTime now_LDT       = database.getCurrentDateTime();
        LocalDate birth_LD          = HelpFuncs.strToLocalDate(client_info.client_birthday);

        BorderPane root             = new BorderPane();
        
        StackPane menu              = new StackPane();

        VBox booking                = createBookingInfoTable(database.getBookingInfoByClientId(client_info.client_id));
        VBox right_box              = new VBox();
        VBox sideMenu               = new VBox();
        VBox head_box               = new VBox();
        VBox buttons_box            = new VBox();
        VBox central_box            = new VBox();

        Label head_lbl              = new Label();
        Label birthday_lbl          = new Label();

        Button btn_booking          = new Button();
        Button btn_history          = new Button();
        Button btn_loyalty          = new Button();
        Button side_menu_btn1       = new Button();
        Button side_menu_btn2       = new Button();
        Button side_menu_btn3       = new Button();
        Button side_menu_btn4       = new Button();
        Button side_menu_btn5       = new Button();
        Button exit_btn             = new Button();
        Button toggleButton         = new Button();
        
        ImageView menu_icon         = new ImageView(menu_icon_img);
        ImageView avatar_imageView  = new ImageView(HelpFuncs.getClientAvatar());
        Circle circle               = new Circle(avatar_imageView.getImage().getHeight()/20);


        exit_btn.setText("Выйти \nиз аккаунта");
        head_lbl.setText("Приветствуем, " + client_info.client_nickname);
        btn_booking.setText("Записаться");
        btn_history.setText("История посещений");
        btn_loyalty.setText("Лояльность и Скидки");
        birthday_lbl.setText("С Днем Рождения!\n\nВы получаете скидку 10% на 7 дней, успейте воспользоваться!");
        side_menu_btn1.setText("О салоне");
        side_menu_btn2.setText("Галерея");
        side_menu_btn3.setText("Контакты");
        side_menu_btn4.setText("Настройки");
        side_menu_btn5.setText("FAQ");

        btn_booking.setPrefWidth(200);
        btn_booking.setPrefHeight(100);

        btn_history.setPrefWidth(200);
        btn_history.setPrefHeight(100);

        btn_loyalty.setPrefWidth(200);
        btn_loyalty.setPrefHeight(100);

        sideMenu.setPrefWidth(MENU_WIDTH);
        right_box.setPrefWidth(MENU_WIDTH);

        menu_icon.setFitWidth(50);
        menu_icon.setFitHeight(50);

        sideMenu.getStyleClass().add("side-menu");
        head_lbl.getStyleClass().add("head-label");
        
        StackPane.setAlignment(sideMenu, Pos.CENTER_LEFT);
        
        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);

        sideMenu.setVisible(false);

        sideMenu.setSpacing(50);
        head_box.setSpacing(20);
        buttons_box.setSpacing(20);
        central_box.setSpacing(50);
        
        VBox.setMargin(side_menu_btn1, new Insets(100, 0, 0, 0));
        VBox.setMargin(head_box, new Insets(40, 10, 10, 10));
        VBox.setMargin(booking, new Insets(0, 10, 10, 10));

        sideMenu.setTranslateX(-(MENU_WIDTH)); 

        toggleButton.setGraphic(menu_icon);

        sideMenu.setAlignment(Pos.TOP_CENTER);
        head_box.setAlignment(Pos.CENTER);
        StackPane.setAlignment(toggleButton, Pos.TOP_LEFT);
        buttons_box.setAlignment(Pos.CENTER);
        central_box.setAlignment(Pos.CENTER);

        Main cur = this;
        btn_booking.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ArrayList<BookingInfo> booking_list = database.getBookingInfoByClientId(client_info.client_id);
                int num_real_bookings = 0;
                for (int i = 0; i < booking_list.size(); i++){
                    if(booking_list.get(i).booking_status == 1){
                        num_real_bookings++;
                    }
                }
                if (num_real_bookings >= 3){
                    HelpFuncs.bookingWarning();
                }
                else{
                    HelpFuncs.loadBookingWindowFunc(btn_booking, cur);
                }
            }
        });
        toggleButton.setOnAction(event -> toggleMenu(sideMenu));
        btn_history.setOnAction(event->HelpFuncs.loadHistoryWindowFunc(btn_history, this));
        btn_loyalty.setOnAction(event->HelpFuncs.loadLoyaltyWindowFunc(btn_loyalty, this));
        exit_btn.setOnAction(event->HelpFuncs.loadAuthorizationWindowFunc(exit_btn, this));
        side_menu_btn1.setOnAction(event->new Thread(() -> HelpFuncs.openLink(about_url)).start());
        side_menu_btn2.setOnAction(event->HelpFuncs.loadGaleryWindowFunc(side_menu_btn2, this, 0));
        side_menu_btn3.setOnAction(event->HelpFuncs.loadContactsWindowFunc(side_menu_btn3, this));
        side_menu_btn4.setOnAction(event->HelpFuncs.loadSettingsWindowFunc(side_menu_btn4, this));
        side_menu_btn5.setOnAction(event->HelpFuncs.loadFAQWindowFunc(side_menu_btn5, this));


        menu.getChildren().addAll(sideMenu, toggleButton);
        sideMenu.getChildren().addAll(side_menu_btn1, side_menu_btn2, side_menu_btn3, side_menu_btn4, side_menu_btn5, exit_btn);
        head_box.getChildren().addAll(head_lbl, avatar_imageView);
        buttons_box.getChildren().addAll(btn_booking, btn_history, btn_loyalty);
        if (now_LDT.getDayOfMonth() == birth_LD.getDayOfMonth() && now_LDT.getMonth() == birth_LD.getMonth()){
            central_box.getChildren().addAll(head_box, booking, birthday_lbl, buttons_box);          
        }
        else{
            central_box.getChildren().addAll(head_box, booking, buttons_box);
        }
        root.setCenter(central_box);
        root.setLeft(menu);
        root.setRight(right_box);
        return root;
    }

    private void toggleMenu(VBox sideMenu) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(100), sideMenu);        
        if (isMenuVisible) {
            transition.setToX(0);
            sideMenu.setVisible(true); // Сначала делаем меню видимым
            sideMenu.setOpacity(0.0); // Устанавливаем начальную непрозрачность в 0
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.1), sideMenu);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);

            fadeIn.play();
             // Скрыть меню
        } 

        else {
            transition.setToX(-(MENU_WIDTH));
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.1), sideMenu);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event ->sideMenu.setVisible(false)); // Скрыть меню после анимации
            fadeOut.play();
             // Показать меню
        }
        
        transition.play();
        isMenuVisible = !isMenuVisible; // Переключаем состояние видимости
    }




    public VBox loadBookingWindow() {
        VBox root               = new VBox();
        
        Label head_lbl          = new Label();
        Label table_head_lbl0   = new Label();
        Label table_head_lbl1   = new Label();
        Label table_head_lbl2   = new Label();
        Label table_head_lbl3   = new Label();
        Label table_head_lbl4   = new Label();
        
        Button back_btn         = new Button();
        
        GridPane table          = new GridPane();


        root.setAlignment(Pos.CENTER);
        table.setAlignment(Pos.CENTER);

        table.setHgap(25);
        table.setVgap(25);

        table.addRow(0, table_head_lbl0, 
                        table_head_lbl1, 
                        table_head_lbl2, 
                        table_head_lbl3, 
                        table_head_lbl4);

        root.setSpacing(100);

        head_lbl.setText("Выберите услугу");
        back_btn.setText("Назад");
        table_head_lbl0.setText("№");
        table_head_lbl1.setText("Услуга");
        table_head_lbl2.setText("Стоимость");
        table_head_lbl3.setText("Длительность");
        table_head_lbl4.setText("Подробнее");

        ArrayList<ServiceInfo> services = database.getServiceInfo();
        
        for(int i = 0; i < services.size(); i++){
            ServiceInfo service         = services.get(i);
            
            Button book_service_btn     = new Button();

            Label table_mid_lbl0        = new Label();
            Label table_mid_lbl1        = new Label();
            Label table_mid_lbl2        = new Label();
            Label table_mid_lbl3        = new Label();
            Label table_mid_lbl4        = new Label();

            table_mid_lbl0.setText(String.format("%d", i+1));
            table_mid_lbl1.setText(service.service_name);
            table_mid_lbl2.setText(String.format("%.2f", service.service_price));
            table_mid_lbl3.setText(HelpFuncs.parseTime(service.service_time));
            table_mid_lbl4.setText(service.service_description);
            book_service_btn.setText("Записаться");

            table.addRow(i+1,   table_mid_lbl0, 
                                table_mid_lbl1, 
                                table_mid_lbl2, 
                                table_mid_lbl3, 
                                table_mid_lbl4, 
                                book_service_btn);
            
            book_service_btn.setOnAction(event -> btnBookingAction(book_service_btn, service.service_id));
        }


        root.getChildren().addAll(head_lbl, table, back_btn);

        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));
        
        return root;
    }

    public void btnBookingAction(Button btn_booking, int service_id){
        int confirmation_code = HelpFuncs.confirmBooking(service_id, database);
        if (confirmation_code == 1){
            HelpFuncs.loadChooseEmployeeWindowFunc(btn_booking, this, service_id);
        }
    }

    public VBox loadChooseEmployeeWindow(int service_id){
        VBox root                   = new VBox();
        HBox date_box               = new HBox();
        employees_table             = new GridPane();
        
        Label head_lbl              = new Label();
        Label date_lbl              = new Label();
        Label lbl_err               = new Label();
        Label no_cells              = new Label();
        
        Button escape_btn           = new Button();
        Button date_confirm_button  = new Button();

        DatePicker date_field       = new DatePicker();
        

        root.setAlignment(Pos.TOP_CENTER);
        date_box.setAlignment(Pos.CENTER);
        employees_table.setAlignment(Pos.CENTER);

        employees_table.setHgap(25);
        employees_table.setVgap(25);
        
        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));

        root.setSpacing(50);
        date_box.setSpacing(20);

        lbl_err.setText("");
        head_lbl.setText("Выберите дату и мастера");
        date_lbl.setText("Выберите интересующую дату: ");
        no_cells.setText("");
        escape_btn.setText("Назад");
        date_confirm_button.setText("Показать");


        Main main = this;
        date_confirm_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String date;
                root.getChildren().remove(employees_table);

                try{
                    date = date_field.getValue().toString(); 
                    lbl_err.setText("");
                }catch(Exception ex){lbl_err.setText("Заполните поле дата корректно"); return;}

                ArrayList<Integer> employees_with_service = database.getAllEmployeesIdWhoHaveService(service_id);
                
                employees_table = new GridPane();
                employees_table.setAlignment(Pos.CENTER);
                employees_table.setHgap(25);
                employees_table.setVgap(25);
                
                int num = 0;

                for (int i = 0; i < employees_with_service.size(); i++){
                    ArrayList<String> free_time_arr = database.getFreeTimeInfo(date, employees_with_service.get(i), service_id);
                    if (!free_time_arr.isEmpty()){
                        EmployeeInfo employee = database.getEmployeeInfoById(employees_with_service.get(i));
                        
                        
                        Image avatar_image = null;
                        try{avatar_image = new Image(new FileInputStream(String.format("photos/employees/%d.jpg", employees_with_service.get(i))));}
                        catch(Exception ex){
                            try{avatar_image = new Image(new FileInputStream("photos/standard.jpg"));}
                            catch(Exception exc){System.out.println(exc);}   
                        }
                        ImageView avatar_imageView = new ImageView(avatar_image);
                        Circle circle = new Circle(avatar_imageView.getImage().getHeight()/20);
                        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
                        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
                        avatar_imageView.setPreserveRatio(true);
                        avatar_imageView.setClip(circle);
                        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
                        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);


                        employees_table.add(avatar_imageView, 0, num);
                        employees_table.add(new Label(employee.employee_name), 1, num);

                        FlowPane btns_time = new FlowPane();

                        for (int j = 0; j < free_time_arr.size(); j++){
                            Button time_cell = new Button(free_time_arr.get(j));
                            
                            btns_time.getChildren().add(time_cell);
                            
                            time_cell.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    String time = time_cell.getText();
                                    if (HelpFuncs.confirmBookingTime(time, service_id, date, employee.employee_id, database) == 1){
                                        database.updateEmployeeWorkDayTimetable(time, service_id, date, employee.employee_id);
                                        database.updateBookingBase(client_info.client_id, service_id, date, time, employee.employee_id);
                                        HelpFuncs.loadMainWindowFunc(escape_btn, main);
                                    }

                                    
                                }
                            });

                        }
                        Button about_employee_btn = new Button("О Мастере");
                        
                        about_employee_btn.setOnAction(e->HelpFuncs.loadEmployeeInfoWindowFunc(about_employee_btn, main, employee.employee_id, service_id));

                        employees_table.add(about_employee_btn, 2, num);
                        employees_table.add(btns_time, 3, num);
                        
                        num++;
                    }    
                }
                
                
                root.getChildren().add(employees_table);

                if (num==0){no_cells.setText("К сожалению на эту дату записаться не получится");}
                else{no_cells.setText("");}
                
            }
        });
        escape_btn.setOnAction(event->HelpFuncs.loadBookingWindowFunc(escape_btn, this));
        

        date_box.getChildren().addAll(date_lbl, date_field, date_confirm_button, lbl_err);
        root.getChildren().addAll(head_lbl, date_box, escape_btn, no_cells, employees_table);
        return root;
    }

    public VBox loadEmployeeInfoWindow(int employee_id, int service_id){
        EmployeeInfo employee           = database.getEmployeeInfoById(employee_id);

        VBox root                       = new VBox();
        
        GridPane table_employee         = new GridPane();
        
        Label head_lbl                  = new Label();
        Label table_employee_head_lbl0  = new Label();
        Label table_employee_head_lbl1  = new Label();
        Label table_employee_head_lbl2  = new Label();
        Label table_employee_head_lbl3  = new Label();
        Label table_employee_mid_lbl1   = new Label();
        Label table_employee_mid_lbl2   = new Label();
        Label table_employee_mid_lbl3   = new Label();
        
        Button escape_btn               = new Button();
        
        ImageView avatar_imageView      = new ImageView(HelpFuncs.getEmployeeAvatarById(employee_id));
        Circle circle                   = new Circle(avatar_imageView.getImage().getHeight()/20);
        

        head_lbl.setText("Информация о мастере");
        escape_btn.setText("Назад");
        table_employee_head_lbl0.setText("Фото мастера");
        table_employee_head_lbl1.setText("Имя мастера");
        table_employee_head_lbl2.setText("Услуги");
        table_employee_head_lbl3.setText("Стаж");
        table_employee_mid_lbl1.setText(employee.employee_name);
        table_employee_mid_lbl2.setText(HelpFuncs.parseEmployeeServicesSet(employee.employee_services_id_set, database));
        table_employee_mid_lbl3.setText(employee.employee_exp);

        root.setSpacing(50);
        
        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));

        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        
        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);

        root.setAlignment(Pos.CENTER);
        GridPane.setHalignment(avatar_imageView, HPos.CENTER);
        GridPane.setValignment(avatar_imageView, VPos.CENTER);
        table_employee.setAlignment(Pos.CENTER);
        
        table_employee.setHgap(20);
        table_employee.setVgap(20);

        escape_btn.setOnAction(event->HelpFuncs.loadChooseEmployeeWindowFunc(escape_btn, this, service_id));

        table_employee.addRow(0,    table_employee_head_lbl0, 
                                    table_employee_head_lbl1, 
                                    table_employee_head_lbl2, 
                                    table_employee_head_lbl3);
        table_employee.addRow(1,    avatar_imageView, 
                                    table_employee_mid_lbl1, 
                                    table_employee_mid_lbl2, 
                                    table_employee_mid_lbl3);
        
    


        root.getChildren().addAll(head_lbl, table_employee, escape_btn);
        return root;
    }



    public VBox loadHistoryWindow() {
        database.updateBookingTableByClientId(client_info.client_id);
        ArrayList<BookingInfo> info = database.getBookingInfoByClientId(client_info.client_id);

        VBox root           = new VBox();
        
        Label head_lbl      = new Label();
        
        Button back_btn     = new Button();
        

        root.setSpacing(100);

        root.setAlignment(Pos.CENTER);

        head_lbl.setText("История посещений");
        back_btn.setText("Назад");

        if (info == null){
            Label err       = new Label("Ошибка получения данных");

            root.getChildren().addAll(head_lbl, err);
        }
        else if(info.isEmpty()){
            Label no_booking = new Label("Нет записей");

            root.getChildren().addAll(head_lbl, no_booking);
        }
        else{
            GridPane table = new GridPane();
            
            Label table_head_lbl0 = new Label();
            Label table_head_lbl1 = new Label();
            Label table_head_lbl2 = new Label();
            Label table_head_lbl3 = new Label();
            Label table_head_lbl4 = new Label();
            Label table_head_lbl5 = new Label();
            Label table_head_lbl6 = new Label();
            
            javafx.scene.control.ScrollPane scroll_table = new javafx.scene.control.ScrollPane(table);

            BookingInfo cur_book_info;
            ServiceInfo cur_service_info;
            EmployeeInfo cur_employee_info;
            
            table_head_lbl0.setText("№");
            table_head_lbl1.setText("Услуга");
            table_head_lbl2.setText("Цена");
            table_head_lbl3.setText("Мастер");
            table_head_lbl4.setText("Когда");
            table_head_lbl5.setText("На сколько");
            table_head_lbl6.setText("Статус");


            table.setAlignment(Pos.CENTER);
            
            table.setVgap(15);
            table.setHgap(15);

            table.addRow(0, table_head_lbl0, 
                            table_head_lbl1, 
                            table_head_lbl2, 
                            table_head_lbl3, 
                            table_head_lbl4, 
                            table_head_lbl5, 
                            table_head_lbl6);

            
            scroll_table.setPrefViewportHeight(500);
            scroll_table.setPrefViewportWidth(300);
            scroll_table.setFitToHeight(true);
            scroll_table.setFitToWidth(true);
            scroll_table.setPrefSize(300, 500);

            int num_row = 1;
            
            for (int i = 0; i < info.size(); i++){
                Button more         = new Button("Подробнее");
                
                Label table_mid_lbl0 = new Label();
                Label table_mid_lbl1 = new Label();
                Label table_mid_lbl2 = new Label();
                Label table_mid_lbl3 = new Label();
                Label table_mid_lbl4 = new Label();
                Label table_mid_lbl5 = new Label();
                Label table_mid_lbl6 = new Label();

                int num              = i;
                String status;
                
                cur_book_info       = info.get(i);
                cur_service_info    = database.getServiceInfoById(cur_book_info.service_id);
                cur_employee_info   = database.getEmployeeInfoById(cur_book_info.employee_id);
                
                status = HelpFuncs.getStrOfBookingStatus(cur_book_info);
                

                table_mid_lbl0.setText(String.format("%d", num_row));
                table_mid_lbl1.setText(cur_service_info.service_name);
                table_mid_lbl2.setText(String.format("%.2f", cur_service_info.service_price));
                table_mid_lbl3.setText(cur_employee_info.employee_name.replaceFirst(" ", "\n"));
                table_mid_lbl4.setText(HelpFuncs.parseDateTime(cur_book_info.booking_datetime));
                table_mid_lbl5.setText(HelpFuncs.parseTime(cur_service_info.service_time));
                table_mid_lbl6.setText(status);

                
                table.addRow(num_row,   table_mid_lbl0, 
                                        table_mid_lbl1, 
                                        table_mid_lbl2, 
                                        table_mid_lbl3, 
                                        table_mid_lbl4, 
                                        table_mid_lbl5, 
                                        table_mid_lbl6, 
                                        more);

                more.setOnAction(event -> HelpFuncs.loadBookingPageWindowFunc(more, this, num, 0));
                
                num_row++;
            }
            VBox.setMargin(scroll_table, new Insets(0, 50, 0, 50));
            root.getChildren().addAll(head_lbl, scroll_table);
        }


        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));

        root.getChildren().addAll(back_btn);
        return root;
    }

    public VBox loadLoyaltyWindow() {
        String loyalty_num          = "Ваш номер в программе лояльности: " + client_info.client_id;
        String discount             = "Ваша текущая скидка: "+HelpFuncs.discountCalc(client_info, database)+"%";
        String birthday_discount    = "Также на день рождения у вас действует скидка 10% на все услуги и продукцию\n(Необходимо будет предоставить документ, подтверждающий право на скидку)";
        
        VBox root                   = new VBox();
        VBox const_discount_box     = new VBox();
        
        Label head_lbl              = new Label();
        Label loyalty_num_lbl       = new Label();
        Label discount_lbl          = new Label();
        Label birthday_discount_lbl = new Label();
        
        Button back_btn             = new Button();


        root.setSpacing(100);
        const_discount_box.setSpacing(25);

        root.setAlignment(Pos.CENTER);
        const_discount_box.setAlignment(Pos.CENTER);

        head_lbl.setText("Это окно карты лояльности");
        back_btn.setText("Назад");
        discount_lbl.setText(discount);
        loyalty_num_lbl.setText(loyalty_num);
        birthday_discount_lbl.setText(birthday_discount);

        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));

        root.getChildren().addAll(head_lbl, const_discount_box, back_btn);
        const_discount_box.getChildren().addAll(loyalty_num_lbl, discount_lbl, birthday_discount_lbl);        
        return root;
    }


    public VBox loadSettingsWindow(){
        VBox root                   = new VBox();
        VBox fio_box                = new VBox();
        
        HBox avatar_box             = new HBox();
        HBox buttons_box            = new HBox();

        Label head_lbl              = new Label();
        Label fio                   = new Label();
        Label nickname              = new Label();
        Label birthday              = new Label();
        Label email                 = new Label();
        Label phone                 = new Label();

        Button back_btn             = new Button();
        Button resetData_btn        = new Button();

        ImageView avatar_imageView  = new ImageView(HelpFuncs.getClientAvatar());
        Circle circle               = new Circle(avatar_imageView.getImage().getHeight()/20);
        

        root.setSpacing(50);
        fio_box.setSpacing(20);
        avatar_box.setSpacing(25);
        buttons_box.setSpacing(25);

        fio.setText("ФИО: " + client_info.client_name);
        email.setText("Email: " + client_info.client_email);
        phone.setText("Номер телефона: " + client_info.client_phone);
        head_lbl.setText("Настройки");
        nickname.setText("Имя пользователя: " + client_info.client_nickname);
        birthday.setText("Дата рождения: " + HelpFuncs.parseDate(client_info.client_birthday));
        back_btn.setText("Назад");
        resetData_btn.setText("Изменить данные");

        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));
        
        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);

        root.setAlignment(Pos.TOP_CENTER);
        fio_box.setAlignment(Pos.CENTER);
        avatar_box.setAlignment(Pos.CENTER);
        buttons_box.setAlignment(Pos.CENTER);

        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));
        resetData_btn.setOnAction(event->HelpFuncs.loadResetDataWindowFunc(resetData_btn, this));

        root.getChildren().addAll(head_lbl, avatar_box, fio_box, buttons_box);
        fio_box.getChildren().addAll(fio, nickname, birthday, email, phone);
        avatar_box.getChildren().addAll(avatar_imageView);
        buttons_box.getChildren().addAll(back_btn, resetData_btn);

        return root;
    }

    public VBox loadResetDataWindow(){
        VBox root                   = new VBox();
        HBox buttons_box            = new HBox();
        GridPane table              = new GridPane();

        Label head_lbl              = new Label();
        Label lbl_err               = new Label();
        Label table_head_lbl0       = new Label();
        Label table_head_lbl1       = new Label();
        Label table_head_lbl2       = new Label();
        Label table_head_lbl3       = new Label();
        Label table_head_lbl4       = new Label();
        Label table_head_lbl5       = new Label();

        TextField fio_field         = new TextField(client_info.client_name);
        TextField nickname_field    = new TextField(client_info.client_nickname);
        TextField email_field       = new TextField(client_info.client_email);
        TextField phone_field       = new TextField(client_info.client_phone);
        DatePicker birthday_field   = new DatePicker(HelpFuncs.strToLocalDate(client_info.client_birthday));
        TextField password_field    = new TextField(client_info.client_psw);

        Button back_btn             = new Button();
        Button confirm_btn          = new Button();

        root.setAlignment(Pos.CENTER);
        table.setAlignment(Pos.CENTER);
        buttons_box.setAlignment(Pos.CENTER);
        
        table.setHgap(20);
        table.setVgap(20);

        root.setSpacing(50);
        buttons_box.setSpacing(25);
        
        lbl_err.setText("");
        head_lbl.setText("Изменение данных");
        back_btn.setText("Назад");
        confirm_btn.setText("Применить\nизменения");
        table_head_lbl0.setText("ФИО");
        table_head_lbl1.setText("Имя пользователя");
        table_head_lbl2.setText("Email");
        table_head_lbl3.setText("Номер телефона");
        table_head_lbl4.setText("Дата Рождения");
        table_head_lbl5.setText("Пароль");

    
        table.addColumn(0,  table_head_lbl0, 
                            table_head_lbl1, 
                            table_head_lbl2, 
                            table_head_lbl3, 
                            table_head_lbl4, 
                            table_head_lbl5);
        table.addColumn(1,  fio_field, 
                            nickname_field, 
                            email_field, 
                            phone_field, 
                            birthday_field, 
                            password_field);



        confirm_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fio      = fio_field.getText();
                String nickname = nickname_field.getText();
                String email    = email_field.getText();
                String phone    = phone_field.getText();
                String password = password_field.getText();
                String birthday;
                
                try{birthday = birthday_field.getValue().toString();}
                catch(Exception ex){lbl_err.setText("Заполните поле дата рождения корректно"); return;}

                ClientInfo new_client       = new ClientInfo();
                new_client.client_id        = client_info.client_id;
                new_client.client_name      = fio;
                new_client.client_nickname  = nickname;
                new_client.client_email     = email;
                new_client.client_phone     = phone;
                new_client.client_psw       = password;
                new_client.client_birthday  = birthday;
                new_client.client_visits    = client_info.client_visits;

                InsertClientCodes code = database.updateClientInfo(new_client); 
                switch (code){
                    case SUCCESS:
                        
                        client_properties.setProperty("login", email);
                        client_info = database.getClientInfoByLogin(email);

                        database.createClientAvatar(client_info.client_id);

                        try(OutputStream out = Files.newOutputStream(Paths.get("sources/client_props.properties"))){
                            client_properties.store(out, "add info");
                        }
                        catch(Exception ex){System.out.println(ex);}

                        confirm_btn.getScene().setRoot(loadSettingsWindow());
                        
                        break;

                    case IN_BASE:
                        lbl_err.setText("Аккаунт с такими данными уже есть. Используйте другие данные");
                        break;
                
                    case DATABASE_CONN_ERR:
                        confirm_btn.getScene().setRoot(loadDataBaseErrorWindow());
                        break;

                    case BIRTH_ERR:
                        lbl_err.setText("Неправильно написана Дата Рождения. Вам должно быть более 1 года и меньше 150 лет!");
                        break;

                    case EMAIL_ERR:
                        lbl_err.setText("Некорректно написан Email(не более 50 символов и знак @)");
                        break;

                    case PHONE_ERR:
                        lbl_err.setText("Некорректно написан телефон. Только номера из РФ");
                        break;

                    case PSW_ERR:
                        lbl_err.setText("Пароль должен состоять не менее чем из 8 символов и не более чем из 20");
                        password_field.clear();
                        break;
                    case FILL_FIELD:
                        lbl_err.setText("Пожалуйста, заполните все поля");
                        break;
                
                }    
            }
        });
        back_btn.setOnAction(event->HelpFuncs.loadSettingsWindowFunc(back_btn, this));


        root.getChildren().addAll(head_lbl, table, lbl_err, buttons_box);
        buttons_box.getChildren().addAll(back_btn, confirm_btn);
        return root;
    }

    public BorderPane loadGaleryWindow(int page_num){
        int last_iter               = 6*page_num+6;
        
        VBox buttons                = new VBox();
        VBox head                   = new VBox();
        VBox left_box               = new VBox();
        VBox right_box              = new VBox();
        BorderPane root             = new BorderPane();
        GridPane table              = new GridPane();
        
        Label head_lbl              = new Label();

        Button back_btn             = new Button();
        Button next_btn             = new Button();
        Button prev_btn             = new Button();

        Image arrowRight_icon_img   = null;
        try{arrowRight_icon_img     = new Image(new FileInputStream("photos/Arrow_right_ICON.png"));}
        catch(Exception ex)         {System.out.println(ex);}
        ImageView arrowRight_icon   = new ImageView(arrowRight_icon_img);

        Image arrowLeft_icon_img    = null;
        try{arrowLeft_icon_img      = new Image(new FileInputStream("photos/Arrow_left_ICON.png"));}
        catch(Exception ex)         {System.out.println(ex);}
        ImageView arrowLeft_icon    = new ImageView(arrowLeft_icon_img);

        
        left_box.setPrefWidth(MENU_WIDTH);
        right_box.setPrefWidth(MENU_WIDTH);

        arrowLeft_icon.setFitWidth(50);
        arrowLeft_icon.setFitHeight(50);
        arrowRight_icon.setFitWidth(50);
        arrowRight_icon.setFitHeight(50);
        
        back_btn.setText("Главное меню");
        head_lbl.setText("Галерея");
        next_btn.setGraphic(arrowRight_icon);
        prev_btn.setGraphic(arrowLeft_icon);
        
        head.setAlignment(Pos.CENTER);
        table.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        left_box.setAlignment(Pos.CENTER_LEFT);
        right_box.setAlignment(Pos.CENTER_RIGHT);
        
        table.setHgap(20);
        table.setVgap(20);

        VBox.setMargin(head_lbl, new Insets(100, 0, 0, 0));
        VBox.setMargin(back_btn, new Insets(0, 0, 100, 0));

        if (page_num == (galery_images_num-1)/6 && page_num != 0){
            left_box.getChildren().addAll(prev_btn);
            VBox.setMargin(prev_btn, new Insets(0, 0, 0, 20));
            last_iter = galery_images_num;
        }
        else if(page_num != (galery_images_num-1)/6 && page_num == 0){
            right_box.getChildren().addAll(next_btn);
            VBox.setMargin(next_btn, new Insets(0, 20, 0, 0));
        }
        else if (page_num != (galery_images_num-1)/6 && page_num != 0){
            right_box.getChildren().addAll(next_btn);
            left_box.getChildren().addAll(prev_btn);
            VBox.setMargin(next_btn, new Insets(0, 20, 0, 0));
            VBox.setMargin(prev_btn, new Insets(0, 0, 0, 20));
        }


        int position = 1;
        for (int i = 6*page_num+1; i <= last_iter; i++){
            String file_path = String.format("photos/galery/%d.jpg", i);
            
            Image avatar_image = null;
            try{avatar_image = new Image(new FileInputStream(file_path));}
            catch(Exception ex){System.out.println(ex);}
            ImageView avatar_imageView = new ImageView(avatar_image);
            avatar_imageView.setFitHeight(200);
            avatar_imageView.setFitWidth(200);
            avatar_imageView.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                avatar_imageView.setScaleX(1.1); // Увеличение по оси X
                avatar_imageView.setScaleY(1.1); // Увеличение по оси Y
            });

            avatar_imageView.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                avatar_imageView.setScaleX(1.0); // Возврат к исходному размеру по оси X
                avatar_imageView.setScaleY(1.0); // Возврат к исходному размеру по оси Y
            });
            avatar_imageView.setOnMouseClicked(event -> HelpFuncs.openImageInBrowser(file_path));
            avatar_imageView.setPreserveRatio(true);
            
            table.add(avatar_imageView, (position-1)%3, (position-1)/3);
            position++;
        }

        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));
        next_btn.setOnAction(event->HelpFuncs.loadGaleryWindowFunc(next_btn, this, page_num+1));
        prev_btn.setOnAction(event->HelpFuncs.loadGaleryWindowFunc(prev_btn, this, page_num-1));
        

        head.getChildren().addAll(head_lbl);
        root.setCenter(table);
        root.setTop(head);
        root.setBottom(buttons);
        root.setLeft(left_box);
        root.setRight(right_box);
        buttons.getChildren().addAll(back_btn);
        return root;
    }


    public VBox loadContactsWindow(){
        SaloonInfo saloon       = database.getSaloonInfo();

        VBox root               = new VBox();
        HBox maps_box           = new HBox();
        HBox contacts_box       = new HBox();
        HBox work_hours_box     = new HBox();
        HBox address_box        = new HBox();
        
        Label head_lbl          = new Label();
        Label map_lbl           = new Label();
        Label contacts_lbl      = new Label();
        Label contacts_info     = new Label();
        Label work_hours_lbl    = new Label();
        Label work_hours_info   = new Label();
        Label address_lbl       = new Label();
        Label address_info      = new Label();
        
        Button back_btn         = new Button();

        ImageView map_view      = new ImageView(saloon.map);
        
        

        map_lbl.setText("Мы на картах: ");
        head_lbl.setText("Контакты");
        back_btn.setText("Главное меню");
        contacts_lbl.setText("Контакты: ");
        contacts_info.setText(saloon.contacts);
        work_hours_lbl.setText("График работы: ");
        work_hours_info.setText(saloon.work_hours);
        address_lbl.setText("Адрес: ");
        address_info.setText(saloon.address);
        
        root.setSpacing(50);
        maps_box.setSpacing(50);
        address_box.setSpacing(50);
        contacts_box.setSpacing(50);
        work_hours_box.setSpacing(50);

        root.setAlignment(Pos.CENTER);
        maps_box.setAlignment(Pos.CENTER);
        address_box.setAlignment(Pos.CENTER);
        contacts_box.setAlignment(Pos.CENTER);
        work_hours_box.setAlignment(Pos.CENTER);

        map_view.setFitHeight(500);
        map_view.setFitWidth(500);
        map_view.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            map_view.setScaleX(1.1); // Увеличение по оси X
            map_view.setScaleY(1.1); // Увеличение по оси Y
        });
        map_view.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            map_view.setScaleX(1.0); // Возврат к исходному размеру по оси X
            map_view.setScaleY(1.0); // Возврат к исходному размеру по оси Y
        });
        map_view.setOnMouseClicked(event -> HelpFuncs.openImageInBrowser("photos/map.jpg"));
        map_view.setPreserveRatio(true);

        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));


        root.getChildren().addAll(head_lbl, contacts_box, work_hours_box, address_box, maps_box, back_btn);
        maps_box.getChildren().addAll(map_lbl, map_view);
        address_box.getChildren().addAll(address_lbl, address_info);
        contacts_box.getChildren().addAll(contacts_lbl, contacts_info);
        work_hours_box.getChildren().addAll(work_hours_lbl, work_hours_info);
        return root;
    }

    public VBox loadFAQWindow(){
        VBox root           = new VBox();
        GridPane table      = new GridPane();

        Label head_lbl      = new Label();
        
        Button back_btn     = new Button();
        
        table.setHgap(30);
        table.setVgap(30);

        root.setSpacing(50);
        
        root.setAlignment(Pos.CENTER);
        table.setAlignment(Pos.CENTER);
        
        head_lbl.setText("Часто задаваемые вопросы");
        back_btn.setText("Назад");

        ArrayList<String[]> list_FAQ = database.getFAQ();
        for (int i = 0; i < list_FAQ.size(); i++){
            Label table_mid_lbl0 = new Label();
            Label table_mid_lbl1 = new Label();

            table_mid_lbl0.setText(list_FAQ.get(i)[0]);
            table_mid_lbl1.setText(list_FAQ.get(i)[1]);

            table.addRow(i, table_mid_lbl0, table_mid_lbl1);
        }

        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));
        
        root.getChildren().addAll(head_lbl, table, back_btn);
        return root;
    }


    @Override
    public void start(Stage stage) {
        database = new DB();

        try{
            database.getConnection();
            database.downloadEmployeesAvatar();
            galery_images_num = database.downloadGalery();

            Properties props = new Properties();
            
            try(InputStream in = Files.newInputStream(Paths.get("sources/client_props.properties"))){
                props.load(in);
            }catch(Exception ex){System.out.println(ex);}
            
            client_properties = props;
            String login = client_properties.getProperty("login", "No");
            
            if (login.equals("No")){stage.setScene(new Scene(loadAuthorizationWindow()));}
            else{
                client_info = database.getClientInfoByLogin(login);
                Scene scene = new Scene(loadMainWindow());
                stage.setScene(scene);
                String style = (getClass().getResource("/test.css")).toExternalForm();
                scene.getStylesheets().add(style);
            }
        }
        catch(Exception ex){
            System.out.println(ex);
            stage.setScene(new Scene(loadDataBaseErrorWindow()));
        }
        
        
         
        stage.setTitle("VeronichkaNails_APP");
        stage.setWidth(1000);
        stage.setHeight(1000);
         
        stage.show();
    }
}
