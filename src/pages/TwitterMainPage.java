package pages;

import repository.PostRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TwitterMainPage extends JFrame {
    private JPanel tweetsPanel;
    private JScrollPane scrollPane;
    private List<String[]> tweetData; // 미리 준비된 트윗 데이터 리스트
    private int tweetIndex = 0;  // 현재 로드된 트윗의 인덱스

    private String userId = "1"; //현재 접속한 유저의 id;

    public TwitterMainPage() {
        setTitle("Twitter Main Page");
        setSize(400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단 메뉴 패널
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topPanel.setBackground(Color.WHITE);

        JButton homeButton = createIconButton("🏠", 20, Color.BLUE);
        JButton twitterLogoButton = createIconButton("src/resources/img.png", 24);
        JButton postButton = createIconButton("✚", 20, Color.BLUE);

        topPanel.add(homeButton);
        topPanel.add(twitterLogoButton);
        topPanel.add(postButton);

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


        // 하단 메뉴 패널
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton profileButton = createIconButton("src/resources/home.png", 24);
        JButton searchButton = createIconButton("src/resources/search.png", 24);
        JButton notificationsButtonBottom = createIconButton("src/resources/alarm.png", 24);
        JButton myProfileButton = createIconButton("src/resources/profile.png", 24); // 프로필 조회 버튼

        myProfileButton.addActionListener(e -> showProfileDialog()); // 프로필 버튼 클릭 시 다이얼로그 표시

        bottomPanel.add(profileButton);
        bottomPanel.add(searchButton);
        bottomPanel.add(notificationsButtonBottom);
        bottomPanel.add(myProfileButton);

        // 메인 프레임에 패널 추가
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 플로팅 트윗 작성 버튼 (하늘색 원형)

        JButton tweetButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // 버튼 배경색 설정
                if (getModel().isArmed()) {
                    g.setColor(new Color(135, 206, 250)); // 버튼을 눌렀을 때 색상
                } else {
                    g.setColor(new Color(29, 161, 242)); // 기본 하늘색
                }
                g.fillOval(0, 0, getWidth(), getHeight()); // 동그란 버튼 배경

                // 이미지 로드
                ImageIcon icon = new ImageIcon("src/resources/feather_white_icon.png"); // 이미지 파일 경로
                if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                    System.out.println("Image failed to load!");
                }

                Image image = icon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH); // 이미지 크기 조정

                // 이미지 중앙 배치
                int x = (getWidth() - 25) / 2; // 버튼 너비 기준 중앙 x 좌표
                int y = (getHeight() - 25) / 2; // 버튼 높이 기준 중앙 y 좌표
                g.drawImage(image, x, y, null); // 이미지 그리기
            }

            @Override
            public void paintBorder(Graphics g) {
                // 버튼 테두리를 없앰
            }
        };

//        tweetButton.setFont(new Font("Arial", Font.PLAIN, 18));
//        tweetButton.setForeground(Color.WHITE);
        tweetButton.setContentAreaFilled(false);
        tweetButton.setFocusPainted(false);
        tweetButton.setBorderPainted(false);
        tweetButton.setPreferredSize(new Dimension(60, 60));
//        tweetButton.setSize(50, 50); // 동그란 모양을 위해 크기 지정
//        tweetButton.setHorizontalTextPosition(SwingConstants.CENTER);
//        tweetButton.setVerticalTextPosition(SwingConstants.CENTER);

        // 플로팅 버튼 위치와 레이어 설정
        JLayeredPane layeredPane = getLayeredPane();
        tweetButton.setBounds(310, 650, 60, 60);
        layeredPane.add(tweetButton, JLayeredPane.POPUP_LAYER);

        // 트윗 작성 버튼 클릭 이벤트
        tweetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTweetDialog();
            }
        });
    }

    // 미리 정의된 트윗 데이터 초기화
    private void initializeTweetData() {
        tweetData = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {  // 50개의 트윗 데이터를 준비
            tweetData.add(new String[]{"User " + i, "@user" + i, "This is tweet number " + i, 
                                       String.valueOf(i * 2), String.valueOf(i), String.valueOf(i * 3)});
        }
    }

    // 주어진 개수만큼 트윗을 불러옴
    private void loadMoreTweets(int count) {
        for (int i = 0; i < count && tweetIndex < tweetData.size(); i++) {
            String[] tweet = tweetData.get(tweetIndex++);
            tweetsPanel.add(createTweetPanel(tweet[0], tweet[1], tweet[2], 
                                             Integer.parseInt(tweet[3]), 
                                             Integer.parseInt(tweet[4]), 
                                             Integer.parseInt(tweet[5])));
        }
        tweetsPanel.revalidate();
    }

    private void showProfileDialog() {
        JDialog profileDialog = new JDialog(this, "My Profile", true);
        profileDialog.setSize(300, 200);
        profileDialog.setLocationRelativeTo(this);
        profileDialog.setLayout(new BorderLayout());

        JLabel profileLabel = new JLabel("<html><h2>Your Profile</h2><p>Username: You</p><p>Handle: @you</p></html>", JLabel.CENTER);
        profileDialog.add(profileLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> profileDialog.dispose());
        profileDialog.add(closeButton, BorderLayout.SOUTH);

        profileDialog.setVisible(true);
    }

    private void showTweetDialog() {
        JDialog tweetDialog = new JDialog(this, "Compose Tweet", true);
        tweetDialog.setSize(300, 200);
        tweetDialog.setLocationRelativeTo(this);
        tweetDialog.setLayout(new BorderLayout());

        JTextArea tweetTextArea = new JTextArea("What's happening?");
        tweetTextArea.setLineWrap(true);
        tweetTextArea.setWrapStyleWord(true);
        tweetTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton postTweetButton = new JButton("Tweet");
        postTweetButton.setBackground(Color.BLUE);
        postTweetButton.setForeground(Color.WHITE);
        postTweetButton.setFocusPainted(false);
        postTweetButton.addActionListener(e -> {

            String tweetContent = tweetTextArea.getText();
            PostRepository postRepository = new PostRepository();
           Connection con =  getConnection();
            try {
                postRepository.writePost(tweetContent,con,userId);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            if (!tweetContent.trim().isEmpty()) {
                tweetsPanel.add(createTweetPanel(userId, "@"+userId, tweetContent, 0, 0, 0));
                tweetsPanel.revalidate();
                tweetDialog.dispose();
            }
            closeConnection();
        });

        tweetDialog.add(new JScrollPane(tweetTextArea), BorderLayout.CENTER);
        tweetDialog.add(postTweetButton, BorderLayout.SOUTH);
        tweetDialog.setVisible(true);
    }

    private JPanel createTweetPanel(String username, String handle, String tweetText, int comments, int retweets, int likes) {
        JPanel tweetPanel = new JPanel();
        tweetPanel.setLayout(new BorderLayout());
        tweetPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        tweetPanel.setBackground(Color.WHITE);

        JLabel userInfo = new JLabel("<html><b>" + username + "</b> " + handle + "</html>");
        userInfo.setForeground(Color.BLACK);

        JLabel tweetContent = new JLabel("<html>" + tweetText + "</html>");
        tweetContent.setForeground(Color.DARK_GRAY);

        JPanel tweetContentPanel = new JPanel();
        tweetContentPanel.setLayout(new BoxLayout(tweetContentPanel, BoxLayout.Y_AXIS));
        tweetContentPanel.setBackground(Color.WHITE);
        tweetContentPanel.add(userInfo);
        tweetContentPanel.add(tweetContent);
        tweetPanel.add(tweetContentPanel, BorderLayout.CENTER);

        JPanel reactionPanel = new JPanel();
        reactionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        reactionPanel.setBackground(Color.WHITE);

        reactionPanel.add(createReactionLabel("💬", comments));
        reactionPanel.add(createReactionLabel("🔄", retweets));
        reactionPanel.add(createReactionLabel("❤️", likes));

        tweetPanel.add(reactionPanel, BorderLayout.SOUTH);

        return tweetPanel;
    }

    private JLabel createReactionLabel(String icon, int count) {
        JLabel label = new JLabel(icon + " " + count);
        label.setForeground(Color.GRAY);
        return label;
    }

    private JButton createIconButton(String text, int fontSize, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, fontSize));
        button.setForeground(color);
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private JButton createIconButton(String imagePath, int size) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);

        JButton button = new JButton(icon);
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false); // 배경 투명 처리
        return button;
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            TwitterMainPage mainPage = new TwitterMainPage();
            mainPage.setVisible(true);
        });
    }
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost/twitter2";
            String user = "root", passwd = "";
            con = DriverManager.getConnection(url, user, passwd);
            System.out.println(con);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        Statement stmt = null;
//        ResultSet rs = null;
        return con;
    }
    public void closeConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/twitter2", "root", "");
            // 데이터베이스 작업 수행
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
