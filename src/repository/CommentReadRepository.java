package repository;

import dto.CommentDto;
import dto.MemberDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentReadRepository {

    public List<CommentDto> getCommentsWithPost(Connection con, String postId, int scrollNum, String userId) {
        List<CommentDto> comments = new ArrayList<>();
        String commentQuery = "SELECT c.comment_id, c.content, c.num_of_likes, c.created_at, " +
                "u.user_id, u.user_name, u.introduce, u.profile_image, u.followers_count, u.following_count, u.created_at as user_created_at, " +
                "cl.liker_id IS NOT NULL as userLiked " +
                "FROM comment c " +
                "LEFT JOIN user u ON c.writter_id = u.user_id " +
                "LEFT JOIN comment_like cl ON c.comment_id = cl.comment_id AND cl.liker_id = ? " +
                "WHERE c.post_id = ? " +
                "ORDER BY c.created_at DESC " +  // 결과를 최신 순으로 정렬
                "LIMIT 10 OFFSET ?";  // 페이지당 10개씩 보여주고, scrollNum에 따른 offset을 계산

        try (PreparedStatement stmt = con.prepareStatement(commentQuery)) {
            stmt.setString(1, userId);  // 현재 로그인한 사용자 ID를 '좋아요' 확인용으로 사용
            stmt.setString(2, postId);  // 특정 게시물에 대한 댓글 가져오기
            stmt.setInt(3, (scrollNum - 1) * 10);  // 페이지 번호에 따른 offset 계산
            ResultSet rs = stmt.executeQuery();

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

                CommentDto comment = new CommentDto(
                        rs.getString("comment_id"),
                        rs.getString("content"),
                        rs.getInt("num_of_likes"),
                        rs.getBoolean("userLiked"),
                        member,
                        rs.getObject("created_at", LocalDateTime.class)
                );

                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }
}
