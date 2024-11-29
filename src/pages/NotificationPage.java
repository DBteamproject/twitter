package pages;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class NotificationPage extends JPanel {
    private ArrayList<JLabel> notifications;
    
    public NotificationPage(TwitterMainPage mainPage, String userId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        notifications = new ArrayList<>();

        // 사용자 프로필 정보
        JLabel profileInfo = new JLabel(
                "<html><h1>Notification</h1>"
                        + "<p><b>User ID:</b> " + userId + "</p>"
                        + "<p><b>Username:</b> Example User</p>"
                        + "<p><b>Handle:</b> @exampleuser</p>"
                        + "</html>",
                JLabel.CENTER
        );
        profileInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // 프로필 정보 패널을 중앙에 추가
        add(profileInfo, BorderLayout.CENTER);

        // 알림 패널을 아래에 추가(알림은 동적으로 추가될 예정)
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(notificationPanel);
        add(scrollPane, BorderLayout.CENTER);
        
    }

    // 알림을 추가하는 메서드
    public void addNotification(String notificationText){
        JLabel notification = new JLabel(notificationText);
        notification.setFont(new Font("Arial", Font.PLAIN, 14));
        notification.setForeground(Color.BLACK);
        notification.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 새로운 알림을 표시할 패널에 추가
        JPanel notificationPanel = (JPanel) ((JScrollPane) getComponent(1)).getViewport().getView();
        notificationPanel.add(notification);
        notificationPanel.revalidate();
        notificationPanel.repaint();
    }
}
