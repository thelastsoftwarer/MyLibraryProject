package internship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private static int currentUserId = -1;
    private static String currentUserRole = "";

    public static void registerUser(String username, String password) {
        String hashedPassword = SecurityUtil.hashPassword(password);
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, 'member')";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            statement.executeUpdate();
            System.out.println("Kullanýcý baþarýyla kaydedildi!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean loginUser(String username, String password) {
        String hashedPassword = SecurityUtil.hashPassword(password);
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, hashedPassword);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currentUserId = resultSet.getInt("id");
                currentUserRole = resultSet.getString("role");
                return true;
            } else {
                logFailedLoginAttempt(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void logFailedLoginAttempt(String username) {
        String message = "Baþarýsýz giriþ denemesi - Kullanýcý: " + username;
        LogUtil.log(message);
        System.out.println(message);  // Konsolda da göstermek için
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static String getCurrentUserRole() {
        return currentUserRole;
    }

    public static void logout() {
        currentUserId = -1;
        currentUserRole = "";
    }
}
