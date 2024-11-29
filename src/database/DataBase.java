package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.UUID;
import java.util.*;

public class DataBase {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection con = null;
        String loggedInUserId = null;

        try {
            // MySQL JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 데이터베이스 연결
            String url = "jdbc:mysql://localhost:3306/twitter";
            String user = "root";
            String password = "0110";
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful: " + con);

            while (true) {
                if (loggedInUserId == null) {
                    System.out.println("Input 0 to sign up, 1 to log in");
                    int operation = scanner.nextInt();
                    scanner.nextLine(); // 개행 제거

                    if (operation == 0) {
                        signUp(scanner, con);
                    } else if (operation == 1) {
                        loggedInUserId = logIn(scanner, con);
                        if (loggedInUserId != null) {
                            System.out.println("Logged in!");
                        }
                    } else {
                        System.out.println("Invalid option. Please try again.");
                    }
                } else {
                    System.out.println("0 to write post, 1 to write comment, 2 to like post, 3 to like comment, 4 to see my followers, 5 to see my following, 6 to view top hashtags, 7 to follow someone, 8 to search posts, 9 to view posts by user");
                    int option = scanner.nextInt();
                    scanner.nextLine(); // 개행 제거

                    switch (option) {
                        case 0:
                            writePost(scanner, con, loggedInUserId);
                            break;
                        case 1:
                            writeComment(scanner, con, loggedInUserId);
                            break;
                        case 2:
                            likePost(scanner, con, loggedInUserId);
                            break;
                        case 3:
                            likeComment(scanner, con, loggedInUserId);
                            break;
                        case 4:
                            viewFollowers(con, loggedInUserId);
                            break;
                        case 5:
                            viewFollowing(con, loggedInUserId);
                            break;
                        case 6:
                            showPopularHashtags(con, scanner);
                            break;
                        case 7:
                            followUser(scanner, con, loggedInUserId);
                            break;
                        case 8:
                            searchPosts(scanner, con); // 검색 기능 호출
                            break;
                        case 9:
                            viewPostsByUser(scanner, con);
                            break;
                        default:
                            System.out.println("Invalid option. Please try again.");
                            break;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection error.");
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
                scanner.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void signUp(Scanner scanner, Connection con) throws SQLException {
        System.out.println("Enter user ID:");
        String userId = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        String query = "INSERT INTO user (user_id, pwd, followers_count, following_count) VALUES (?, ?, 0, 0)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, password);
            stmt.executeUpdate();
            System.out.println("User signed up successfully.");
        }
    }

    private static String logIn(Scanner scanner, Connection con) throws SQLException {
        System.out.println("Enter user ID:");
        String userId = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        String query = "SELECT * FROM user WHERE user_id = ? AND pwd = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return userId;
            } else {
                System.out.println("Invalid user ID or password.");
                return null;
            }
        }
    }

    private static void searchPosts(Scanner scanner, Connection con) throws SQLException {
        System.out.println("Enter keyword to search for in posts:");
        String keyword = scanner.nextLine();

        String query = "SELECT post_id, content, writter_id FROM posts WHERE content LIKE ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            System.out.println("Search results for keyword \"" + keyword + "\":");
            while (rs.next()) {
                String postId = rs.getString("post_id");
                String content = rs.getString("content");
                String writterId = rs.getString("writter_id");
                System.out.println("Post ID: " + postId + ", Content: " + content + ", Written by: " + writterId);
            }
        }
    }

    private static void writePost(Scanner scanner, Connection con, String userId) throws SQLException {
        System.out.println("Enter post content:");
        String content = scanner.nextLine();
        String postId = UUID.randomUUID().toString().substring(0, 8);

        String postQuery = "INSERT INTO posts (post_id, content, writter_id, num_of_likes) VALUES (?, ?, ?, 0)";
        try (PreparedStatement stmt = con.prepareStatement(postQuery)) {
            stmt.setString(1, postId);
            stmt.setString(2, content);
            stmt.setString(3, userId);
            stmt.executeUpdate();
            System.out.println("Post created successfully with ID: " + postId);
        }

        // 해시태그 입력 및 추가
        System.out.println("Enter hashtags for the post (separated by commas):");
        String[] hashtags = scanner.nextLine().split(",");
        for (String tag : hashtags) {
            tag = tag.trim();
            if (!tag.isEmpty()) {
                int hashtagId = getOrCreateHashtagId(con, tag);
                addPostHashtag(con, postId, hashtagId);
            }
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

    private static void viewPostsByUser(Scanner scanner, Connection con) throws SQLException {
        System.out.println("Enter the user ID to view their posts:");
        String userId = scanner.nextLine();

        String query = "SELECT post_id, content, writter_id FROM posts WHERE writter_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Posts written by user \"" + userId + "\":");
            while (rs.next()) {
                String postId = rs.getString("post_id");
                String content = rs.getString("content");
                String writterId = rs.getString("writter_id");
                System.out.println("Post ID: " + postId + ", Content: " + content + ", Written by: " + writterId);
            }
        }
    }

    private static void showPopularHashtags(Connection con, Scanner scanner) throws SQLException {
        String query = "SELECT h.tag_name, COUNT(ph.hashtag_id) AS usage_count " +
                       "FROM hashtags h " +
                       "JOIN post_hashtags ph ON h.hashtag_id = ph.hashtag_id " +
                       "GROUP BY h.tag_name " +
                       "ORDER BY usage_count DESC " +
                       "LIMIT 5";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("Popular hashtags:");
            int index = 1;
            Map<Integer, String> hashtagOptions = new HashMap<>();
            while (rs.next()) {
                String tagName = rs.getString("tag_name");
                int usageCount = rs.getInt("usage_count");
                System.out.println(index + ". " + tagName + " (" + usageCount + " uses)");
                hashtagOptions.put(index, tagName);
                index++;
            }
            
            System.out.println("Select a hashtag number to view related posts:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // 개행 제거
            if (hashtagOptions.containsKey(choice)) {
                String selectedTag = hashtagOptions.get(choice);
                showPostsByHashtag(con, selectedTag);
            } else {
                System.out.println("Invalid selection.");
            }
        }
    }

    private static void showPostsByHashtag(Connection con, String hashtag) throws SQLException {
        String query = "SELECT p.post_id, p.content, p.writter_id " +
                       "FROM posts p " +
                       "JOIN post_hashtags ph ON p.post_id = ph.post_id " +
                       "JOIN hashtags h ON ph.hashtag_id = h.hashtag_id " +
                       "WHERE h.tag_name = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, hashtag);
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("Posts with hashtag #" + hashtag + ":");
            while (rs.next()) {
                String postId = rs.getString("post_id");
                String content = rs.getString("content");
                String writterId = rs.getString("writter_id");
                System.out.println("Post ID: " + postId + ", Content: " + content + ", Written by: " + writterId);
            }
        }
    }
    


    private static void writeComment(Scanner scanner, Connection con, String userId) throws SQLException {
        System.out.println("Enter post ID to comment on:");
        String postId = scanner.nextLine();
        System.out.println("Enter comment content:");
        String content = scanner.nextLine();

        // 간단한 comment_id 생성 (UUID의 앞 8자리만 사용)
        String commentId = UUID.randomUUID().toString().substring(0, 8);

        String query = "INSERT INTO comment (comment_id, content, writter_id, post_id, num_of_likes) VALUES (?, ?, ?, ?, 0)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, commentId); // 간단한 comment_id 설정
            stmt.setString(2, content);   // 댓글 내용
            stmt.setString(3, userId);    // 작성자 ID
            stmt.setString(4, postId);    // 대상 게시물 ID
            stmt.executeUpdate();
            System.out.println("Comment added successfully with ID: " + commentId);
        }
    }


    private static void likePost(Scanner scanner, Connection con, String userId) throws SQLException {
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

    private static void likeComment(Scanner scanner, Connection con, String userId) throws SQLException {
        System.out.println("Enter comment ID to like:");
        String commentId = scanner.nextLine();

        String likeQuery = "INSERT INTO comment_like (l_id, comment_id, liker_id) VALUES (UUID(), ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(likeQuery)) {
            stmt.setString(1, commentId);
            stmt.setString(2, userId);
            stmt.executeUpdate();
            System.out.println("Comment liked successfully.");
        }

        String updateLikesQuery = "UPDATE comment SET num_of_likes = num_of_likes + 1 WHERE comment_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(updateLikesQuery)) {
            stmt.setString(1, commentId);
            stmt.executeUpdate();
        }
    }

    private static void viewFollowers(Connection con, String userId) throws SQLException {
        String query = "SELECT follower_id FROM follower WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Followers:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("follower_id"));
            }
        }
    }

    private static void viewFollowing(Connection con, String userId) throws SQLException {
        String query = "SELECT user_id FROM follower WHERE follower_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Following:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("user_id"));
            }
        }
    }

    private static void followUser(Scanner scanner, Connection con, String userId) throws SQLException {
        System.out.println("Enter the user ID to follow:");
        String followId = scanner.nextLine();

        // 팔로우 정보 추가
        String query = "INSERT INTO follower (f_id, user_id, follower_id) VALUES (UUID(), ?, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, followId);   // 팔로우 된 사람의 ID
            stmt.setString(2, userId);     // 팔로우 한 사람의 ID
            stmt.executeUpdate();
            System.out.println("Now following " + followId);
        }

        // 팔로우 한 사람의 following_count 증가
        String updateFollowingCount = "UPDATE user SET following_count = following_count + 1 WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(updateFollowingCount)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }

        // 팔로우 된 사람의 followers_count 증가
        String updateFollowersCount = "UPDATE user SET followers_count = followers_count + 1 WHERE user_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(updateFollowersCount)) {
            stmt.setString(1, followId);
            stmt.executeUpdate();
        }
    }
}
