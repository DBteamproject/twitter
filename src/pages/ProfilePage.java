package pages;

import dialog.TweetDesignPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ProfilePage extends JPanel {
    private JPanel tweetsPanel;
    private JScrollPane scrollPane;
    private List<String[]> tweetData;
    private int tweetIndex = 0; // 현재 로드된 트윗의 인덱스

    public ProfilePage(TwitterMainPage mainPage, String userId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단: 프로필 정보
        JPanel profileInfoPanel = new JPanel();
        profileInfoPanel.setLayout(new BorderLayout());
        profileInfoPanel.setBackground(Color.LIGHT_GRAY);
        profileInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 프로필 이미지
        JLabel profileImageLabel = new JLabel();
        ImageIcon profileIcon = new ImageIcon("src/resources/profile.png"); // 프로필 이미지 경로
        Image scaledProfileImage = profileIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        profileImageLabel.setIcon(new ImageIcon(scaledProfileImage));

        // 프로필 텍스트 정보
        JPanel profileTextPanel = new JPanel();
        profileTextPanel.setLayout(new BoxLayout(profileTextPanel, BoxLayout.Y_AXIS));
        profileTextPanel.setBackground(Color.LIGHT_GRAY);

        JLabel usernameLabel = new JLabel("<html><h2>Example User</h2></html>");
        JLabel handleLabel = new JLabel("<html><span style='color:gray;'>@" + userId + "</span></html>");
        JLabel bioLabel = new JLabel("<html>Just a simple bio about myself...</html>");
        JLabel followStatsLabel = new JLabel("<html>Followers: <b>123</b> | Following: <b>456</b></html>");
        JLabel joinedDateLabel = new JLabel("<html>Joined: <b>January 2021</b></html>");

        profileTextPanel.add(usernameLabel);
        profileTextPanel.add(handleLabel);
        profileTextPanel.add(Box.createVerticalStrut(5));
        profileTextPanel.add(bioLabel);
        profileTextPanel.add(Box.createVerticalStrut(5));
        profileTextPanel.add(followStatsLabel);
        profileTextPanel.add(Box.createVerticalStrut(5));
        profileTextPanel.add(joinedDateLabel);

        // 프로필 정보 패널 구성
        profileInfoPanel.add(profileImageLabel, BorderLayout.WEST);
        profileInfoPanel.add(profileTextPanel, BorderLayout.CENTER);

        // 하단: 트윗 목록
        tweetsPanel = new JPanel();
        tweetsPanel.setLayout(new BoxLayout(tweetsPanel, BoxLayout.Y_AXIS));
        tweetsPanel.setBackground(Color.WHITE);

        // 트윗 데이터 초기화
        initializeTweetData();

        // 초기 트윗 10개 로드
        loadMoreTweets(10);

        // 스크롤 가능한 트윗 패널
        scrollPane = new JScrollPane(tweetsPanel);
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
                    loadMoreTweets(10);
                }
            }
        });

        // 구성 요소 추가
        add(profileInfoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeTweetData() {
        tweetData = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            tweetData.add(new String[]{"User " + i, "@user" + i, "This is tweet number " + i,
                    String.valueOf(i * 2), String.valueOf(i), String.valueOf(i * 3), "src/resources/profile.png", "12/03"});
        }
    }

    private void loadMoreTweets(int count) {
        TweetDesignPanel tweetDesignPanel = new TweetDesignPanel();
        for (int i = 0; i < count && tweetIndex < tweetData.size(); i++) {
            String[] tweet = tweetData.get(tweetIndex++);
            String[] imagePaths = {
                    "src/resources/school_test.png",
                    "src/resources/school_test.png",
                    "src/resources/school_test.png",
                    "src/resources/school_test.png"
            };
            tweetsPanel.add(tweetDesignPanel.base(tweet[0], tweet[1], tweet[2],
                    Integer.parseInt(tweet[3]),
                    Integer.parseInt(tweet[4]), Integer.parseInt(tweet[5]), tweet[6], imagePaths, tweet[7]));
        }
        tweetsPanel.revalidate();
    }
}

