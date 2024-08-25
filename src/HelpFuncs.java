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
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;



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

	public static void loadLoyaltyWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadLoyaltyWindow());
	}

	public static void loadResetDataWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadResetDataWindow());
	}

	public static void loadGaleryWindowFunc(Node node, Main cur, int page_num){
		node.getScene().setRoot(cur.loadGaleryWindow(page_num));
	}

	public static void loadContactsWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadContactsWindow());
	}

	public static void loadFAQWindowFunc(Node node, Main cur){
		node.getScene().setRoot(cur.loadFAQWindow());
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

    public static int discountCalc(ClientInfo client, DB database){
    	int[] discounts_info = database.getDiscountsInfo();
    	if (discounts_info == null || client.client_visits < discounts_info[1]){
    		return 0;
    	}
    	else if(client.client_visits < discounts_info[3]){
    		return discounts_info[0];
    	}
    	else if(client.client_visits < discounts_info[5]){
    		return discounts_info[2];
    	}
    	else{
    		return discounts_info[4];
    	}
    }

    public static LocalDate strToLocalDate(String date){
    	int year = Integer.parseInt(date.split("-")[0]);
    	int month = Integer.parseInt(date.split("-")[1]);
    	int day = Integer.parseInt(date.split("-")[2]);
    	return LocalDate.of(year, month, day);
    }

    public static void openImageInBrowser(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // Используем xdg-open для открытия файла
                ProcessBuilder processBuilder = new ProcessBuilder("xdg-open", imageFile.getAbsolutePath());
                processBuilder.start();
            } else {
                System.out.println("Файл не найден: " + imagePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Image getEmployeeAvatarById(int id){
		Image avatar_image = null;
		try{avatar_image = new Image(new FileInputStream(String.format("photos/employees/%d.jpg", id)));}
        catch(Exception ex){
            try{avatar_image = new Image(new FileInputStream("photos/standard.jpg"));}
            catch(Exception exc){System.out.println(exc);}
        }
        return avatar_image;
	}

	public static Image getClientAvatar(){
		Image avatar_image = null;
        try{avatar_image = new Image(new FileInputStream("photos/client_avatar.jpg"));}
        catch(Exception ex){System.out.println(ex);}
        return avatar_image;
	}

	public static String getStrOfBookingStatus(BookingInfo info){
		if(info.booking_status == 1){return "Ждем Вас!";}
        else if(info.booking_status == 2){return "Отменено";}
        else{return "Завершено";}
	}

	public static boolean sendMail(String from, String psw, String to, String subject, String content){
		final String username = from;
	    final String password = psw;

	    Properties prop = new Properties();
	    prop.put("mail.smtp.host", "smtp.gmail.com");
	    prop.put("mail.smtp.port", "587");
	    prop.put("mail.smtp.auth", "true");
	    prop.put("mail.smtp.starttls.enable", "true"); //TLS
	    
	    Session session = Session.getInstance(prop,
	        new javax.mail.Authenticator() {
	            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
	                return new javax.mail.PasswordAuthentication(username, password);
	            }
	        });

	        try {

	            Message message = new MimeMessage(session);
	            message.setFrom(new InternetAddress(from));
	            message.setRecipients(
	                    Message.RecipientType.TO,
	                    InternetAddress.parse(to)
	            );
	            message.setSubject(subject);
	            message.setText(content);

	            Transport.send(message);

	            System.out.println("Done");
	            return true;

	        } catch (MessagingException e) {
	            e.printStackTrace();
	            return false;
	    }
	}

	public static String getRandomPswAsStr(int size){
		String ans = "";
		Random rnd = new Random();
		for (int i = 0; i < size; i++){
			ans+=String.valueOf(rnd.nextInt(10));
		}
		return ans;
	}

}

class EncryptionUtil {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES";
	private static final String SECRET_KEY = "1549713486237445";

	public static String encrypt(String data) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedData = cipher.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(encryptedData);
	}

	public static String decrypt(String encryptedData) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(TRANSFORMATION);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
		return new String(decryptedData);
	}

	


}