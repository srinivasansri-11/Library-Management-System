import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {
    static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    static final String USER = "root";  
    static final String PASS = "";      

    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to the database.");

            int choice;
            do {
                System.out.println("\n=== Library Management System ===");
                System.out.println("1. Add Book");
                System.out.println("2. View All Books");
                System.out.println("3. Issue Book");
                System.out.println("4. Return Book");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> viewBooks();
                    case 3 -> issueBook();
                    case 4 -> returnBook();
                    case 5 -> System.out.println("👋 Exiting...");
                    default -> System.out.println("❌ Invalid choice!");
                }
            } while (choice != 5);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new book
    private static void addBook() {
        try {
            System.out.print("Enter book title: ");
            String title = sc.nextLine();
            System.out.print("Enter author name: ");
            String author = sc.nextLine();

            String sql = "INSERT INTO books (title, author, available) VALUES (?, ?, TRUE)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.executeUpdate();
            System.out.println(" Book added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all books
    private static void viewBooks() {
        try {
            String sql = "SELECT * FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n--- Book List ---");
            while (rs.next()) {
                System.out.printf("ID: %d | Title: %s | Author: %s | Available: %s%n",
                        rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                        rs.getBoolean("available") ? "Yes" : "No");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Issue a book
    private static void issueBook() {
        try {
            System.out.print("Enter Book ID to issue: ");
            int id = sc.nextInt();

            String checkSql = "SELECT available FROM books WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean available = rs.getBoolean("available");
                if (available) {
                    String sql = "UPDATE books SET available = FALSE WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    System.out.println(" Book issued successfully!");
                } else {
                    System.out.println(" Book already issued.");
                }
            } else {
                System.out.println(" Book not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Return a book
    private static void returnBook() {
        try {
            System.out.print("Enter Book ID to return: ");
            int id = sc.nextInt();

            String checkSql = "SELECT available FROM books WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean available = rs.getBoolean("available");
                if (!available) {
                    String sql = "UPDATE books SET available = TRUE WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    System.out.println("Book returned successfully!");
                } else {
                    System.out.println("Book was not issued.");
                }
            } else {
                System.out.println("Book not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
