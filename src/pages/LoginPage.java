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
    private LoginListener loginListener;

    public LoginPage() {
        setTitle("Login Page");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2)); // 버튼 추가로 그리드 행 수 변경

        JLabel userIdLabel = new JLabel("User ID:");
        userIdField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText();
                String password = new String(passwordField.getPassword()); // 비밀번호 필드의 값을 가져옴

                // 여기서 실제 로그인 검증 로직을 추가 (예: 데이터베이스 확인)
                boolean isLoginValid = validateLogin(userId, password); // 로그인 검증 메소드

                if (isLoginValid) {
                    if (loginListener != null) {
                        loginListener.onLoginSuccess(userId);
                    }
                    dispose(); // 로그인 성공 시 현재 창 닫기
                    new TwitterMainPage(userId).setVisible(true);
                } else {
                    // 로그인 실패 시 경고 팝업 띄우기
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Invalid User ID or Password. Please try again.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpPage(); // Sign Up 페이지로 이동
                dispose(); // 현재 로그인 페이지 닫기
            }
        });

        add(userIdLabel);
        add(userIdField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(signUpButton); // Sign Up 버튼 추가

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

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public interface LoginListener {
        void onLoginSuccess(String userId);
    }
}
