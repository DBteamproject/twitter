package pages;

import javax.swing.*;
import java.awt.*;

public class NotificationPage extends JPanel {
    public NotificationPage(TwitterMainPage mainPage, String userId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

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

        add(profileInfo, BorderLayout.CENTER);
    }
}
