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
        setLayout(new GridLayout(3, 2));

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
                } else {
                    // 로그인 실패 시 경고 팝업 띄우기
                    JOptionPane.showMessageDialog(LoginPage.this,
                            "Invalid User ID or Password. Please try again.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(userIdLabel);
        add(userIdField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // 빈 칸
        add(loginButton);

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
