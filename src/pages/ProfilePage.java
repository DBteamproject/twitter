package pages;

import config.DatabaseConnection;
import dialog.FollowUserListDialog;
import dto.MemberDto;
import dto.PostDto;
import listener.UserEditActionListener;
import panel.TweetDesignPanel;
import repository.MemberRepository;
import repository.PostReadRepository;
import repository.FollowRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class ProfilePage extends JPanel {
    private JPanel tweetsPanel;
    private JScrollPane scrollPane;
    private int tweetScrollNum = 1; // 현재 로드된 트윗의 인덱스
    private boolean tweetScrollStatus = true;

    private String loggedInUserId;
    private String profileUserId;
    private JButton followButton;
    private FollowRepository followRepository;
    private Connection con;


    public ProfilePage(TwitterMainPage mainPage, String searchUserId, String userId) {
        // DB에서 데이터 가져오기
        this.con = DatabaseConnection.getConnection();
        MemberRepository memberRepository = new MemberRepository();
        MemberDto memberInfo = memberRepository.getMemberInfo(con, searchUserId);
        this.followRepository = new FollowRepository();
        this.loggedInUserId = userId;
        this.profileUserId = searchUserId;

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
        followButton = new JButton("Follow");
        followButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleFollowAction(mainPage);
            }
        });
        updateFollowButton();

        usernamePanel.add(usernameLabel);
        if (!userId.equals(searchUserId)) {
            usernamePanel.add(followButton);
        }

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

        JPanel listButtonPanel = getButtonJPanel(searchUserId, userId, memberInfo, mainPage);

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

    private void handleFollowAction(TwitterMainPage mainPage) {
        try {
            if (followRepository.isFollowing(con, loggedInUserId, profileUserId)) {
                followRepository.removeFollow(con, loggedInUserId, profileUserId);
                JOptionPane.showMessageDialog(this, "Successfully un-followed user @" + profileUserId, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                followRepository.addFollow(con, loggedInUserId, profileUserId);
                JOptionPane.showMessageDialog(this, "Successfully followed user @" + profileUserId, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
//            updateFollowButton();
            mainPage.showPage(new ProfilePage(mainPage, profileUserId, loggedInUserId));
        } catch (Exception ex) {
//            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "에러 발생: " + ex.getMessage());
        }
    }

    private void updateFollowButton() {
        try {
            if (followRepository.isFollowing(con, loggedInUserId, profileUserId)) {
                followButton.setText("Un-Follow");
            } else {
                followButton.setText("Follow");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static JPanel getButtonJPanel(String searchUserId, String userId, MemberDto memberDto, TwitterMainPage mainPage) {
        Connection con = DatabaseConnection.getConnection();
        FollowRepository followRepository = new FollowRepository();
        JPanel listButtonPanel = new JPanel();
        listButtonPanel.setLayout(new BoxLayout(listButtonPanel, BoxLayout.Y_AXIS));
        listButtonPanel.setBackground(Color.LIGHT_GRAY);
        listButtonPanel.setOpaque(true);

        // 첫 번째 행 버튼들
        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        firstRow.setBackground(Color.LIGHT_GRAY);
        JButton followerListButton = new JButton("Follower List");
        JButton followingListButton = new JButton("Following List");
        firstRow.add(followerListButton);
        firstRow.add(followingListButton);

        // 두 번째 행 버튼들
        JPanel secondRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        secondRow.setBackground(Color.LIGHT_GRAY);
        JButton userEditButton = new JButton("User Edit");
        JButton userDeleteButton = new JButton("Delete Account");
        secondRow.add(userEditButton);
        secondRow.add(userDeleteButton);

        // 패널에 두 행 추가
        listButtonPanel.add(firstRow);
        if (searchUserId.equals(userId)) {
            listButtonPanel.add(secondRow);
        }

        followerListButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<MemberDto> followerList = followRepository.getFollowers(con, searchUserId);
                FollowUserListDialog dialog = new FollowUserListDialog(
                        (Frame) SwingUtilities.getWindowAncestor(listButtonPanel),
                        "Follower List",
                        followerList,
                        mainPage,
                        userId
                );
                dialog.setVisible(true);
            }
        });

        followingListButton.addActionListener(new ActionListener() {
            Connection con = DatabaseConnection.getConnection();
            @Override
            public void actionPerformed(ActionEvent e) {
                List<MemberDto> followingList = followRepository.getFollowings(con, searchUserId);
                FollowUserListDialog dialog = new FollowUserListDialog(
                        (Frame) SwingUtilities.getWindowAncestor(listButtonPanel),
                        "Following List",
                        followingList,
                        mainPage,
                        userId
                );
                dialog.setVisible(true);
            }
        });

        userEditButton.addActionListener(new UserEditActionListener(
                (Frame) SwingUtilities.getWindowAncestor(listButtonPanel),
                memberDto,
                mainPage,
                userId
        ));
        userDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 확인 다이얼로그 띄우기
                int response = JOptionPane.showConfirmDialog(
                        listButtonPanel, // 다이얼로그를 띄울 부모 컴포넌트
                        "Do you want to delete my account?", // 메시지 내용
                        "Delete", // 다이얼로그 제목
                        JOptionPane.YES_NO_OPTION, // YES/NO 버튼을 추가
                        JOptionPane.WARNING_MESSAGE // 경고 아이콘 사용
                );

                // 사용자가 YES를 클릭했을 경우
                if (response == JOptionPane.YES_OPTION) {
                    // 실제 삭제 작업을 수행하는 함수 호출
                    Connection con = DatabaseConnection.getConnection();
                    MemberRepository memberRepository = new MemberRepository();
                    try {
                        memberRepository.deleteMember(con, userId);

                        JOptionPane.showMessageDialog(listButtonPanel, "Member deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        SwingUtilities.invokeLater(() -> {
                            mainPage.dispose(); // 현재 TwitterMainPage 닫기
                            new LoginPage().setVisible(true);
                        });
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    DatabaseConnection.closeConnection(con);
                }
            }
        });

        return listButtonPanel;
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

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (con != null) {
            System.out.println("DB 연결 해제");
            DatabaseConnection.closeConnection(con);
        }
    }
}

