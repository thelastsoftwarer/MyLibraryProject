package internship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transaction {

    // Kitap �d�n� alma i�lemi
    public static void borrowBook(int bookId) {
        int userId = User.getCurrentUserId();
        String query = "INSERT INTO transactions (user_id, book_id) VALUES (?, ?)";
        String updateQuery = "UPDATE books SET available = false WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            
            if (!doesBookExist(bookId)) {
                System.out.println("Kitap ID bulunamad�: " + bookId);
                return;
            }
            
            if (doesUserHaveBook(userId, bookId)) {
                System.out.println("Zaten bu kitap sizde var.");
                return;
            }

            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            statement.executeUpdate();

            updateStatement.setInt(1, bookId);
            updateStatement.executeUpdate();

            System.out.println("Kitap ba�ar�yla �d�n� al�nd�!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kitap geri verme i�lemi
    public static void returnBook(int bookId) {
        int userId = User.getCurrentUserId();
        String query = "UPDATE transactions SET return_date = CURRENT_TIMESTAMP WHERE book_id = ? AND user_id = ? AND return_date IS NULL";
        String updateQuery = "UPDATE books SET available = true WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            statement.setInt(1, bookId);
            statement.setInt(2, userId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                updateStatement.setInt(1, bookId);
                updateStatement.executeUpdate();
                System.out.println("Kitap ba�ar�yla geri verildi!");
            } else {
                System.out.println("Geri verilecek kitap bulunamad� veya zaten geri verilmi�.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullan�c�n�n �d�n� ald��� kitaplar� g�r�nt�leme
    public static void viewUserBooks() throws SQLException {
        int userId = User.getCurrentUserId();
        String query = "SELECT books.id, books.title, books.author, books.published_year, books.available FROM books " +
                       "JOIN transactions ON books.id = transactions.book_id " +
                       "WHERE transactions.user_id = ? AND transactions.return_date IS NULL";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String decryptedTitle = EncryptionUtil.decrypt(resultSet.getString("title"));
                String decryptedAuthor = EncryptionUtil.decrypt(resultSet.getString("author"));

                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Ba�l�k: " + decryptedTitle);
                System.out.println("Yazar: " + decryptedAuthor);
                System.out.println("Yay�n Y�l�: " + resultSet.getInt("published_year"));
                System.out.println("Mevcut: " + resultSet.getBoolean("available"));
                System.out.println("---------------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // T�m kullan�c�lar�n �d�n� ald��� kitaplar� g�r�nt�leme
    public static void viewAllUserBooks() throws SQLException {
        String query = "SELECT users.username, books.id, books.title, books.author FROM books " +
                       "JOIN transactions ON books.id = transactions.book_id " +
                       "JOIN users ON transactions.user_id = users.id " +
                       "WHERE transactions.return_date IS NULL";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String decryptedTitle = EncryptionUtil.decrypt(resultSet.getString("title"));
                String decryptedAuthor = EncryptionUtil.decrypt(resultSet.getString("author"));

                System.out.println("Kullan�c�: " + resultSet.getString("username"));
                System.out.println("Kitap ID: " + resultSet.getInt("id"));
                System.out.println("Ba�l�k: " + decryptedTitle);
                System.out.println("Yazar: " + decryptedAuthor);
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kitab�n veritaban�nda var olup olmad���n� kontrol etme
    private static boolean doesBookExist(int bookId) {
        String query = "SELECT id FROM books WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, bookId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kullan�c�n�n kitab� zaten �d�n� al�p almad���n� kontrol etme
    private static boolean doesUserHaveBook(int userId, int bookId) {
        String query = "SELECT * FROM transactions WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, bookId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
