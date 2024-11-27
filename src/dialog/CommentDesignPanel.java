package dialog;

import dto.CommentDto;
import pages.TwitterMainPage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;


public class CommentDesignPanel {
    public JPanel base(TwitterMainPage mainPage, CommentDto comment, String userId) {
        JPanel commentPanel = new JPanel();
        commentPanel.setLayout(new BorderLayout());
        commentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        commentPanel.setBackground(Color.WHITE);
        commentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, commentPanel.getPreferredSize().height)); // 최대 높이 제한

        // 프로필 이미지
        JLabel profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(40, 40));
        ImageIcon icon;
        if (comment.getMember().getProfileImage() != null && !comment.getMember().getProfileImage().isEmpty()) {
            icon = new ImageIcon(comment.getMember().getProfileImage());
        } else {
            icon = new ImageIcon("src/resources/profile.png");
        }
        Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        profileImageLabel.setIcon(new ImageIcon(scaledImage));

        // 사용자 정보 패널 (닉네임, 아이디, 생성일)
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(Color.WHITE);

        JLabel userNameLabel = new JLabel("<html><b>" + comment.getMember().getUserName() + "</b> <span style='color:gray;'>@" + comment.getMember().getUserId() + "</span></html>");
        JLabel createdDateLabel = new JLabel(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd HH:mm")));
        createdDateLabel.setForeground(Color.GRAY);
        createdDateLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        userInfoPanel.add(userNameLabel);
        userInfoPanel.add(createdDateLabel);

        JLabel commentContent = new JLabel("<html><p style='width: 240px;'>" + comment.getContent() + "</p></html>");
        commentContent.setForeground(Color.DARK_GRAY);
        commentContent.setBorder(new EmptyBorder(5, 0, 0, 0));

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton likeButton = new JButton("Like (" + comment.getNumLikes() + ")");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        actionPanel.add(likeButton);
        actionPanel.add(updateButton);
        actionPanel.add(deleteButton);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(profileImageLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(userInfoPanel);
        centerPanel.add(commentContent);
        centerPanel.add(actionPanel);
        centerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fullCommentPanel = new JPanel();
        fullCommentPanel.setLayout(new BoxLayout(fullCommentPanel, BoxLayout.X_AXIS));
        fullCommentPanel.setBackground(Color.WHITE);
        fullCommentPanel.add(leftPanel);
        fullCommentPanel.add(Box.createHorizontalStrut(10));
        fullCommentPanel.add(centerPanel);
        fullCommentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, fullCommentPanel.getPreferredSize().height));

        return fullCommentPanel;
    }
}
