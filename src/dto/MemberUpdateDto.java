package dto;

public class MemberUpdateDto {
    private String password;
    private String userName;
    private String introduce;

    public MemberUpdateDto(String password, String userName, String introduce) {
        this.password = password;
        this.userName = userName;
        this.introduce = introduce;
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
}
