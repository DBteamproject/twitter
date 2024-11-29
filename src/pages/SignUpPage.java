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
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 회원가입 패널 생성
        JPanel signUpPanel = new JPanel();
        signUpPanel.setLayout(new BoxLayout(signUpPanel, BoxLayout.Y_AXIS));
        signUpPanel.setBackground(Color.WHITE);

        // 회원가입 제목
        JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("돋움", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 중앙 입력 필드 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // User ID 필드
        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userIdField = new JTextField();
        userIdField.setMaximumSize(new Dimension(300, 30));
        userIdField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password 필드
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password 재입력 필드
        JLabel passwordReLabel = new JLabel("Re-enter Password:");
        passwordReLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordReField = new JPasswordField();
        passwordReField.setMaximumSize(new Dimension(300, 30));
        passwordReField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User Name 필드
        JLabel userNameLabel = new JLabel("User name:");
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userNameField = new JTextField();
        userNameField.setMaximumSize(new Dimension(300, 30));
        userNameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Introduce 필드
        JLabel introduceLabel = new JLabel("Introduce:");
        introduceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        introduceArea = new JTextArea(3, 20);
        introduceArea.setLineWrap(true);
        introduceArea.setWrapStyleWord(true);
        JScrollPane introduceScrollPane = new JScrollPane(introduceArea);
        introduceScrollPane.setMaximumSize(new Dimension(300, 100));
        introduceScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 프로필 이미지 업로드
        JLabel profileImageTitleLabel = new JLabel("Profile Image:");
        profileImageTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton uploadButton = new JButton("Image Upload");
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        profileImageLabel = new JLabel();
        profileImageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        // 회원가입 버튼
        JButton signUpButton = new JButton("SignUp");
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 실제 회원가입 로직 추가
                String userId = userIdField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String passwordRe = new String(passwordReField.getPassword()).trim();
                String userName = userNameField.getText().trim();
                String introduce = introduceArea.getText().trim();

                if (userId.isEmpty() || password.isEmpty() || passwordRe.isEmpty() || userName.isEmpty() || introduce.isEmpty()) {
                    JOptionPane.showMessageDialog(SignUpPage.this, "Please fill out all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(passwordRe)) {
                    JOptionPane.showMessageDialog(SignUpPage.this, "Password doesn't match.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String path = null;
                if (selectedImageFile != null) {
                    String fileName = selectedImageFile.getName();
                    String extension = fileName.substring(fileName.lastIndexOf('.'));
                    String directoryPath = "src/resources/profiles";
                    Path targetPath = Paths.get(directoryPath, "user_" + userId + extension);
                    try {
                        Files.copy(selectedImageFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
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

                    JOptionPane.showMessageDialog(SignUpPage.this, "SignUp Success!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // 가입 완료 후 회원가입 창 닫기
                    new LoginPage().setVisible(true);
                } catch (SQLException e2) {
                    JOptionPane.showMessageDialog(SignUpPage.this, e2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    if (con != null) {
                        DatabaseConnection.closeConnection(con);
                    }
                }
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
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(passwordReLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(passwordReField);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(userNameLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(userNameField);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(introduceLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(introduceScrollPane);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(profileImageTitleLabel);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(uploadButton);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(profileImageLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(signUpButton);

        // 입력 필드가 좌우에 붙지 않도록 패딩 추가
        JPanel paddedPanel = new JPanel();
        paddedPanel.setLayout(new BoxLayout(paddedPanel, BoxLayout.X_AXIS));
        paddedPanel.setBackground(Color.WHITE);
        paddedPanel.add(Box.createHorizontalGlue()); // 왼쪽 여백
        paddedPanel.add(centerPanel); // 입력 필드
        paddedPanel.add(Box.createHorizontalGlue()); // 오른쪽 여백

        // 패널 배치
        signUpPanel.add(Box.createVerticalStrut(50)); // 상단 여백
        signUpPanel.add(titleLabel);
        signUpPanel.add(Box.createVerticalStrut(20));
        signUpPanel.add(paddedPanel);

        add(signUpPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}

