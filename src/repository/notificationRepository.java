package repository;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    // 알림 추가
    public void addNotification(String userId, String message) throws SQLException {
        String query = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, message);
            stmt.executeUpdate();
        }
    }

    // 사용자 알림 조회
    public List<String> getNotifications(String userId) throws SQLException {
        String query = "SELECT message FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<String> notifications = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(rs.getString("message"));
                }
            }
        }
        return notifications;
    }

    // 알림 읽음 처리
    public void markNotificationsAsRead(String userId) throws SQLException {
        String query = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }
}
