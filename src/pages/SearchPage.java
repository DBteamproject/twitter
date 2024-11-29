package pages;

import config.DatabaseConnection;
import dto.PostDto;
import panel.TweetDesignPanel;
import repository.PostRankingRepository;
import repository.PostReadRepository;
import repository.PostRepository;
import repository.PostSearchRepository;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class SearchPage extends JPanel {
    private JPanel tweetsPanel;
    private JScrollPane scrollPane;
    private int tweetScrollNum = 1; // 현재 로드된 트윗의 인덱스
    private boolean tweetScrollStatus = true;

    private String searchword = "";

    public SearchPage(TwitterMainPage mainPage, String userId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 상단 검색창 패널
        JPanel searchBarPanel = new JPanel();
        searchBarPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchBarPanel.setBackground(Color.WHITE);
        searchBarPanel.setPreferredSize(new Dimension(400, 60));

        // 검색창 필드 + 아이콘이 포함된 패널
        JPanel searchFieldPanel = new JPanel();
        searchFieldPanel.setLayout(new BorderLayout());
        searchFieldPanel.setPreferredSize(new Dimension(350, 40));
        searchFieldPanel.setBackground(new Color(240, 243, 245));
        searchFieldPanel.setBorder(BorderFactory.createLineBorder(new Color(240, 243, 245), 10));
        searchFieldPanel.setOpaque(true);

        // 검색창 텍스트 필드
        JTextField searchField = new JTextField();
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 40)); // 오른쪽에 공간 확보
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setForeground(new Color(120, 124, 130));
        searchField.setBackground(new Color(240, 243, 245));
        searchField.setCaretColor(Color.BLACK);

        // 검색창 입력값 이벤트 처리
        searchField.addActionListener(e -> {
            searchword = searchField.getText(); // 검색어 저장
            tweetScrollNum = 1; // 검색 초기화
            tweetScrollStatus = true; // 새로운 검색 가능 설정

            // 이전 검색 결과 삭제
            tweetsPanel.removeAll();
            tweetsPanel.revalidate();
            tweetsPanel.repaint();

            // 새로운 검색 결과 로드
            loadMoreTweets(mainPage, userId, searchword);
        });

        // 검색 아이콘 (🔍)
        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Arial", Font.PLAIN, 16));
        searchIcon.setForeground(new Color(120, 124, 130));

        // 검색 아이콘 위치 설정 (텍스트 필드 오른쪽)
        JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        iconPanel.setPreferredSize(new Dimension(40, 40));
        iconPanel.setBackground(new Color(240, 243, 245));
        iconPanel.add(searchIcon);

        // 패널에 텍스트 필드와 아이콘 추가
        searchFieldPanel.add(searchField, BorderLayout.CENTER);
        searchFieldPanel.add(iconPanel, BorderLayout.EAST);

        searchBarPanel.add(searchFieldPanel);
        add(searchBarPanel, BorderLayout.NORTH);

        // 중앙 트렌드 영역
        JPanel trendsPanel = new JPanel();
        trendsPanel.setLayout(new BoxLayout(trendsPanel, BoxLayout.Y_AXIS));
        trendsPanel.setBackground(Color.WHITE);

        JLabel trendsLabel = new JLabel("Search Results");
        trendsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        trendsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noResultsLabel = new JLabel("No results found.");
        noResultsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        trendsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        trendsPanel.add(trendsLabel);
        trendsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        trendsPanel.add(noResultsLabel);

        add(trendsPanel, BorderLayout.CENTER);


//
//        // 하단 돌아가기 버튼
//        JPanel backButtonPanel = new JPanel();
//        backButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        backButtonPanel.setBackground(Color.WHITE);
//
//        JButton backButton = new JButton("Back");
//        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
//        backButton.setBackground(Color.LIGHT_GRAY);
//        backButton.setFocusPainted(false);
//        backButton.setBorderPainted(false);
//        backButton.setPreferredSize(new Dimension(80, 30));
//        backButton.addActionListener(e -> mainFrame.showPage(new MainPage(mainFrame, userId))); // MainPage로 이동
//
//        backButtonPanel.add(backButton);
//        add(backButtonPanel, BorderLayout.SOUTH);

        // 트윗 패널
        tweetsPanel = new JPanel();
        tweetsPanel.setLayout(new BoxLayout(tweetsPanel, BoxLayout.Y_AXIS));
        tweetsPanel.setBackground(Color.WHITE);

        // 초기 트윗 10개 로드
        loadMoreTweets(mainPage, userId,searchword);

        // 스크롤 가능한 트윗 패널
        scrollPane = new JScrollPane(tweetsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
                    loadMoreTweets(mainPage, userId,searchword);
                }
            }
        });

        // 스크롤 패널 추가
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadMoreTweets(TwitterMainPage mainPage, String userId, String keyword) {
        if(tweetScrollStatus) {
            Connection con = DatabaseConnection.getConnection();

            List<PostDto> userPosts;
            if (keyword.equals("")) {
                PostRankingRepository postRankingRepository = new PostRankingRepository();
                userPosts = postRankingRepository.getAllPostsOrderByViews(con, tweetScrollNum, userId);
            }else{
                PostSearchRepository postSearchRepository = new PostSearchRepository();
                userPosts = postSearchRepository.searchPosts(con, tweetScrollNum, userId,keyword,keyword);
            }
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
