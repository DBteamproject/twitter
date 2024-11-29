package pages;

import config.DatabaseConnection;
import dto.MemberLoginDto;
import repository.MemberRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginPage extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login Page");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 로그인 패널 생성
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);

        // 로그인 제목
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("돋움", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 중앙 입력 필드 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // 사용자 ID 필드
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userIdField = new JTextField();
        userIdField.setMaximumSize(new Dimension(300, 30));
        userIdField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 비밀번호 필드
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 로그인 버튼
        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (userId.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPage.this, "모든 필드를 채워주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 로그인 검증
                boolean isLoginValid = validateLogin(userId, password);
                if (isLoginValid) {
                    dispose(); // 로그인 성공 시 현재 창 닫기
                    SwingUtilities.invokeLater(() -> new TwitterMainPage(userId).setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Invalid User ID or Password. Please try again.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 회원가입 버튼
        JButton signUpButton = new JButton("SignUp");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpPage(); // Sign Up 페이지로 이동
                dispose(); // 현재 로그인 페이지 닫기
            }
        });

        // 중앙 패널에 컴포넌트 추가
        centerPanel.add(userIdLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(userIdField);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(passwordLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(passwordField);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(loginButton);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(signUpButton);

        // 입력 필드가 왼쪽에 붙지 않도록 패딩 추가
        JPanel paddedPanel = new JPanel();
        paddedPanel.setLayout(new BoxLayout(paddedPanel, BoxLayout.X_AXIS));
        paddedPanel.setBackground(Color.WHITE);
        paddedPanel.add(Box.createHorizontalGlue()); // 왼쪽 여백
        paddedPanel.add(centerPanel); // 입력 필드
        paddedPanel.add(Box.createHorizontalGlue()); // 오른쪽 여백

        // 패널 배치
        loginPanel.add(Box.createVerticalStrut(50)); // 상단 여백
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(paddedPanel);

        // 메인 패널에 로그인 패널 추가
        add(loginPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private boolean validateLogin(String userId, String password) {
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            MemberRepository memberRepository = new MemberRepository();
            String loginId = memberRepository.logIn(new MemberLoginDto(userId, password), con);
            return loginId != null;
        } catch (SQLException e) {
            System.err.println("An error occurred while validating login: " + e.getMessage());
            return false; // 예외가 발생한 경우 false 반환
        } finally {
            if (con != null) {
                DatabaseConnection.closeConnection(con);
            }
        }
    }
}
