package pages;

import dialog.TweetDesignPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainPage extends JPanel {
    private JPanel tweetsPanel;
    private JScrollPane scrollPane;
    private List<String[]> tweetData;
    private int tweetIndex = 0; // 현재 로드된 트윗의 인덱스

    public MainPage(TwitterMainPage mainPage, String userId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 트윗 데이터 초기화
        initializeTweetData();

        // 트윗 패널
        tweetsPanel = new JPanel();
        tweetsPanel.setLayout(new BoxLayout(tweetsPanel, BoxLayout.Y_AXIS));
        tweetsPanel.setBackground(Color.WHITE);

        // 초기 트윗 10개 로드
        loadMoreTweets(10);

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
                    loadMoreTweets(10);
                }
            }
        });

        // 스크롤 패널 추가
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

