import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Help {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Считываем ФИО
        System.out.print("Введите ваше ФИО: ");
        String fullName = scanner.nextLine();
        
        // Получаем инициалы
        String initials = getInitials(fullName);
        
        // Загружаем фоновое изображение
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
        g.setFont(new Font("Arial", Font.BOLD, 100));
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
        saveImage(outputImage, "photos/output.jpg");
        
        System.out.println("Изображение успешно создано: output.jpg");
    }

    private static String getInitials(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length < 2) return "";
        return parts[0].charAt(0) + "" + parts[1].charAt(0); // Первые буквы имени и фамилии
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
}
