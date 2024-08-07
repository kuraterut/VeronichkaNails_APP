import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

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
            // System.out.println("kek");
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

    public VerifyClientCodes verifyClientInDB(String login, String password){ // 0-успешно; 1-нет человека; 2-пароль не тот; 3-ошибка получения данных
        try{
            String sqlST = "SELECT * FROM CLIENTS WHERE Client_EMAIL = ? OR Client_PHONE = ?";
            PreparedStatement prep_statement = this.db_conn.prepareStatement(sqlST);
            prep_statement.setString(1, login);
            prep_statement.setString(2, login);
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

    public InsertClientCodes insertNewClient(String fio, String nickname, String email, String phone, String birth, String psw){
        // 0-успешно добавлени, 1-такой уже есть, 2-ошибка, 3-ошибка др, 4-ошибка почты, 5-ошибка телефона, 6-ошибка пароля, 7-не все поля заполнены
        try{
            ////////////////////////////////////////
            //////CHECKING FILLING ALL FIELDS///////
            ////////////////////////////////////////
            if (fio.length()==0 || nickname.length()==0 || email.length()==0 || phone.length()==0 || psw.length()==0){
                return InsertClientCodes.FILL_FIELD;
            }

            ////////////////////////////////////////
            //////CHECKING ALREADY IN BASE//////////
            ////////////////////////////////////////
            VerifyClientCodes verify_code_email = this.verifyClientInDB(email, psw);
            VerifyClientCodes verify_code_phone = this.verifyClientInDB(phone, psw);
            if( verify_code_email == VerifyClientCodes.SUCCESS || 
            	verify_code_phone == VerifyClientCodes.SUCCESS || 
            	verify_code_email == VerifyClientCodes.PSW_INC || 
            	verify_code_phone == VerifyClientCodes.PSW_INC	 ) {return InsertClientCodes.IN_BASE;}
            


            ////////////////////////////////////////
            ////////CHECKING BIRTHDAY DATE//////////
            ////////////////////////////////////////
            Statement statement = this.db_conn.createStatement();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            ResultSet date_now_res = statement.executeQuery("SELECT CURDATE()");
            date_now_res.next();
            LocalDate date_now = LocalDate.parse(date_now_res.getString(1), formatter);
            LocalDate date_birth = LocalDate.parse(birth, formatter);
            Period period = Period.between(date_birth, date_now);


            if (period.getYears() < 1 || birth.indexOf("-") != 4 || birth.lastIndexOf("-") != 7 || birth.length() != 10 || period.getYears() > 150){System.out.println(birth);return InsertClientCodes.BIRTH_ERR;}
            


            ////////////////////////////////////////
            /////////////CHECKING EMAIL/////////////
            ////////////////////////////////////////
            if (email.indexOf("@") == -1 || email.length() > 50){return InsertClientCodes.EMAIL_ERR;}

            ////////////////////////////////////////
            /////////////CHECKING PHONE/////////////
            ////////////////////////////////////////
            try{long k = Long.parseLong(phone.substring(1));}
            catch(Exception ex){return InsertClientCodes.PHONE_ERR;}
            if (phone.startsWith("+7") == false || phone.length() != 12){return InsertClientCodes.PHONE_ERR;}


            ////////////////////////////////////////
            /////////////CHECKING PSW///////////////
            ////////////////////////////////////////
            if (psw.length() < 8 || psw.length() > 20){return InsertClientCodes.PSW_ERR;}

            String sql = "INSERT INTO CLIENTS (Client_EMAIL, Client_PSW, Client_PHONE, Client_NAME, Client_NICK, Client_BIRTH, Admin_comment) Values (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = this.db_conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, psw);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, fio);
            preparedStatement.setString(5, nickname);
            preparedStatement.setString(6, birth);
            preparedStatement.setString(7, "Nothing");
            
            int rows = preparedStatement.executeUpdate();
            if (rows == 0){return InsertClientCodes.DATABASE_CONN_ERR;}
            else{return InsertClientCodes.SUCCESS;}
        }
        catch(Exception ex){
            System.out.println(ex);
            return InsertClientCodes.DATABASE_CONN_ERR;
        }
        
    }

    public String getNickname(String login){
        try{
            String sqlST = "SELECT * FROM CLIENTS WHERE Client_EMAIL = ? OR Client_PHONE = ?";
            PreparedStatement prep_statement = this.db_conn.prepareStatement(sqlST);
            prep_statement.setString(1, login);
            prep_statement.setString(2, login);
            ResultSet res = prep_statement.executeQuery();
            res.next();
            return res.getString("Client_NICK");
        }
        catch(Exception ex){
            System.out.println(ex);
            return "";            
        }
    }

    public ArrayList<BookingInfo> getBookingInfoByClientId(int client_id){
        try{
            String sqlST = "SELECT * FROM BOOKING WHERE Client_ID = ?";
            PreparedStatement prep_statement = this.db_conn.prepareStatement(sqlST);
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
            PreparedStatement prep_statement = this.db_conn.prepareStatement(sqlST);
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

    public ClientInfo getClientInfoById(int client_id){
        try{
            String sqlST = "SELECT * FROM CLIENTS WHERE Client_ID = ?";
            PreparedStatement prep_statement = this.db_conn.prepareStatement(sqlST);
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
            String sqlST = "SELECT * FROM CLIENTS WHERE Client_EMAIL = ? OR Client_PHONE = ?";
            PreparedStatement prep_statement = this.db_conn.prepareStatement(sqlST);
            prep_statement.setString(1, login);
            prep_statement.setString(2, login);
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
            PreparedStatement prep_statement = this.db_conn.prepareStatement(sqlST);
            prep_statement.setInt(1, employee_id);
            ResultSet res = prep_statement.executeQuery();
            res.next();
            EmployeeInfo employee = new EmployeeInfo();
            employee.employee_id = res.getInt("Employee_ID");
            employee.employee_name = res.getString("Employee_NAME");
            employee.employee_exp = res.getString("Employee_EXP");
            employee.employee_salary = res.getDouble("Employee_SALARY");
            employee.employee_type = res.getString("Employee_TYPE");
            

            return employee;
        }
        catch(Exception ex){
            System.out.println(ex);
            return null;
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
    String admin_comment;
}

class BookingInfo{
    int booking_id;
    int service_id;
    int client_id;
    String booking_datetime;
    int employee_id;
    String admin_comment;
    boolean is_valid = true;
}

class EmployeeInfo{
    int employee_id;
    String employee_name;
    String employee_exp;
    double employee_salary;
    String employee_type;
}