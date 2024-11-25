NotificationPanel notificationPanel = new NotificationPanel();

// 예시: 댓글 추가 시
commentSection.addComment("New comment by @user");
notificationPanel.addNotification("You have a new comment on your post.");

// 예시: 좋아요 클릭 시
likeButton.addActionListener(e -> {
    notificationPanel.addNotification("Your post got a new like!");
});
