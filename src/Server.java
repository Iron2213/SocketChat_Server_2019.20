import javax.swing.*;
import java.awt.*;

public class Server {
    private JTextArea mMainChat;
    private JTextArea mInputArea;
    private JButton mBtnSend;

    private final int GAP = 10;
    private final int WIDTH = 80;
    private final int HEIGHT = 60;

    public Server() {

        Color darkIntensity_1 = new Color(66, 66, 66);
        Color darkIntensity_2 = new Color(48, 48, 48);
        Color darkIntensity_3 = new Color(33, 33, 33);
        Color darkIntensity_4 = new Color(0, 0, 0);

        //
        JPanel mLiveChatPanel = new JPanel();
        mLiveChatPanel.setBackground(darkIntensity_2);

        //
        GroupLayout mGroupLayout = new GroupLayout(mLiveChatPanel);
        mLiveChatPanel.setLayout(mGroupLayout);

        //
        JPanel mAdminControls = new JPanel();
        mAdminControls.setBackground(darkIntensity_2);

        //
        JLabel mTopLabel = new JLabel("Live chat");
        mTopLabel.setForeground(Color.white);

        //
        mBtnSend = new JButton("Send");
        mBtnSend.setBackground(darkIntensity_3);
        mBtnSend.setBorder(BorderFactory.createLineBorder(darkIntensity_3, 1));
        mBtnSend.setForeground(Color.white);
        mBtnSend.setFocusPainted(false);

        //
        mMainChat = new JTextArea();
        mMainChat.setBackground(darkIntensity_1);
        mMainChat.setForeground(Color.white);
        mMainChat.setLineWrap(true);
        mMainChat.setWrapStyleWord(true);

        //
        mInputArea = new JTextArea();
        mInputArea.setBackground(darkIntensity_1);
        mInputArea.setForeground(Color.white);
        mInputArea.setLineWrap(true);
        mInputArea.setWrapStyleWord(true);

        //
        JScrollPane mScrollPane = new JScrollPane(mMainChat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mScrollPane.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

        //
        JSeparator mSeparator = new JSeparator();
        mSeparator.setBackground(darkIntensity_1);
        mSeparator.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

        //
        mGroupLayout.setHorizontalGroup(mGroupLayout.createParallelGroup()
                .addGroup(mGroupLayout.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(mTopLabel))
                .addGroup(mGroupLayout.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(mScrollPane)
                        .addGap(GAP))
                .addComponent(mSeparator)
                .addGroup(mGroupLayout.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(mInputArea, 100, 100, Short.MAX_VALUE)
                        .addGap(GAP)
                        .addComponent(mBtnSend, WIDTH, WIDTH, WIDTH)
                        .addGap(GAP))
        );
        mGroupLayout.setVerticalGroup(mGroupLayout.createSequentialGroup()
                .addGap(GAP)
                .addComponent(mTopLabel)
                .addGap(GAP)
                .addComponent(mScrollPane)
                .addGap(GAP)
                .addComponent(mSeparator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(GAP)
                .addGroup(mGroupLayout.createParallelGroup()
                        .addComponent(mInputArea, HEIGHT, HEIGHT, HEIGHT)
                        .addComponent(mBtnSend, HEIGHT, HEIGHT, HEIGHT))
                .addGap(GAP)
        );


        //
        JTabbedPane mTabbedPane = new JTabbedPane();
        mTabbedPane.setBackground(darkIntensity_3);
        mTabbedPane.setForeground(Color.white);
        mTabbedPane.setBorder(BorderFactory.createLineBorder(darkIntensity_2));
        mTabbedPane.addTab("Live chat", mLiveChatPanel);
        mTabbedPane.addTab("Admin controls", mAdminControls);

        JFrame frame = new JFrame("Server");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.add(mTabbedPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public void appendLiveChatText(String username, String text) {
        String newText = String.format("<%s>: %s\n", username, text);
        mMainChat.append(newText);
    }
}
