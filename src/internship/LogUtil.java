package internship;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class LogUtil {

    private static final String LOG_FILE = "login_attempts.log";

    public static void log(String message) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println(LocalDateTime.now() + " - " + message);
        } catch (IOException e) {
            System.out.println("Loglama sýrasýnda bir hata oluþtu: " + e.getMessage());
        }
    }
}
