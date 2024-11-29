package pages;

import dialog.TweetWriteDialogHandler;

import javax.swing.*;
import java.awt.*;

public class TwitterMainPage extends JFrame {
    private JPanel topPanel; // 상단 메뉴 패널
    private JPanel bottomPanel; // 하단 메뉴 패널
    private JButton tweetButton; // 트윗 작성 버튼 (항상 떠 있음)
//    private String userId = "1"; // 현재 접속한 유저의 id;


    public TwitterMainPage(String userId) {
        setTitle("Twitter Main Page");
        setSize(400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단 메뉴 패널
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.BLUE);

        JButton userIdButton = createIconButton("@" + userId, 17, Color.LIGHT_GRAY);
        JButton twitterLogoButton = createIconButton("Twitter (DB 9조)", 20, Color.WHITE);
        JButton logoutButton = createIconButton("Logout", 17, Color.LIGHT_GRAY);
        logoutButton.addActionListener(e -> logoutAction());

        topPanel.add(userIdButton, BorderLayout.WEST);
        topPanel.add(twitterLogoButton, BorderLayout.CENTER);
        topPanel.add(logoutButton, BorderLayout.EAST);

        // 하단 메뉴 패널
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 60, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton homeButton = createIconButton("src/resources/home.png", 24);
        JButton searchButton = createIconButton("src/resources/search.png", 24);
        JButton notificationsButtonBottom = createIconButton("src/resources/alarm.png", 24);
        JButton myProfileButton = createIconButton("src/resources/profile.png", 24);

        homeButton.addActionListener(e -> showPage(new MainPage(this, userId))); // 홈 버튼 클릭 시 MainPage로 이동
        searchButton.addActionListener(e -> showPage(new SearchPage(this, userId))); // 검색 페이지
        notificationsButtonBottom.addActionListener(e -> showPage(new NotificationPage(this, userId))); // 알림 페이지
        myProfileButton.addActionListener(e -> showPage(new ProfilePage(this, userId, userId))); // 프로필 페이지

        bottomPanel.add(homeButton);
        bottomPanel.add(searchButton);
        bottomPanel.add(notificationsButtonBottom);
        bottomPanel.add(myProfileButton);

        // 트윗 작성 버튼 (항상 떠 있음)
        tweetButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (getModel().isArmed()) {
                    g.setColor(new Color(135, 206, 250));
                } else {
                    g.setColor(new Color(29, 161, 242));
                }
                g.fillOval(0, 0, getWidth(), getHeight());

                ImageIcon icon = new ImageIcon("src/resources/feather_white_icon.png"); // 이미지 파일 경로
                if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                    System.out.println("Image failed to load!");
                    return;
                }

                Image image = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // 이미지 크기 조정
                icon = new ImageIcon(image);

                // 이미지 중앙 배치
                int x = (getWidth() - 40) / 2; // 버튼 너비 기준 중앙 x 좌표
                int y = (getHeight() - 40) / 2; // 버튼 높이 기준 중앙 y 좌표

                g.drawImage(icon.getImage(), x, y, null); // 이미지 그리기
            }

            @Override
            public void paintBorder(Graphics g) {
                // 버튼 테두리 없음
            }
        };
        tweetButton.setContentAreaFilled(false);
        tweetButton.setFocusPainted(false);
        tweetButton.setBorderPainted(false);
        tweetButton.setPreferredSize(new Dimension(60, 60));
        tweetButton.addActionListener(e -> {
            TweetWriteDialogHandler tweetDialogHandler = new TweetWriteDialogHandler(this, userId);
            tweetDialogHandler.showTweetDialog(this);
        });

        // 메인 프레임 구성
        add(topPanel, BorderLayout.NORTH);
        add(new MainPage(this, userId), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // JLayeredPane을 사용해 버튼을 항상 떠 있도록 설정
        JLayeredPane layeredPane = getLayeredPane();
        tweetButton.setBounds(310, 650, 60, 60); // 위치 설정
        layeredPane.add(tweetButton, JLayeredPane.POPUP_LAYER);

        setVisible(true);
    }

    public void logoutAction() {
        SwingUtilities.invokeLater(() -> {
            dispose(); // 현재 메인 프레임을 완전히 닫기

            // 새로운 로그인 페이지 열기
            LoginPage loginPage = new LoginPage();
            loginPage.setLoginListener(newUserId -> {
                // 로그인이 완료되면 새로운 TwitterMainPage를 생성
                SwingUtilities.invokeLater(() -> new TwitterMainPage(newUserId));
            });
        });
    }

    public void showPage(Component component) {
        // 기존 패널 제거
        getContentPane().removeAll();

        // 상단, 중앙, 하단 구성
        add(topPanel, BorderLayout.NORTH);
        add(component, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 화면 갱신
        revalidate();
        repaint();
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
        button.setContentAreaFilled(false);
        return button;
    }

    public static void main(String[] args) {
//        SwingUtilities.invokeLater(TwitterMainPage::new);
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setLoginListener(userId -> {
                new TwitterMainPage(userId);
            });
        });
    }
}
