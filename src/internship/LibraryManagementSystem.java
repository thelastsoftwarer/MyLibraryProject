package internship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class LibraryManagementSystem {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            if (User.getCurrentUserId() == -1) {
                displayLoginMenu();
            } else {
                displayMainMenu();
            }
        }
    }

    private static void displayLoginMenu() {
        while (true) {
            System.out.println("1. Kullan�c� Kayd�");
            System.out.println("2. Kullan�c� Giri�i");
            System.out.println("3. Kitaplar� G�r�nt�le (Giri� Yapmadan)");
            System.out.println("4. ��k��");
            System.out.print("Bir se�enek se�in: ");

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); 

                switch (option) {
                    case 1:
                        registerUser();
                        return;
                    case 2:
                        loginUser();
                        return;
                    case 3:
					try {
						viewBooksWithoutLogin();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}  // Giri� yapmadan kitaplar� g�r�nt�le
                        break;
                    case 4:
                        System.exit(0);
                    default:
                        System.out.println("Ge�ersiz se�enek, l�tfen tekrar deneyin.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Ge�ersiz giri�, l�tfen bir say� girin.");
                scanner.nextLine(); 
            }
        }
    }

    private static void displayMainMenu() {
        while (true) {
            if (User.getCurrentUserRole().equals("admin")) {
                System.out.println("1. Kitap Ekle");
                System.out.println("2. Kitaplar� G�r�nt�le");
                System.out.println("3. Kitap G�ncelle");
                System.out.println("4. Kitap Sil");
                System.out.println("5. Kitap �d�n� Al");
                System.out.println("6. Kitap Geri Ver");
                System.out.println("7. Kullan�c� Kitaplar�n� G�r�nt�le");
                System.out.println("8. T�m Kullan�c�lar�n Kitaplar�n� G�r�nt�le");
                System.out.println("9. ��k�� Yap");
                System.out.print("Bir se�enek se�in: ");
            } else {
                System.out.println("1. Kitap �d�n� Al");
                System.out.println("2. Kitap Geri Ver");
                System.out.println("3. Kitaplar� G�r�nt�le");
                System.out.println("4. Kitap Ara");
                System.out.println("5. Ald���m Kitaplar� G�r�nt�le");
                System.out.println("6. ��k�� Yap");
                System.out.print("Bir se�enek se�in: ");
            }

            try {
                int option = scanner.nextInt();
                scanner.nextLine(); 

                if (User.getCurrentUserRole().equals("admin")) {
                    switch (option) {
                        case 1:
                            addBook();
                            break;
                        case 2:
						try {
							viewBooks();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                            break;
                        case 3:
						try {
							updateBook();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                            break;
                        case 4:
                            deleteBook();
                            break;
                        case 5:
                            borrowBook();
                            break;
                        case 6:
                            returnBook();
                            break;
                        case 7:
                            viewUserBooks();
                            break;
                        case 8:
                            viewAllUserBooks();
                            break;
                        case 9:
                            User.logout();
                            return;
                        default:
                            System.out.println("Ge�ersiz se�enek, l�tfen tekrar deneyin.");
                    }
                } else {
                    switch (option) {
                        case 1:
                            borrowBook();
                            break;
                        case 2:
                            returnBook();
                            break;
                        case 3:
						try {
							viewBooks();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                            break;
                        case 4:
                            searchBooks();
                            break;
                        case 5:
                            viewUserBooks();
                            break;
                        case 6:
                            User.logout();
                            return;
                        default:
                            System.out.println("Ge�ersiz se�enek, l�tfen tekrar deneyin.");
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("Ge�ersiz giri�, l�tfen bir say� girin.");
                scanner.nextLine(); 
            }
        }
    }

    private static void registerUser() {
        System.out.print("Kullan�c� ad� girin: ");
        String username = scanner.nextLine();
        System.out.print("�ifre girin: ");
        String password = scanner.nextLine();
        User.registerUser(username, password);
    }

    private static void loginUser() {
        System.out.print("Kullan�c� ad� girin: ");
        String username = scanner.nextLine();
        System.out.print("�ifre girin: ");
        String password = scanner.nextLine();
        if (User.loginUser(username, password)) {
            System.out.println("Giri� ba�ar�l�!");
        } else {
            System.out.println("Ge�ersiz kullan�c� ad� veya �ifre.");
        }
    }

    private static void addBook() {
        if (!User.getCurrentUserRole().equals("admin")) {
            System.out.println("Yaln�zca adminler kitap ekleyebilir.");
            return;
        }
        System.out.print("Ba�l�k girin: ");
        String title = scanner.nextLine();
        System.out.print("Yazar girin: ");
        String author = scanner.nextLine();
        System.out.print("Yay�n y�l�n� girin: ");
        int year = scanner.nextInt();
        try {
            Book.addBook(title, author, year);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewBooks() throws SQLException {
        try {
            Book.viewBooks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewBooksWithoutLogin() throws SQLException {
        String query = "SELECT title, author, published_year FROM books";
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String encryptedAuthor = resultSet.getString("author");
                String encryptedYear = resultSet.getString("published_year");

                System.out.println("Ba�l�k: " + EncryptionUtil.decrypt(title));
                System.out.println("Yazar: " + encryptedAuthor);  // �ifreli g�ster
                System.out.println("Yay�n Y�l�: " + encryptedYear);  // �ifreli g�ster
                System.out.println("---------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateBook() throws SQLException {
        if (!User.getCurrentUserRole().equals("admin")) {
            System.out.println("Yaln�zca adminler kitap g�ncelleyebilir.");
            return;
        }
        System.out.print("Kitap ID girin: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 
        System.out.print("Yeni ba�l�k girin: ");
        String title = scanner.nextLine();
        System.out.print("Yeni yazar girin: ");
        String author = scanner.nextLine();
        System.out.print("Yeni yay�n y�l�n� girin: ");
        int year = scanner.nextInt();
        Book.updateBook(id, title, author, year);
    }

    private static void deleteBook() {
        if (!User.getCurrentUserRole().equals("admin")) {
            System.out.println("Yaln�zca adminler kitap silebilir.");
            return;
        }
        System.out.print("Kitap ID girin: ");
        int id = scanner.nextInt();
        Book.deleteBook(id);
    }

    private static void borrowBook() {
        System.out.print("Kitap ID girin: ");
        int bookId = scanner.nextInt();
        Transaction.borrowBook(bookId);
    }

    private static void returnBook() {
        System.out.print("Kitap ID girin: ");
        int bookId = scanner.nextInt();
        Transaction.returnBook(bookId);
    }

    private static void viewUserBooks() {
        try {
            Transaction.viewUserBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewAllUserBooks() {
        try {
            Transaction.viewAllUserBooks();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void searchBooks() {
        System.out.print("Anahtar kelime girin: ");
        String keyword = scanner.nextLine();
        try {
            Book.searchBooks(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
