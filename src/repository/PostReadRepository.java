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


public class PostReadRepository {
    /**
     * 모든 포스트 목록 가져오기
     */
    public List<PostDto> getAllPosts(Connection con, int scrollNum, String userId) {
        List<PostDto> posts = new ArrayList<>();
        String postQuery = "SELECT p.post_id, p.content, p.num_of_likes, p.num_of_views, p.num_of_comments, p.created_at, " +
                "u.user_id, u.user_name, u.introduce, u.profile_image, u.followers_count, u.following_count, u.created_at as user_created_at, " +
                "pl.liker_id IS NOT NULL as userLiked " +
                "FROM posts p " +
                "LEFT JOIN user u ON p.writter_id = u.user_id " +
                "LEFT JOIN post_like pl ON p.post_id = pl.post_id AND pl.liker_id = ? " +
                "ORDER BY p.created_at DESC " +  // 결과를 최신 순으로 정렬
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


    /**
     * 특정 유저의 포스트 목록 가져오기
     */
    public List<PostDto> getUserPosts(Connection con, int scrollNum, String searchUserId, String userId) {
        List<PostDto> posts = new ArrayList<>();
        // LIMIT와 OFFSET을 이용하여 페이지네이션 구현
        String postQuery = "SELECT p.post_id, p.content, p.num_of_likes, p.num_of_views, p.num_of_comments, p.created_at, " +
                "u.user_id, u.user_name, u.introduce, u.profile_image, u.followers_count, u.following_count, u.created_at as user_created_at, " +
                "pl.liker_id IS NOT NULL as userLiked " +
                "FROM posts p " +
                "LEFT JOIN user u ON p.writter_id = u.user_id " +
                "LEFT JOIN post_like pl ON p.post_id = pl.post_id AND pl.liker_id = ? " +
                "WHERE p.writter_id = ? " +
                "ORDER BY p.created_at DESC " + // 결과를 최신 순으로 정렬
                "LIMIT 10 OFFSET ?"; // 10개씩, scrollNum에 따른 offset 계산

        try (PreparedStatement stmt = con.prepareStatement(postQuery)) {
            stmt.setString(1, userId); // 현재 로그인한 사용자 ID를 '좋아요' 확인용으로 사용
            stmt.setString(2, searchUserId); // 검색할 사용자 ID
            stmt.setInt(3, (scrollNum - 1) * 10); // 페이지 번호에 따른 offset 계산
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // 사용자 정보 생성
                MemberDto member = new MemberDto(
                        rs.getString("user_id"),
                        rs.getString("user_name"),
                        rs.getString("introduce"),
                        rs.getString("profile_image"),
                        rs.getInt("followers_count"),
                        rs.getInt("following_count"),
                        rs.getObject("user_created_at", LocalDateTime.class)
                );

                // 포스트 정보 및 '좋아요' 상태 추가
                posts.add(new PostDto(
                        rs.getString("post_id"),
                        rs.getString("content"),
                        rs.getInt("num_of_likes"),
                        rs.getInt("num_of_views"),
                        rs.getInt("num_of_comments"),
                        rs.getBoolean("userLiked"), // '좋아요' 상태를 포스트 DTO에 추가
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


    /**
     * 특정 포스트 1개만 가져오기
     */
    public PostDto getSinglePost(Connection con, String userId, String postId) {
        String postQuery = "SELECT p.post_id, p.content, p.num_of_likes, p.num_of_views, p.num_of_comments, p.created_at, " +
                "u.user_id, u.user_name, u.introduce, u.profile_image, u.followers_count, u.following_count, u.created_at as user_created_at, " +
                "pl.liker_id IS NOT NULL as userLiked " +
                "FROM posts p " +
                "LEFT JOIN user u ON p.writter_id = u.user_id " +
                "LEFT JOIN post_like pl ON p.post_id = pl.post_id AND pl.liker_id = ? " +
                "WHERE p.post_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(postQuery)) {
            stmt.setString(1, userId); // 현재 로그인한 사용자 ID를 '좋아요' 확인용으로 사용
            stmt.setString(2, postId); // 조회할 특정 포스트 ID
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                MemberDto member = new MemberDto(
                        rs.getString("user_id"),
                        rs.getString("user_name"),
                        rs.getString("introduce"),
                        rs.getString("profile_image"),
                        rs.getInt("followers_count"),
                        rs.getInt("following_count"),
                        rs.getObject("user_created_at", LocalDateTime.class)
                );

                return new PostDto(
                        rs.getString("post_id"),
                        rs.getString("content"),
                        rs.getInt("num_of_likes"),
                        rs.getInt("num_of_views"),
                        rs.getInt("num_of_comments"),
                        rs.getBoolean("userLiked"),
                        member,
                        new PostPhotoRepository().getPostPhotos(con, rs.getString("post_id")),
                        rs.getObject("created_at", LocalDateTime.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 포스트가 없을 경우 null 반환
    }
}
