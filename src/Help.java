import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

import java.sql.*;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.net.*;
import java.awt.*;


class EncryptionUtil {
   private static final String ALGORITHM = "AES";
   private static final String TRANSFORMATION = "AES";

   // Ваш фиксированный ключ (должен быть 16 байт для AES-128)
   private static final String SECRET_KEY = "1549713486237445"; // Замените на ваш ключ

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

public class Help{
    public static void main(String[] args) {
        try{
            System.out.println(EncryptionUtil.encrypt("jdbc:mysql://95.165.99.218:3306/VeronichkaNailsApp?serverTimezone=Europe/Moscow&useSSL=false&allowPublicKeyRetrieval=true"));
        }
        catch(Exception ex){
            System.out.println(ex);
        }
   }

   public static void loadDatabaseProperties() {
       try {
           // Предположим, что вы извлекли зашифрованные данные из файла
           String encryptedUrl = "..."; // Получите зашифрованный URL
           String encryptedUsername = "..."; // Получите зашифрованный username
           String encryptedPassword = "..."; // Получите зашифрованный password

           // Дешифрование данных
           String dbUrl = EncryptionUtil.decrypt(encryptedUrl);
           String username = EncryptionUtil.decrypt(encryptedUsername);
           String password = EncryptionUtil.decrypt(encryptedPassword);

           // Используйте эти данные для подключения к базе данных
           System.out.println("DB URL: " + dbUrl);
           System.out.println("Username: " + username);
           System.out.println("Password: " + password);
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}