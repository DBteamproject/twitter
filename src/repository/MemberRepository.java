package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class MemberRepository {
    public void signUp(Scanner scanner, Connection con) throws SQLException {
        System.out.println("Enter user ID:");
        String userId = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        String query = "INSERT INTO user (user_id, pwd, followers_count, following_count) VALUES (?, ?, 0, 0)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, password);
            stmt.executeUpdate();
            System.out.println("User signed up successfully.");
        }
    }

    public String logIn(Scanner scanner, Connection con) throws SQLException {
        System.out.println("Enter user ID:");
        String userId = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        String query = "SELECT * FROM user WHERE user_id = ? AND pwd = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return userId;
            } else {
                System.out.println("Invalid user ID or password.");
                return null;
            }
        }
    }


}
