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

}