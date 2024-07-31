import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
 
public class Help{
       
    public static void main(String[] args) {
         try{
             Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
             try (Connection conn = getConnection()){
                  
                System.out.println("Connection to VeronichkaNailsApp DB succesfull!");
             }
         }
         catch(Exception ex){
             System.out.println("Connection failed...");
              
             System.out.println(ex);
         }
    }
     
    public static Connection getConnection() throws SQLException, IOException{
         
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(Paths.get("sources/database.properties"))){
            props.load(in);
        }
        String url = props.getProperty("url");
        String username = props.getProperty("username");
        String password = props.getProperty("password");
         
        return DriverManager.getConnection(url, username, password);
    }
}