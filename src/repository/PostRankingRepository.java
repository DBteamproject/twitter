package repository;

import dto.MemberDto;
import dto.PostDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class PostRankingRepository {
    public List<PostDto> getAllPostsOrderByViews(Connection con, int scrollNum, String userId) {
        List<PostDto> posts = new ArrayList<>();
        String postQuery = "SELECT p.post_id, p.content, p.num_of_likes, p.num_of_views, p.num_of_comments, p.created_at, " +
                "u.user_id, u.user_name, u.introduce, u.profile_image, u.followers_count, u.following_count, u.created_at as user_created_at, " +
                "pl.liker_id IS NOT NULL as userLiked " +
                "FROM posts p " +
                "LEFT JOIN user u ON p.writter_id = u.user_id " +
                "LEFT JOIN post_like pl ON p.post_id = pl.post_id AND pl.liker_id = ? " +
                "ORDER BY p.num_of_views DESC, p.created_at DESC " +  // 결과를 최신 순으로 정렬
                "LIMIT 10 OFFSET ?";  // 페이지당 10개씩 보여주고, scrollNum에 따른 offset을 계산

        try (PreparedStatement stmt = con.prepareStatement(postQuery)) {
            stmt.setString(1, userId);  // 현재 로그인한 사용자 ID를 '좋아요' 확인용으로 사용
            stmt.setInt(2, (scrollNum - 1) * 10);  // 페이지 번호에 따른 offset 계산
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

                posts.add(new PostDto(
                        rs.getString("post_id"),
                        rs.getString("content"),
                        rs.getInt("num_of_likes"),
                        rs.getInt("num_of_views"),
                        rs.getInt("num_of_comments"),
                        rs.getBoolean("userLiked"),
                        member,
                        new PostPhotoRepository().getPostPhotos(con, rs.getString("post_id")),
                        rs.getObject("created_at", LocalDateTime.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
}
