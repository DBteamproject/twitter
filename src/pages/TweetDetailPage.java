package pages;

import config.DatabaseConnection;
import dialog.CommentDesignPanel;
import dialog.TweetDesignPanel;
import dto.CommentDto;
import dto.MemberDto;
import dto.PostDto;
import repository.MemberRepository;
import repository.PostReadRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


public class TweetDetailPage extends JPanel {
    private JPanel tweetsPanel;

    public TweetDetailPage(TwitterMainPage mainPage, String postId, String userId) {
        // DB에서 데이터 가져오기 (댓글 데이터 포함)
        Connection con = DatabaseConnection.getConnection();
        PostReadRepository postReadRepository = new PostReadRepository();
        PostDto postDto = postReadRepository.getSinglePost(con, userId, postId);

        MemberRepository memberRepository = new MemberRepository();
        MemberDto memberInfo = memberRepository.getMemberInfo(con, userId);
        DatabaseConnection.closeConnection(con);

        // 임시 데이터 추가
        CommentDto commentDto = new CommentDto("abc", "sdf sdfsdfs", 2, false, memberInfo, LocalDateTime.now());
        List<CommentDto> comments = Arrays.asList(commentDto, commentDto);

        // 레이아웃 설정
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 댓글 목록 패널 설정
        tweetsPanel = new JPanel();
        tweetsPanel.setLayout(new BoxLayout(tweetsPanel, BoxLayout.Y_AXIS));
        tweetsPanel.setBackground(Color.WHITE);

        // 트윗 로드
        loadTweet(mainPage, postDto, userId);

        // 상단: 댓글 제목 및 입력 박스 추가
        JPanel commentHeaderPanel = new JPanel();
        commentHeaderPanel.setLayout(new BoxLayout(commentHeaderPanel, BoxLayout.Y_AXIS));
        commentHeaderPanel.setBackground(Color.WHITE);
        commentHeaderPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        commentHeaderPanel.setMaximumSize(new Dimension(400, 150)); // 부모 패널의 고정 크기 설정

        // 댓글 제목 패널 (왼쪽 정렬)
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Color.WHITE);
        JLabel commentsTitle = new JLabel("댓글 (" + comments.size() + "개)");
        commentsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        commentsTitle.setPreferredSize(new Dimension(150, 30)); // 고정된 크기 설정
        titlePanel.add(commentsTitle);
        titlePanel.setMaximumSize(new Dimension(400, 30)); // 제목 패널의 최대 크기 설정

        // 댓글 입력창 설정 (왼쪽 정렬)
        JTextArea commentInputField = new JTextArea(3, 30);
        commentInputField.setLineWrap(true);
        commentInputField.setWrapStyleWord(true);
        commentInputField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane commentScrollPane = new JScrollPane(commentInputField);
        commentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        commentScrollPane.setPreferredSize(new Dimension(350, 60)); // 댓글 입력창의 크기 고정
        commentScrollPane.setMaximumSize(new Dimension(350, 60)); // 댓글 입력창의 최대 크기 고정

        // 댓글 작성 버튼 패널 (왼쪽 정렬)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(400, 40)); // 버튼 패널의 최대 크기 설정

        JButton postCommentButton = new JButton("댓글쓰기");
        postCommentButton.setFont(new Font("Arial", Font.PLAIN, 12));
        postCommentButton.setForeground(Color.WHITE);
        postCommentButton.setBackground(Color.BLUE);
        postCommentButton.setFocusPainted(false);
        postCommentButton.setBorderPainted(false);
        postCommentButton.setOpaque(true);
        postCommentButton.setPreferredSize(new Dimension(100, 30)); // 댓글쓰기 버튼 크기 고정
        buttonPanel.add(postCommentButton);

        postCommentButton.addActionListener(e -> {
            String newCommentContent = commentInputField.getText().trim();
            if (!newCommentContent.isEmpty()) {
                // 새로운 댓글 추가 로직
                System.out.println("New comment: " + newCommentContent);
                // 예시로 댓글을 추가하는 코드. 실제로는 데이터베이스에 저장 후 다시 로드하는 방식이어야 함
                CommentDto newComment = new CommentDto(userId, newCommentContent, 0, false, memberInfo, LocalDateTime.now());
                loadComment(mainPage, newComment, userId);
                commentInputField.setText("");  // 입력창 초기화
            }
        });

        // 댓글 제목 및 입력 패널에 요소 추가
        commentHeaderPanel.add(titlePanel);
        commentHeaderPanel.add(Box.createVerticalStrut(5)); // 제목과 입력창 사이의 간격
        commentHeaderPanel.add(commentScrollPane);
        commentHeaderPanel.add(Box.createVerticalStrut(5)); // 입력창과 버튼 사이의 간격
        commentHeaderPanel.add(buttonPanel);

        // 댓글 제목 및 입력 패널을 트윗 아래에 추가
        tweetsPanel.add(commentHeaderPanel);
        tweetsPanel.add(Box.createVerticalStrut(5)); // 트윗과 댓글 목록 사이 간격 줄임


        // 댓글 로드
        loadComments(mainPage, comments, userId);

        JScrollPane scrollPane = new JScrollPane(tweetsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTweet(TwitterMainPage mainPage, PostDto postDto, String userId) {
        TweetDesignPanel tweetDesignPanel = new TweetDesignPanel();
        JPanel tweetPanel = tweetDesignPanel.base(mainPage, postDto, userId);
        tweetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, tweetPanel.getPreferredSize().height));
        tweetsPanel.add(tweetPanel);
        tweetsPanel.add(Box.createVerticalStrut(10)); // 트윗과 댓글 사이 간격
        tweetsPanel.revalidate();
    }

    private void loadComments(TwitterMainPage mainPage, List<CommentDto> comments, String userId) {
        CommentDesignPanel commentDesignPanel = new CommentDesignPanel();
        for (CommentDto comment : comments) {
            JPanel commentPanel = commentDesignPanel.base(mainPage, comment, userId);
            tweetsPanel.add(commentPanel);
            tweetsPanel.add(Box.createVerticalStrut(10)); // 각 댓글 사이의 간격
        }
        tweetsPanel.revalidate();
    }

    private void loadComment(TwitterMainPage mainPage, CommentDto comment, String userId) {
        CommentDesignPanel commentDesignPanel = new CommentDesignPanel();
        JPanel commentPanel = commentDesignPanel.base(mainPage, comment, userId);
        tweetsPanel.add(commentPanel);
        tweetsPanel.add(Box.createVerticalStrut(10)); // 각 댓글 사이의 간격
        tweetsPanel.revalidate();
    }
}
