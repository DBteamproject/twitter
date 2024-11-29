package repository;

import dto.MemberDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FollowRepository {

    // 팔로우 추가
    public void addFollow(Connection con, String followerId, String followingId) throws SQLException {
        String followQuery = "INSERT INTO twitter_following (follower_id, following_id, created_at) VALUES (?, ?, NOW())";
        try (PreparedStatement pstmt = con.prepareStatement(followQuery)) {
            pstmt.setString(1, followerId);
            pstmt.setString(2, followingId);
            pstmt.executeUpdate();
        }
    }

    // 팔로우 삭제 (언팔로우)
    public void removeFollow(Connection con, String followerId, String followingId) throws SQLException {
        String unfollowQuery = "DELETE FROM twitter_following WHERE follower_id = ? AND following_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(unfollowQuery)) {
            pstmt.setString(1, followerId);
            pstmt.setString(2, followingId);
            pstmt.executeUpdate();
        }
    }

    // 팔로우 상태 확인
    public boolean isFollowing(Connection con, String followerId, String followingId) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM twitter_following WHERE follower_id = ? AND following_id = ?";
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

    public List<MemberDto> getFollowers(Connection con, String userId) {
        List<MemberDto> followers = new ArrayList<>();
        String sql = "SELECT m.user_id, m.user_name, m.profile_image, m.introduce " +
                "FROM follow f " +
                "JOIN member m ON f.follower_id = m.user_id " +
                "WHERE f.following_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MemberDto member = new MemberDto();
                    member.setUserId(rs.getString("user_id"));
                    member.setUserName(rs.getString("user_name"));
                    member.setProfileImage(rs.getString("profile_image"));
                    member.setIntroduce(rs.getString("introduce"));
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
        String sql = "SELECT m.user_id, m.user_name, m.profile_image, m.introduce " +
                "FROM follow f " +
                "JOIN member m ON f.following_id = m.user_id " +
                "WHERE f.follower_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MemberDto member = new MemberDto();
                    member.setUserId(rs.getString("user_id"));
                    member.setUserName(rs.getString("user_name"));
                    member.setProfileImage(rs.getString("profile_image"));
                    member.setIntroduce(rs.getString("introduce"));
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

}
