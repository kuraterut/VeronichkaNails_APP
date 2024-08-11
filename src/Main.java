import javafx.application.Application;
import javafx.stage.Stage;


import javafx.scene.Scene;
import javafx.scene.Group;

import javafx.scene.control.Alert.AlertType;



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
    private  boolean isMenuVisible = true; // Флаг видимости меню
    ClientInfo client_info;
    GridPane employees_table;
    DB database;
     
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
            }
        });

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

        HBox buttons = new HBox(150, authorization_btn, registration_btn);

        FlowPane root = new FlowPane(Orientation.VERTICAL, 25, 25, head_label, reg_form, lbl_err, buttons);
        
        head_label.setAlignment(Pos.CENTER);
        reg_form.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        
        return root;

    }

    public String parseDateTime(String datetime){
        //2020-10-10 10:10:10
        String years = datetime.split(" ")[0].split("-")[0];
        String months = datetime.split(" ")[0].split("-")[1];
        String days = datetime.split(" ")[0].split("-")[2];
        String hours = datetime.split(" ")[1].split(":")[0];
        String minutes = datetime.split(" ")[1].split(":")[1];
        String seconds = datetime.split(" ")[1].split(":")[2];
        String ans = days+"."+months+"."+years+"\n"+hours+":"+minutes;
        return ans;
    }

    public String parseTime(String time){
        return time.split(":")[0]+":"+time.split(":")[1];
    }


    public VBox parseBookingInfo(ArrayList<BookingInfo> info){
        
        if (info == null){
            Label err = new Label("Ошибка получения данных");
            VBox root = new VBox(err);
            root.setAlignment(Pos.CENTER);
            return root;

        }
        else{
            Label no_booking = new Label("Нет текущих записей");

            Label cur_info_lbl;
            VBox root = new VBox();
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
            
            boolean is_empty = true;
            int num_row = 1;

            for (int i = 0; i < info.size(); i++){
                if (info.get(i).booking_status == 1){
                    is_empty = false;
                    Button more = new Button("Подробнее");
                    int num = i;
                    cur_book_info = info.get(i);
                    cur_service_info = database.getServiceInfoById(cur_book_info.service_id);
                    cur_employee_info = database.getEmployeeInfoById(cur_book_info.employee_id);
                    
                    table.add(new Label(String.format("%d", num_row)), 0, num_row);
                    table.add(new Label(cur_service_info.service_name), 1, num_row);
                    table.add(new Label(String.format("%.2f", cur_service_info.service_price)), 2, num_row);
                    table.add(new Label(cur_employee_info.employee_name.replaceFirst(" ", "\n")), 3, num_row);
                    table.add(new Label(parseDateTime(cur_book_info.booking_datetime)), 4, num_row);
                    table.add(new Label(parseTime(cur_service_info.service_time)), 5, num_row);
                    table.add(more, 6, num_row);

                    more.setOnAction(event -> actionBookingPage(num, more, 1));
                    num_row++;
                    // if (info.size() > 3 && i == 2){
                    //     Button btn = new Button("Все записи");
                    //     root.getChildren().add(btn);
                    //     break;
                    // }
                }
            }

            if (!is_empty){
                root.getChildren().add(table);
            }
            else{
                root.getChildren().add(no_booking);
            }
            return root;
        }
    }

    public void actionBookingPage(final int num_page, Button more, int need_btn_cancel){
        try{more.getScene().setRoot(loadBookingPageWindow(num_page, need_btn_cancel));}
        catch(Exception ex){System.out.println(ex);}
    }

    public String parseEmployeeServicesSet(String set){
        String[] split_set = set.split(" ");
        String ans = "";
        for (int i = 0; i < split_set.length; i++){
            ans+=database.getServiceInfoById(Integer.parseInt(split_set[i])).service_name+"\n";
        }
        return ans;
    }

    public VBox loadBookingPageWindow(int num_page, int need_btn_cancel) {
        BookingInfo booking_info = database.getBookingInfoByClientId(client_info.client_id).get(num_page);
        ServiceInfo service_info = database.getServiceInfoById(booking_info.service_id);
        EmployeeInfo employee_info = database.getEmployeeInfoById(booking_info.employee_id);
        
        VBox root = new VBox(50);
        root.setAlignment(Pos.CENTER);
        
        Label head_booking_info = new Label("Информация о записи:\n");

        GridPane table_booking = new GridPane();
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
        table_booking.add(new Label(parseDateTime(booking_info.booking_datetime)), 4, 1);
        table_booking.add(new Label(parseTime(service_info.service_time)), 5, 1);

        Label head_employee_info = new Label("Информация о Мастере");
        Image avatar_image = null;
        try{avatar_image = new Image(new FileInputStream(String.format("photos/%d.jpg", employee_info.employee_id)));}
        catch(Exception ex){
            try{avatar_image = new Image(new FileInputStream("photos/standard.jpg"));}
            catch(Exception exc){System.out.println(exc);}
        }
        ImageView avatar_imageView = new ImageView(avatar_image);
        Circle circle = new Circle(75);
        avatar_imageView.setFitHeight(150);
        avatar_imageView.setFitWidth(150);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(75);
        circle.setCenterY(75);

        GridPane table_employee = new GridPane();
        table_employee.setAlignment(Pos.CENTER);
        table_employee.setHgap(20);
        table_employee.setVgap(20);

        table_employee.add(new Label("Фото мастера"), 0, 0);
        table_employee.add(new Label("Имя мастера"), 1, 0);
        table_employee.add(new Label("Услуги"), 2, 0);
        table_employee.add(new Label("Стаж"), 3, 0);
        table_employee.add(avatar_imageView, 0, 1);
        table_employee.add(new Label(employee_info.employee_name), 1, 1);
        table_employee.add(new Label(parseEmployeeServicesSet(employee_info.employee_services_id_set)), 2, 1);
        table_employee.add(new Label(employee_info.employee_exp), 3, 1);
        
        Button cancel_btn = new Button("Отменить запись");
        Button escape_btn = new Button("Назад");
        Button transfer_btn = new Button("Перенести запись");
        HBox btns = new HBox(150);
        if (need_btn_cancel == 1){
            btns.getChildren().addAll(cancel_btn, transfer_btn, escape_btn);
            escape_btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try{escape_btn.getScene().setRoot(loadMainWindow());}
                    catch(FileNotFoundException e){System.out.println("No file");}
                }   
            });


            transfer_btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    transfer_btn.getScene().setRoot(loadChooseEmployeeWindow(service_info.service_id));
                    
                }   
            });
        }
        else{
            btns.getChildren().addAll(escape_btn);
            escape_btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try{escape_btn.getScene().setRoot(loadHistoryWindow());}
                    catch(FileNotFoundException e){System.out.println("No file");}
                }
            });
        }
        btns.setAlignment(Pos.CENTER);
        root.getChildren().addAll(head_booking_info, table_booking, head_employee_info, table_employee, btns);

        

        cancel_btn.setOnAction(event -> cancelBookingBtnAction(booking_info.booking_id, cancel_btn));

        return root;
    }

    public void cancelBookingBtnAction(int booking_id, Button btn){
        int alert_code = confirmDeleteBookingDialog();
        if(alert_code == 1){
            int deleting_code = database.deleteBookingById(booking_id);
            
            if (deleting_code == 1){
                try{btn.getScene().setRoot(loadMainWindow());}
                catch(FileNotFoundException e){System.out.println("No file");}
            }
            else{
                createInfoOfDeletingBookingDialog(deleting_code);
            }
        }
    }

    public void createInfoOfDeletingBookingDialog(int code){
        Alert alert = new Alert(AlertType.INFORMATION);
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

    public int confirmDeleteBookingDialog(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Нажмите ОК, если подтверждаете, Cancel в ином случае");
        alert.setContentText("Подтвердите");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL){
            return 0;
        }
        else {
            return 1;
        }
    }


    public BorderPane loadMainWindow() throws FileNotFoundException{
        database.updateBookingTableByClientId(client_info.client_id);
        database.createClientAvatar(client_info.client_id);
        isMenuVisible = true;
        BorderPane root = new BorderPane();
        String guest_name = database.getNickname(client_properties.getProperty("login", "No"));

        Label head_label = new Label("Приветствуем, " + guest_name);


        Image avatar_image = new Image(new FileInputStream("photos/client_avatar.jpg"));
        ImageView avatar_imageView = new ImageView(avatar_image);
        Circle circle = new Circle(avatar_imageView.getImage().getHeight()/20);

        avatar_imageView.setFitHeight(avatar_imageView.getImage().getHeight()/10);
        avatar_imageView.setFitWidth(avatar_imageView.getImage().getWidth()/10);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(avatar_imageView.getImage().getWidth()/20);
        circle.setCenterY(avatar_imageView.getImage().getHeight()/20);


        String login = client_properties.getProperty("login", "No");


        VBox booking = parseBookingInfo(database.getBookingInfoByClientId(client_info.client_id));

        final Button btn_booking = new Button();
        btn_booking.setText("Записаться");
        btn_booking.setPrefWidth(200);
        btn_booking.setPrefHeight(100);

        final Button btn_history = new Button();
        btn_history.setText("История посещений");
        btn_history.setPrefWidth(200);
        btn_history.setPrefHeight(100);

        final Button btn_promotions = new Button();
        btn_promotions.setText("Карта лояльности");
        btn_promotions.setPrefWidth(200);
        btn_promotions.setPrefHeight(100);



        VBox sideMenu = new VBox();
        sideMenu.setStyle("-fx-background-color: lightgray;");
        
        sideMenu.setPrefWidth(MENU_WIDTH);
        StackPane.setAlignment(sideMenu, Pos.CENTER_LEFT);

        ImageView menu_icon = new ImageView(new Image(new FileInputStream("photos/Menu-ICON.png")));
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
        VBox.setMargin(side_menu_btn1, new Insets(70, 0, 0, 0));

        // Скрываем меню за пределами экрана
        sideMenu.setTranslateX(-(MENU_WIDTH)); 

        // Кнопка для открытия/закрытия меню
        Button toggleButton = new Button("", menu_icon);
        StackPane menu = new StackPane();
        menu.getChildren().addAll(sideMenu, toggleButton);
        
        StackPane.setAlignment(toggleButton, Pos.TOP_LEFT);


        VBox head_box = new VBox(20, head_label, avatar_imageView);
        head_box.setAlignment(Pos.CENTER);


        VBox buttons_box = new VBox(20, btn_booking, 
                                        btn_history, 
                                        btn_promotions);
        buttons_box.setAlignment(Pos.CENTER);


        VBox central_box = new VBox(50, head_box, booking, buttons_box);        
        VBox.setMargin(head_box, new Insets(40, 10, 10, 10));
        BorderPane.setAlignment(central_box, Pos.CENTER);
        
        root.setCenter(central_box);
        root.setLeft(menu);
        
        toggleButton.setOnAction(event -> toggleMenu(sideMenu));
        

        btn_booking.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{btn_booking.getScene().setRoot(loadBookingWindow());}
                catch(FileNotFoundException e){System.out.println("No file");} 
            }
        });

        btn_history.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{btn_history.getScene().setRoot(loadHistoryWindow());}
                catch(FileNotFoundException e){System.out.println("No file");} 
            }
        });

        exit_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                isMenuVisible = true;
                exit_btn.getScene().setRoot(loadAuthorizationWindow());
            }
        });

        side_menu_btn4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                side_menu_btn4.getScene().setRoot(loadSettingsWindow());

            }
        });

        side_menu_btn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // try{
                //     if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)){
                //         Desktop.getDesktop().browse(new URI("https://ru.wikipedia.org/wiki/%D0%97%D0%B0%D0%B3%D0%BB%D0%B0%D0%B2%D0%BD%D0%B0%D1%8F_%D1%81%D1%82%D1%80%D0%B0%D0%BD%D0%B8%D1%86%D0%B0"));
                //     }
                // }
                // catch(Exception ex){
                //     System.out.println(ex);
                //     side_menu_btn1.getScene().setRoot(new FlowPane(new Label("ERROR:  "+ex)));
                // }

            }
        });

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




    public VBox loadBookingWindow() throws FileNotFoundException{
        Label head_label = new Label("Выберите услугу");
        final Button back_btn = new Button("Назад");
        VBox root = new VBox(100, head_label);
        root.setAlignment(Pos.CENTER);

        GridPane table = new GridPane();
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
            table.add(new Label(parseTime(service.service_time)), 3, i+1);
            table.add(new Label(service.service_description), 4, i+1);
            table.add(book_service_btn, 5, i+1);
           
            book_service_btn.setOnAction(event -> btnBookingAction(book_service_btn, service.service_id));
           
        }


        root.getChildren().addAll(table, back_btn);

        back_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{back_btn.getScene().setRoot(loadMainWindow());}
                catch(FileNotFoundException e){System.out.println("No file");}
            }
        });
        return root;
    }

    public void btnBookingAction(Button btn_booking, int service_id){
        int confirmation_code = confirmBooking();
        if (confirmation_code == 1){
            btn_booking.getScene().setRoot(loadChooseEmployeeWindow(service_id));

        }
    }

    public VBox loadChooseEmployeeWindow(int service_id){
        VBox root = new VBox(50);
        Label head_lbl = new Label("Выберите дату и мастера");
        Button escape_btn = new Button("Назад");

        HBox date_box = new HBox(20);
        
        Label date_lbl = new Label("Выберите интересующую дату: ");
        DatePicker date_field = new DatePicker();
        Button date_confirm_button = new Button("Показать");
        Label lbl_err = new Label("");
        Label no_cells = new Label("");

        employees_table = new GridPane();
        employees_table.setAlignment(Pos.CENTER);
        employees_table.setHgap(25);
        employees_table.setVgap(25);

        date_confirm_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String date;
                try{date = date_field.getValue().toString();lbl_err.setText("");}
                catch(Exception ex){lbl_err.setText("Заполните поле дата корректно"); return;}

                ArrayList<Integer> employees_with_service = database.getAllEmployeesIdWhoHaveService(service_id);
                root.getChildren().remove(employees_table);
                
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
                                    if (confirmBookingTime(time, service_id, date, employee.employee_id) == 1){
                                        database.updateEmployeeWorkDayTimetable(time, service_id, date, employee.employee_id);
                                        database.updateBookingBase(client_info.client_id, service_id, date, time, employee.employee_id);
                                        try{escape_btn.getScene().setRoot(loadMainWindow());}
                                        catch(Exception ex){System.out.println(ex); return;}
                                    }

                                    
                                }
                            });

                        }
                        Button about_employee_btn = new Button("О Мастере");
                        about_employee_btn.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                try{about_employee_btn.getScene().setRoot(loadEmployeeInfoWindow(employee.employee_id, service_id));}
                                catch(Exception ex){System.out.println(ex); return;}
                                
                            }
                        });

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

        escape_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{escape_btn.getScene().setRoot(loadBookingWindow());}
                catch(Exception ex){System.out.println(ex); return;}
                
            }
        });

        date_box.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));
        date_box.getChildren().addAll(date_lbl, date_field, date_confirm_button, lbl_err);
        root.getChildren().addAll(head_lbl, date_box, escape_btn, no_cells, employees_table);
        return root;


    }

    public VBox loadEmployeeInfoWindow(int employee_id, int service_id){
        VBox root = new VBox(50);
        Label head_lbl = new Label("Информация о мастере");
        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));
        root.setAlignment(Pos.CENTER);

        Button escape_btn = new Button("Назад");

        EmployeeInfo employee = database.getEmployeeInfoById(employee_id);
        Image avatar_image = null;
        try{avatar_image = new Image(new FileInputStream(String.format("photos/%d.jpg", employee_id)));}
        catch(Exception ex){
            try{avatar_image = new Image(new FileInputStream("photos/standard.jpg"));}
            catch(Exception exc){System.out.println(exc);}
        }
        
        ImageView avatar_imageView = new ImageView(avatar_image);
        Circle circle = new Circle(75);
        avatar_imageView.setFitHeight(150);
        avatar_imageView.setFitWidth(150);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(75);
        circle.setCenterY(75);


        GridPane table_employee = new GridPane();
        table_employee.setAlignment(Pos.CENTER);
        table_employee.setHgap(20);
        table_employee.setVgap(20);

        table_employee.add(new Label("Фото мастера"), 0, 0);
        table_employee.add(new Label("Имя мастера"), 1, 0);
        table_employee.add(new Label("Услуги"), 2, 0);
        table_employee.add(new Label("Стаж"), 3, 0);
        table_employee.add(avatar_imageView, 0, 1);
        table_employee.add(new Label(employee.employee_name), 1, 1);
        table_employee.add(new Label(parseEmployeeServicesSet(employee.employee_services_id_set)), 2, 1);
        table_employee.add(new Label(employee.employee_exp), 3, 1);


        escape_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{escape_btn.getScene().setRoot(loadChooseEmployeeWindow(service_id));}
                catch(Exception ex){System.out.println(ex); return;}
                
            }
        });

        root.getChildren().addAll(head_lbl, table_employee, escape_btn);
        return root;
    }

    public String parseDate(String date){
        String years = date.split("-")[0];
        String months = date.split("-")[1];
        String days = date.split("-")[2];
        return days+"."+months+"."+years;
    }

    public int confirmBookingTime(String time, int service_id, String date, int employee_id){
        String employee_name = database.getEmployeeInfoById(employee_id).employee_name;
        String service_name = database.getServiceInfoById(service_id).service_name;
        String final_date = parseDate(date);
        String final_time = parseTime(time);

        String final_statement = "";
        final_statement+="Услуга: "+service_name+"\n";
        final_statement+="Мастер: "+employee_name+"\n";
        final_statement+="Дата: "+final_date+"\n";
        final_statement+="Время: "+final_time+"\n";


        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(final_statement);
        alert.setContentText("Вы уверены, что хотите записаться?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL){
            return 0;
        }
        else {
            return 1;
        }
    }

    public int confirmBooking(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Нажмите ОК, если подтверждаете запись, Cancel в ином случае");
        alert.setContentText("Вы уверены, что хотите записаться?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL){
            return 0;
        }
        else {
            return 1;
        }
    }


    public VBox loadHistoryWindow() throws FileNotFoundException{
        Label head_label = new Label("История посещений");
        final Button back_btn = new Button("Назад");
        VBox root = new VBox(100, head_label);
        
        database.updateBookingTableByClientId(client_info.client_id);
        ArrayList<BookingInfo> info = database.getBookingInfoByClientId(client_info.client_id);


        if (info == null){
            Label err = new Label("Ошибка получения данных");
            root.getChildren().add(err);
            root.setAlignment(Pos.CENTER);
            

        }
        else if(info.isEmpty()){
            Label no_booking = new Label("Нет записей");
            root.getChildren().add(no_booking);
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
            
            root.getChildren().add(table);
            int num_row = 1;
            for (int i = 0; i < info.size(); i++){
                Button more = new Button("Подробнее");
                int num = i;
                String status;
                cur_book_info = info.get(i);
                cur_service_info = database.getServiceInfoById(cur_book_info.service_id);
                cur_employee_info = database.getEmployeeInfoById(cur_book_info.employee_id);
                if(cur_book_info.booking_status == 1){status = "Ждем Вас!";}
                else{status = "Завершено!";}

                table.add(new Label(String.format("%d", num_row)), 0, num_row);
                table.add(new Label(cur_service_info.service_name), 1, num_row);
                table.add(new Label(String.format("%.2f", cur_service_info.service_price)), 2, num_row);
                table.add(new Label(cur_employee_info.employee_name.replaceFirst(" ", "\n")), 3, num_row);
                table.add(new Label(parseDateTime(cur_book_info.booking_datetime)), 4, num_row);
                table.add(new Label(parseTime(cur_service_info.service_time)), 5, num_row);
                table.add(new Label(status), 6, num_row);
                table.add(more, 7, num_row);

                more.setOnAction(event -> actionBookingPage(num, more, 0));
                num_row++;
            }
        }


        back_btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try{back_btn.getScene().setRoot(loadMainWindow());}
                catch(FileNotFoundException e){System.out.println("No file");}
            }
        });

        root.getChildren().add(back_btn);
        return root;
    }

    public VBox loadLoyaltyWindow() throws FileNotFoundException{
        Label head_label = new Label("Это окно карты лояльности");
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


    public VBox loadSettingsWindow(){
        VBox root = new VBox(50);
        Label head_lbl = new Label("Настройки");
        VBox.setMargin(head_lbl, new Insets(50, 0, 0, 0));
        root.setAlignment(Pos.CENTER);

        HBox avatar_box = new HBox(25);
        
        Image avatar_image = null;
        try{avatar_image = new Image(new FileInputStream("photos/client_avatar.jpg"));}
        catch(Exception ex){
            try{avatar_image = new Image(new FileInputStream("photos/standard.jpg"));}
            catch(Exception exc){System.out.println(exc);}
            
        }
        ImageView avatar_imageView = new ImageView(avatar_image);
        Circle circle = new Circle(75);
        avatar_imageView.setFitHeight(150);
        avatar_imageView.setFitWidth(150);
        avatar_imageView.setPreserveRatio(true);
        avatar_imageView.setClip(circle);
        circle.setCenterX(75);
        circle.setCenterY(75);
        Button change_avatar_btn = new Button("Сменить аватарку");

        avatar_box.getChildren().addAll(avatar_imageView, change_avatar_btn);

        VBox fio_box = new VBox(20);

        Label fio = new Label("ФИО: " + client_info.client_name);
        Label nickname = new Label("Имя пользователя: " + client_info.client_nickname);
        Label birthday = new Label("Дата рождения: " + parseDate(client_info.client_birthday));
        Label email = new Label("Email: " + client_info.client_email);
        Label phone = new Label("Номер телефона: " + client_info.client_phone);

        fio_box.getChildren().addAll(fio, nickname, birthday, email, phone);

        Button back_btn = new Button("Назад");
        root.getChildren().addAll(head_lbl, avatar_box, fio_box, back_btn);

        avatar_box.setAlignment(Pos.CENTER);
        fio_box.setAlignment(Pos.CENTER);

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
        database = new DB();
        try{
            database.getConnection();
            Properties props = new Properties();
            try(InputStream in = Files.newInputStream(Paths.get("sources/client_props.properties"))){
                props.load(in);
            }
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
