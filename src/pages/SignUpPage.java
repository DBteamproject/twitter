package pages;

import config.DatabaseConnection;
import dto.MemberSignUpDto;
import repository.MemberRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;


public class SignUpPage extends JFrame {

    private JTextField userIdField;
    private JPasswordField passwordField;
    private JPasswordField passwordReField;
    private JTextField userNameField;
    private JTextArea introduceArea;
    private JLabel profileImageLabel;
    private File selectedImageFile = null;

    public SignUpPage() {
        setTitle("Sign Up Page");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User ID
        JLabel userIdLabel = new JLabel("User ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userIdLabel, gbc);

        userIdField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(userIdField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        // Password Confirmation
        JLabel passwordReLabel = new JLabel("Re-enter Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(passwordReLabel, gbc);

        passwordReField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordReField, gbc);

        // User Name
        JLabel userNameLabel = new JLabel("User Name:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(userNameLabel, gbc);

        userNameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(userNameField, gbc);

        // Introduce (3줄 입력 가능)
        // Introduce (3줄 입력 가능)
        JLabel introduceLabel = new JLabel("Introduce:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;  // 레이블을 위쪽과 왼쪽으로 정렬
        gbc.fill = GridBagConstraints.HORIZONTAL; // 이전에 추가된 내용을 초기화
        add(introduceLabel, gbc);

        introduceArea = new JTextArea(3, 20);
        introduceArea.setLineWrap(true);
        introduceArea.setWrapStyleWord(true);
        JScrollPane introduceScrollPane = new JScrollPane(introduceArea);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;  // 가로와 세로 공간을 모두 사용하도록 설정
        gbc.weighty = 1.0;  // IntroduceArea가 충분한 세로 공간을 차지하도록 설정
        add(introduceScrollPane, gbc);

        // Profile Image Upload
        JLabel profileImageTitleLabel = new JLabel("Profile Image:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;  // weighty를 초기화하여 다른 컴포넌트가 비정상적으로 확장되지 않도록
        add(profileImageTitleLabel, gbc);

        JButton uploadButton = new JButton("Upload Image");
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(uploadButton, gbc);

        profileImageLabel = new JLabel();
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(profileImageLabel, gbc);

        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(SignUpPage.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    selectedImageFile = fileChooser.getSelectedFile();
                    profileImageLabel.setText("Selected: " + selectedImageFile.getName());
                }
            }
        });

        // Sign Up Button
        JButton signUpButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(signUpButton, gbc);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 여기에 실제 회원가입 로직을 추가합니다 (예: 데이터베이스에 사용자 정보 저장)
                String userId = userIdField.getText();
                String password = new String(passwordField.getPassword());
                String passwordRe = new String(passwordReField.getPassword());
                String userName = userNameField.getText();
                String introduce = introduceArea.getText();

                if (userId.isEmpty() || password.isEmpty() || passwordRe.isEmpty() || userName.isEmpty() || introduce.isEmpty()) {
                    JOptionPane.showMessageDialog(SignUpPage.this, "All fields must be filled in.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(passwordRe)) {
                    JOptionPane.showMessageDialog(SignUpPage.this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String path = null;
                if(selectedImageFile != null) {
                    String fileName = selectedImageFile.getName();
                    String extension = fileName.substring(fileName.lastIndexOf('.'));
                    String directoryPath = "src/resources/profiles";
                    Path targetPath = Paths.get(directoryPath, "user_" + userId + extension); // 확장자를 포함한 새 파일 이름
                    try {
                        Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING); // 파일 복사
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    path = targetPath.toString();
                }

                Connection con = null;
                try {
                    con = DatabaseConnection.getConnection();
                    MemberRepository memberRepository = new MemberRepository();
                    memberRepository.signUp(new MemberSignUpDto(userId, password, userName, introduce, path), con);
                } catch (SQLException e2) {
                    System.err.println("An error occurred while validating login: " + e2.getMessage());
                } finally {
                    if (con != null) {
                        DatabaseConnection.closeConnection(con);
                    }
                }

                // 이후 회원가입 성공 처리 (예: 데이터베이스에 저장 후 로그인 페이지로 돌아가기)
                JOptionPane.showMessageDialog(SignUpPage.this, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 가입 완료 후 회원가입 창 닫기
                new LoginPage().setVisible(true);
            }
        });

        setVisible(true);
    }
}

