import java.util.UUID;

public class ConnectedClient {
	private UUID mID;
	private String mUser;
	private ConnectionListener.ClientThread mThread;

	public ConnectedClient() {
		mID = UUID.randomUUID();
	}

	public UUID getID() {
		return mID;
	}

	public void setUsername(String user) {
		mUser = user;
	}

	public String getUsername() {
		return mUser;
	}

	public void setThread(ConnectionListener.ClientThread thread) {
		mThread = thread;
	}

	public ConnectionListener.ClientThread getThread() {
		return mThread;
	}
}
