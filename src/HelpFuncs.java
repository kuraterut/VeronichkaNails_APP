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


public class HelpFuncs extends Main{
	public static void loadAuthorizationWindowFunc(Node node, Main cur){
		cur.isMenuVisible = true;
		node.getScene().setRoot(cur.loadAuthorizationWindow());
	}

	public static void loadRegistrationWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadRegistrationWindow());
	}

	public static void loadMainWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadMainWindow());
	}

	public static void loadBookingWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadBookingWindow());
	}

	public static void loadHistoryWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadHistoryWindow());
	}
	
	public static void loadBookingPageWindowFunc(Node node, Main cur, int num_page, int need_btn_cancel){
		node.getScene().setRoot(cur.loadBookingPageWindow(num_page, need_btn_cancel));
	}

	public static void loadSettingsWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadSettingsWindow());
	}

	public static void loadChooseEmployeeWindowFunc(Node node, Main cur, int service_id){
		node.getScene().setRoot(cur.loadChooseEmployeeWindow(service_id));
	}

	public static void loadEmployeeInfoWindowFunc(Node node, Main cur, int employee_id, int service_id){
		node.getScene().setRoot(cur.loadEmployeeInfoWindow(employee_id, service_id));
	}


	public static String parseDateTime(String datetime){
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

    public static String parseTime(String time){
        return time.split(":")[0]+":"+time.split(":")[1];
    }

    public static String parseDate(String date){
        String years = date.split("-")[0];
        String months = date.split("-")[1];
        String days = date.split("-")[2]; 
        
        return days+"."+months+"."+years;
    }

    public static String parseEmployeeServicesSet(String set, DB database){
        String[] split_set = set.split(" ");
        String ans = "";
        for (int i = 0; i < split_set.length; i++){
            ans+=database.getServiceInfoById(Integer.parseInt(split_set[i])).service_name+"\n";
        }

        return ans;
    }


    public static int confirmBookingTime(String time, int service_id, String date, int employee_id, DB database){
        String employee_name = database.getEmployeeInfoById(employee_id).employee_name;
        String service_name = database.getServiceInfoById(service_id).service_name;
        String final_date = HelpFuncs.parseDate(date);
        String final_time = HelpFuncs.parseTime(time);

        String final_statement = "";
        final_statement+="Услуга: "+service_name+"\n";
        final_statement+="Мастер: "+employee_name+"\n";
        final_statement+="Дата: "+final_date+"\n";
        final_statement+="Время: "+final_time+"\n\n";
        final_statement+="Подтвердите запись.";


        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(final_statement);
        alert.setContentText("Вы уверены?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL){
            return 0;
        }
        else {
            return 1;
        }
    }


    public static void bookingWarning(){
    	Alert alert = new Alert(AlertType.WARNING);
        String statement = "У вас более 3-х текущих записей. Для следующих записей необходимо обратиться к администратору.\n";
        statement+="Приносим свои извинения за неудобства.";

        alert.setTitle("Предупреждение");
        alert.setHeaderText(statement);
        alert.showAndWait();
    }

    public static int confirmBooking(int service_id, DB database){
    	ServiceInfo service = database.getServiceInfoById(service_id);

    	String final_statement = "";
        final_statement+="Услуга: "+service.service_name+"\n";
        final_statement+="Стоимость: "+service.service_price+"\n";
        final_statement+="Время: "+HelpFuncs.parseTime(service.service_time)+"\n\n";
        final_statement+="Подтвердите выбор услуги.";


        Alert alert = new Alert(AlertType.CONFIRMATION);
        
        alert.setTitle("Подтверждение");
        alert.setHeaderText(final_statement);
        alert.setContentText("Вы уверены?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL){
            return 0;
        }
        else {
            return 1;
        }
    }

    public static int confirmDeleteBookingDialog(int booking_id, DB database){
    	BookingInfo booking = database.getBookingInfoById(booking_id);

    	String employee_name = database.getEmployeeInfoById(booking.employee_id).employee_name;
        String service_name = database.getServiceInfoById(booking.service_id).service_name;
        String final_date = HelpFuncs.parseDate(booking.booking_datetime.split(" ")[0]);
        String final_time = HelpFuncs.parseTime(booking.booking_datetime.split(" ")[1]);

        String final_statement = "";
        final_statement+="Услуга: "+service_name+"\n";
        final_statement+="Мастер: "+employee_name+"\n";
        final_statement+="Дата: "+final_date+"\n";
        final_statement+="Время: "+final_time+"\n\n";
        final_statement+="Запись будет удалена.";

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(final_statement);
        alert.setContentText("Вы уверены?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL){
            return 0;
        }
        else {
            return 1;
        }
    }

    public static int confirmReBookingDialog(BookingInfo booking, DB database){
    	String employee_name = database.getEmployeeInfoById(booking.employee_id).employee_name;
        String service_name = database.getServiceInfoById(booking.service_id).service_name;
        String final_date = HelpFuncs.parseDate(booking.booking_datetime.split(" ")[0]);
        String final_time = HelpFuncs.parseTime(booking.booking_datetime.split(" ")[1]);

        String final_statement = "";
        final_statement+="Услуга: "+service_name+"\n";
        final_statement+="Мастер: "+employee_name+"\n";
        final_statement+="Дата: "+final_date+"\n";
        final_statement+="Время: "+final_time+"\n\n";
        final_statement+="Запись будет удалена. Вы будете направлены на страницу выбора даты.";


        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(final_statement);
        alert.setContentText("Вы уверены?");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL){
            return 0;
        }
        else {
            return 1;
        }
    }

    public static void openLink(String url){
    	String[] command = { "xdg-open", url };
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    }

}