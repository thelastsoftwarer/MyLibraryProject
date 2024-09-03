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
            // Kitap baþlýðýný ve yazarý þifrele
            String encryptedTitle = EncryptionUtil.encrypt(title);
            String encryptedAuthor = EncryptionUtil.encrypt(author);
            
            statement.setString(1, encryptedTitle);
            statement.setString(2, encryptedAuthor);
            statement.setInt(3, year);
            statement.executeUpdate();
            System.out.println("Kitap baþarýyla eklendi!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBook(int id, String title, String author, int year) throws SQLException {
        String query = "UPDATE books SET title = ?, author = ?, published_year = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            // Kitap baþlýðýný ve yazarý þifrele
            String encryptedTitle = EncryptionUtil.encrypt(title);
            String encryptedAuthor = EncryptionUtil.encrypt(author);
            
            statement.setString(1, encryptedTitle);
            statement.setString(2, encryptedAuthor);
            statement.setInt(3, year);
            statement.setInt(4, id);
            statement.executeUpdate();
            System.out.println("Kitap baþarýyla güncellendi!");
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
                System.out.println("Kitap baþarýyla silindi!");
            } else {
                System.out.println("Kitap bulunamadý.");
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
                    // Þifrelenmiþ verileri çöz
                    String decryptedTitle = EncryptionUtil.decrypt(encryptedTitle);
                    String decryptedAuthor = EncryptionUtil.decrypt(encryptedAuthor);

                    // Çözülen verileri ekrana yazdýr
                    System.out.println("ID: " + resultSet.getInt("id"));
                    System.out.println("Baþlýk: " + decryptedTitle);
                    System.out.println("Yazar: " + decryptedAuthor);
                    System.out.println("Yayýn Yýlý: " + resultSet.getInt("published_year"));
                    System.out.println("Mevcut: " + resultSet.getBoolean("available"));
                    System.out.println("---------------------------");
                } catch (Exception e) {
                    System.out.println("Þifre çözme iþlemi baþarýsýz oldu.");
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
            // Anahtar kelimeyi þifrele ve arama sorgusunda kullan
            statement.setString(1, "%" + EncryptionUtil.encrypt(keyword) + "%");
            statement.setString(2, "%" + EncryptionUtil.encrypt(keyword) + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // Þifrelenmiþ baþlýk ve yazar bilgilerini çöz
                String decryptedTitle = EncryptionUtil.decrypt(resultSet.getString("title"));
                String decryptedAuthor = EncryptionUtil.decrypt(resultSet.getString("author"));
                
                System.out.println("ID: " + resultSet.getInt("id"));
                System.out.println("Baþlýk: " + decryptedTitle);
                System.out.println("Yazar: " + decryptedAuthor);
                System.out.println("Yayýn Yýlý: " + resultSet.getInt("published_year"));
                System.out.println("Mevcut: " + resultSet.getBoolean("available"));
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
