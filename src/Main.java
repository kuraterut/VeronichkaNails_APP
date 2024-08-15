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


 



public class Main extends Application{
    Properties client_properties = null;
    private static final double MENU_WIDTH = 150; // Ширина меню
    boolean isMenuVisible = true; // Флаг видимости меню
    ClientInfo client_info;
    GridPane employees_table;
    DB database;
    String about_url = "https://ru.stackoverflow.com/";
     
    public static void main(String[] args) throws FileNotFoundException{
        launch(args);
    }

    public VBox loadDataBaseErrorWindow(){
        VBox root = new VBox(25);
        
        Label is_valid = new Label("Ошибка подключения к базе данных. Повторите попытку");
        Button btn = new Button("Повторить");
        
        btn.setPrefWidth(200);
        btn.setPrefHeight(100);

        root.setAlignment(Pos.CENTER);

        btn.setOnAction(event->HelpFuncs.loadAuthorizationWindowFunc(btn, this));

        root.getChildren().addAll(is_valid, btn);
        
        return root;
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

        registration_btn.setOnAction(event->HelpFuncs.loadRegistrationWindowFunc(registration_btn, this));

        authorization_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String login    = login_field.getText();
                String password = password_field.getText();

                VerifyClientCodes verify_code = database.verifyClientInDB(login, password);
                switch (verify_code){
                    
                    case SUCCESS:
                        lbl_err.setText("");
                        client_properties.setProperty("login", login);
                        client_info = database.getClientInfoByLogin(login);
                        database.createClientAvatar(client_info.client_id);
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
                        password_field.clear();
                        break;
                    case DATABASE_CONN_ERR:
                        authorization_btn.getScene().setRoot(loadDataBaseErrorWindow());
                        break;
                
                }
            }
        });

        
        lbls_form.getChildren().addAll(login_lbl, password_lbl);
        fields_form.getChildren().addAll(login_field, password_field);
        auth_form.getChildren().addAll(lbls_form, fields_form);
        buttons.getChildren().addAll(registration_btn, authorization_btn);
        root.getChildren().addAll(head_label, auth_form, lbl_err, buttons);
        
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

                InsertClientCodes insert_code = database.insertNewClient(fio, nickname, email, phone, birthday, password);

                switch (insert_code){
                    case SUCCESS:
                        
                        client_properties.setProperty("login", email);
                        client_info = database.getClientInfoByLogin(email);

                        database.createClientAvatar(client_info.client_id);

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
        });


        
        head_label.setAlignment(Pos.CENTER);
        reg_form.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        

        buttons.getChildren().addAll(authorization_btn, registration_btn);
        fields_form.getChildren().addAll(fio_field, nickname_field, email_field, phone_field, birthday_field, password_field);
        lbls_form.getChildren().addAll(fio_lbl, nickname_lbl, email_lbl, phone_lbl, birthday_lbl, password_lbl);
        reg_form.getChildren().addAll(lbls_form, fields_form);
        root.getChildren().addAll(head_label, reg_form, lbl_err, buttons);
        
        return root;

    }


    public VBox createBookingInfoTable(ArrayList<BookingInfo> info){
        if (info == null){
            Label err = new Label("Ошибка получения данных");
            VBox root = new VBox(err);
            
            root.setAlignment(Pos.CENTER);
            
            return root;

        }
        else{
            VBox root               = new VBox(20);
            GridPane table          = new GridPane();
            
            Label no_booking        = new Label("Нет текущих записей");
            Label head_booking_lbl  = new Label("Текущие записи");

            BookingInfo cur_book_info;
            ServiceInfo cur_service_info;
            EmployeeInfo cur_employee_info;
                     
            root.setAlignment(Pos.CENTER);
            
            table.setAlignment(Pos.CENTER);
            table.setVgap(15);
            table.setHgap(15);

            table.add(new Label("№"), 0, 0);
            table.add(new Label("Услуга"), 1, 0);
            table.add(new Label("Цена"), 2, 0);
            table.add(new Label("Мастер"), 3, 0);
            table.add(new Label("Когда"), 4, 0);
            table.add(new Label("На сколько"), 5, 0);
            
            boolean is_empty = true;
            int num_row = 1;

            for (int i = 0; i < info.size(); i++){
                if (info.get(i).booking_status == 1){
                    Button more = new Button("Подробнее");
                    is_empty = false;

                    cur_book_info = info.get(i);
                    cur_service_info = database.getServiceInfoById(cur_book_info.service_id);
                    cur_employee_info = database.getEmployeeInfoById(cur_book_info.employee_id);
                    
                    table.add(new Label(String.format("%d", num_row)), 0, num_row);
                    table.add(new Label(cur_service_info.service_name), 1, num_row);
                    table.add(new Label(String.format("%.2f", cur_service_info.service_price)), 2, num_row);
                    table.add(new Label(cur_employee_info.employee_name.replaceFirst(" ", "\n")), 3, num_row);
                    table.add(new Label(HelpFuncs.parseDateTime(cur_book_info.booking_datetime)), 4, num_row);
                    table.add(new Label(HelpFuncs.parseTime(cur_service_info.service_time)), 5, num_row);
                    table.add(more, 6, num_row);

                    int num = i;
                    more.setOnAction(event -> HelpFuncs.loadBookingPageWindowFunc(more, this, num, 1));
                    num_row++;
                    // if (info.size() > 3 && i == 2){
                    //     Button btn = new Button("Все записи");
                    //     root.getChildren().add(btn);
                    //     break;
                    // }
                }
            }

            if (!is_empty){
                javafx.scene.control.ScrollPane scroll_table = new javafx.scene.control.ScrollPane(table);
                scroll_table.setPrefViewportHeight(150);
                scroll_table.setFitToHeight(true);
                scroll_table.setFitToWidth(true);
                

                root.getChildren().addAll(head_booking_lbl, scroll_table);
            }
            else{
                root.getChildren().addAll(head_booking_lbl, no_booking);
            }

            return root;
        }
    }

    

    

    public VBox loadBookingPageWindow(int num_page, int need_btn_cancel) {
        BookingInfo booking_info    = database.getBookingInfoByClientId(client_info.client_id).get(num_page);
        ServiceInfo service_info    = database.getServiceInfoById(booking_info.service_id);
        EmployeeInfo employee_info  = database.getEmployeeInfoById(booking_info.employee_id);
        
        VBox root                   = new VBox(50);
        HBox btns                   = new HBox(150);
        GridPane table_booking      = new GridPane();
        GridPane table_employee     = new GridPane();
        
        Label head_booking_info     = new Label("Информация о записи:\n");
        Label head_employee_info    = new Label("Информация о Мастере");
        
        Image avatar_image          = null;

        Button cancel_btn           = new Button("Отменить запись");
        Button escape_btn           = new Button("Назад");
        Button transfer_btn         = new Button("Перенести запись");
        
        
        root.setAlignment(Pos.CENTER);
        btns.setAlignment(Pos.CENTER);
        
        table_booking.setHgap(20);
        table_booking.setVgap(20);
        table_booking.setAlignment(Pos.CENTER);

        table_booking.add(new Label("№"), 0, 0);
        table_booking.add(new Label("Услуга"), 1, 0);
        table_booking.add(new Label("Цена"), 2, 0);
        table_booking.add(new Label("Мастер"), 3, 0);
        table_booking.add(new Label("Когда"), 4, 0);
        table_booking.add(new Label("На сколько"), 5, 0);        
        table_booking.add(new Label(String.format("%d", num_page+1)), 0, 1);
        table_booking.add(new Label(service_info.service_name), 1, 1);
        table_booking.add(new Label(String.format("%.2f", service_info.service_price)), 2, 1);
        table_booking.add(new Label(employee_info.employee_name.replaceFirst(" ", "\n")), 3, 1);
        table_booking.add(new Label(HelpFuncs.parseDateTime(booking_info.booking_datetime)), 4, 1);
        table_booking.add(new Label(HelpFuncs.parseTime(service_info.service_time)), 5, 1);

        try{avatar_image = new Image(new FileInputStream(String.format("photos/%d.jpg", employee_info.employee_id)));}
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


        table_employee.setAlignment(Pos.CENTER);
        table_employee.setHgap(20);
        table_employee.setVgap(20);

        table_employee.add(new Label("Фото мастера"), 0, 0);
        table_employee.add(new Label("Имя мастера"), 1, 0);
        table_employee.add(new Label("Услуги"), 2, 0);
        table_employee.add(new Label("Стаж"), 3, 0);
        table_employee.add(avatar_imageView, 0, 1);
        table_employee.add(new Label(employee_info.employee_name), 1, 1);
        table_employee.add(new Label(HelpFuncs.parseEmployeeServicesSet(employee_info.employee_services_id_set, database)), 2, 1);
        table_employee.add(new Label(employee_info.employee_exp), 3, 1);
        
        if (need_btn_cancel == 1){
            escape_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(escape_btn, this));
            transfer_btn.setOnAction(event->HelpFuncs.loadChooseEmployeeWindowFunc(transfer_btn, this, service_info.service_id));
            
            btns.getChildren().addAll(cancel_btn, transfer_btn, escape_btn);
        }
        else{
            escape_btn.setOnAction(event->HelpFuncs.loadHistoryWindowFunc(escape_btn, this));
            
            btns.getChildren().addAll(escape_btn);
        }


        
        cancel_btn.setOnAction(event -> cancelBookingBtnAction(booking_info.booking_id, cancel_btn));

        root.getChildren().addAll(head_booking_info, table_booking, head_employee_info, table_employee, btns);

        return root;
    }

    public void cancelBookingBtnAction(int booking_id, Button btn){
        int alert_code = HelpFuncs.confirmDeleteBookingDialog();
        
        if(alert_code == 1){
            int deleting_code = database.deleteBookingById(booking_id);
            
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
        isMenuVisible = true;
        String login = client_properties.getProperty("login", "No");
        
        BorderPane root = new BorderPane();

        Label head_label = new Label("Приветствуем, " + client_info.client_nickname);

        Image avatar_image = null;
        try{avatar_image = new Image(new FileInputStream("photos/client_avatar.jpg"));}
        catch(Exception ex){System.out.println(ex);}
        ImageView avatar_imageView = new ImageView(avatar_image);
        Circle circle = new Circle(avatar_imageView.getImage().getHeight()/20);
        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);

        VBox booking = createBookingInfoTable(database.getBookingInfoByClientId(client_info.client_id));
        VBox right_box = new VBox();
        VBox sideMenu = new VBox();
        
        StackPane menu = new StackPane();

        Button btn_booking = new Button();
        btn_booking.setText("Записаться");
        btn_booking.setPrefWidth(200);
        btn_booking.setPrefHeight(100);

        Button btn_history = new Button();
        btn_history.setText("История посещений");
        btn_history.setPrefWidth(200);
        btn_history.setPrefHeight(100);

        Button btn_promotions = new Button();
        btn_promotions.setText("Карта лояльности");
        btn_promotions.setPrefWidth(200);
        btn_promotions.setPrefHeight(100);


        sideMenu.setStyle("-fx-background-color: lightgray;");
        sideMenu.setPrefWidth(MENU_WIDTH);
        right_box.setPrefWidth(MENU_WIDTH);
        StackPane.setAlignment(sideMenu, Pos.CENTER_LEFT);
        

        Image menu_icon_img = null;
        try{menu_icon_img = new Image(new FileInputStream("photos/Menu-ICON.png"));}
        catch(Exception ex){System.out.println(ex);}
        ImageView menu_icon = new ImageView(menu_icon_img);
        menu_icon.setFitWidth(50);
        menu_icon.setFitHeight(50);
        
        Button side_menu_btn1 = new Button("О салоне");
        Button side_menu_btn2 = new Button("Галерея");
        Button side_menu_btn3 = new Button("Контакты");
        Button side_menu_btn4 = new Button("Настройки");
        Button side_menu_btn5 = new Button("FAQ");
        Button exit_btn = new Button("Выход");
        
        sideMenu.getChildren().addAll(side_menu_btn1, side_menu_btn2, side_menu_btn3, side_menu_btn4, side_menu_btn5);
        sideMenu.getChildren().add(exit_btn);
        sideMenu.setVisible(false);
        sideMenu.setSpacing(50);
        sideMenu.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(side_menu_btn1, new Insets(100, 0, 0, 0));

        // Скрываем меню за пределами экрана
        sideMenu.setTranslateX(-(MENU_WIDTH)); 

        // Кнопка для открытия/закрытия меню
        Button toggleButton = new Button("", menu_icon);
        
        menu.getChildren().addAll(sideMenu, toggleButton);
        
        StackPane.setAlignment(toggleButton, Pos.TOP_LEFT);


        VBox head_box = new VBox(20);
        head_box.getChildren().addAll(head_label, avatar_imageView);
        head_box.setAlignment(Pos.CENTER);


        VBox buttons_box = new VBox(20);
        buttons_box.getChildren().addAll(btn_booking, btn_history, btn_promotions);
        buttons_box.setAlignment(Pos.CENTER);


        VBox central_box = new VBox(50);
        central_box.getChildren().addAll(head_box, booking, buttons_box);        
        VBox.setMargin(head_box, new Insets(40, 10, 10, 10));
        VBox.setMargin(booking, new Insets(0, 10, 10, 10));
        
        root.setCenter(central_box);
        root.setLeft(menu);
        root.setRight(right_box);
        
        toggleButton.setOnAction(event -> toggleMenu(sideMenu));
        btn_booking.setOnAction(event->HelpFuncs.loadBookingWindowFunc(btn_booking, this));
        btn_history.setOnAction(event->HelpFuncs.loadHistoryWindowFunc(btn_booking, this));
        exit_btn.setOnAction(event->HelpFuncs.loadAuthorizationWindowFunc(exit_btn, this));
        side_menu_btn4.setOnAction(event->HelpFuncs.loadSettingsWindowFunc(side_menu_btn4, this));
        side_menu_btn1.setOnAction(event->new Thread(() -> HelpFuncs.openLink(about_url)).start());
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
        } else {
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
        VBox root           = new VBox(100);
        
        Label head_label    = new Label("Выберите услугу");
        
        Button back_btn     = new Button("Назад");
        
        GridPane table      = new GridPane();

        root.setAlignment(Pos.CENTER);

        table.setAlignment(Pos.CENTER);
        table.setHgap(25);
        table.setVgap(25);

        table.add(new Label("№"), 0, 0);
        table.add(new Label("Услуга"), 1, 0);
        table.add(new Label("Стоимость"), 2, 0);
        table.add(new Label("Длительность"), 3, 0);
        table.add(new Label("Подробнее"), 4, 0);

        ArrayList<ServiceInfo> services = database.getServiceInfo();
        
        for(int i = 0; i < services.size(); i++){
            Button book_service_btn = new Button("Записаться");
            ServiceInfo service = services.get(i);
            
            table.add(new Label(String.format("%d", i+1)), 0, i+1);
            table.add(new Label(service.service_name), 1, i+1);
            table.add(new Label(String.format("%.2f", service.service_price)), 2, i+1);
            table.add(new Label(HelpFuncs.parseTime(service.service_time)), 3, i+1);
            table.add(new Label(service.service_description), 4, i+1);
            table.add(book_service_btn, 5, i+1);
           
            book_service_btn.setOnAction(event -> btnBookingAction(book_service_btn, service.service_id));
           
        }


        root.getChildren().addAll(head_label, table, back_btn);

        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));
        
        return root;
    }

    public void btnBookingAction(Button btn_booking, int service_id){
        int confirmation_code = HelpFuncs.confirmBooking();
        if (confirmation_code == 1){
            HelpFuncs.loadChooseEmployeeWindowFunc(btn_booking, this, service_id);
        }
    }

    public VBox loadChooseEmployeeWindow(int service_id){
        VBox root                   = new VBox(50);
        HBox date_box               = new HBox(20);
        
        Label head_lbl              = new Label("Выберите дату и мастера");
        Label date_lbl              = new Label("Выберите интересующую дату: ");
        Label lbl_err               = new Label("");
        Label no_cells              = new Label("");
        
        Button escape_btn           = new Button("Назад");
        Button date_confirm_button  = new Button("Показать");

        DatePicker date_field       = new DatePicker();
        
        employees_table = new GridPane();
        employees_table.setAlignment(Pos.CENTER);
        employees_table.setHgap(25);
        employees_table.setVgap(25);


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
                        try{avatar_image = new Image(new FileInputStream(String.format("photos/%d.jpg", employees_with_service.get(i))));}
                        catch(Exception ex){
                            try{avatar_image = new Image(new FileInputStream("photos/standard.jpg"));}
                            catch(Exception exc){System.out.println(exc);}   
                        }
                        ImageView avatar_imageView = new ImageView(avatar_image);
                        avatar_imageView.setFitHeight(150);
                        avatar_imageView.setFitWidth(150);
                        avatar_imageView.setPreserveRatio(true);


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


        date_box.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));
        
        escape_btn.setOnAction(event->HelpFuncs.loadBookingWindowFunc(escape_btn, this));
        
        date_box.getChildren().addAll(date_lbl, date_field, date_confirm_button, lbl_err);
        root.getChildren().addAll(head_lbl, date_box, escape_btn, no_cells, employees_table);
        
        return root;
    }

    public VBox loadEmployeeInfoWindow(int employee_id, int service_id){
        EmployeeInfo employee = database.getEmployeeInfoById(employee_id);
        

        VBox root               = new VBox(50);
        
        GridPane table_employee = new GridPane();
        
        Label head_lbl          = new Label("Информация о мастере");
        
        Button escape_btn       = new Button("Назад");
        
        Image avatar_image      = null;
        try{avatar_image = new Image(new FileInputStream(String.format("photos/%d.jpg", employee_id)));}
        catch(Exception ex){
            try{avatar_image = new Image(new FileInputStream("photos/standard.jpg"));}
            catch(Exception exc){System.out.println(exc);}
        }
        ImageView avatar_imageView = new ImageView(avatar_image);
        

        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));
        root.setAlignment(Pos.CENTER);

        Circle circle = new Circle(avatar_imageView.getImage().getHeight()/20);
        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);

        table_employee.setAlignment(Pos.CENTER);
        table_employee.setHgap(20);
        table_employee.setVgap(20);

        table_employee.add(new Label("Фото мастера"), 0, 0);
        table_employee.add(new Label("Имя мастера"), 1, 0);
        table_employee.add(new Label("Услуги"), 2, 0);
        table_employee.add(new Label("Стаж"), 3, 0);
        table_employee.add(avatar_imageView, 0, 1);
        table_employee.add(new Label(employee.employee_name), 1, 1);
        table_employee.add(new Label(HelpFuncs.parseEmployeeServicesSet(employee.employee_services_id_set, database)), 2, 1);
        table_employee.add(new Label(employee.employee_exp), 3, 1);

        escape_btn.setOnAction(event->HelpFuncs.loadChooseEmployeeWindowFunc(escape_btn, this, service_id));

        root.getChildren().addAll(head_lbl, table_employee, escape_btn);
        return root;
    }



    public VBox loadHistoryWindow() {
        VBox root           = new VBox(100);
        
        Label head_label    = new Label("История посещений");
        
        Button back_btn     = new Button("Назад");
        
        database.updateBookingTableByClientId(client_info.client_id);
        ArrayList<BookingInfo> info = database.getBookingInfoByClientId(client_info.client_id);

        if (info == null){
            Label err       = new Label("Ошибка получения данных");
            root.getChildren().addAll(head_label, err);
            root.setAlignment(Pos.CENTER);
            

        }
        else if(info.isEmpty()){
            Label no_booking = new Label("Нет записей");
            root.getChildren().addAll(head_label, no_booking);
            root.setAlignment(Pos.CENTER);
            
        }
        else{
            root.setAlignment(Pos.CENTER);
            HBox lbl_btn_box;
            BookingInfo cur_book_info;
            ServiceInfo cur_service_info;
            EmployeeInfo cur_employee_info;
            

            GridPane table = new GridPane();
            
            table.setAlignment(Pos.CENTER);
            table.setVgap(15);
            table.setHgap(15);

            table.add(new Label("№"), 0, 0);
            table.add(new Label("Услуга"), 1, 0);
            table.add(new Label("Цена"), 2, 0);
            table.add(new Label("Мастер"), 3, 0);
            table.add(new Label("Когда"), 4, 0);
            table.add(new Label("На сколько"), 5, 0);
            table.add(new Label("Статус"), 6, 0);
            
            javafx.scene.control.ScrollPane scroll_table = new javafx.scene.control.ScrollPane(table);
            scroll_table.setPrefViewportHeight(500);
            scroll_table.setPrefViewportWidth(300);
            scroll_table.setFitToHeight(true);
            scroll_table.setFitToWidth(true);
            scroll_table.setPrefSize(300, 500);

            int num_row = 1;
            
            for (int i = 0; i < info.size(); i++){
                Button more = new Button("Подробнее");
                
                int num = i;
                String status;
                
                cur_book_info       = info.get(i);
                cur_service_info    = database.getServiceInfoById(cur_book_info.service_id);
                cur_employee_info   = database.getEmployeeInfoById(cur_book_info.employee_id);
                if(cur_book_info.booking_status == 1){status = "Ждем Вас!";}
                else{status = "Завершено!";}

                table.add(new Label(String.format("%d", num_row)), 0, num_row);
                table.add(new Label(cur_service_info.service_name), 1, num_row);
                table.add(new Label(String.format("%.2f", cur_service_info.service_price)), 2, num_row);
                table.add(new Label(cur_employee_info.employee_name.replaceFirst(" ", "\n")), 3, num_row);
                table.add(new Label(HelpFuncs.parseDateTime(cur_book_info.booking_datetime)), 4, num_row);
                table.add(new Label(HelpFuncs.parseTime(cur_service_info.service_time)), 5, num_row);
                table.add(new Label(status), 6, num_row);
                table.add(more, 7, num_row);


                more.setOnAction(event -> HelpFuncs.loadBookingPageWindowFunc(more, this, num, 0));
                
                num_row++;
            }
            VBox.setMargin(scroll_table, new Insets(0, 50, 0, 50));
            root.getChildren().addAll(head_label, scroll_table);
        }


        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));

        root.getChildren().add(back_btn);
        return root;
    }

    public VBox loadLoyaltyWindow() {
        VBox root           = new VBox(100);
        
        Label head_label    = new Label("Это окно карты лояльности");
        
        Button back_btn     = new Button("Назад");


        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));

        
        root.getChildren().addAll(head_label, back_btn);
        
        return root;
    }


    public VBox loadSettingsWindow(){
        VBox root                   = new VBox(50);
        VBox fio_box                = new VBox(20);
        
        HBox avatar_box             = new HBox(25);
        
        Label head_lbl              = new Label("Настройки");
        Label fio = new Label("ФИО: " + client_info.client_name);
        Label nickname = new Label("Имя пользователя: " + client_info.client_nickname);
        Label birthday = new Label("Дата рождения: " + HelpFuncs.parseDate(client_info.client_birthday));
        Label email = new Label("Email: " + client_info.client_email);
        Label phone = new Label("Номер телефона: " + client_info.client_phone);

        Button back_btn             = new Button("Назад");

        Image avatar_image = null;
        try{avatar_image = new Image(new FileInputStream("photos/client_avatar.jpg"));}
        catch(Exception ex){
            try{avatar_image = new Image(new FileInputStream("photos/standard.jpg"));}
            catch(Exception exc){System.out.println(exc);}
            
        }
        ImageView avatar_imageView  = new ImageView(avatar_image);


        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));
        root.setAlignment(Pos.CENTER);
        
        Circle circle = new Circle(avatar_imageView.getImage().getHeight()/20);
        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);

        avatar_box.setAlignment(Pos.CENTER);
        fio_box.setAlignment(Pos.CENTER);

        back_btn.setOnAction(event->HelpFuncs.loadMainWindowFunc(back_btn, this));


        avatar_box.getChildren().addAll(avatar_imageView);
        fio_box.getChildren().addAll(fio, nickname, birthday, email, phone);
        root.getChildren().addAll(head_lbl, avatar_box, fio_box, back_btn);

        return root;
    }



    @Override
    public void start(Stage stage) {
        database = new DB();

        try{
            database.getConnection();
            
            Properties props = new Properties();
            
            try(InputStream in = Files.newInputStream(Paths.get("sources/client_props.properties"))){
                props.load(in);
            }catch(Exception ex){System.out.println(ex);}
            
            client_properties = props;
            String login = client_properties.getProperty("login", "No");
            
            if (login.equals("No")){stage.setScene(new Scene(loadAuthorizationWindow()));}
            else{
                client_info = database.getClientInfoByLogin(login);
                stage.setScene(new Scene(loadMainWindow()));
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
