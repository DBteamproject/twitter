package repository;

import dto.MemberDto;
import dto.MemberLoginDto;
import dto.MemberSignUpDto;
import dto.MemberUpdateDto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MemberRepository {
    /**
     * 유저 아이디를 통해서 유저 정보 가져오기
     */
    public MemberDto getMemberInfo(Connection con, String userId) {
        String query = "SELECT user_id, user_name, introduce, profile_image, followers_count, following_count, created_at FROM user WHERE user_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, userId); // 첫 번째 '?'에 userId를 바인딩
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MemberDto(
                            rs.getString("user_id"),
                            rs.getString("user_name"),
                            rs.getString("introduce"),
                            rs.getString("profile_image"),
                            rs.getInt("followers_count"),
                            rs.getInt("following_count"),
                            rs.getObject("created_at", LocalDateTime.class)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 쿼리 결과가 없거나 예외 발생 시 null 반환
    }

    public void signUp(MemberSignUpDto dto, Connection con) throws SQLException {
        String query = "INSERT INTO user (user_id, pwd, user_name, introduce, profile_image, followers_count, following_count, created_at) VALUES (?, ?, ?, ?, ?, 0, 0, ?)";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, dto.getUserId());
            stmt.setString(2, dto.getPassword());
            stmt.setString(3, dto.getUserName());
            stmt.setString(4, dto.getIntroduce());
            stmt.setString(5, dto.getProfileImage());
            stmt.setTimestamp(6, new Timestamp(new Date().getTime()));
            stmt.executeUpdate();
            System.out.println("User signed up successfully.");
        }
    }

    public String logIn(MemberLoginDto dto, Connection con) throws SQLException {
        String query = "SELECT * FROM user WHERE user_id = ? AND pwd = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, dto.getUserId());
            stmt.setString(2, dto.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return dto.getUserId();
            } else {
                System.out.println("Invalid user ID or password.");
                return null;
            }
        }
    }


    public void updateProfileImageUser(String profileImage, String userId, Connection con) throws SQLException {
        // 업데이트 쿼리 생성
        String query = "UPDATE user SET profile_image = ? WHERE user_id = ?";

        try (PreparedStatement stmt = con.prepareStatement(query)) {
            // 매개변수 설정
            stmt.setString(1, profileImage);
            stmt.setString(2, userId);

            // 쿼리 실행
            int rowsUpdated = stmt.executeUpdate();

            // 업데이트 결과 처리
            if (rowsUpdated > 0) {
                System.out.println("Profile image updated successfully for user ID: " + userId);
            } else {
                System.out.println("No user found with the provided user ID: " + userId);
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while updating profile image: " + e.getMessage());
            throw e; // 예외를 다시 던져 호출한 측에서 추가적인 처리를 할 수 있도록 합니다.
        }
    }


    public void updateUser(MemberUpdateDto dto, String userId, Connection con) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder("UPDATE user SET ");
        boolean needsUpdate = false;

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            queryBuilder.append("pwd = ?, ");
            needsUpdate = true;
        }

        if (dto.getUserName() != null && dto.getUserName().length() >= 2) {
            queryBuilder.append("user_name = ?, ");
            needsUpdate = true;
        }

        if (dto.getIntroduce() != null && dto.getIntroduce().length() >= 2) {
            queryBuilder.append("introduce = ?, ");
            needsUpdate = true;
        }

        // 업데이트 할 내용이 없다면 함수 종료
        if (!needsUpdate) {
            System.out.println("No valid fields to update.");
            return;
        }

        // 마지막 쉼표 제거 및 WHERE 절 추가
        queryBuilder.setLength(queryBuilder.length() - 2);
        queryBuilder.append(" WHERE user_id = ?");

        try (PreparedStatement stmt = con.prepareStatement(queryBuilder.toString())) {
            int parameterIndex = 1;

            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                stmt.setString(parameterIndex++, dto.getPassword());
            }

            if (dto.getUserName() != null && dto.getUserName().length() >= 2) {
                stmt.setString(parameterIndex++, dto.getUserName());
            }

            if (dto.getIntroduce() != null && dto.getIntroduce().length() >= 2) {
                stmt.setString(parameterIndex++, dto.getIntroduce());
            }

            stmt.setString(parameterIndex, userId); // WHERE 절의 user_id

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("No user found with the provided user_id.");
            }
        }
    }


    public void deleteMember(Connection con, String userId) throws SQLException {
        // 트랜잭션 시작
        con.setAutoCommit(false); // 자동 커밋 비활성화

        try {
            // 사용자가 작성한 포스트 ID 목록을 가져오기
            String postIdQuery = "SELECT post_id FROM posts WHERE writter_id = ?";
            List<String> postIds = new ArrayList<>();

            try (PreparedStatement stmt = con.prepareStatement(postIdQuery)) {
                stmt.setString(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    postIds.add(rs.getString("post_id"));
                }
            }

            // 사용자가 작성한 각 포스트에 대해 관련 데이터 삭제
            for (String postId : postIds) {
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
                String[] tables = new String[]{"post_like", "post_photos", "post_hashtags"};
                for (String table : tables) {
                    String deleteQuery = "DELETE FROM " + table + " WHERE post_id = ?";
                    try (PreparedStatement stmt = con.prepareStatement(deleteQuery)) {
                        stmt.setString(1, postId);
                        stmt.executeUpdate();
                    }
                }

                // 마지막으로 posts 테이블에서 해당 포스트 삭제
                String deletePostQuery = "DELETE FROM posts WHERE post_id = ?";
                try (PreparedStatement stmt = con.prepareStatement(deletePostQuery)) {
                    stmt.setString(1, postId);
                    stmt.executeUpdate();
                }
            }

            // 사용자가 작성한 댓글 좋아요 삭제
            String deleteCommentLikesQuery = "DELETE FROM comment_like WHERE user_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deleteCommentLikesQuery)) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
            }

            // 사용자가 작성한 포스트 좋아요 삭제
            String deletePostLikesQuery = "DELETE FROM post_like WHERE user_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deletePostLikesQuery)) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
            }

            // follower 테이블에서 해당 사용자를 팔로우한 관계 삭제
            String deleteFollowerQuery = "DELETE FROM follower WHERE user_id = ? OR follower_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deleteFollowerQuery)) {
                stmt.setString(1, userId);
                stmt.setString(2, userId);
                stmt.executeUpdate();
            }

            // following 테이블에서 해당 사용자가 팔로우한 관계 삭제
            String deleteFollowingQuery = "DELETE FROM following WHERE user_id = ? OR follwer_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deleteFollowingQuery)) {
                stmt.setString(1, userId);
                stmt.setString(2, userId);
                stmt.executeUpdate();
            }

            // 마지막으로 사용자 테이블에서 사용자 삭제
            String deleteUserQuery = "DELETE FROM user WHERE user_id = ?";
            try (PreparedStatement stmt = con.prepareStatement(deleteUserQuery)) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
            }

            // 모든 변경 사항 커밋
            con.commit();
            System.out.println("Member and all related data deleted successfully.");
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
