import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class Server {
    private JTextArea mMainChat;
    private JTextArea mInputArea;
    private JButton mBtnSend;
    private JList mUsersList;

    private ConnectionListener mConnectionListener;

    private DefaultListModel<ConnectedClient> mUsers;

    private final int GAP = 10;
    private final int WIDTH = 80;
    private final int HEIGHT = 60;

    public Server() {
        Color darkIntensity_1 = new Color(66, 66, 66);
        Color darkIntensity_2 = new Color(48, 48, 48);
        Color darkIntensity_3 = new Color(33, 33, 33);
        Color darkIntensity_4 = new Color(0, 0, 0);

        //
        mUsers = new DefaultListModel<ConnectedClient>();

        //
        mUsersList = new JList(mUsers);

        //
        JPanel liveChatPanel = new JPanel();
        liveChatPanel.setBackground(darkIntensity_2);

        //
        GroupLayout groupLayout = new GroupLayout(liveChatPanel);
        liveChatPanel.setLayout(groupLayout);

        //
        JPanel adminControls = new JPanel();
        adminControls.setBackground(darkIntensity_2);

        //
        JLabel topLabel = new JLabel("Live chat");
        topLabel.setForeground(Color.white);

        //
        mBtnSend = new JButton("Send");
        mBtnSend.setBackground(darkIntensity_3);
        mBtnSend.setBorder(BorderFactory.createLineBorder(darkIntensity_3, 1));
        mBtnSend.setForeground(Color.white);
        mBtnSend.setFocusPainted(false);
        mBtnSend.setEnabled(false);
        mBtnSend.addActionListener(actionEvent -> {
            sendToAllClients("SERVER", mInputArea.getText());
            appendLiveChatText("You", mInputArea.getText());
            mInputArea.setText("");
            mBtnSend.setEnabled(false);
        });

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
        mInputArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                checkText();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                checkText();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                checkText();
            }

            public void checkText() {
                if (!mInputArea.getText().trim().equals("")) {
                    if (!mBtnSend.isEnabled()) {
                        mBtnSend.setEnabled(true);
                    }
                }
                else {
                    if (mBtnSend.isEnabled()) {
                        mBtnSend.setEnabled(false);
                    }
                }
            }
        });

        //
        JScrollPane scrollPane = new JScrollPane(mMainChat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

        //
        JSeparator separator = new JSeparator();
        separator.setBackground(darkIntensity_1);
        separator.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

        //
        groupLayout.setHorizontalGroup(groupLayout.createParallelGroup()
                .addGroup(groupLayout.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(topLabel))
                .addGroup(groupLayout.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(scrollPane)
                        .addGap(GAP))
                .addComponent(separator)
                .addGroup(groupLayout.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(mInputArea, 100, 100, Short.MAX_VALUE)
                        .addGap(GAP)
                        .addComponent(mBtnSend, WIDTH, WIDTH, WIDTH)
                        .addGap(GAP))
        );
        groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
                .addGap(GAP)
                .addComponent(topLabel)
                .addGap(GAP)
                .addComponent(scrollPane)
                .addGap(GAP)
                .addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(GAP)
                .addGroup(groupLayout.createParallelGroup()
                        .addComponent(mInputArea, HEIGHT, HEIGHT, HEIGHT)
                        .addComponent(mBtnSend, HEIGHT, HEIGHT, HEIGHT))
                .addGap(GAP)
        );

        //
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(darkIntensity_3);
        tabbedPane.setForeground(Color.white);
        tabbedPane.setBorder(BorderFactory.createLineBorder(darkIntensity_2));
        tabbedPane.addTab("Live chat", liveChatPanel);
        tabbedPane.addTab("Admin controls", adminControls);

        //
        JFrame frame = new JFrame("Server");
        frame.setPreferredSize(new Dimension(500, 500));
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    public void start() {
        mConnectionListener = new ConnectionListener(this);
        mConnectionListener.start();
    }

    public void appendLiveChatText(String username, String text) {
        String newText = String.format("<%s>: %s\n", username, text);
        mMainChat.append(newText);
        mMainChat.setCaretPosition(mMainChat.getDocument().getLength());
    }

    public void addConnectedClient(ConnectedClient connectedClient) {
        mUsers.addElement(connectedClient);
    }

    public void removeConnectedClient(ConnectedClient connectedClient) {
        mUsers.removeElement(connectedClient);
    }

    public void sendToAllClients(String user, String text, ConnectedClient client) {
        for (int i = 0; i < mUsers.size(); i++) {
            if (mUsers.elementAt(i) != client)
                mUsers.elementAt(i).getThread().sendToClient(user, text);
        }
    }

    public void sendToAllClients(String user, String text) {
        for (int i = 0; i < mUsers.size(); i++) {
            mUsers.elementAt(i).getThread().sendToClient(user, text);
        }
    }
}