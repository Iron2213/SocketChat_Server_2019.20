import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener extends Thread {

	private Server mServer;
	private ServerSocket mServerSocket;

	public ConnectionListener(Server server) {
		mServer = server;

		try {
			mServerSocket = new ServerSocket(6789);
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public void run() {
		while (!mServerSocket.isClosed()) {
			try {
				Socket client = mServerSocket.accept();
				new ClientThread(client).start();
			}
			catch (Exception e) {
				System.out.println("ConnectionListener.run(): " + e.getMessage());
				break;
			}
		}
		return;
	}

	/**
	 * This method stops the ServerSocket and by so any further client connection
	 */
	public void stopServerSocket() {
		try {
			mServer.kickAllClients("The server has been closed");
			mServerSocket.close();
		}
		catch (Exception e) {
			System.out.println("stopServerSocket(): " + e.getMessage());
		}
	}

	class ClientThread extends Thread {

		private DataInputStream mInputStream;
		private DataOutputStream mOutputStream;
		private ConnectedClient mCurrentClient;
		private Socket mClientSocket;
		private String mMsg = " left the chat";

		public ClientThread(Socket clientSocket) {
			mClientSocket = clientSocket;
			try {
				mInputStream = new DataInputStream(clientSocket.getInputStream());
				mOutputStream = new DataOutputStream(clientSocket.getOutputStream());
			}
			catch (Exception ex) {
				// TODO:
				System.out.println("ClientThread(): " + ex.getMessage());
			}

			mCurrentClient = new ConnectedClient();

			try {
				mCurrentClient.setUsername(mInputStream.readUTF());
				mCurrentClient.setThread(this);

				mServer.addConnectedClient(mCurrentClient);

				mServer.sendToAllClients("SERVER", mCurrentClient.getUsername() + " joined the chat", mCurrentClient);
				mServer.appendLiveChatText("You", mCurrentClient.getUsername() + " joined the chat");
			}
			catch (IOException ex) {
				// TODO:
				System.out.println("ClientThread(): " + ex.getMessage());
			}
		}

		@Override
		public void run() {
			String text;

			while (!mClientSocket.isClosed()) {
				try {
					text = mInputStream.readUTF();

					mServer.appendLiveChatText(mCurrentClient.getUsername(), text);
					mServer.sendToAllClients(mCurrentClient.getUsername(), text, mCurrentClient);
				}
				catch (IOException ex) {
					// TODO:
					System.out.println("ClientThread.run(): " + ex.getMessage());
					mServer.removeConnectedClient(mCurrentClient);

					mCurrentClient.setThread(null);

					mServer.appendLiveChatText("You", mCurrentClient.getUsername() + mMsg);
					mServer.sendToAllClients("SERVER", mCurrentClient.getUsername() + mMsg);
					break;
				}
			}
		}

		/**
		 * This methods stop every connection with the current client
		 */
		public void kickClient() {
			try {
				mClientSocket.close();

				mMsg = " have been kicked from the chat";
			}
			catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		/**
		 * This method sends a message to the current client
		 *
		 * @param user The user that sent the message
		 * @param text The message to send
		 */
		public void sendToClient(String user, String text) {
			try {
				mOutputStream.writeUTF(user);
				mOutputStream.writeUTF(text);
			}
			catch (IOException ex) {
				// TODO:
				System.out.println("sendToClient(): " + ex.getMessage());
			}
		}
	}
}
