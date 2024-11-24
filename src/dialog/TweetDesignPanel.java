package dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class TweetDesignPanel {
    public JPanel base(String username, String handle, String tweetText, int comments, int likes, int views, String profileImagePath, String[] imagePaths, String date) {
        JPanel tweetPanel = new JPanel();
        tweetPanel.setLayout(new BorderLayout());
        tweetPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        tweetPanel.setBackground(Color.WHITE);

        // 왼쪽: 프로필 이미지
        JLabel profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(50, 50));
        if (profileImagePath != null && !profileImagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(profileImagePath);
            Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            profileImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            profileImageLabel.setText("🖼️"); // 기본 이미지 아이콘
        }

        // 사용자 정보 (이름, 핸들)
        JLabel userInfo = new JLabel("<html><b>" + username + "</b> <span style='color:gray;'>" + handle + "</span></html>");
        userInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 본문 (해시태그 포함)
        String styledText = tweetText.replaceAll("#(\\w+)", "<span style='color:blue;'>#$1</span>");
        JLabel tweetContent = new JLabel("<html>" + styledText + "</html>");
        tweetContent.setForeground(Color.DARK_GRAY);
        tweetContent.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 날짜
        JLabel tweetDateLabel = new JLabel(date);
        tweetDateLabel.setForeground(Color.GRAY);
        tweetDateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        tweetDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 트윗 내용 패널
        JPanel tweetContentPanel = new JPanel();
        tweetContentPanel.setLayout(new BoxLayout(tweetContentPanel, BoxLayout.Y_AXIS));
        tweetContentPanel.setBackground(Color.WHITE);
        tweetContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        tweetContentPanel.add(userInfo); // 사용자 정보
        tweetContentPanel.add(Box.createVerticalStrut(5)); // 간격 추가
        tweetContentPanel.add(tweetContent); // 본문 추가

        // 이미지 패널 (가로 스크롤 지원)
        JPanel imagesPanel = new JPanel();
        imagesPanel.setLayout(new BoxLayout(imagesPanel, BoxLayout.X_AXIS));
        imagesPanel.setBackground(Color.WHITE);

        if (imagePaths != null) {
            for (String imagePath : imagePaths) {
                if (imagePath != null && !imagePath.isEmpty()) {
                    JLabel imageLabel = createImageLabel(imagePath);
                    imagesPanel.add(imageLabel);
                    imagesPanel.add(Box.createHorizontalStrut(5)); // 이미지 간격 추가
                }
            }
        }

        JScrollPane imageScrollPane = new JScrollPane(imagesPanel);
        imageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        imageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        imageScrollPane.setPreferredSize(new Dimension(300, 120));
        imageScrollPane.setBorder(null);

        tweetContentPanel.add(imageScrollPane); // 본문 아래 이미지 추가
        tweetContentPanel.add(Box.createVerticalStrut(5)); // 간격 추가
        tweetContentPanel.add(tweetDateLabel); // 이미지 아래에 날짜 추가

        // 반응 아이콘 (댓글, 좋아요, 조회수)
        JPanel reactionPanel = new JPanel();
        reactionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        reactionPanel.setBackground(Color.WHITE);

        reactionPanel.add(createReactionLabel("💬", comments)); // 댓글 수
        reactionPanel.add(createReactionLabel("❤️", likes)); // 좋아요 수
        reactionPanel.add(createReactionLabel("👁️", views)); // 조회수

        // 삭제 버튼 추가
        JButton deleteButton = new JButton("삭제");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteButton.setForeground(Color.RED);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> {
            Container parent = tweetPanel.getParent();
            if (parent != null) {
                parent.remove(tweetPanel);
                parent.revalidate();
                parent.repaint();
                System.out.println("Tweet deleted!");
            } else {
                System.err.println("Parent container is null. Unable to delete tweet.");
            }
        });
        reactionPanel.add(deleteButton); // 삭제 버튼을 조회수 옆에 추가

        // 프로필 이미지와 내용을 결합
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(profileImageLabel, BorderLayout.NORTH);

        tweetPanel.add(leftPanel, BorderLayout.WEST); // 프로필 이미지
        tweetPanel.add(tweetContentPanel, BorderLayout.CENTER); // 트윗 내용
        tweetPanel.add(reactionPanel, BorderLayout.SOUTH); // 반응 아이콘

        return tweetPanel;
    }

    private JLabel createImageLabel(String imagePath) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(100, 100));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            label.setText("🖼️");
            System.err.println("Failed to load image: " + imagePath);
        }

        return label;
    }

    private JLabel createReactionLabel(String icon, int count) {
        JLabel label = new JLabel(icon + " " + count);
        label.setForeground(Color.GRAY);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        return label;
    }
}
