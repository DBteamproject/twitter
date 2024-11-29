package database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginGUI extends JFrame {
    public LoginGUI() {
        // 기본 설정
        setTitle("트위터");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 메인 패널 (CardLayout 사용)
        JPanel mainPanel = new JPanel(new CardLayout());
        add(mainPanel);

        // 회원가입 패널을 메인 화면으로 설정
        JPanel signUpPanel = createSignUpPanel(mainPanel);
        mainPanel.add(signUpPanel, "SignUp");

        // 로그인 패널 생성
        JPanel loginPanel = createLoginPanel(mainPanel);
        mainPanel.add(loginPanel, "Login");

        // 로그인 후 페이지 생성
        JPanel userHomePanel = createUserHomePanel(mainPanel);
        mainPanel.add(userHomePanel, "UserHome");

        // 초기 화면 설정 (회원가입 페이지를 메인 화면으로 설정)
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, "SignUp");
    }

    private JPanel createSignUpPanel(JPanel mainPanel) {
        // 회원가입 패널
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new BoxLayout(signUpPanel, BoxLayout.Y_AXIS)); // BoxLayout 사용
        signUpPanel.setBackground(Color.WHITE);

        // 상단 로고 추가 (가운데 정렬)
        JLabel logoLabel = new JLabel(new ImageIcon("twitterLogo.png"));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 로고 가운데 정렬

        // 회원가입 제목
        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(new Font("돋움", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬

        // 중앙 패널 (회원가입 필드)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        JTextField newUserIdField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JButton registerButton = new JButton("회원가입 완료");

        newUserIdField.setMaximumSize(new Dimension(300, 30));
        newPasswordField.setMaximumSize(new Dimension(300, 30));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(new JLabel("새 사용자 ID:"));
        centerPanel.add(newUserIdField);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(new JLabel("새 비밀번호:"));
        centerPanel.add(newPasswordField);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(registerButton);

        // 회원가입 버튼 이벤트
        registerButton.addActionListener(e -> {
            String userId = newUserIdField.getText().trim();
            String password = new String(newPasswordField.getPassword()).trim();

            if (userId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginGUI.this, "모든 필드를 채워주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 회원가입 성공 메시지
            JOptionPane.showMessageDialog(LoginGUI.this, "회원가입 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
        });

        // 하단 패널 (로그인 링크 추가)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(Color.WHITE);

        JLabel haveAccountLabel = new JLabel("계정이 있으신가요? ");
        JLabel loginLabel = new JLabel("로그인하기");
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        bottomPanel.add(haveAccountLabel);
        bottomPanel.add(loginLabel);

        // 로그인 링크 이벤트
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                cardLayout.show(mainPanel, "Login"); // 로그인 페이지로 이동
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginLabel.setForeground(Color.RED); // 마우스 올릴 때 색상 변경
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginLabel.setForeground(Color.BLUE); // 마우스 벗어났을 때 색상 복원
            }
        });

        // 컴포넌트 추가
        signUpPanel.add(Box.createVerticalStrut(20)); // 여백
        signUpPanel.add(logoLabel); // 로고 추가 (가운데)
        signUpPanel.add(Box.createVerticalStrut(10)); // 간격
        signUpPanel.add(titleLabel); // 회원가입 제목 추가
        signUpPanel.add(Box.createVerticalStrut(20)); // 간격
        signUpPanel.add(centerPanel); // 회원가입 필드 추가
        signUpPanel.add(Box.createVerticalStrut(20)); // 간격
        signUpPanel.add(bottomPanel); // 로그인 링크 추가

        return signUpPanel;
    }


    private JPanel createLoginPanel(JPanel mainPanel) {
        // 로그인 패널
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS)); // BoxLayout 사용
        loginPanel.setBackground(Color.WHITE);

        // 로그인 제목
        JLabel titleLabel = new JLabel("로그인");
        titleLabel.setFont(new Font("돋움", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 제목 가운데 정렬

        // 중앙 입력 필드 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // 사용자 ID 필드
        JTextField userIdField = new JTextField();
        userIdField.setMaximumSize(new Dimension(300, 30)); // 입력 칸 크기
        userIdField.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬

        // 비밀번호 필드
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 30)); // 입력 칸 크기
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT); // 가운데 정렬

        // 로그인 버튼
        JButton loginButton = new JButton("로그인");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // 버튼 가운데 정렬

        // 중앙 패널에 컴포넌트 추가
        centerPanel.add(new JLabel("사용자 ID:"));
        centerPanel.add(Box.createVerticalStrut(5)); // 간격
        centerPanel.add(userIdField);
        centerPanel.add(Box.createVerticalStrut(15)); // 간격
        centerPanel.add(new JLabel("비밀번호:"));
        centerPanel.add(Box.createVerticalStrut(5)); // 간격
        centerPanel.add(passwordField);
        centerPanel.add(Box.createVerticalStrut(20)); // 간격
        centerPanel.add(loginButton);

        // 로그인 버튼 이벤트
        loginButton.addActionListener(e -> {
            String userId = userIdField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (userId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginGUI.this, "모든 필드를 채워주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 로그인 성공 시 UserHome 페이지로 이동
            CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
            cardLayout.show(mainPanel, "UserHome");
        });

        // 입력 필드가 왼쪽에 붙지 않도록 패딩 추가
        JPanel paddedPanel = new JPanel();
        paddedPanel.setLayout(new BoxLayout(paddedPanel, BoxLayout.X_AXIS));
        paddedPanel.setBackground(Color.WHITE);
        paddedPanel.add(Box.createHorizontalGlue()); // 왼쪽 여백
        paddedPanel.add(centerPanel); // 입력 필드
        paddedPanel.add(Box.createHorizontalGlue()); // 오른쪽 여백

        // 패널 배치
        loginPanel.add(Box.createVerticalStrut(50)); // 상단 여백
        loginPanel.add(titleLabel); // 제목 추가
        loginPanel.add(Box.createVerticalStrut(20)); // 간격
        loginPanel.add(paddedPanel); // 패딩 처리된 입력 필드 추가

        return loginPanel;
    }




    private JPanel createUserHomePanel(JPanel mainPanel) {
        // 사용자 홈 패널
        JPanel userHomePanel = new JPanel();
        userHomePanel.setLayout(new BoxLayout(userHomePanel, BoxLayout.Y_AXIS)); // BoxLayout 사용
        userHomePanel.setBackground(Color.WHITE);

        // 사용자 이미지 크기 축소
        ImageIcon userImageIcon = new ImageIcon("userImage.png");
        Image scaledImage = userImageIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH); // 크기 조정
        JLabel userImageLabel = new JLabel(new ImageIcon(scaledImage));
        userImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // "트위터 시작하기" 버튼
        JButton startTwitterButton = new JButton("트위터 시작하기");
        startTwitterButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startTwitterButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(LoginGUI.this, "트위터가 시작되었습니다!", "환영", JOptionPane.INFORMATION_MESSAGE);
        });

        userHomePanel.add(Box.createVerticalStrut(50)); // 여백
        userHomePanel.add(userImageLabel); // 사용자 이미지 추가
        userHomePanel.add(Box.createVerticalStrut(20)); // 여백
        userHomePanel.add(startTwitterButton);

        return userHomePanel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginGUI app = new LoginGUI();
            app.setVisible(true);
        });
    }
}
