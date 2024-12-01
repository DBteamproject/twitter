package dto;

import java.time.LocalDateTime;

public class NotificationDto {
    private String nId;
    private String message;
    private MemberDto member;
    private LocalDateTime createdAt;

    public NotificationDto(String nId, String message, MemberDto member, LocalDateTime createdAt) {
        this.nId = nId;
        this.message = message;
        this.member = member;
        this.createdAt = createdAt;
    }

    public String getnId() {
        return nId;
    }

    public void setnId(String nId) {
        this.nId = nId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
