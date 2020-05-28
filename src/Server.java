import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server {
	private static final int GAP = 10;
	private static final int WIDTH = 80;
	private static final int HEIGHT = 60;

	private JTextArea mLiveChat;
	private JTextArea mInputArea;
	private JList mUsersList;
	private ConnectionListener mConnectionListener;
	private List<ConnectedClient> mUsers;
	private DefaultListModel<String> mUsersNames;
	private UUID mSelectedID;

	public Server() {
		Color darkIntensity_1 = new Color(66, 66, 66);
		Color darkIntensity_2 = new Color(48, 48, 48);
		Color darkIntensity_3 = new Color(33, 33, 33);
		//Color darkIntensity_4 = new Color(0, 0, 0);

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
			if (mSelectedID != null) {
				for (ConnectedClient User : mUsers) {
					if (User.getID() == mSelectedID) {
						User.getThread().kickClient();
						removeConnectedClient(User);

						if (mUsersNames.size() < 1) {
							btnKick.setEnabled(false);
						}

						break;
					}
				}
			}
		});

		// Open server button
		JButton btnOpenServer = new JButton("Open server");
		btnOpenServer.setBackground(darkIntensity_3);
		btnOpenServer.setForeground(Color.white);
		btnOpenServer.setFocusPainted(false);
		btnOpenServer.setBorder(BorderFactory.createLineBorder(darkIntensity_3, 1));
		btnOpenServer.addActionListener(actionEvent -> {
			if (!mConnectionListener.isStarted()) {
				mConnectionListener = null;
				appendLiveChatText("You", "The server has been open");
				start();
			}
		});

		// Close server button
		JButton btnCloseServer = new JButton("Close server");
		btnCloseServer.setBackground(darkIntensity_3);
		btnCloseServer.setForeground(Color.white);
		btnCloseServer.setFocusPainted(false);
		btnCloseServer.setBorder(BorderFactory.createLineBorder(darkIntensity_3, 1));
		btnCloseServer.addActionListener(actionEvent -> {
			appendLiveChatText("You", "The server has been closed, any further connection requests will be ignored");
			kickAllClients();
			mConnectionListener.stopServerSocket();
		});

		// Send button
		JButton btnSend = new JButton("Send");
		btnSend.setBackground(darkIntensity_3);
		btnSend.setBorder(BorderFactory.createLineBorder(darkIntensity_3, 1));
		btnSend.setForeground(Color.white);
		btnSend.setFocusPainted(false);
		btnSend.setEnabled(false);
		btnSend.addActionListener(actionEvent -> {
			String rawText = mInputArea.getText();

			sendToAllClients("SERVER", rawText);
			appendLiveChatText("You", rawText);

			mInputArea.setText("");
			btnSend.setEnabled(false);
		});

		// JList that shows currently connected users
		mUsersList = new JList(mUsersNames);
		mUsersList.setBackground(darkIntensity_1);
		mUsersList.setForeground(Color.white);
		mUsersList.setFont(new Font("Arial", Font.BOLD, 20));
		mUsersList.addListSelectionListener(listSelectionEvent -> {

			if(!listSelectionEvent.getValueIsAdjusting()) {
				int currentIndex = mUsersList.getSelectedIndex();
				if (currentIndex > -1) {
					btnKick.setEnabled(true);
					ConnectedClient selectUser = mUsers.get(currentIndex);
					mSelectedID = selectUser.getID();
				}
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
					if (!btnSend.isEnabled()) {
						btnSend.setEnabled(true);
					}
				}
				else {
					if (btnSend.isEnabled()) {
						btnSend.setEnabled(false);
					}
				}
			}
		});

		//
		JScrollPane scrollPaneLiveChat = new JScrollPane(mLiveChat, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneLiveChat.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

		//
		JScrollPane scrollPaneInputArea = new JScrollPane(mInputArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPaneInputArea.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

		//
		JSeparator separator1 = new JSeparator();
		separator1.setBackground(darkIntensity_1);
		separator1.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

		//
		JSeparator separator2 = new JSeparator();
		separator2.setBackground(darkIntensity_1);
		separator2.setBorder(BorderFactory.createLineBorder(darkIntensity_1, 1));

		// ********************************************************************************************************** //
		layoutLiveChat.setHorizontalGroup(layoutLiveChat.createParallelGroup()
				.addGroup(layoutLiveChat.createSequentialGroup()
						.addGap(GAP)
						.addComponent(adminControlsTopLabel))
				.addGroup(layoutLiveChat.createSequentialGroup()
						.addGap(GAP)
						.addComponent(scrollPaneLiveChat)
						.addGap(GAP))
				.addGroup(layoutLiveChat.createSequentialGroup()
						.addGap(GAP)
						.addComponent(separator1)
						.addGap(GAP))
				.addGroup(layoutLiveChat.createSequentialGroup()
						.addGap(GAP)
						.addComponent(scrollPaneInputArea, 100, 100, Short.MAX_VALUE)
						.addGap(GAP)
						.addComponent(btnSend, WIDTH, WIDTH, WIDTH)
						.addGap(GAP))
		);
		layoutLiveChat.setVerticalGroup(layoutLiveChat.createSequentialGroup()
				.addGap(GAP)
				.addComponent(adminControlsTopLabel)
				.addGap(GAP)
				.addComponent(scrollPaneLiveChat)
				.addGap(GAP)
				.addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(GAP)
				.addGroup(layoutLiveChat.createParallelGroup()
						.addComponent(scrollPaneInputArea, HEIGHT, HEIGHT, HEIGHT)
						.addComponent(btnSend, HEIGHT, HEIGHT, HEIGHT))
				.addGap(GAP)
		);

		// ********************************************************************************************************** //
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
						.addGap(GAP)
						.addComponent(btnOpenServer, WIDTH, WIDTH, WIDTH)
						.addGap(GAP)
						.addComponent(btnCloseServer, WIDTH, WIDTH, WIDTH)
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
				.addGroup(layoutAdminControls.createParallelGroup()
						.addComponent(btnKick, HEIGHT, HEIGHT, HEIGHT)
						.addGap(GAP)
						.addComponent(btnOpenServer, HEIGHT, HEIGHT, HEIGHT)
						.addGap(GAP)
						.addComponent(btnCloseServer, HEIGHT, HEIGHT, HEIGHT))
				.addGap(GAP)
		);

		// ********************************************************************************************************** //
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(darkIntensity_3);
		tabbedPane.setForeground(Color.white);
		tabbedPane.setBorder(BorderFactory.createLineBorder(darkIntensity_2));
		tabbedPane.addTab("Live chat", liveChatPanel);
		tabbedPane.addTab("Admin controls", adminControls);

		// ********************************************************************************************************** //
		JFrame frame = new JFrame("Server");
		frame.setPreferredSize(new Dimension(500, 500));
		frame.setResizable(true);
		frame.setLayout(new BorderLayout());
		frame.add(tabbedPane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowClosing(WindowEvent windowEvent) {
				mConnectionListener.stopServerSocket();
				System.exit(0);
			}

			@Override
			public void windowOpened(WindowEvent windowEvent) {

			}

			@Override
			public void windowClosed(WindowEvent windowEvent) {

			}

			@Override
			public void windowIconified(WindowEvent windowEvent) {

			}

			@Override
			public void windowDeiconified(WindowEvent windowEvent) {

			}

			@Override
			public void windowActivated(WindowEvent windowEvent) {

			}

			@Override
			public void windowDeactivated(WindowEvent windowEvent) {

			}
		});
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * This method starts the thread that listens to client connection requests
	 */
	public void start() {
		mConnectionListener = new ConnectionListener(this);
		mConnectionListener.start();
		appendLiveChatText("You", "The server has been open");
	}

	/**
	 * This method adds a message in the main chat
	 *
	 * @param user The user that sent the message
	 * @param text The text to add in the chat
	 */
	public void appendLiveChatText(String user, String text) {
		String newText = String.format("[ %s ]: %s\n", user, text);
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
		mUsersNames.addElement("> " + mUsers.size() + " - " + connectedClient.getUsername());
	}

	/**
	 * This method removes the current ConnectedClient item from the connected users list
	 *
	 * @param connectedClient The item to remove from the list
	 */
	public void removeConnectedClient(ConnectedClient connectedClient) {
		mUsersNames.removeElementAt(mUsersList.getSelectedIndex());
		mUsers.remove(connectedClient);
		mUsersList.updateUI();
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

	/**
	 * This methods disconnects all the clients currently connected
	 */
	public void kickAllClients() {
		for (ConnectedClient User : mUsers) {
			User.getThread().kickClient();
		}
	}

	/**
	 * This methods disconnects all the clients currently connected
	 *
	 * @param text The message to send
	 */
	public void kickAllClients(String text) {
		sendToAllClients("SERVER", text);
		kickAllClients();
	}
}