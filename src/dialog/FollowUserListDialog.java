package dialog;

import dto.MemberDto;
import pages.ProfilePage;
import pages.TwitterMainPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FollowUserListDialog extends JDialog {

    public FollowUserListDialog(Frame owner, String title, List<MemberDto> userList, TwitterMainPage mainPage, String userId) {
        super(owner, title, true);
        setSize(300, 400);
        setLayout(new BorderLayout());

        // 리스트 패널 생성
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(listPanel);

        // 사용자 목록 추가
        for (MemberDto dto : userList) {
            JPanel userPanel = new JPanel();
            userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            userPanel.setBackground(Color.WHITE);

            // 프로필 이미지 추가
            JLabel profileImageLabel = new JLabel();
            String profileImagePath = (dto.getProfileImage() != null && !dto.getProfileImage().isEmpty()) ? dto.getProfileImage() : "src/resources/profile.png";
            ImageIcon profileIcon = new ImageIcon(profileImagePath);
            Image scaledProfileImage = profileIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            profileImageLabel.setIcon(new ImageIcon(scaledProfileImage));

            profileImageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mainPage.showPage(new ProfilePage(mainPage, dto.getUserId(), userId));
                    dispose();
                }
            });

            // 사용자 정보 텍스트 추가
            JPanel userInfoPanel = new JPanel();
            userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
            userInfoPanel.setBackground(Color.WHITE);

            JLabel userNameLabel = new JLabel("<html><b>" + dto.getUserName() + "</b></html>");
            JLabel userIdLabel = new JLabel("<html><span style='color:gray;'>@" + dto.getUserId() + "</span></html>");

            userInfoPanel.add(userNameLabel);
            userInfoPanel.add(userIdLabel);

            // 패널에 프로필 이미지와 사용자 정보 추가 (FlowLayout 사용)
            userPanel.add(profileImageLabel);
            userPanel.add(userInfoPanel);

            // userPanel의 최대 크기를 설정하여 다이얼로그를 가득 채우지 않도록 설정
            userPanel.setMaximumSize(new Dimension(300, userPanel.getPreferredSize().height));

            // 사용자 패널을 리스트 패널에 추가
            listPanel.add(userPanel);
        }

        // 스크롤 패널 설정
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // 다이얼로그에 스크롤 패널 추가
        add(scrollPane, BorderLayout.CENTER);

        // 닫기 버튼
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        add(closeButton, BorderLayout.SOUTH);

        // 다이얼로그를 중앙에 배치
        setLocationRelativeTo(owner);
    }
}
