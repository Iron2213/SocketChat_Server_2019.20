import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private JTextArea mLiveChat;
    private JTextArea mInputArea;
    private JButton mBtnSend;
    private JList mUsersList;

    private ConnectionListener mConnectionListener;

    private List<ConnectedClient> mUsers;
    private DefaultListModel<String> mUsersNames;

    private int mSelectedID = -1;

    private final int GAP = 10;
    private final int WIDTH = 80;
    private final int HEIGHT = 60;

    public Server() {
        Color darkIntensity_1 = new Color(66, 66, 66);
        Color darkIntensity_2 = new Color(48, 48, 48);
        Color darkIntensity_3 = new Color(33, 33, 33);
        Color darkIntensity_4 = new Color(0, 0, 0);

        // Arrays/Lists
        mUsers = new ArrayList<>();
        mUsersNames = new DefaultListModel<>();

        // LiveChat panel
        JPanel liveChatPanel = new JPanel();
        liveChatPanel.setBackground(darkIntensity_2);

        // Layout for the LiveChat panel
        GroupLayout layoutLiveChat = new GroupLayout(liveChatPanel);
        liveChatPanel.setLayout(layoutLiveChat);

        // AdminControls panel
        JPanel adminControls = new JPanel();
        adminControls.setBackground(darkIntensity_2);

        // Layout for the AdminControls panel
        GroupLayout layoutAdminControls = new GroupLayout(adminControls);
        adminControls.setLayout(layoutAdminControls);

        // LiveChat top text label
        JLabel liveChatTopLabel = new JLabel("Live chat");
        liveChatTopLabel.setForeground(Color.white);

        // AdminControls top text label
        JLabel adminControlsTopLabel = new JLabel("Connected users");
        adminControlsTopLabel.setForeground(Color.white);

        // Kick button
        JButton btnKick = new JButton("Kick");
        btnKick.setBackground(darkIntensity_3);
        btnKick.setBorder(BorderFactory.createLineBorder(darkIntensity_3, 1));
        btnKick.setForeground(Color.white);
        btnKick.setFocusPainted(false);
        btnKick.setEnabled(false);
        btnKick.addActionListener(actionEvent -> {
            if (mSelectedID > -1) {
                for (ConnectedClient a : mUsers) {
                    if (a.getID() == mSelectedID) {
                        a.getThread().kickClient();
                        break;
                    }
                }
            }
        });

        // Send button
        mBtnSend = new JButton("Send");
        mBtnSend.setBackground(darkIntensity_3);
        mBtnSend.setBorder(BorderFactory.createLineBorder(darkIntensity_3, 1));
        mBtnSend.setForeground(Color.white);
        mBtnSend.setFocusPainted(false);
        mBtnSend.setEnabled(false);
        mBtnSend.addActionListener(actionEvent -> {
            String text = mInputArea.getText();
            System.out.println(text);
            sendToAllClients("SERVER", text);
            appendLiveChatText("You", text);
            mInputArea.setText("");
            mBtnSend.setEnabled(false);
        });

        // JList that shows currently connected users
        mUsersList = new JList(mUsersNames);
        mUsersList.setBackground(darkIntensity_1);
        mUsersList.setForeground(Color.white);
        mUsersList.setFont(new Font("Arial", Font.BOLD, 20));
        mUsersList.addListSelectionListener(listSelectionEvent -> {

            if (listSelectionEvent.getValueIsAdjusting()) {
                btnKick.setEnabled(true);
                String value = mUsersList.getSelectedValue().toString();

                String ID = "";

                for (int i = 2; i < value.length(); i++) {
                    if (value.charAt(i) != ' ') {
                        ID += value.charAt(i);
                    }
                    else {
                        break;
                    }
                }

                mSelectedID = Integer.parseInt(ID);
            }

        });

        // Main text area that shows the live chat
        mLiveChat = new JTextArea();
        mLiveChat.setBackground(darkIntensity_1);
        mLiveChat.setForeground(Color.white);
        mLiveChat.setLineWrap(true);
        mLiveChat.setWrapStyleWord(true);
        mLiveChat.setFont(new Font("Arial", Font.PLAIN, 15));
        mLiveChat.setEditable(false);

        // Text area for the users input
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
        JScrollPane scrollPane = new JScrollPane(mLiveChat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

        //
        JSeparator separator1 = new JSeparator();
        separator1.setBackground(darkIntensity_1);
        separator1.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

        //
        JSeparator separator2 = new JSeparator();
        separator2.setBackground(darkIntensity_1);
        separator2.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

        //
        layoutLiveChat.setHorizontalGroup(layoutLiveChat.createParallelGroup()
                .addGroup(layoutLiveChat.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(adminControlsTopLabel))
                .addGroup(layoutLiveChat.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(scrollPane)
                        .addGap(GAP))
                .addGroup(layoutLiveChat.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(separator1)
                        .addGap(GAP))
                .addGroup(layoutLiveChat.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(mInputArea, 100, 100, Short.MAX_VALUE)
                        .addGap(GAP)
                        .addComponent(mBtnSend, WIDTH, WIDTH, WIDTH)
                        .addGap(GAP))
        );
        layoutLiveChat.setVerticalGroup(layoutLiveChat.createSequentialGroup()
                .addGap(GAP)
                .addComponent(adminControlsTopLabel)
                .addGap(GAP)
                .addComponent(scrollPane)
                .addGap(GAP)
                .addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(GAP)
                .addGroup(layoutLiveChat.createParallelGroup()
                        .addComponent(mInputArea, HEIGHT, HEIGHT, HEIGHT)
                        .addComponent(mBtnSend, HEIGHT, HEIGHT, HEIGHT))
                .addGap(GAP)
        );

        //
        layoutAdminControls.setHorizontalGroup(layoutAdminControls.createParallelGroup()
                .addGroup(layoutAdminControls.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(adminControlsTopLabel)
                        .addGap(GAP))
                .addGroup(layoutAdminControls.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(mUsersList, 100, 100, Short.MAX_VALUE)
                        .addGap(GAP))
                .addGroup(layoutAdminControls.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(separator2)
                        .addGap(GAP))
                .addGroup(layoutAdminControls.createSequentialGroup()
                        .addGap(GAP)
                        .addComponent(btnKick, WIDTH, WIDTH, WIDTH)
                        .addGap(GAP))
        );
        layoutAdminControls.setVerticalGroup(layoutAdminControls.createSequentialGroup()
                .addGap(GAP)
                .addComponent(adminControlsTopLabel)
                .addGap(GAP)
                .addComponent(mUsersList, 100, 100, Short.MAX_VALUE)
                .addGap(GAP)
                .addComponent(separator2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(GAP)
                .addComponent(btnKick, HEIGHT, HEIGHT, HEIGHT)
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

    /**
     * This method starts the thread that listens to client connection requests
     */
    public void start() {
        mConnectionListener = new ConnectionListener(this);
        mConnectionListener.start();
    }

    /**
     * This method adds a message in the main chat
     *
     * @param user The user that sent the message
     * @param text The text to add in the chat
     */
    public void appendLiveChatText(String user, String text) {
        String newText = String.format("<%s>: %s\n", user, text);
        mLiveChat.append(newText);
        mLiveChat.setCaretPosition(mLiveChat.getDocument().getLength());
    }

    /**
     * This method adds a ConnectedClient item to the lists of currently connected users
     *
     * @param connectedClient The ConnectedClient item created by the thread that manages it
     */
    public void addConnectedClient(ConnectedClient connectedClient) {
        mUsers.add(connectedClient);
        mUsersNames.addElement("> " + connectedClient.getID() + " - " + connectedClient.getUsername());
    }

    /**
     * This method removes the current ConnectedClient item from the connected users list
     *
     * @param connectedClient The item to remove from the list
     */
    public void removeConnectedClient(ConnectedClient connectedClient) {
        mUsers.remove(connectedClient);
        mUsersNames.removeElement("> " + connectedClient.getID() + " - " + connectedClient.getUsername());
    }

    /**
     * This method sends a message to every connected user except for the passed one
     *
     * @param user   The user that sent the message
     * @param text   The message to send
     * @param client The user that do not have to receive the message
     */
    public void sendToAllClients(String user, String text, ConnectedClient client) {
        for (ConnectedClient User : mUsers) {
            if (User != client)
                User.getThread().sendToClient(user, text);
        }
    }

    /**
     * This method sends a message to every connected user
     *
     * @param user The user that sent the message
     * @param text The message to send
     */
    public void sendToAllClients(String user, String text) {
        for (ConnectedClient User : mUsers) {
            User.getThread().sendToClient(user, text);
        }
    }
}