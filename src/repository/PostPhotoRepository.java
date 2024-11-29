package repository;

import dto.PostPhotoDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostPhotoRepository {
    // 포스트에 연결된 모든 사진을 조회하는 메서드
    public List<PostPhotoDto> getPostPhotos(Connection con, String postId) {
        List<PostPhotoDto> photos = new ArrayList<>();
        String photoQuery = "SELECT photo_id, path FROM post_photos WHERE post_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(photoQuery)) {
            stmt.setString(1, postId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PostPhotoDto photo = new PostPhotoDto(
                        rs.getString("photo_id"),
                        rs.getString("path")
                );
                photos.add(photo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return photos;
    }
}
