package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;

public class PostRepository {

    public void writePost(String inputext, Connection con, String userId) throws SQLException {
        System.out.println("Enter post content:");
        String content = inputext;
        String postId = UUID.randomUUID().toString().substring(0, 8);

        String postQuery = "INSERT INTO posts (post_id, content, writter_id, num_of_likes) VALUES (?, ?, ?, 0)";
        try (PreparedStatement stmt = con.prepareStatement(postQuery)) {
            stmt.setString(1, postId);
            stmt.setString(2, content);
            stmt.setString(3, userId);
            stmt.executeUpdate();
            System.out.println("Post created successfully with ID: " + postId);

        }

//        // 해시태그 입력 및 추가
//        System.out.println("Enter hashtags for the post (separated by commas):");
//        String[] hashtags = scanner.nextLine().split(",");
//        for (String tag : hashtags) {
//            tag = tag.trim();
//            if (!tag.isEmpty()) {
//                int hashtagId = getOrCreateHashtagId(con, tag);
//                addPostHashtag(con, postId, hashtagId);
//            }
//        }
    }

    public void likePost(Scanner scanner, Connection con, String userId) throws SQLException {
        System.out.println("Enter post ID to like:");
        String postId = scanner.nextLine();

        String likeQuery = "INSERT INTO post_like (l_id, post_id, liker_id) VALUES (UUID(), ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(likeQuery)) {
            stmt.setString(1, postId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            System.out.println("Post liked successfully.");
        }

        String updateLikesQuery = "UPDATE posts SET num_of_likes = num_of_likes + 1 WHERE post_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(updateLikesQuery)) {
            stmt.setString(1, postId);
            stmt.executeUpdate();
        }
    }

    private static int getOrCreateHashtagId(Connection con, String tag) throws SQLException {
        String selectQuery = "SELECT hashtag_id FROM hashtags WHERE tag_name = ?";
        try (PreparedStatement stmt = con.prepareStatement(selectQuery)) {
            stmt.setString(1, tag);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("hashtag_id");
            }
        }

        String insertQuery = "INSERT INTO hashtags (tag_name) VALUES (?)";
        try (PreparedStatement stmt = con.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tag);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to retrieve or create hashtag ID.");
    }

    private static void addPostHashtag(Connection con, String postId, int hashtagId) throws SQLException {
        String query = "INSERT INTO post_hashtags (post_id, hashtag_id) VALUES (?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, postId);
            stmt.setInt(2, hashtagId);
            stmt.executeUpdate();
        }
    }





}
