package listener;
import config.DatabaseConnection;
import dto.MemberDto;
import dto.MemberUpdateDto;
import pages.ProfilePage;
import pages.TwitterMainPage;
import repository.CommentRepository;
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


public class UserEditActionListener implements ActionListener {
    private Frame frame;
    private MemberDto memberDto;
    private TwitterMainPage mainPage;
    private String userId;

    public UserEditActionListener(Frame frame, MemberDto memberDto, TwitterMainPage mainPage, String userId) {
        this.frame = frame;
        this.memberDto = memberDto;
        this.mainPage = mainPage;
        this.userId = userId;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 다이얼로그 생성 (부모 컴포넌트를 기준으로 모달 다이얼로그 생성)
        JDialog dialog = new JDialog(frame, "Edit User", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Name 입력
        JLabel userNameLabel = new JLabel("User Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(userNameLabel, gbc);

        JTextField userNameField = new JTextField(20);
        userNameField.setText(memberDto.getUserName());
        gbc.gridx = 1;
        gbc.gridy = 0;
        dialog.add(userNameField, gbc);

        // Password 입력
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dialog.add(passwordField, gbc);

        // Introduce 입력
        JLabel introduceLabel = new JLabel("Introduce:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(introduceLabel, gbc);

        JTextArea introduceArea = new JTextArea(3, 20); // 3줄 입력 가능, 20열 너비
        introduceArea.setLineWrap(true); // 자동 줄 바꿈 설정
        introduceArea.setWrapStyleWord(true); // 단어 단위로 줄 바꿈
        JScrollPane introduceScrollPane = new JScrollPane(introduceArea); // 스크롤 가능하게
        introduceArea.setText(memberDto.getIntroduce());

        gbc.gridx = 1;
        gbc.gridy = 2;
        dialog.add(introduceScrollPane, gbc);

        // Profile Image 업로드 버튼
        JLabel profileImageLabel = new JLabel("Profile Image:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(profileImageLabel, gbc);

        JButton uploadButton = new JButton("Upload Image");
        gbc.gridx = 1;
        gbc.gridy = 3;
        dialog.add(uploadButton, gbc);

        // 업로드 버튼 클릭 시 파일 선택
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(dialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File image = fileChooser.getSelectedFile();
                    String fileName = image.getName();
                    String extension = fileName.substring(fileName.lastIndexOf('.'));
                    String directoryPath = "src/resources/profiles";
                    Path targetPath = Paths.get(directoryPath, "user_" + userId + extension); // 확장자를 포함한 새 파일 이름
                    try {
                        Files.copy(image.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING); // 파일 복사
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    String path = targetPath.toString();

                    Connection con = DatabaseConnection.getConnection();
                    MemberRepository memberRepository = new MemberRepository();
                    try {
                        memberRepository.updateProfileImageUser(path, userId, con);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                    DatabaseConnection.closeConnection(con);
                    JOptionPane.showMessageDialog(dialog, "Profile image updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainPage.showPage(new ProfilePage(mainPage, userId, userId));
                    dialog.dispose();  // 다이얼로그 닫기
                }
            }
        });

        // Save 버튼
        JButton saveButton = new JButton("Save");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(saveButton, gbc);

        // Save 버튼 클릭 시 동작
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 저장 작업 수행 (데이터 처리)
                String userName = userNameField.getText();
                String password = new String(passwordField.getPassword());
                String introduce = introduceArea.getText();

                Connection con = DatabaseConnection.getConnection();
                MemberRepository memberRepository = new MemberRepository();
                try {
                    memberRepository.updateUser(new MemberUpdateDto(password, userName, introduce), userId, con);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                DatabaseConnection.closeConnection(con);

                JOptionPane.showMessageDialog(dialog, "User updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                mainPage.showPage(new ProfilePage(mainPage, userId, userId));
                dialog.dispose();  // 다이얼로그 닫기
            }
        });

        dialog.setVisible(true);
    }
}
