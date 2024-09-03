package internship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Book {
    public static void addBook(String title, String author, int year) throws SQLException {
        String query = "INSERT INTO books (title, author, published_year) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Kitap ba�l���n� ve yazar� �ifrele
            String encryptedTitle = EncryptionUtil.encrypt(title);
            String encryptedAuthor = EncryptionUtil.encrypt(author);
            
            statement.setString(1, encryptedTitle);
            statement.setString(2, encryptedAuthor);
            statement.setInt(3, year);
            statement.executeUpdate();
            System.out.println("Kitap ba�ar�yla eklendi!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBook(int id, String title, String author, int year) throws SQLException {
        String query = "UPDATE books SET title = ?, author = ?, published_year = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Kitap ba�l���n� ve yazar� �ifrele
            String encryptedTitle = EncryptionUtil.encrypt(title);
            String encryptedAuthor = EncryptionUtil.encrypt(author);
            
            statement.setString(1, encryptedTitle);
            statement.setString(2, encryptedAuthor);
            statement.setInt(3, year);
            statement.setInt(4, id);
            statement.executeUpdate();
            System.out.println("Kitap ba�ar�yla g�ncellendi!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteBook(int id) {
        String deleteTransactionsQuery = "DELETE FROM transactions WHERE book_id = ?";
        String deleteBookQuery = "DELETE FROM books WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement deleteTransactionsStatement = connection.prepareStatement(deleteTransactionsQuery);
             PreparedStatement deleteBookStatement = connection.prepareStatement(deleteBookQuery)) {
            
            deleteTransactionsStatement.setInt(1, id);
            deleteTransactionsStatement.executeUpdate();

            deleteBookStatement.setInt(1, id);
            int rowsAffected = deleteBookStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Kitap ba�ar�yla silindi!");
            } else {
                System.out.println("Kitap bulunamad�.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void viewBooks() {
        String query = "SELECT * FROM books";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String encryptedTitle = resultSet.getString("title");
                String encryptedAuthor = resultSet.getString("author");

                try {
                    // �ifrelenmi� verileri ��z
                    String decryptedTitle = EncryptionUtil.decrypt(encryptedTitle);
                    String decryptedAuthor = EncryptionUtil.decrypt(encryptedAuthor);

                    // ��z�len verileri ekrana yazd�r
                    System.out.println("ID: " + resultSet.getInt("id"));
                    System.out.println("Ba�l�k: " + decryptedTitle);
                    System.out.println("Yazar: " + decryptedAuthor);
                    System.out.println("Yay�n Y�l�: " + resultSet.getInt("published_year"));
                    System.out.println("Mevcut: " + resultSet.getBoolean("available"));
                    System.out.println("---------------------------");
                } catch (Exception e) {
                    System.out.println("�ifre ��zme i�lemi ba�ar�s�z oldu.");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public static void searchBooks(String keyword) throws SQLException {
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Anahtar kelimeyi �ifrele ve arama sorgusunda kullan
            statement.setString(1, "%" + EncryptionUtil.encrypt(keyword) + "%");
            statement.setString(2, "%" + EncryptionUtil.encrypt(keyword) + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // �ifrelenmi� ba�l�k ve yazar bilgilerini ��z
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
}
