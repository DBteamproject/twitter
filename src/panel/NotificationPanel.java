package panel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationPanel extends JPanel {
    private List<String> notifications;
    private JPanel notificationListPanel;

    public NotificationPanel() {
        setLayout(new BorderLayout());
        notifications = new ArrayList<>();

        notificationListPanel = new JPanel();
        notificationListPanel.setLayout(new BoxLayout(notificationListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(notificationListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void addNotification(String notification) {
        notifications.add(notification);
        JLabel notificationLabel = new JLabel(notification);
        notificationListPanel.add(notificationLabel);
        notificationListPanel.revalidate();
        notificationListPanel.repaint();
    }
}
