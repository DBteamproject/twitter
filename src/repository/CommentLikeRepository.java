package repository;

import dto.CommentLikeDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class CommentLikeRepository {
    public CommentLikeDto updateLike(Connection con, String commentId, String userId) throws SQLException {
        // 좋아요 상태 확인
        String checkQuery = "SELECT l_id FROM comment_like WHERE comment_id = ? AND liker_id = ?";
        boolean isLiked = false;
        try (PreparedStatement stmt = con.prepareStatement(checkQuery)) {
            stmt.setString(1, commentId);
            stmt.setString(2, userId);
            ResultSet rs = stmt.executeQuery();
            isLiked = rs.next();
        }

        if (isLiked) {
            // 좋아요 취소 로직
            String deleteLikeQuery = "DELETE FROM comment_like WHERE comment_id = ? AND liker_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deleteLikeQuery)) {
                stmt.setString(1, commentId);
                stmt.setString(2, userId);
                stmt.executeUpdate();
                System.out.println("Comment like removed successfully.");
            }

            // 좋아요 수 감소
            String decreaseLikesQuery = "UPDATE comment SET num_of_likes = num_of_likes - 1 WHERE comment_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(decreaseLikesQuery)) {
                stmt.setString(1, commentId);
                stmt.executeUpdate();
            }
        } else {
            // 좋아요 추가 로직
            String lId = UUID.randomUUID().toString().substring(0, 8);
            String insertLikeQuery = "INSERT INTO comment_like (l_id, comment_id, liker_id) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(insertLikeQuery)) {
                stmt.setString(1, lId);
                stmt.setString(2, commentId);
                stmt.setString(3, userId);
                stmt.executeUpdate();
                System.out.println("Comment liked successfully.");
            }

            // 좋아요 수 증가
            String increaseLikesQuery = "UPDATE comment SET num_of_likes = num_of_likes + 1 WHERE comment_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(increaseLikesQuery)) {
                stmt.setString(1, commentId);
                stmt.executeUpdate();
            }
        }

        // 좋아요 수 조회
        String likesQuery = "SELECT num_of_likes FROM comment WHERE comment_id = ?";
        int numLikes = 0;
        try (PreparedStatement stmt = con.prepareStatement(likesQuery)) {
            stmt.setString(1, commentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                numLikes = rs.getInt("num_of_likes");
            }
        }

        return new CommentLikeDto(!isLiked, numLikes); // 최종 좋아요 상태 반환 (좋아요를 취소했다면 false, 추가했다면 true)
    }
}
