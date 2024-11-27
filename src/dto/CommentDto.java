package dto;

import java.time.LocalDateTime;

public class CommentDto {
    private String commentId;
    private String content;
    private int numLikes;
    private Boolean userLiked;
    private MemberDto member;
    private LocalDateTime createdAt;

    public CommentDto(String commentId, String content, int numLikes, Boolean userLiked, MemberDto member, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.content = content;
        this.numLikes = numLikes;
        this.userLiked = userLiked;
        this.member = member;
        this.createdAt = createdAt;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
