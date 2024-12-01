package repository;

import dto.MemberDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FollowRepository {

    // 팔로우 추가
    public void addFollow(Connection con, String followerId, String followingId) throws SQLException {
        String followFollowingQuery = "INSERT INTO following (f_id, user_id, follwer_id) VALUES (?, ?, ?)";
        try (PreparedStatement followStmt = con.prepareStatement(followFollowingQuery)) {
            followStmt.setString(1, java.util.UUID.randomUUID().toString());
            followStmt.setString(2, followerId);
            followStmt.setString(3, followingId);
            followStmt.executeUpdate();
        }

        String followFollowerQuery = "INSERT INTO follower (f_id, user_id, follower_id) VALUES (?, ?, ?)";
        try (PreparedStatement followStmt = con.prepareStatement(followFollowerQuery)) {
            followStmt.setString(1, java.util.UUID.randomUUID().toString());
            followStmt.setString(2, followingId);
            followStmt.setString(3, followerId);
            followStmt.executeUpdate();
        }

        // Increment followers_count for followId and following_count for userId
        updateCounts(con, followingId, followerId, 1);

        // Notification
        MemberDto memberInfo = new MemberRepository().getMemberInfo(con, followerId);
        String notificationMessage = memberInfo.getUserName() + " (@" + memberInfo.getUserId() + ") has followed me.";
        new NotificationRepository().addNotification(followingId, followerId, notificationMessage);
    }

    // 팔로우 삭제 (언팔로우)
    public void removeFollow(Connection con, String followerId, String followingId) throws SQLException {
        String unfollowFollowingQuery = "DELETE FROM following WHERE user_id = ? AND follwer_id = ?";
        try (PreparedStatement unfollowStmt = con.prepareStatement(unfollowFollowingQuery)) {
            unfollowStmt.setString(1, followerId);
            unfollowStmt.setString(2, followingId);
            unfollowStmt.executeUpdate();
        }

        String unfollowFollowerQuery = "DELETE FROM follower WHERE user_id = ? AND follower_id = ?";
        try (PreparedStatement unfollowStmt = con.prepareStatement(unfollowFollowerQuery)) {
            unfollowStmt.setString(1, followingId);
            unfollowStmt.setString(2, followerId);
            unfollowStmt.executeUpdate();
        }

        // Decrement followers_count for followId and following_count for userId
        updateCounts(con, followingId, followerId, -1);
    }

    // 팔로우 상태 확인
    public boolean isFollowing(Connection con, String followerId, String followingId) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM following WHERE user_id = ? AND follwer_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(checkQuery)) {
            pstmt.setString(1, followerId);
            pstmt.setString(2, followingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private void updateCounts(Connection con, String followId, String userId, int value) throws SQLException {
        // Update followers count for the target user being followed/unfollowed
        String updateFollowersCount = "UPDATE user SET followers_count = followers_count + ? WHERE user_id = ?";
        try (PreparedStatement followersStmt = con.prepareStatement(updateFollowersCount)) {
            followersStmt.setInt(1, value);
            followersStmt.setString(2, followId);
            followersStmt.executeUpdate();
        }

        // Update following count for the current user
        String updateFollowingCount = "UPDATE user SET following_count = following_count + ? WHERE user_id = ?";
        try (PreparedStatement followingStmt = con.prepareStatement(updateFollowingCount)) {
            followingStmt.setInt(1, value);
            followingStmt.setString(2, userId);
            followingStmt.executeUpdate();
        }
    }

    public List<MemberDto> getFollowers(Connection con, String userId) {
        List<MemberDto> followers = new ArrayList<>();
        String sql = "SELECT u.user_id, u.user_name, u.introduce, u.profile_image, " +
                "u.followers_count, u.following_count, u.created_at " +
                "FROM follower f " +
                "JOIN user u ON f.follower_id = u.user_id " +
                "WHERE f.user_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MemberDto member = new MemberDto(
                            rs.getString("user_id"),
                            rs.getString("user_name"),
                            rs.getString("introduce"),
                            rs.getString("profile_image"),
                            rs.getInt("followers_count"),
                            rs.getInt("following_count"),
                            rs.getObject("created_at", LocalDateTime.class)
                    );
                    followers.add(member);
                }
            }
        } catch (SQLException e) {
            // 예외 처리: 로그 출력 및 사용자 정의 예외로 감싸서 던지기
            e.printStackTrace();
            throw new RuntimeException("팔로워 목록을 가져오는 중 오류 발생", e);
        }
        return followers;
    }


    public List<MemberDto> getFollowings(Connection con, String userId) {
        List<MemberDto> followings = new ArrayList<>();
        String sql = "SELECT u.user_id, u.user_name, u.introduce, u.profile_image, " +
                "u.followers_count, u.following_count, u.created_at " +
                "FROM following f " +
                "JOIN user u ON f.follwer_id = u.user_id " +
                "WHERE f.user_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MemberDto member = new MemberDto(
                            rs.getString("user_id"),
                            rs.getString("user_name"),
                            rs.getString("introduce"),
                            rs.getString("profile_image"),
                            rs.getInt("followers_count"),
                            rs.getInt("following_count"),
                            rs.getObject("created_at", LocalDateTime.class)
                    );
                    followings.add(member);
                }
            }
        } catch (SQLException e) {
            // 예외 처리: 로그 출력 및 사용자 정의 예외로 감싸기
            e.printStackTrace();
            throw new RuntimeException("팔로잉 목록을 가져오는 중 오류 발생", e);
        }
        return followings;
    }


    public List<String> getFollowerIds(Connection con, String userId) {
        List<String> followerIds = new ArrayList<>();
        String sql = "SELECT follower_id FROM follower WHERE user_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    followerIds.add(rs.getString("follower_id"));
                }
            }
        } catch (SQLException e) {
            // Log and rethrow or handle the exception appropriately
            e.printStackTrace();
            throw new RuntimeException("Error retrieving follower IDs", e);
        }
        return followerIds;
    }

}
