package repository;

import dto.MemberDto;

import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class CommentRepository {
    public void writeComment(Connection con, String postUserId, String postId, String content, String userId) throws SQLException {
        String commentId = UUID.randomUUID().toString().substring(0, 8);

        String insertCommentQuery = "INSERT INTO comment (comment_id, content, writter_id, post_id, num_of_likes, created_at) VALUES (?, ?, ?, ?, 0, ?)";
        String increaseCommentsQuery = "UPDATE posts SET num_of_comments = num_of_comments + 1 WHERE post_id = ?";

        try {
            // 트랜잭션 시작
            con.setAutoCommit(false);

            // 1. Insert the new comment
            try (PreparedStatement stmt = con.prepareStatement(insertCommentQuery)) {
                stmt.setString(1, commentId);
                stmt.setString(2, content);
                stmt.setString(3, userId);
                stmt.setString(4, postId);
                stmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                stmt.executeUpdate();
                System.out.println("Comment created successfully with ID: " + commentId);
            }

            // 2. Increase the comment count in the associated post
            try (PreparedStatement increaseCommentsStmt = con.prepareStatement(increaseCommentsQuery)) {
                increaseCommentsStmt.setString(1, postId);
                int rowsUpdated = increaseCommentsStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Post comment count increased for post ID: " + postId);
                } else {
                    System.out.println("Failed to update comment count for post ID: " + postId);
                    con.rollback(); // 댓글 수 증가 실패 시 롤백
                    return;
                }
            }

            // 커밋 트랜잭션
            con.commit();

            // Notification
            if(!userId.equals(postUserId)){
                MemberDto memberInfo = new MemberRepository().getMemberInfo(con, userId);
                String truncatedContent = content.length() > 6 ? content.substring(0, 6) + "..." : content;
                String notificationMessage = memberInfo.getUserName() + " (@" + memberInfo.getUserId() + ") has add new comment.\n(" + truncatedContent + ")";
                new NotificationRepository().addNotification(postUserId, userId, notificationMessage);
            }
        } catch (SQLException e) {
            // 롤백 트랜잭션
            con.rollback();
            System.err.println("An error occurred while writing the comment: " + e.getMessage());
            throw e; // 예외 다시 던지기
        } finally {
            // 트랜잭션 모드를 기본값으로 복원
            con.setAutoCommit(true);
        }
    }

    public void updateComment(Connection con, String commentId, String content, String userId) throws SQLException {
        String checkOwnershipQuery = "SELECT writter_id FROM comment WHERE comment_id = ?";
        String updateCommentQuery = "UPDATE comment SET content = ? WHERE comment_id = ? AND writter_id = ?";

        try {
            // 트랜잭션 시작
            con.setAutoCommit(false);

            // 1. Check if the user is the owner of the comment
            try (PreparedStatement checkOwnershipStmt = con.prepareStatement(checkOwnershipQuery)) {
                checkOwnershipStmt.setString(1, commentId);
                ResultSet rs = checkOwnershipStmt.executeQuery();

                if (rs.next()) {
                    String writerId = rs.getString("writter_id");
                    if (!writerId.equals(userId)) {
                        System.out.println("User does not have permission to update this comment.");
                        con.rollback(); // 권한이 없으면 롤백
                        return;
                    }
                } else {
                    System.out.println("No comment found with ID: " + commentId);
                    con.rollback(); // 댓글이 없으면 롤백
                    return;
                }
            }

            // 2. Update the comment itself
            try (PreparedStatement updateCommentStmt = con.prepareStatement(updateCommentQuery)) {
                updateCommentStmt.setString(1, content);
                updateCommentStmt.setString(2, commentId);
                updateCommentStmt.setString(3, userId);

                int commentUpdated = updateCommentStmt.executeUpdate();
                if (commentUpdated > 0) {
                    System.out.println("Comment updated successfully with ID: " + commentId);
                } else {
                    System.out.println("Failed to update the comment with ID: " + commentId);
                }
            }

            // 커밋 트랜잭션
            con.commit();
        } catch (SQLException e) {
            // 롤백 트랜잭션
            con.rollback();
            System.err.println("An error occurred while updating the comment: " + e.getMessage());
            throw e; // 예외 다시 던지기
        } finally {
            // 트랜잭션 모드를 기본값으로 복원
            con.setAutoCommit(true);
        }
    }


    public void deleteComment(Connection con, String commentId, String userId) throws SQLException {
        String checkOwnershipQuery = "SELECT writter_id, post_id FROM comment WHERE comment_id = ?";
        String deleteLikesQuery = "DELETE FROM comment_like WHERE comment_id = ?";
        String deleteCommentQuery = "DELETE FROM comment WHERE comment_id = ?";
        String decreaseCommentsQuery = "UPDATE posts SET num_of_comments = num_of_comments - 1 WHERE post_id = ?";

        String postId = null; // post_id 값을 저장할 변수

        try {
            // 트랜잭션 시작
            con.setAutoCommit(false);

            // 1. Check if the user is the owner of the comment and get post_id
            try (PreparedStatement checkOwnershipStmt = con.prepareStatement(checkOwnershipQuery)) {
                checkOwnershipStmt.setString(1, commentId);
                ResultSet rs = checkOwnershipStmt.executeQuery();

                if (rs.next()) {
                    String writerId = rs.getString("writter_id");
                    if (!writerId.equals(userId)) {
                        System.out.println("User does not have permission to delete this comment.");
                        con.rollback(); // 권한이 없으면 롤백
                        return;
                    }
                    postId = rs.getString("post_id"); // post_id 가져오기
                } else {
                    System.out.println("No comment found with ID: " + commentId);
                    con.rollback(); // 댓글이 없으면 롤백
                    return;
                }
            }

            // 2. Delete all likes associated with the comment
            try (PreparedStatement deleteLikesStmt = con.prepareStatement(deleteLikesQuery)) {
                deleteLikesStmt.setString(1, commentId);
                int likesDeleted = deleteLikesStmt.executeUpdate();
                System.out.println("Deleted " + likesDeleted + " like(s) associated with the comment.");
            }

            // 3. Delete the comment itself
            try (PreparedStatement deleteCommentStmt = con.prepareStatement(deleteCommentQuery)) {
                deleteCommentStmt.setString(1, commentId);
                int commentDeleted = deleteCommentStmt.executeUpdate();
                if (commentDeleted > 0) {
                    System.out.println("Comment deleted successfully with ID: " + commentId);
                } else {
                    System.out.println("Failed to delete the comment with ID: " + commentId);
                    con.rollback(); // 삭제 실패 시 롤백
                    return;
                }
            }

            // 4. Decrease the comment count in the associated post
            try (PreparedStatement decreaseCommentsStmt = con.prepareStatement(decreaseCommentsQuery)) {
                decreaseCommentsStmt.setString(1, postId); // post_id를 사용하여 댓글 수 감소
                int rowsUpdated = decreaseCommentsStmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Post comment count decreased for post ID: " + postId);
                } else {
                    System.out.println("Failed to update comment count for post ID: " + postId);
                }
            }

            // 커밋 트랜잭션
            con.commit();
        } catch (SQLException e) {
            // 롤백 트랜잭션
            con.rollback();
            System.err.println("An error occurred while deleting the comment: " + e.getMessage());
            throw e; // 예외 다시 던지기
        } finally {
            // 트랜잭션 모드를 기본값으로 복원
            con.setAutoCommit(true);
        }
    }

}
