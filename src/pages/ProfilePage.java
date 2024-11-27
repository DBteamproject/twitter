package pages;

import config.DatabaseConnection;
import dialog.TweetDesignPanel;
import dto.MemberDto;
import dto.PostDto;
import repository.MemberRepository;
import repository.PostReadRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class ProfilePage extends JPanel {
    private JPanel tweetsPanel;
    private JScrollPane scrollPane;
    private int tweetScrollNum = 1; // 현재 로드된 트윗의 인덱스
    private boolean tweetScrollStatus = true;

    public ProfilePage(TwitterMainPage mainPage, String searchUserId, String userId) {
        // DB에서 데이터 가져오기
        Connection con = DatabaseConnection.getConnection();
        MemberRepository memberRepository = new MemberRepository();
        MemberDto memberInfo = memberRepository.getMemberInfo(con, searchUserId);
        DatabaseConnection.closeConnection(con);

        // 레이아웃 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단: 프로필 정보 패널
        JPanel profileInfoPanel = new JPanel();
        profileInfoPanel.setLayout(new BorderLayout());
        profileInfoPanel.setBackground(Color.LIGHT_GRAY);
        profileInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 프로필 이미지
        JLabel profileImageLabel = new JLabel();
        String profileImagePath = (memberInfo.getProfileImage() != null && !memberInfo.getProfileImage().isEmpty()) ? memberInfo.getProfileImage() : "src/resources/profile.png";
        ImageIcon profileIcon = new ImageIcon(profileImagePath);
        Image scaledProfileImage = profileIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        profileImageLabel.setIcon(new ImageIcon(scaledProfileImage));

        // 프로필 텍스트 정보
        JPanel profileTextPanel = new JPanel();
        profileTextPanel.setLayout(new BoxLayout(profileTextPanel, BoxLayout.Y_AXIS));
        profileTextPanel.setBackground(Color.LIGHT_GRAY);

        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // 가로로 배치, 간격 없음
        usernamePanel.setBackground(Color.LIGHT_GRAY);
        JLabel usernameLabel = new JLabel("<html><h2>" + memberInfo.getUserName() + "</h2></html>");
        JButton followButton = new JButton("Follow");
        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Follow button clicked"); // 팔로우 기능 구현 필요
            }
        });
        usernamePanel.add(usernameLabel);
        usernamePanel.add(followButton);

        JLabel handleLabel = new JLabel("<html><span style='color:gray;'>@" + memberInfo.getUserId() + "</span></html>");
        JLabel bioLabel = new JLabel("<html>" + memberInfo.getIntroduce() + "</html>");
        JLabel followStatsLabel = new JLabel("<html>Followers: <b>" + memberInfo.getFollowersCount() + "명</b> | Following: <b>" + memberInfo.getFollowingCount() + "명</b></html>");
        LocalDateTime createdAt = memberInfo.getCreatedAt();
        String formattedDate = (createdAt != null) ? createdAt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")) : "Unknown";
        JLabel joinedDateLabel = new JLabel("<html>Joined: <b>" + formattedDate + "</b></html>");

        JPanel textInfoPanel = new JPanel();
        textInfoPanel.setLayout(new GridBagLayout());
        textInfoPanel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        textInfoPanel.add(usernamePanel, gbc);
        textInfoPanel.add(handleLabel, gbc);
        textInfoPanel.add(bioLabel, gbc);
        textInfoPanel.add(followStatsLabel, gbc);
        textInfoPanel.add(joinedDateLabel, gbc);

        profileTextPanel.add(textInfoPanel);

        JButton followerListButton = new JButton("Follower List");
        JButton followingListButton = new JButton("Following List");

        JPanel listButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        listButtonPanel.setBackground(Color.LIGHT_GRAY);
        listButtonPanel.add(followerListButton);
        listButtonPanel.add(followingListButton);

        profileInfoPanel.add(profileImageLabel, BorderLayout.WEST);
        profileInfoPanel.add(textInfoPanel, BorderLayout.CENTER);
        profileInfoPanel.add(listButtonPanel, BorderLayout.SOUTH);

        // 하단: 트윗 목록
        tweetsPanel = new JPanel();
        tweetsPanel.setLayout(new BoxLayout(tweetsPanel, BoxLayout.Y_AXIS));
        tweetsPanel.setBackground(Color.WHITE);

        // 초기 트윗 10개 로드
        loadMoreTweets(mainPage, searchUserId, userId);

        // 스크롤 가능한 트윗 패널
        scrollPane = new JScrollPane(tweetsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 스크롤 속도 높이기
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(10);
        verticalScrollBar.setBlockIncrement(80);

        // 스크롤 이벤트로 무한 스크롤 기능 추가
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                if (scrollBar.getValue() + scrollBar.getVisibleAmount() >= scrollBar.getMaximum()) {
                    loadMoreTweets(mainPage, searchUserId, userId);
                }
            }
        });

        // 구성 요소 추가
        add(profileInfoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }


    private void loadMoreTweets(TwitterMainPage mainPage, String searchUserId, String userId) {
        if(tweetScrollStatus) {
            Connection con = DatabaseConnection.getConnection();
            PostReadRepository postReadRepository = new PostReadRepository();
            List<PostDto> userPosts = postReadRepository.getUserPosts(con, tweetScrollNum, searchUserId, userId);
            DatabaseConnection.closeConnection(con);

            if (userPosts.isEmpty()) {
                System.out.println("posts loaded is empty. so stopped loading tweets.");
                tweetScrollStatus = false;
            }
            tweetScrollNum++;

            TweetDesignPanel tweetDesignPanel = new TweetDesignPanel();
            for (PostDto userPost : userPosts) {
                JPanel tweetPanel = tweetDesignPanel.base(mainPage, userPost, userId);
                tweetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, tweetPanel.getPreferredSize().height));
                tweetsPanel.add(tweetPanel);
            }
            tweetsPanel.revalidate();
        }
    }
}

