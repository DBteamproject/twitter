package dialog;

import config.DatabaseConnection;
import pages.ProfilePage;
import pages.TwitterMainPage;
import repository.PostRepository;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TweetWriteDialogHandler {
    private TwitterMainPage mainPage;
    private final String userId; // 사용자 ID

    public TweetWriteDialogHandler(TwitterMainPage mainPage, String userId) {
        this.mainPage = mainPage;
        this.userId = userId;
    }

    public void showTweetDialog(JFrame parent) {
        JDialog tweetDialog = new JDialog(parent, "Compose Tweet", true);
        tweetDialog.setSize(500, 400);
        tweetDialog.setLocationRelativeTo(parent);
        tweetDialog.setLayout(new BorderLayout());

        // 상단 제목 패널
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel textLabel = new JLabel("Text");
        textLabel.setFont(new Font("Arial", Font.BOLD, 16));
        textLabel.setHorizontalAlignment(SwingConstants.LEFT); // 왼쪽 정렬
        titlePanel.add(textLabel, BorderLayout.WEST);

        JLabel imagesLabel = new JLabel("Images");
        imagesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        imagesLabel.setHorizontalAlignment(SwingConstants.RIGHT); // 오른쪽 정렬
        titlePanel.add(imagesLabel, BorderLayout.EAST);

        // 트윗 입력 영역
        JTextArea tweetTextArea = new JTextArea();
        tweetTextArea.setLineWrap(true);
        tweetTextArea.setWrapStyleWord(true);
        tweetTextArea.setFont(new Font("Arial", Font.PLAIN, 14));

        // 이미지를 표시할 프리뷰 패널
        JPanel imagePreviewPanel = new JPanel();
        imagePreviewPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        imagePreviewPanel.setBackground(Color.WHITE);
        JScrollPane previewScrollPane = new JScrollPane(imagePreviewPanel);
        previewScrollPane.setPreferredSize(new Dimension(200, 200));

        // 이미지 파일 리스트를 관리할 변수
        List<File> selectedImages = new ArrayList<>();

        // 사진 추가 버튼
        JButton addPhotoButton = new JButton("Add Photos");
        addPhotoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true); // 여러 파일 선택 가능
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));

            int result = fileChooser.showOpenDialog(tweetDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                for (File file : files) {
                    selectedImages.add(file);

                    // 썸네일 패널 생성
                    JPanel thumbnailPanel = new JPanel();
                    thumbnailPanel.setLayout(new BorderLayout());
                    thumbnailPanel.setPreferredSize(new Dimension(90, 90));
                    thumbnailPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                    // 썸네일 이미지
                    try {
                        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                        Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                        thumbnailPanel.add(imageLabel, BorderLayout.CENTER);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    // 삭제 버튼
                    JButton removeButton = new JButton("X");
                    removeButton.setFont(new Font("Arial", Font.BOLD, 10));
                    removeButton.setForeground(Color.RED);
                    removeButton.setMargin(new Insets(0, 0, 0, 0));
                    removeButton.setFocusPainted(false);
                    removeButton.setContentAreaFilled(false);
                    removeButton.setBorderPainted(false);
                    removeButton.addActionListener(event -> {
                        imagePreviewPanel.remove(thumbnailPanel);
                        selectedImages.remove(file);
                        imagePreviewPanel.revalidate();
                        imagePreviewPanel.repaint();
                    });

                    // 삭제 버튼을 오른쪽 상단에 배치
                    JPanel removeButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
                    removeButtonPanel.setOpaque(false);
                    removeButtonPanel.add(removeButton);
                    thumbnailPanel.add(removeButtonPanel, BorderLayout.NORTH);

                    imagePreviewPanel.add(thumbnailPanel);
                }
                imagePreviewPanel.revalidate();
                imagePreviewPanel.repaint();
            }
        });

        // 트윗 게시 버튼
        JButton postTweetButton = new JButton("Tweet");
        postTweetButton.addActionListener(e -> {
            String tweetContent = tweetTextArea.getText();
            if (!tweetContent.trim().isEmpty() || !selectedImages.isEmpty()) {
                Connection con = DatabaseConnection.getConnection();
                PostRepository postRepository = new PostRepository();
                try {
                    postRepository.writePost(tweetContent, selectedImages, con, userId);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                mainPage.showPage(new ProfilePage(mainPage, userId, userId));
                tweetDialog.dispose();
                DatabaseConnection.closeConnection(con);
            } else {
                JOptionPane.showMessageDialog(tweetDialog, "Cannot post an empty tweet!");
            }
        });

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addPhotoButton);
        buttonPanel.add(postTweetButton);

        tweetDialog.add(titlePanel, BorderLayout.NORTH);
        tweetDialog.add(new JScrollPane(tweetTextArea), BorderLayout.CENTER);
        tweetDialog.add(previewScrollPane, BorderLayout.EAST);
        tweetDialog.add(buttonPanel, BorderLayout.SOUTH);
        tweetDialog.setVisible(true);
    }
}
