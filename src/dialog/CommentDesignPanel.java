package dialog;

import config.DatabaseConnection;
import dto.CommentDto;
import dto.CommentLikeDto;
import pages.ProfilePage;
import pages.TweetDetailPage;
import pages.TwitterMainPage;
import repository.CommentLikeRepository;
import repository.CommentRepository;
import repository.PostRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;


public class CommentDesignPanel {
    public JPanel base(TwitterMainPage mainPage, CommentDto comment, String postId, String userId) {
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

        profileImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPage.showPage(new ProfilePage(mainPage, comment.getMember().getUserId(), userId));
            }
        });

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

        String styledText = comment.getContent().replaceAll("\n", "<br>");
        JLabel commentContent = new JLabel("<html><p style='width: 240px;'>" + styledText + "</p></html>");
        commentContent.setForeground(Color.DARK_GRAY);
        commentContent.setBorder(new EmptyBorder(5, 0, 0, 0));

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton likeButton = new JButton("Like (" + comment.getNumLikes() + ")");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");


        likeButton.setForeground(Color.GRAY);
        likeButton.setContentAreaFilled(false);
        likeButton.setBorderPainted(false);
        likeButton.setFocusPainted(false);

        if (comment.getUserLiked()) {
            likeButton.setBackground(Color.YELLOW);
            likeButton.setOpaque(true);
        } else {
            likeButton.setBackground(null);
            likeButton.setOpaque(true);
        }

        likeButton.addActionListener(e -> {
            CommentLikeRepository commentLikeRepository = new CommentLikeRepository();
            try {
                Connection con = DatabaseConnection.getConnection();
                CommentLikeDto commentLikeDto = commentLikeRepository.updateLike(con, comment.getCommentId(), userId);

                likeButton.setText("Like (" + commentLikeDto.getCount() + ")");
                // 좋아요 상태에 따른 버튼 텍스트와 색상 변경
                if (commentLikeDto.getStatus()) {
                    likeButton.setBackground(Color.YELLOW);
                    likeButton.setOpaque(true);
                } else {
                    likeButton.setBackground(null);
                    likeButton.setOpaque(true);
                }
            } catch (SQLException ex) {
                System.err.println("An error occurred while updating the like status: " + ex.getMessage());
            }
        });

        updateButton.addActionListener(e -> showUpdateDialog(comment, userId, commentContent));

        deleteButton.addActionListener(e -> {
            // 삭제 확인을 요청하는 대화 상자 표시
            int response = JOptionPane.showConfirmDialog(null, "Do you want to delete this comment?", "DELETE CONFIRM", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                Connection con = DatabaseConnection.getConnection();
                CommentRepository commentRepository = new CommentRepository();
                try {
                    commentRepository.deleteComment(con, comment.getCommentId(), userId);
                    mainPage.showPage(new TweetDetailPage(mainPage, postId, userId));
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                DatabaseConnection.closeConnection(con);
            } else {
                // 사용자가 '아니오'를 선택한 경우
                System.out.println("Comment deletion canceled.");
            }
        });

        actionPanel.add(likeButton);

        if (comment.getMember().getUserId().equals(userId)) {
            actionPanel.add(updateButton);
            actionPanel.add(deleteButton);
        }

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


    private void showUpdateDialog(CommentDto comment, String userId, JLabel commentContentLabel) {
        // 다이얼로그 생성
        JDialog dialog = new JDialog((Frame) null, "Update Comment", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(null);

        // 여러 줄 입력 가능한 텍스트 영역
        JTextArea contentTextArea = new JTextArea(comment.getContent(), 10, 30);
        contentTextArea.setLineWrap(true);
        contentTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 확인 버튼 생성
        JButton confirmButton = new JButton("Update");
        confirmButton.addActionListener(ev -> {
            String updatedContent = contentTextArea.getText().trim();
            if (!updatedContent.isEmpty()) {
                // 업데이트 액션 취하기
                try {
                    Connection con = DatabaseConnection.getConnection();
                    CommentRepository commentRepository = new CommentRepository();
                    commentRepository.updateComment(con, comment.getCommentId(), updatedContent, userId);
                    DatabaseConnection.closeConnection(con);

                    // 댓글 업데이트 후 UI 업데이트
                    comment.setContent(updatedContent);
                    String styledText = updatedContent.replaceAll("\n", "<br>");
                    commentContentLabel.setText("<html><p style='width: 240px;'>" + styledText + "</p></html>"); // UI에 직접 반영
                    commentContentLabel.revalidate(); // 컴포넌트 재검증
                    commentContentLabel.repaint(); // 컴포넌트 다시 그리기

                    JOptionPane.showMessageDialog(dialog, "Comment updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Failed to update comment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            dialog.dispose(); // 다이얼로그 닫기
        });

        // 버튼 패널에 확인 버튼 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 다이얼로그 표시
        dialog.setVisible(true);
    }
}
