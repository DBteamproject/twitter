package repository;

import config.DatabaseConnection;
import dto.MemberDto;
import dto.NotificationDto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NotificationRepository {

    public void addNotification(String userId, String fromUserId, String message) throws SQLException {
        String query = "INSERT INTO notifications (n_id, user_id, from_user_id, message, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            String nId = UUID.randomUUID().toString().substring(0, 8);
            stmt.setString(1, nId);
            stmt.setString(2, userId); // recipient user id
            stmt.setString(3, fromUserId); // sender user id
            stmt.setString(4, message);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        }
    }

    public List<NotificationDto> getNotifications(String userId) throws SQLException {
        String query = "SELECT n.n_id, n.message, n.created_at, u.user_id, u.user_name, u.introduce, u.profile_image, u.followers_count, u.following_count, u.created_at AS user_created_at " +
                "FROM notifications n " +
                "JOIN user u ON n.from_user_id = u.user_id " + // Join using from_user_id to get sender details
                "WHERE n.user_id = ? AND n.is_read = FALSE " + // Filter by recipient user_id
                "ORDER BY n.created_at DESC";
        List<NotificationDto> notifications = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MemberDto member = new MemberDto(
                            rs.getString("user_id"),
                            rs.getString("user_name"),
                            rs.getString("introduce"),
                            rs.getString("profile_image"),
                            rs.getInt("followers_count"),
                            rs.getInt("following_count"),
                            rs.getObject("user_created_at", LocalDateTime.class)
                    );
                    notifications.add(new NotificationDto(
                            rs.getString("n_id"),
                            rs.getString("message"),
                            member,
                            rs.getObject("created_at", LocalDateTime.class)
                    ));
                }
            }
        }
        return notifications;
    }

    public void markNotificationsAsRead(String nId) throws SQLException {
        String query = "UPDATE notifications SET is_read = TRUE WHERE n_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, nId);
            stmt.executeUpdate();
        }
    }
}
