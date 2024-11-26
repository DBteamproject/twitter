package repository;

import dto.MemberDto;
import dto.MemberLoginDto;
import dto.MemberSignUpDto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Date;


public class MemberRepository {
    /**
     * 유저 아이디를 통해서 유저 정보 가져오기
     */
    public MemberDto getMemberInfo(Connection con, String userId) {
        String query = "SELECT user_id, user_name, introduce, profile_image, followers_count, following_count, created_at FROM user WHERE user_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId); // 첫 번째 '?'에 userId를 바인딩
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MemberDto(
                            rs.getString("user_id"),
                            rs.getString("user_name"),
                            rs.getString("introduce"),
                            rs.getString("profile_image"),
                            rs.getInt("followers_count"),
                            rs.getInt("following_count"),
                            rs.getObject("created_at", LocalDateTime.class)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 쿼리 결과가 없거나 예외 발생 시 null 반환
    }

    public void signUp(MemberSignUpDto dto, Connection con) throws SQLException {
        String query = "INSERT INTO user (user_id, pwd, user_name, introudce, profile_image, followers_count, following_count, created_at) VALUES (?, ?, ?, ?, ?, 0, 0, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, dto.getUserId());
            stmt.setString(2, dto.getPassword());
            stmt.setString(3, dto.getUserName());
            stmt.setString(4, dto.getIntroduce());
            stmt.setString(5, dto.getProfileImage());
            stmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            stmt.executeUpdate();
            System.out.println("User signed up successfully.");
        }
    }

    public String logIn(MemberLoginDto dto, Connection con) throws SQLException {
        String query = "SELECT * FROM user WHERE user_id = ? AND pwd = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, dto.getUserId());
            stmt.setString(2, dto.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return dto.getUserId();
            } else {
                System.out.println("Invalid user ID or password.");
                return null;
            }
        }
    }
}
