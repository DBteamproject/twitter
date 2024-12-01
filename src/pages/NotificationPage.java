package pages;

import dto.NotificationDto;
import repository.NotificationRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationPage extends JPanel {
    private final NotificationRepository notificationRepository;
    private final String userId;

    private final JPanel notificationPanel;

    public NotificationPage(TwitterMainPage mainPage, String userId) {
        this.userId = userId;
        this.notificationRepository = new NotificationRepository();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 프로필 정보
        JLabel profileInfo = new JLabel(
                "<html><h1>Notifications</h1>"
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
        loadNotifications(mainPage);
    }

    // 알림 데이터를 로드하고 표시
    private void loadNotifications(TwitterMainPage mainPage) {
        notificationPanel.removeAll(); // Clear existing notifications
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for notifications

        try {
            List<NotificationDto> notifications = notificationRepository.getNotifications(userId);
            if (notifications.isEmpty()) {
                JLabel noNotificationLabel = new JLabel("There are no new notifications.", SwingConstants.CENTER);
                noNotificationLabel.setFont(new Font("Arial", Font.BOLD, 16));
                noNotificationLabel.setForeground(Color.RED);
                noNotificationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                notificationPanel.add(noNotificationLabel);
            } else {
                for (NotificationDto dto : notifications) {
                    JPanel singleNotificationPanel = new JPanel();
                    singleNotificationPanel.setLayout(new BoxLayout(singleNotificationPanel, BoxLayout.X_AXIS));
                    singleNotificationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    singleNotificationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // Set max height

                    // Profile image
                    JLabel profileImageLabel = createProfileLabel(mainPage, dto.getMember().getUserId(), dto.getMember().getProfileImage());
                    singleNotificationPanel.add(profileImageLabel);

                    // Message and date
                    JLabel notificationLabel = createNotificationLabel(dto);
                    singleNotificationPanel.add(notificationLabel);

                    // Close button
                    JButton closeButton = createCloseButton(dto.getnId(), singleNotificationPanel);
                    singleNotificationPanel.add(closeButton);

                    notificationPanel.add(singleNotificationPanel);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "There was an error loading notifications: " + ex.getMessage());
            ex.printStackTrace();
        }

        notificationPanel.revalidate();
        notificationPanel.repaint();
    }

    private JLabel createProfileLabel(TwitterMainPage mainPage, String fromUserId, String imageUrl) {
        JLabel profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(40, 40));
        ImageIcon icon;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            icon = new ImageIcon(imageUrl);
        } else {
            icon = new ImageIcon("src/resources/profile.png");
        }
        Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        profileImageLabel.setIcon(new ImageIcon(scaledImage));

        profileImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(fromUserId);
                mainPage.showPage(new ProfilePage(mainPage, fromUserId, userId));
            }
        });
        return profileImageLabel;
    }

    private JLabel createNotificationLabel(NotificationDto dto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
        String formattedDate = dto.getCreatedAt().format(formatter);
        String messageWithBreaks = dto.getMessage().replace("\n", "<br>"); // Replace \n with HTML line breaks
        JLabel notificationLabel = new JLabel("<html>" + messageWithBreaks + "<br><small style='color: gray'>" + formattedDate + "</small></html>");
        notificationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        return notificationLabel;
    }

    private JButton createCloseButton(String nId, JPanel panel) {
        JButton closeButton = new JButton("X");
        closeButton.addActionListener(e -> {
            try {
                notificationRepository.markNotificationsAsRead(nId);
                panel.setVisible(false); // Hide panel after marking as read
                notificationPanel.revalidate(); // Refresh the panel
                notificationPanel.repaint();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error marking notification as read: " + ex.getMessage());
            }
        });
        return closeButton;
    }
}
