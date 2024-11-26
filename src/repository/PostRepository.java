package repository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PostRepository {
    public void writePost(String content, List<File> files, Connection con, String userId) throws SQLException {
        String postId = UUID.randomUUID().toString().substring(0, 8);

        String postQuery = "INSERT INTO posts (post_id, content, writter_id, num_of_likes, created_at) VALUES (?, ?, ?, 0, ?)";
        try (PreparedStatement stmt = con.prepareStatement(postQuery)) {
            stmt.setString(1, postId);
            stmt.setString(2, content);
            stmt.setString(3, userId);
            stmt.setTimestamp(4, new Timestamp(new Date().getTime()));
            stmt.executeUpdate();
            System.out.println("Post created successfully with ID: " + postId);
        }

        // 사진 추가
        for (File image : files) {
            addPhoto(con, postId, image);
        }

        // 해시태그 입력 및 추가
        Pattern pattern = Pattern.compile("#(\\S+)");
        Matcher matcher = pattern.matcher(content);

        List<String> tags = new ArrayList<>();
        while (matcher.find()) {
            tags.add(matcher.group(1).trim());
        }

        for (String tag : tags) {
            int hashtagId = getOrCreateHashtagId(con, tag);
            addPostHashtag(con, postId, hashtagId);
        }
    }


    // 포스트에 사진 추가
    private static void addPhoto(Connection con, String postId, File image) {
        String photoId = UUID.randomUUID().toString().substring(0, 8);

        String fileName = image.getName();
        String extension = fileName.substring(fileName.lastIndexOf('.'));

        // 최종 파일 경로 생성
        try {
            String directoryPath = "src/resources/post_photos";
            Path targetPath = Paths.get(directoryPath, photoId + extension); // 확장자를 포함한 새 파일 이름
            Files.copy(image.toPath(), targetPath); // 파일 복사

            // 데이터베이스에 저장할 파일의 경로
            String path = targetPath.toString();
            System.out.println(path);

            String postQuery = "INSERT INTO post_photos (photo_id, path, post_id) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = con.prepareStatement(postQuery)) {
                stmt.setString(1, photoId);
                stmt.setString(2, path);
                stmt.setString(3, postId);
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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


    public void deletePost(Connection con, String postId, String userId) throws SQLException {
        // 트랜잭션 시작
        con.setAutoCommit(false); // 자동 커밋 비활성화

        try {
            // 포스트의 작성자 확인
            String authorQuery = "SELECT writter_id FROM posts WHERE post_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(authorQuery)) {
                stmt.setString(1, postId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String writerId = rs.getString("writter_id");
                    if (!userId.equals(writerId)) {
                        throw new SQLException("Unauthorized attempt to delete post by different user.");
                    }
                } else {
                    throw new SQLException("Post not found.");
                }
            }

            // comment_like 테이블에서 관련 데이터 삭제
            String deleteCommentLikesQuery = "DELETE FROM comment_like WHERE comment_id IN (SELECT comment_id FROM comment WHERE post_id = ?)";
            try (PreparedStatement stmt = con.prepareStatement(deleteCommentLikesQuery)) {
                stmt.setString(1, postId);
                stmt.executeUpdate();
            }

            // comments 테이블에서 관련 데이터 삭제
            String deleteCommentsQuery = "DELETE FROM comment WHERE post_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deleteCommentsQuery)) {
                stmt.setString(1, postId);
                stmt.executeUpdate();
            }

            // post_like, post_photos, post_hashtags 테이블에서 관련 데이터 삭제
            String[] tables = new String[] {"post_like", "post_photos", "post_hashtags"};
            for (String table : tables) {
                String deleteQuery = "DELETE FROM " + table + " WHERE post_id = ?";
                try (PreparedStatement stmt = con.prepareStatement(deleteQuery)) {
                    stmt.setString(1, postId);
                    stmt.executeUpdate();
                }
            }

            // 마지막으로 post 테이블에서 해당 포스트 삭제
            String deletePostQuery = "DELETE FROM posts WHERE post_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deletePostQuery)) {
                stmt.setString(1, postId);
                stmt.executeUpdate();
            }

            // 모든 변경 사항 커밋
            con.commit();
            System.out.println("Post and all related data deleted successfully.");
        } catch (SQLException e) {
            // 오류 발생 시 롤백
            con.rollback();
            throw e;
        } finally {
            // 자동 커밋 다시 활성화
            con.setAutoCommit(true);
        }
    }

}
