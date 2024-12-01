package repository;

import dto.MemberDto;
import dto.PostLikeDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class PostLikeRepository {
    public PostLikeDto updateLike(Connection con, String postUserId, String postId, String userId) throws SQLException {
        // 좋아요 상태 확인
        String checkQuery = "SELECT l_id FROM post_like WHERE post_id = ? AND liker_id = ?";
        boolean isLiked = false;
        try (PreparedStatement stmt = con.prepareStatement(checkQuery)) {
            stmt.setString(1, postId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            isLiked = rs.next();
        }

        if (isLiked) {
            // 좋아요 취소 로직
            String deleteLikeQuery = "DELETE FROM post_like WHERE post_id = ? AND liker_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deleteLikeQuery)) {
                stmt.setString(1, postId);
                stmt.setString(2, userId);
                stmt.executeUpdate();
                System.out.println("Post like removed successfully.");
            }

            // 좋아요 수 감소
            String decreaseLikesQuery = "UPDATE posts SET num_of_likes = num_of_likes - 1 WHERE post_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(decreaseLikesQuery)) {
                stmt.setString(1, postId);
                stmt.executeUpdate();
            }
        } else {
            // 좋아요 추가 로직
            String lId = UUID.randomUUID().toString().substring(0, 8);
            String insertLikeQuery = "INSERT INTO post_like (l_id, post_id, liker_id) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(insertLikeQuery)) {
                stmt.setString(1, lId);
                stmt.setString(2, postId);
                stmt.setString(3, userId);
                stmt.executeUpdate();
                System.out.println("Post liked successfully.");
            }

            // 좋아요 수 증가
            String increaseLikesQuery = "UPDATE posts SET num_of_likes = num_of_likes + 1 WHERE post_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(increaseLikesQuery)) {
                stmt.setString(1, postId);
                stmt.executeUpdate();
            }

            // Notification
            if(!postUserId.equals(userId)){
                MemberDto memberInfo = new MemberRepository().getMemberInfo(con, userId);
                String notificationMessage = memberInfo.getUserName() + " (@" + memberInfo.getUserId() + ") has liked my post.";
                new NotificationRepository().addNotification(postUserId, userId, notificationMessage);
            }
        }

        // 좋아요 수 조회
        String likesQuery = "SELECT num_of_likes FROM posts WHERE post_id = ?";
        int numLikes = 0;
        try (PreparedStatement stmt = con.prepareStatement(likesQuery)) {
            stmt.setString(1, postId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                numLikes = rs.getInt("num_of_likes");
            }
        }

        return new PostLikeDto(!isLiked, numLikes); // 최종 좋아요 상태 반환 (좋아요를 취소했다면 false, 추가했다면 true)
    }
}
