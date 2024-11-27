package dialog;

import config.DatabaseConnection;
import dto.PostDto;
import dto.PostLikeDto;
import dto.PostPhotoDto;
import pages.ProfilePage;
import pages.TweetDetailPage;
import pages.TwitterMainPage;
import repository.PostLikeRepository;
import repository.PostRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class TweetDesignPanel {
    public JPanel base(TwitterMainPage mainPage, PostDto postDto, String userId) {
        JPanel tweetPanel = new JPanel();
        tweetPanel.setLayout(new BorderLayout());
        tweetPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        tweetPanel.setBackground(Color.WHITE);

        // 왼쪽: 프로필 이미지
        JLabel profileImageLabel = new JLabel();
        profileImageLabel.setPreferredSize(new Dimension(50, 50));
        ImageIcon icon;
        if (postDto.getMember().getProfileImage() != null && !postDto.getMember().getProfileImage().isEmpty()) {
            icon = new ImageIcon(postDto.getMember().getProfileImage());
        } else {
            icon = new ImageIcon("src/resources/profile.png");
        }
        Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        profileImageLabel.setIcon(new ImageIcon(scaledImage));

        profileImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainPage.showPage(new ProfilePage(mainPage, postDto.getMember().getUserId(), userId));
            }
        });

        // 사용자 정보 (이름, 핸들)
        JLabel userInfo = new JLabel("<html><b>" + postDto.getMember().getUserName() + "</b> <span style='color:gray;'>@" + postDto.getMember().getUserId() + "</span></html>");
        userInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 본문 (해시태그 포함)
        String content = postDto.getContent(); // DB에서 가져온 내용
        String styledText = content.replaceAll("\n", "<br>");
        styledText = styledText.replaceAll("#(\\p{L}+)", "<span style='color:blue;'>#$1</span>");
        JLabel tweetContent = new JLabel("<html><p style='width: 240px;'>" + styledText + "</p></html>");
        tweetContent.setForeground(Color.DARK_GRAY);
        tweetContent.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 날짜
        LocalDateTime createdAt = postDto.getCreatedAt();
        String formattedDate = (createdAt != null) ? createdAt.format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) : "N/A";
        JLabel tweetDateLabel = new JLabel(formattedDate);
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
//        imagesPanel.setPreferredSize(new Dimension(50, 100));

        // postDto.getPhotos()가 비어있지 않은 경우만 처리
        List<PostPhotoDto> photos = postDto.getPhotos();
        if (!photos.isEmpty()) {
            for (PostPhotoDto photoDto : photos) {
                if (photoDto.getPath() != null && !photoDto.getPath().isEmpty()) {
                    JLabel imageLabel = createImageLabel(photoDto.getPath());
                    imagesPanel.add(imageLabel);
                    imagesPanel.add(Box.createHorizontalStrut(5)); // 이미지 간격 추가
                }
            }

            // 스크롤 패널에 이미지 패널을 추가
            JScrollPane imageScrollPane = new JScrollPane(imagesPanel);
            imageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            imageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            imageScrollPane.setPreferredSize(new Dimension(10, 100));
            imageScrollPane.setBorder(null);

            // 이미지 스크롤 패널을 트윗 내용 패널에 추가
            tweetContentPanel.add(imageScrollPane);
        }

        // 기타 구성 요소 추가
        tweetContentPanel.add(Box.createVerticalStrut(5)); // 간격 추가
        tweetContentPanel.add(tweetDateLabel); // 날짜 라벨 추가

        // 반응 아이콘 (댓글, 좋아요, 조회수)
        JPanel reactionPanel = new JPanel();
        reactionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        reactionPanel.setBackground(Color.WHITE);

        reactionPanel.add(createReactionLabel("💬", postDto.getNumComments())); // 댓글 수
        reactionPanel.add(createLikeButton(postDto.getUserLiked(), postDto.getNumLikes(), postDto.getPostId(), userId)); // 좋아요 수
        reactionPanel.add(createReactionLabel("Views", postDto.getNumViews())); // 조회수

        // 삭제 버튼 추가
        JButton deleteButton = new JButton("Delete >");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 12));
        deleteButton.setForeground(Color.RED);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> {
            // 삭제 확인을 요청하는 대화 상자 표시
            int response = JOptionPane.showConfirmDialog(null, "Do you want to delete this tweet?", "DELETE CONFIRM", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.YES_OPTION) {
                Connection con = DatabaseConnection.getConnection();
                PostRepository postRepository = new PostRepository();
                try {
                    postRepository.deletePost(con, postDto.getPostId(), userId);

                    Container parent = tweetPanel.getParent();
                    if (parent != null) {
                        parent.remove(tweetPanel);
                        parent.revalidate();
                        parent.repaint();
                        System.out.println("Tweet deleted!");
                    } else {
                        System.err.println("Parent container is null. Unable to delete tweet.");
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                DatabaseConnection.closeConnection(con);
            } else {
                // 사용자가 '아니오'를 선택한 경우
                System.out.println("Tweet deletion canceled.");
            }
        });

        if (postDto.getMember().getUserId().equals(userId)) {
            reactionPanel.add(deleteButton);  // 삭제 버튼을 조회수 옆에 추가
        }

        // Detail 버튼 추가
        JButton detailButton = new JButton("Detail >");
        detailButton.setFont(new Font("Arial", Font.PLAIN, 12));
        detailButton.setForeground(Color.BLUE);
        detailButton.setContentAreaFilled(false);
        detailButton.setBorderPainted(false);
        detailButton.setFocusPainted(false);
        detailButton.addActionListener(e -> {
            // 삭제 확인을 요청하는 대화 상자 표시
            mainPage.showPage(new TweetDetailPage(mainPage, postDto.getPostId(), userId));
        });
        reactionPanel.add(detailButton);

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

    private JButton createLikeButton(boolean userLiked, int count, String postId, String userId) {
        // 아이콘 경로 설정
        String iconPath = userLiked ? "src/resources/like_on.png" : "src/resources/like_off.png";
        ImageIcon icon = new ImageIcon(iconPath);
        Image image = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // 아이콘 크기 조정
        icon = new ImageIcon(image);

        // 버튼 생성
        JButton likeButton = new JButton(icon);
        likeButton.setText(" " + count);
        likeButton.setHorizontalTextPosition(JButton.RIGHT); // 텍스트를 아이콘 오른쪽에 배치
        likeButton.setBorderPainted(false); // 테두리 없음
        likeButton.setContentAreaFilled(false); // 배경 채우기 없음
        likeButton.setFocusPainted(false); // 포커스 테두리 없음
        likeButton.setForeground(Color.GRAY);
        likeButton.setFont(new Font("Arial", Font.PLAIN, 12));

        // 클릭 이벤트 리스너 추가
        likeButton.addActionListener(e -> {
            Connection con = DatabaseConnection.getConnection();
            PostLikeRepository postLikeRepository = new PostLikeRepository();
            try {
                PostLikeDto postLikeDto = postLikeRepository.updateLike(con, postId, userId);

                String newIconPath = postLikeDto.getStatus() ? "src/resources/like_on.png" : "src/resources/like_off.png";
                ImageIcon newIcon = new ImageIcon(newIconPath);
                Image newImage = newIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                newIcon = new ImageIcon(newImage);
                likeButton.setIcon(newIcon);
                likeButton.setText(" " + postLikeDto.getCount());
            } catch (SQLException ex) {
                System.out.println("좋아요 오류 발생");
            }
            DatabaseConnection.closeConnection(con);
        });

        return likeButton;
    }
}
