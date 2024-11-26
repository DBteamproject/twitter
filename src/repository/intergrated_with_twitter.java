package dialog;

import components.CommentSection;
import components.LikeButton;
import components.NotificationPanel;

import javax.swing.*;
import java.awt.*;

public class TweetDesignPanel {
    public JPanel base(String username, String handle, String tweetText,
                       int likes, int retweets, int comments, String profilePath,
                       String[] images, String date) {
        JPanel tweetPanel = new JPanel();
        tweetPanel.setLayout(new BorderLayout());
        tweetPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // 상단: 사용자 정보
        JLabel userLabel = new JLabel("<html><b>" + username + "</b> " + handle + "</html>");

        // 본문: 트윗 텍스트
        JTextArea tweetContent = new JTextArea(tweetText);
        tweetContent.setLineWrap(true);
        tweetContent.setWrapStyleWord(true);
        tweetContent.setEditable(false);

        // 하단: 좋아요, 댓글
        JPanel actionsPanel = new JPanel();
        LikeButton likeButton = new LikeButton();
        CommentSection commentSection = new CommentSection();

        // 좋아요, 댓글 버튼 클릭 시
        JButton commentButton = new JButton("Show Comments");
        commentButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, commentSection, "Comments", JOptionPane.PLAIN_MESSAGE);
        });

        actionsPanel.add(likeButton);
        actionsPanel.add(commentButton);

        // 구성
        tweetPanel.add(userLabel, BorderLayout.NORTH);
        tweetPanel.add(tweetContent, BorderLayout.CENTER);
        tweetPanel.add(actionsPanel, BorderLayout.SOUTH);

        return tweetPanel;
    }
}
