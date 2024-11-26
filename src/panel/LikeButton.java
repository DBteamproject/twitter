package panel;

import javax.swing.*;

public class LikeButton extends JPanel {
    private int likeCount;
    private JLabel likeCountLabel;
    private JButton likeButton;

    public LikeButton() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        likeCount = 0;
        likeCountLabel = new JLabel("Likes: " + likeCount);
        likeButton = new JButton("Like");

        likeButton.addActionListener(e -> {
            likeCount++;
            likeCountLabel.setText("Likes: " + likeCount);
        });

        add(likeCountLabel);
        add(Box.createHorizontalStrut(10));
        add(likeButton);
    }

    public int getLikeCount() {
        return likeCount;
    }
}
