package pages;

import repository.NotificationRepository;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class NotificationPage extends JPanel {
    private final NotificationRepository notificationRepository;
    private final String userId;

    private final JPanel notificationPanel;

    public NotificationPage(String userId) {
        this.userId = userId;
        this.notificationRepository = new NotificationRepository();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 프로필 정보
        JLabel profileInfo = new JLabel(
                "<html><h1>알림</h1>"
                        + "<p><b>사용자 ID:</b> " + userId + "</p>"
                        + "</html>",
                JLabel.CENTER
        );
        profileInfo.setHorizontalAlignment(SwingConstants.CENTER);
        add(profileInfo, BorderLayout.NORTH);

        // 알림 목록 패널
        notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(notificationPanel);
        add(scrollPane, BorderLayout.CENTER);

        // 알림 데이터 로드
        loadNotifications();
    }

    // 알림 데이터를 로드하고 표시
    private void loadNotifications() {
        notificationPanel.removeAll(); // 기존 알림 초기화

        try {
            List<String> notifications = notificationRepository.getNotifications(userId);
            if (notifications.isEmpty()) {
                JLabel noNotificationLabel = new JLabel("새로운 알림이 없습니다.");
                noNotificationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                noNotificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
                notificationPanel.add(noNotificationLabel);
            } else {
                for (String message : notifications) {
                    JLabel notificationLabel = new JLabel(message);
                    notificationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    notificationLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    notificationLabel.setOpaque(true);
                    notificationLabel.setBackground(Color.LIGHT_GRAY);
                    notificationPanel.add(notificationLabel);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "알림을 불러오는 중 오류가 발생했습니다: " + ex.getMessage());
            ex.printStackTrace();
        }

        notificationPanel.revalidate();
        notificationPanel.repaint();
    }
}
