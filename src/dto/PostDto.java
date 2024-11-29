package dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {
    private String postId;
    private String content; // 콘텐츠 내용
    private int numLikes; // 좋아요 개수
    private int numViews;
    private int numComments;
    private Boolean userLiked; // 로그인한 유저가 좋아요를 눌렀는지 확인
    private MemberDto member; // 회원 정보
    private List<PostPhotoDto> photos; // 이미지 목록
    private LocalDateTime createdAt; // 생성일

    public PostDto(String postId, String content, int numLikes, int numViews, int numComments, Boolean userLiked, MemberDto member, List<PostPhotoDto> photos, LocalDateTime createdAt) {
        this.postId = postId;
        this.content = content;
        this.numLikes = numLikes;
        this.numViews = numViews;
        this.numComments = numComments;
        this.userLiked = userLiked;
        this.member = member;
        this.photos = photos;
        this.createdAt = createdAt;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(int numLikes) {
        this.numLikes = numLikes;
    }

    public int getNumViews() {
        return numViews;
    }

    public void setNumViews(int numViews) {
        this.numViews = numViews;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public Boolean getUserLiked() {
        return userLiked;
    }

    public void setUserLiked(Boolean userLiked) {
        this.userLiked = userLiked;
    }

    public MemberDto getMember() {
        return member;
    }

    public void setMember(MemberDto member) {
        this.member = member;
    }

    public List<PostPhotoDto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PostPhotoDto> photos) {
        this.photos = photos;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
