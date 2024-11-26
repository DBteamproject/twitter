package dto;

public class PostLikeDto {
    private Boolean status;
    private int count;

    public PostLikeDto(Boolean status, int count) {
        this.status = status;
        this.count = count;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
