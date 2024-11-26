package components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CommentSection extends JPanel {
    private List<String> comments; // 댓글 저장
    private JPanel commentListPanel;

    public CommentSection() {
        setLayout(new BorderLayout());
        comments = new ArrayList<>();
        commentListPanel = new JPanel();
        commentListPanel.setLayout(new BoxLayout(commentListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(commentListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 댓글 추가 필드
        JPanel addCommentPanel = new JPanel(new BorderLayout());
        JTextField commentField = new JTextField();
        JButton addCommentButton = new JButton("Add Comment");

        addCommentButton.addActionListener(e -> {
            String comment = commentField.getText().trim();
            if (!comment.isEmpty()) {
                addComment(comment);
                commentField.setText("");
            }
        });

        addCommentPanel.add(commentField, BorderLayout.CENTER);
        addCommentPanel.add(addCommentButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(addCommentPanel, BorderLayout.SOUTH);
    }

    public void addComment(String comment) {
        comments.add(comment);
        JLabel commentLabel = new JLabel(comment);
        commentListPanel.add(commentLabel);
        commentListPanel.revalidate();
        commentListPanel.repaint();
    }
}