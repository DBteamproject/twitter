package repository;

import dto.FollowingDto;
import dto.MemberDto;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FollowRepository {
    public FollowingDto updateFollow(Connection con, String userId, String followingId) throws SQLException {

        // 팔로우 상태 확인
        String checkQuery = "SELECT f_id FROM following WHERE user_id = ? AND follower_id = ?";
        boolean isFollowing = false;
        try (PreparedStatement stmt = con.prepareStatement(checkQuery)) {
            stmt.setString(1, userId);
            stmt.setString(2, followingId);
            ResultSet rs = stmt.executeQuery();
            isFollowing = rs.next();
        }

        if (isFollowing) {
            // 팔로우 삭제 (언팔로우)
            String unfollowingQuery = "DELETE FROM following WHERE follower_id = ? AND user_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(unfollowingQuery)) {
                pstmt.setString(1, followingId);
                pstmt.setString(2, userId);
                pstmt.executeUpdate();
                System.out.println("Unfollowing successfully.");
            }
            String unfollowedQuery = "DELETE FROM follower WHERE follower_id = ? AND user_id = ?";
            try (PreparedStatement pstmt = con.prepareStatement(unfollowedQuery)) {
                pstmt.setString(1, userId);
                pstmt.setString(2, followingId);
                pstmt.executeUpdate();
                System.out.println("Unfollowed successfully.");
            }
            String decreaseFollowingQuery = "UPDATE user SET following_count = following_count - 1 WHERE user_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(decreaseFollowingQuery)) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
            }
            String decreaseFollowedQuery = "UPDATE user SET followers_count = followers_count - 1 WHERE user_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(decreaseFollowedQuery)) {
                stmt.setString(1, followingId);
                stmt.executeUpdate();
            }
        } else {
            // 팔로우 추가
            String fId = UUID.randomUUID().toString().substring(0, 8);

            String followQuery = "INSERT INTO following (f_id, user_id, follower_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(followQuery)) {
                pstmt.setString(1, fId);
                pstmt.setString(2, userId);
                pstmt.setString(3, followingId);
                pstmt.executeUpdate();
                System.out.println("Following successfully.");
            }
            String followedQuery = "INSERT INTO follower (f_id, user_id, follower_id) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = con.prepareStatement(followedQuery)) {
                pstmt.setString(1, fId);
                pstmt.setString(2, followingId);
                pstmt.setString(3, userId);
                pstmt.executeUpdate();
                System.out.println("Followed successfully.");
            }

            // 팔로잉 수 증가
            String increaseFollowingQuery = "UPDATE user SET following_count = following_count + 1 WHERE user_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(increaseFollowingQuery)) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
            }
            String increaseFollowedQuery = "UPDATE user SET followers_count = followers_count + 1 WHERE user_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(increaseFollowedQuery)) {
                stmt.setString(1, followingId);
                stmt.executeUpdate();
            }
            // 팔로잉 수 조회

        }
        String followingCountQuery = "SELECT following_count FROM user WHERE user_id = ?";
        int numfollowing = 0;
        try (PreparedStatement stmt = con.prepareStatement(followingCountQuery)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                numfollowing = rs.getInt("following_count");
            }
        }

        return new FollowingDto(!isFollowing, numfollowing);
    }


    public List<MemberDto> getFollowerList(Connection con,String userId) {
        List<MemberDto> followerList = new ArrayList<>();
        String sql = "SELECT u.user_id, u.user_name " +
                "FROM follower f " +
                "JOIN user u ON f.follower_id = u.user_id " +
                "WHERE f.user_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, userId); // userId를 쿼리에 바인딩
            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    MemberDto member = new MemberDto();
                    member.setUserId(rs.getString("user_id"));
                    member.setUserName(rs.getString("user_name")); // user_name 설정
                    followerList.add(member);
                }
            }
        } catch (SQLException e) {
            // 예외 처리: 로그 출력 및 사용자 정의 예외로 감싸서 던지기
            e.printStackTrace();
            throw new RuntimeException("팔로워 목록을 가져오는 중 오류 발생", e);
        }
        return followerList;}

    public List<MemberDto> getFollowingList(Connection con, String userId) {
        List<MemberDto> followingList = new ArrayList<>();
        String sql = "SELECT u.user_id, u.user_name " +
                "FROM follower f " +
                "JOIN user u ON f.user_id = u.user_id " +
                "WHERE f.follower_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MemberDto member = new MemberDto();
                    member.setUserId(rs.getString("user_id"));
                    member.setUserName(rs.getString("user_name"));
                    followingList.add(member);
                }
            }
        } catch (SQLException e) {
            // 예외 처리: 로그 출력 및 사용자 정의 예외로 감싸기
            e.printStackTrace();
            throw new RuntimeException("팔로잉 목록을 가져오는 중 오류 발생", e);
        }
        return followingList;
    }

}
