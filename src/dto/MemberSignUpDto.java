package dto;

public class MemberSignUpDto {
    private String userId;
    private String password;
    private String userName;
    private String introduce;
    private String profileImage;

    public MemberSignUpDto(String userId, String password, String userName, String introduce, String profileImage) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.introduce = introduce;
        this.profileImage = profileImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
