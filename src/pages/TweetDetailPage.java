package pages;

import config.DatabaseConnection;
import dialog.CommentDesignPanel;
import dialog.TweetDesignPanel;
import dto.CommentDto;
import dto.PostDto;
import repository.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class TweetDetailPage extends JPanel {
    private JPanel tweetsPanel;
    private int tweetScrollNum = 1; // 현재 로드된 트윗의 인덱스
    private boolean tweetScrollStatus = true;

    public TweetDetailPage(TwitterMainPage mainPage, String postId, String userId) {
        // DB에서 데이터 가져오기 (댓글 데이터 포함)
        Connection con = DatabaseConnection.getConnection();
        PostReadRepository postReadRepository = new PostReadRepository();
        PostDto postDto = postReadRepository.getSinglePost(con, userId, postId);

        PostRepository postRepository = new PostRepository();
        postRepository.updateViews(con, postId);
        DatabaseConnection.closeConnection(con);

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
        JLabel commentsTitle = new JLabel("Comment (" + postDto.getNumComments() + "개)");
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

        JButton postCommentButton = new JButton("Write Comment");
        postCommentButton.setFont(new Font("Arial", Font.PLAIN, 12));
        postCommentButton.setForeground(Color.WHITE);
        postCommentButton.setBackground(Color.BLUE);
        postCommentButton.setFocusPainted(false);
        postCommentButton.setBorderPainted(false);
        postCommentButton.setOpaque(true);
        postCommentButton.setPreferredSize(new Dimension(200, 30)); // 댓글쓰기 버튼 크기 고정
        buttonPanel.add(postCommentButton);

        postCommentButton.addActionListener(e -> {
            String newCommentContent = commentInputField.getText().trim();
            if (!newCommentContent.isEmpty()) {
                Connection con2 = DatabaseConnection.getConnection();
                CommentRepository commentRepository = new CommentRepository();
                try {
                    commentRepository.writeComment(con2, postId, newCommentContent, userId);
                    mainPage.showPage(new TweetDetailPage(mainPage, postId, userId));
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                DatabaseConnection.closeConnection(con2);
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
        loadComments(mainPage, postId, userId);

        JScrollPane scrollPane = new JScrollPane(tweetsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);

        // 스크롤 속도 높이기
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(10);
        verticalScrollBar.setBlockIncrement(80);

        // 스크롤 이벤트로 무한 스크롤 기능 추가
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
                if (scrollBar.getValue() + scrollBar.getVisibleAmount() >= scrollBar.getMaximum()) {
                    loadComments(mainPage, postId, userId);
                }
            }
        });

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

    private void loadComments(TwitterMainPage mainPage, String postId, String userId) {
        if(tweetScrollStatus) {
            Connection con = DatabaseConnection.getConnection();
            CommentReadRepository commentReadRepository = new CommentReadRepository();
            List<CommentDto> comments = commentReadRepository.getCommentsWithPost(con, postId, tweetScrollNum, userId);
            DatabaseConnection.closeConnection(con);

            if (comments.isEmpty()) {
                System.out.println("comments loaded is empty. so stopped loading comments.");
                tweetScrollStatus = false;
            }
            tweetScrollNum++;

            CommentDesignPanel commentDesignPanel = new CommentDesignPanel();
            for (CommentDto comment : comments) {
                JPanel commentPanel = commentDesignPanel.base(mainPage, comment, postId, userId);
                tweetsPanel.add(commentPanel);
                tweetsPanel.add(Box.createVerticalStrut(10)); // 각 댓글 사이의 간격
            }
            tweetsPanel.revalidate();
        }
    }
}
