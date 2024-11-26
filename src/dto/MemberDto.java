package dto;

import java.time.LocalDateTime;

public class MemberDto {
    private String userId;
    private String userName;
    private String introduce;
    private String profileImage;
    private int followersCount;
    private int followingCount;
    private LocalDateTime createdAt;

    // 기본 생성자
    public MemberDto() {
    }

    // 모든 매개변수를 받는 생성자
    public MemberDto(String userId, String userName, String introduce, String profileImage, int followersCount, int followingCount, LocalDateTime createdAt) {
        this.userId = userId;
        this.userName = userName;
        this.introduce = introduce;
        this.profileImage = profileImage;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.createdAt = createdAt;
    }

    // Getter와 Setter 메서드
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
