import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

import javax.imageio.*;
import java.awt.*;
import java.awt.image.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

enum InsertClientCodes{
    
    SUCCESS,
    IN_BASE,
    DATABASE_CONN_ERR,
    BIRTH_ERR,
    EMAIL_ERR,
    PHONE_ERR,
    PSW_ERR,
    FILL_FIELD
}

enum VerifyClientCodes{

	SUCCESS,
	NO_LOGIN,
	PSW_INC,
	DATABASE_CONN_ERR
}

public class DB {
    Connection connection;

    
    public DB(){
        this.connection = null;
    }

    public void getConnection() throws SQLException, IOException{
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("sources/database.properties"))){
            props.load(in);
        }
        String encryptedUrl = props.getProperty("url");
        String encryptedUsername = props.getProperty("username");
        String encryptedPassword = props.getProperty("password");
        String url = "";
        String username = "";
        String password = "";

        try{
            url = EncryptionUtil.decrypt(encryptedUrl);
            username = EncryptionUtil.decrypt(encryptedUsername);
            password = EncryptionUtil.decrypt(encryptedPassword);
            System.out.println(url);
            System.out.println(username);
            System.out.println(password);
        }
        catch(Exception ex){System.out.println(ex);}
        try {
            this.connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection to VeronichkaNailsApp DB succesfull!");
        } 
        catch(Exception ex){
            System.out.println("Connection to VeronichkaNailsApp failed...");
            System.out.println(ex);
            this.connection = null;
        }
    }

    public VerifyClientCodes verifyClientInDB(int id, String login, String password){ // 0-успешно; 1-нет человека; 2-пароль не тот; 3-ошибка получения данных
        try{
            String sqlST = "SELECT * FROM CLIENTS WHERE (Client_EMAIL = ? OR Client_PHONE = ? OR Client_PHONE = ?) AND Client_ID != ? ";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setString(1, login);
            prep_statement.setString(2, login);
            prep_statement.setString(3, "+7"+login.substring(1));
            prep_statement.setInt(4, id);
            ResultSet res = prep_statement.executeQuery();
            if(res.next() == false){
                return VerifyClientCodes.NO_LOGIN;
            }
            else{
                if(password.equals(res.getString("Client_PSW"))){
                    return VerifyClientCodes.SUCCESS;
                }
                else{
                    return VerifyClientCodes.PSW_INC;
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex);
            return VerifyClientCodes.DATABASE_CONN_ERR;
        }
    }

    public InsertClientCodes insertNewClient(ClientInfo client){
        // 0-успешно добавлени, 1-такой уже есть, 2-ошибка, 3-ошибка др, 4-ошибка почты, 5-ошибка телефона, 6-ошибка пароля, 7-не все поля заполнены
        try{
            InsertClientCodes checking_code = checkClientInfo(client);
            if (checking_code != InsertClientCodes.SUCCESS){return checking_code;}
            if (client.client_phone.startsWith("8")){client.client_phone = "+7"+client.client_phone.substring(1);}

            String sql = "INSERT INTO CLIENTS (Client_EMAIL, Client_PSW, Client_PHONE, Client_NAME, Client_NICK, Client_BIRTH, Client_VISITS, Admin_comment) Values (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
            preparedStatement.setString(1, client.client_email);
            preparedStatement.setString(2, client.client_psw);
            preparedStatement.setString(3, client.client_phone);
            preparedStatement.setString(4, client.client_name);
            preparedStatement.setString(5, client.client_nickname);
            preparedStatement.setString(6, client.client_birthday);
            preparedStatement.setInt(7, 0);
            preparedStatement.setString(8, "Nothing");
            
            int rows = preparedStatement.executeUpdate();
            if (rows == 0){return InsertClientCodes.DATABASE_CONN_ERR;}
            else{return InsertClientCodes.SUCCESS;}
        }
        catch(Exception ex){
            System.out.println(ex);
            return InsertClientCodes.DATABASE_CONN_ERR;
        }
        
    }

    public InsertClientCodes updateClientInfo(ClientInfo client){
        try{
            InsertClientCodes checking_code = checkClientInfo(client);
            if(checking_code != InsertClientCodes.SUCCESS){return checking_code;}
            if (client.client_phone.startsWith("8")){client.client_phone = "+7"+client.client_phone.substring(1);}
            String sql0 = "UPDATE CLIENTS SET ";
            String sql1 = "Client_EMAIL=?, Client_PSW=?, Client_PHONE=?, Client_NAME=?, Client_NICK=?, Client_BIRTH=? WHERE Client_ID = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(sql0+sql1);
            preparedStatement.setString(1, client.client_email);
            preparedStatement.setString(2, client.client_psw);
            preparedStatement.setString(3, client.client_phone);
            preparedStatement.setString(4, client.client_name);
            preparedStatement.setString(5, client.client_nickname);
            preparedStatement.setString(6, client.client_birthday);
            preparedStatement.setInt(7, client.client_id);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0){return InsertClientCodes.DATABASE_CONN_ERR;}
            else{return InsertClientCodes.SUCCESS;}
        }
        catch(Exception ex){
            System.out.println(ex);
            return InsertClientCodes.DATABASE_CONN_ERR;
        }
    }

    public InsertClientCodes checkClientInfo(ClientInfo client){
        try{
            ////////////////////////////////////////
            //////CHECKING FILLING ALL FIELDS///////
            ////////////////////////////////////////
            if (client.client_name.length()==0 || client.client_nickname.length()==0 || client.client_email.length()==0 || client.client_phone.length()==0 || client.client_psw.length()==0){
                return InsertClientCodes.FILL_FIELD;
            }

            ////////////////////////////////////////
            //////CHECKING ALREADY IN BASE//////////
            ////////////////////////////////////////
            VerifyClientCodes verify_code_email = this.verifyClientInDB(client.client_id, client.client_email, client.client_psw);
            VerifyClientCodes verify_code_phone = this.verifyClientInDB(client.client_id, client.client_phone, client.client_psw);
            if( verify_code_email == VerifyClientCodes.SUCCESS || 
                verify_code_phone == VerifyClientCodes.SUCCESS || 
                verify_code_email == VerifyClientCodes.PSW_INC || 
                verify_code_phone == VerifyClientCodes.PSW_INC   ) {return InsertClientCodes.IN_BASE;}
            


            ////////////////////////////////////////
            ////////CHECKING BIRTHDAY DATE//////////
            ////////////////////////////////////////
            Statement statement = this.connection.createStatement();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            ResultSet date_now_res = statement.executeQuery("SELECT CURDATE()");
            date_now_res.next();
            LocalDate date_now = LocalDate.parse(date_now_res.getString(1), formatter);
            LocalDate date_birth = LocalDate.parse(client.client_birthday, formatter);
            Period period = Period.between(date_birth, date_now);


            if (period.getYears() < 1 || client.client_birthday.indexOf("-") != 4 || client.client_birthday.lastIndexOf("-") != 7 || client.client_birthday.length() != 10 || period.getYears() > 150){return InsertClientCodes.BIRTH_ERR;}
            


            ////////////////////////////////////////
            /////////////CHECKING EMAIL/////////////
            ////////////////////////////////////////
            if (client.client_email.indexOf("@") == -1 || client.client_email.length() > 50){return InsertClientCodes.EMAIL_ERR;}

            ////////////////////////////////////////
            /////////////CHECKING PHONE/////////////
            ////////////////////////////////////////
            try{long k = Long.parseLong(client.client_phone.substring(1));}
            catch(Exception ex){return InsertClientCodes.PHONE_ERR;}
            boolean phone_expr1 = client.client_phone.startsWith("+7") && client.client_phone.length() == 12;
            boolean phone_expr2 = client.client_phone.startsWith("8") && client.client_phone.length() == 11;
            if ((phone_expr1||phone_expr2) == false){return InsertClientCodes.PHONE_ERR;}


            ////////////////////////////////////////
            /////////////CHECKING PSW///////////////
            ////////////////////////////////////////
            if (client.client_psw.length() < 8 || client.client_psw.length() > 20){return InsertClientCodes.PSW_ERR;}
            return InsertClientCodes.SUCCESS;
        }
        catch (Exception ex) {
            System.out.println(ex);
            return InsertClientCodes.DATABASE_CONN_ERR;
        }
    }
    

    public BookingInfo getBookingInfoById(int id){
        try{
            String sqlST = "SELECT * FROM BOOKING WHERE Booking_ID = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setInt(1, id);
            
            ResultSet res = prep_statement.executeQuery();
            
            res.next();
            BookingInfo cur_book_info = new BookingInfo();
            
            cur_book_info.booking_id = res.getInt("Booking_ID");
            cur_book_info.service_id = res.getInt("Service_ID");
            cur_book_info.client_id = res.getInt("Client_ID");
            cur_book_info.booking_datetime = res.getString("Booking_DATETIME");
            cur_book_info.employee_id = res.getInt("Booking_EMPLOYEEID");
            cur_book_info.booking_status = res.getInt("Booking_STATUS");
            cur_book_info.admin_comment = res.getString("Admin_comment");

            
            return cur_book_info;
            }
        
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    } 
    

    public ArrayList<BookingInfo> getBookingInfoByClientId(int client_id){
        try{
            String sqlST = "SELECT * FROM BOOKING WHERE Client_ID = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setInt(1, client_id);
            
            ResultSet res = prep_statement.executeQuery();
            ArrayList<BookingInfo> arr_info = new ArrayList<BookingInfo>();
            BookingInfo cur_book_info;
            while(res.next()){
                cur_book_info = new BookingInfo();
                
                cur_book_info.booking_id = res.getInt("Booking_ID");
                cur_book_info.service_id = res.getInt("Service_ID");
                cur_book_info.client_id = res.getInt("Client_ID");
                cur_book_info.booking_datetime = res.getString("Booking_DATETIME");
                cur_book_info.employee_id = res.getInt("Booking_EMPLOYEEID");
                cur_book_info.booking_status = res.getInt("Booking_STATUS");
                cur_book_info.admin_comment = res.getString("Admin_comment");

                arr_info.add(cur_book_info);
            }
            return arr_info;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    } 

    public ServiceInfo getServiceInfoById(int service_id){
        try{
            String sqlST = "SELECT * FROM PRICE_LIST WHERE Service_ID = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setInt(1, service_id);
            ResultSet res = prep_statement.executeQuery();
            res.next();
            ServiceInfo service = new ServiceInfo();
            service.service_id = res.getInt("Service_ID");
            service.service_name = res.getString("Service_NAME");
            service.service_price = res.getDouble("Service_PRICE");
            service.service_description = res.getString("Service_DESCRIPTION");
            service.service_time = res.getString("Service_TIME");

            return service;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public ArrayList<ServiceInfo> getServiceInfo(){
        try{
            String sqlST = "SELECT * FROM PRICE_LIST";
            Statement statement = this.connection.createStatement();
            ResultSet res = statement.executeQuery(sqlST);


            ArrayList<ServiceInfo> arr_info = new ArrayList<ServiceInfo>();
            ServiceInfo cur_service_info;
            while(res.next()){
                cur_service_info = new ServiceInfo();

                cur_service_info.service_id = res.getInt("Service_ID");
                cur_service_info.service_name = res.getString("Service_NAME");
                cur_service_info.service_price = res.getDouble("Service_PRICE");
                cur_service_info.service_time = res.getString("Service_TIME");
                cur_service_info.service_description = res.getString("Service_DESCRIPTION");
                

                arr_info.add(cur_service_info);
            }
            return arr_info;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    } 

    public ClientInfo getClientInfoById(int client_id){
        try{
            String sqlST = "SELECT * FROM CLIENTS WHERE Client_ID = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setInt(1, client_id);
            ResultSet res = prep_statement.executeQuery();
            res.next();
            ClientInfo client = new ClientInfo();
            client.client_id = res.getInt("Client_ID");
            client.client_email = res.getString("Client_EMAIL");
            client.client_psw = res.getString("Client_PSW");
            client.client_phone = res.getString("Client_PHONE");
            client.client_name = res.getString("Client_NAME");
            client.client_nickname = res.getString("Client_NICK");
            client.client_birthday = res.getString("Client_BIRTH");
            client.client_visits = res.getInt("Client_VISITS");
            client.admin_comment = res.getString("Admin_comment");
            

            return client;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public ClientInfo getClientInfoByLogin(String login){
        try{
            String sqlST = "SELECT * FROM CLIENTS WHERE Client_EMAIL = ? OR Client_PHONE = ? OR Client_PHONE = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setString(1, login);
            prep_statement.setString(2, login);
            prep_statement.setString(3, "+7"+login.substring(1));
            ResultSet res = prep_statement.executeQuery();

            res.next();
            ClientInfo client = new ClientInfo();
            client.client_id = res.getInt("Client_ID");
            client.client_email = res.getString("Client_EMAIL");
            client.client_psw = res.getString("Client_PSW");
            client.client_phone = res.getString("Client_PHONE");
            client.client_name = res.getString("Client_NAME");
            client.client_nickname = res.getString("Client_NICK");
            client.client_birthday = res.getString("Client_BIRTH");
            client.admin_comment = res.getString("Admin_comment");
            return client;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public EmployeeInfo getEmployeeInfoById(int employee_id){
        try{
            String sqlST = "SELECT * FROM EMPLOYEES WHERE Employee_ID = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setInt(1, employee_id);
            ResultSet res = prep_statement.executeQuery();
            res.next();
            EmployeeInfo employee = new EmployeeInfo();
            employee.employee_id = res.getInt("Employee_ID");
            employee.employee_name = res.getString("Employee_NAME");
            employee.employee_exp = res.getString("Employee_EXP");
            employee.employee_salary = res.getDouble("Employee_SALARY");
            employee.employee_services_id_set = res.getString("Employee_ServicesIDSet");            

            return employee;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }


//заменить на cancelBookingById()

    public int cancelBookingById(int id){
        try{
            BookingInfo booking = getBookingInfoById(id);
            ServiceInfo service = getServiceInfoById(booking.service_id);

            String sqlST;
            PreparedStatement prep_statement;
            // String sqlST = "DELETE FROM BOOKING WHERE Booking_ID = ?";
            // PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            // prep_statement.setInt(1, id);
            // int rows = prep_statement.executeUpdate();

            this.changeBookingStatusById(id, 2);

            sqlST = "SELECT * FROM WORK_DAYS WHERE Employee_ID = ? AND Date = ?";
            prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setInt(1, booking.employee_id);
            prep_statement.setString(2, booking.booking_datetime.split(" ")[0].replace("-", ""));
            ResultSet res = prep_statement.executeQuery();
            res.next();
            String cur_timetable = res.getString("Timetable");

            String booking_time = booking.booking_datetime.split(" ")[1];
            String service_time = service.service_time;
            int service_cell_count = (60*Integer.parseInt(service_time.substring(0, 2))+Integer.parseInt(service_time.substring(3, 5)))/30;


            String time_start_str = res.getString("Time_START");
            int hours_start = Integer.parseInt(time_start_str.substring(0, 2));
            int minutes_start = Integer.parseInt(time_start_str.substring(3, 5));
            LocalTime time_start = LocalTime.of(hours_start, minutes_start);

            int hours_book = Integer.parseInt(booking_time.substring(0, 2));
            int minutes_book = Integer.parseInt(booking_time.substring(3, 5));
            LocalTime time_book = LocalTime.of(hours_book, minutes_book);

            Duration duration = Duration.between(time_start, time_book);
            
            int cells_from_start = (int)duration.toMinutes()/30;
            
            
            String new_timetable = cur_timetable.substring(0, cells_from_start)+"1".repeat(service_cell_count)+cur_timetable.substring(cells_from_start+service_cell_count, cur_timetable.length());
            
            sqlST = "UPDATE WORK_DAYS SET Timetable = ? WHERE Employee_ID = ? AND Date = ?";
            prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setString(1, new_timetable);
            prep_statement.setInt(2, booking.employee_id);
            prep_statement.setString(3, booking.booking_datetime.split(" ")[0].replace("-", ""));
            int rows = prep_statement.executeUpdate();

            return rows;
        }
        catch(Exception ex){
            System.out.println(ex);
            return 0;
        }
    }

    public void updateBookingTableByClientId(int id){
        try{
            ArrayList<BookingInfo> booking_info = this.getBookingInfoByClientId(id);
            Statement statement = this.connection.createStatement();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ResultSet datetime_now_res = statement.executeQuery("SELECT NOW()");
            datetime_now_res.next();
            LocalDateTime datetime_now = LocalDateTime.parse(datetime_now_res.getString(1), formatter);
            for (int i = 0; i < booking_info.size(); i++){
                LocalDateTime datetime_cur_booking = LocalDateTime.parse(booking_info.get(i).booking_datetime, formatter);
                Duration duration = Duration.between(datetime_now, datetime_cur_booking);

                if (duration.toDays() <= -1 && booking_info.get(i).booking_status == 1){
                    this.changeBookingStatusById(booking_info.get(i).booking_id, 2);
                }

            }
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }

    public LocalDateTime getCurrentDateTime(){
        try{
            Statement statement = this.connection.createStatement();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ResultSet datetime_now_res = statement.executeQuery("SELECT NOW()");
            datetime_now_res.next();
            LocalDateTime datetime_now = LocalDateTime.parse(datetime_now_res.getString(1), formatter);
            return datetime_now;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }



    public void changeBookingStatusById(int id, int status){
        try{
            String sqlST = "UPDATE BOOKING SET Booking_STATUS = ? WHERE Booking_ID = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setInt(1, status);
            prep_statement.setInt(2, id);
            int rows = prep_statement.executeUpdate();
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }

    public ArrayList<String> getFreeTimeInfo(String date, int employee_id, int service_id){
        try{
            String sqlST = "SELECT * FROM WORK_DAYS WHERE Date = ? AND Employee_ID = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setString(1, date.replace("-", ""));
            prep_statement.setInt(2, employee_id);
            ResultSet res = prep_statement.executeQuery();
            boolean res_next = res.next();
            ServiceInfo service = getServiceInfoById(service_id);
            ArrayList<String> result_array = new ArrayList<String>();
            if (res_next == false){
                return result_array;
            }

            String timetable_array = res.getString("Timetable");
            String time_start_str = res.getString("Time_START");
            String service_time = service.service_time;

            int service_cell_count = (60*Integer.parseInt(service_time.substring(0, 2))+Integer.parseInt(service_time.substring(3, 5)))/30;

            int hours_start = Integer.parseInt(time_start_str.substring(0, 2));
            int minutes_start = Integer.parseInt(time_start_str.substring(3, 5));

            LocalTime time_start = LocalTime.of(hours_start, minutes_start);

            for (int i = 0; i <= timetable_array.length() - service_cell_count; i++){
                String cur_arr = timetable_array.substring(i, i+service_cell_count);
                if (cur_arr.indexOf("0") == -1){
                    LocalTime time_to_arr = time_start.plusMinutes(i*30);
                    result_array.add(time_to_arr.toString());
                }
            }
            return result_array;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }   


    public ArrayList<Integer> getAllEmployeesIdWhoHaveService(int service_id){
        try{
            String sqlST = "SELECT * FROM EMPLOYEES";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            ResultSet res = prep_statement.executeQuery();
            ArrayList<Integer> ans = new ArrayList<Integer>();
            while(res.next()){
                String[] employee_services_id_set = res.getString("Employee_ServicesIDSet").split(" ");
                ArrayList<String> services_id_set_arr = new ArrayList<String>(Arrays.asList(employee_services_id_set));
                if (services_id_set_arr.indexOf(String.valueOf(service_id)) != -1){
                    ans.add(res.getInt("Employee_ID"));
                }
            }
            return ans;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public void updateEmployeeWorkDayTimetable(String time, int service_id, String date, int employee_id){
        try{
            ServiceInfo service = getServiceInfoById(service_id);
            String service_time = service.service_time;
            int service_cell_count = (60*Integer.parseInt(service_time.substring(0, 2))+Integer.parseInt(service_time.substring(3, 5)))/30;
            
            String sqlST = "SELECT * FROM WORK_DAYS WHERE Date = ? AND Employee_ID = ?";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setString(1, date.replace("-", ""));
            prep_statement.setInt(2, employee_id);
            ResultSet res = prep_statement.executeQuery();
            res.next();
            String cur_timetable = res.getString("Timetable");

            String time_start_str = res.getString("Time_START");
            int hours_start = Integer.parseInt(time_start_str.substring(0, 2));
            int minutes_start = Integer.parseInt(time_start_str.substring(3, 5));
            LocalTime time_start = LocalTime.of(hours_start, minutes_start);

            int hours_book = Integer.parseInt(time.substring(0, 2));
            int minutes_book = Integer.parseInt(time.substring(3, 5));
            LocalTime time_book = LocalTime.of(hours_book, minutes_book);

            Duration duration = Duration.between(time_start, time_book);
            
            int cells_from_start = (int)duration.toMinutes()/30;
            
            
            String new_timetable = cur_timetable.substring(0, cells_from_start)+"0".repeat(service_cell_count)+cur_timetable.substring(cells_from_start+service_cell_count, cur_timetable.length());
            
            sqlST = "UPDATE WORK_DAYS SET Timetable = ? WHERE Employee_ID = ? AND Date = ?";
            prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setString(1, new_timetable);
            prep_statement.setInt(2, employee_id);
            prep_statement.setString(3, date.replace("-", ""));
            int rows = prep_statement.executeUpdate();
        }

        catch(Exception ex){
            System.out.println(ex);
            return;
        }
    }

    public void updateBookingBase(int client_id, int service_id, String date, String time, int employee_id){
        try{
            
            String final_datetime = date.replace("-", "") + time.replace(":", "")+"00";

            String sqlST = "INSERT INTO BOOKING (Service_ID, Client_ID, Booking_DATETIME, Booking_EMPLOYEEID, Booking_STATUS, Admin_comment) Values (?, ?, ?, ?, ?, ?);";;
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            prep_statement.setInt(1, service_id);
            prep_statement.setInt(2, client_id);
            prep_statement.setString(3, final_datetime);
            prep_statement.setInt(4, employee_id);
            prep_statement.setInt(5, 1);
            prep_statement.setString(6, "Доп Информация: ");
            
            int rows = prep_statement.executeUpdate();
        }

        catch(Exception ex){
            System.out.println(ex);
            return;
        }
    }

    public int[] getDiscountsInfo(){
        try{
            int[] discount_info = new int[6];
            String sqlST = "SELECT * FROM DISCOUNTS_INFO";
            PreparedStatement prep_statement = this.connection.prepareStatement(sqlST);
            ResultSet res = prep_statement.executeQuery();
            while(res.next()){
                discount_info[0] = res.getInt("Discount_min");
                discount_info[1] = res.getInt("Discount_min_start");
                discount_info[2] = res.getInt("Discount_mid");
                discount_info[3] = res.getInt("Discount_mid_start");
                discount_info[4] = res.getInt("Discount_max");
                discount_info[5] = res.getInt("Discount_max_start");
            }
            return discount_info;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }

    }


    public void createClientAvatar(int client_id){
        ClientInfo client = getClientInfoById(client_id);
        String[] fio = client.client_name.split(" ");
        String initials = "";

        if(fio.length == 1){initials+=fio[0].charAt(0);}
        else{initials+=fio[0].charAt(0)+""+fio[1].charAt(0);}


        BufferedImage background = loadImage("photos/background.jpg");
        if (background == null) {
            System.out.println("Ошибка загрузки фонового изображения.");
            return;
        }

        // Создаем новое изображение с фоном
        BufferedImage outputImage = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = outputImage.createGraphics();
        
        // Рисуем фоновое изображение
        g.drawImage(background, 0, 0, null);
        
        // Устанавливаем шрифт и цвет для инициалов
        g.setFont(new Font("Arial", Font.BOLD, 350));
        g.setColor(Color.WHITE); // Цвет текста (можно изменить)
        
        // Вычисляем размеры текста и его положение
        FontMetrics fm = g.getFontMetrics();
        int x = (outputImage.getWidth() - fm.stringWidth(initials)) / 2; // Центрируем по X
        int y = (outputImage.getHeight() - fm.getHeight()) / 2 + fm.getAscent(); // Центрируем по Y

        // Рисуем инициалы на изображении
        g.drawString(initials, x, y);
        
        // Освобождаем ресурсы графики
        g.dispose();

        // Сохраняем итоговое изображение
        saveImage(outputImage, "photos/client_avatar.jpg");
    }

    private static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveImage(BufferedImage image, String path) {
        try {
            ImageIO.write(image, "jpg", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadEmployeesAvatar(){
        try{
            String sql = "SELECT * FROM EMPLOYEES_AVATARS";
            PreparedStatement pstmt = this.connection.prepareStatement(sql);
            // pstmt.setInt(1, 1);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Image_NAME");
                byte[] imageBytes = rs.getBytes("Image_FILE");

                // Сохранение изображения в файл
                try (FileOutputStream fos = new FileOutputStream("photos/employees/"+name)) {
                    fos.write(imageBytes);
                    System.out.println("Image retrieved and saved in employees_avatar as: " + name);
                }
            }
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }

    public int downloadGalery(){
        try{
            String sql = "SELECT * FROM GALERY";
            PreparedStatement pstmt = this.connection.prepareStatement(sql);
            // pstmt.setInt(1, 1);
            int num = 0;
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                num++;
                String name = rs.getString("Image_NAME");
                byte[] imageBytes = rs.getBytes("Image_FILE");

                // Сохранение изображения в файл
                try (FileOutputStream fos = new FileOutputStream("photos/galery/"+name)) {
                    fos.write(imageBytes);
                    System.out.println("Image retrieved and saved in galery as: " + name);
                }
            }
            return num;
        }
        catch(Exception ex){
            System.out.println(ex);
            return 0;
        }
    }

    public SaloonInfo getSaloonInfo(){
        try{
            SaloonInfo saloon = new SaloonInfo();
            String sql = "SELECT * FROM MAIN_SALOON_INFO";
            PreparedStatement pstmt = this.connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                saloon.address = rs.getString("Address");
                saloon.contacts = rs.getString("Contacts");
                saloon.work_hours = rs.getString("WorkHours");
                saloon.map = null;

                byte[] imageBytes = rs.getBytes("Map");
                try (FileOutputStream fos = new FileOutputStream("photos/map.jpg")) {
                    fos.write(imageBytes);
                    System.out.println("Image retrieved and saved in photos as: map.jpg" );
                }
                try{saloon.map = new Image(new FileInputStream("photos/map.jpg"));}
                catch(Exception ex){
                    saloon.map = null;
                    System.out.println(ex);
                }
            }
            return saloon;   
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
        }
    }

    public ArrayList<String[]> getFAQ(){
        try{
            ArrayList<String[]> list = new ArrayList<String[]>();
            String sql = "SELECT * FROM FAQ";
            PreparedStatement pstmt = this.connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String[] cur_quest = new String[2];
                cur_quest[0] = rs.getString("Question");
                cur_quest[1] = rs.getString("Answer");
                list.add(cur_quest);
            }
            return list;
        }
        catch(Exception ex){
            System.out.println(ex);
            return new ArrayList<String[]>();
        }
    }

}


class ServiceInfo{
    int service_id;
    String service_name;
    double service_price;
    String service_description;
    String service_time;
}

class ClientInfo{
    int client_id;
    String client_email;
    String client_psw;
    String client_phone;
    String client_name;
    String client_nickname;
    String client_birthday;
    int client_visits;
    String admin_comment;
}

class BookingInfo{
    int booking_id;
    int service_id;
    int client_id;
    String booking_datetime;
    int employee_id;
    int booking_status;
    String admin_comment;
    boolean is_valid = true;
}

class EmployeeInfo{
    int employee_id;
    String employee_name;
    String employee_exp;
    double employee_salary;
    String employee_services_id_set;
}

class SaloonInfo{
    Image map;
    String address;
    String contacts;
    String work_hours;
}