package dto;

public class PostPhotoDto {
    private String photoId;
    private String path;

    public PostPhotoDto(String photoId, String path) {
        this.photoId = photoId;
        this.path = path;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
