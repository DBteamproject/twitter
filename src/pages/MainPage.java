package pages;

import config.DatabaseConnection;
import dialog.TweetDesignPanel;
import dto.PostDto;
import repository.PostReadRepository;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.List;


public class MainPage extends JPanel {
    private JPanel tweetsPanel;
    private JScrollPane scrollPane;
    private List<String[]> tweetData;
    private int tweetScrollNum = 1; // 현재 로드된 트윗의 인덱스
    private boolean tweetScrollStatus = true;

    public MainPage(TwitterMainPage mainPage, String userId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 트윗 패널
        tweetsPanel = new JPanel();
        tweetsPanel.setLayout(new BoxLayout(tweetsPanel, BoxLayout.Y_AXIS));
        tweetsPanel.setBackground(Color.WHITE);

        // 초기 트윗 10개 로드
        loadMoreTweets(userId);

        // 스크롤 가능한 트윗 패널
        scrollPane = new JScrollPane(tweetsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 스크롤 속도 높이기
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(10); // 한 번에 스크롤되는 픽셀 수
        verticalScrollBar.setBlockIncrement(80); // 드래그 시 스크롤되는 픽셀 수

        // 스크롤 이벤트로 무한 스크롤 기능 추가
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                if (scrollBar.getValue() + scrollBar.getVisibleAmount() >= scrollBar.getMaximum()) {
                    loadMoreTweets(userId);
                }
            }
        });

        // 스크롤 패널 추가
        add(scrollPane, BorderLayout.CENTER);
    }


    private void loadMoreTweets(String userId) {
        if(tweetScrollStatus) {
            Connection con = DatabaseConnection.getConnection();
            PostReadRepository postReadRepository = new PostReadRepository();
            List<PostDto> userPosts = postReadRepository.getAllPosts(con, tweetScrollNum, userId);
            DatabaseConnection.closeConnection(con);

            if (userPosts.isEmpty()) {
                System.out.println("posts loaded is empty. so stopped loading tweets.");
                tweetScrollStatus = false;
            }
            tweetScrollNum++;

            TweetDesignPanel tweetDesignPanel = new TweetDesignPanel();
            for (PostDto userPost : userPosts) {
                tweetsPanel.add(tweetDesignPanel.base(userPost, userId));
            }
            tweetsPanel.revalidate();
        }
    }
}
