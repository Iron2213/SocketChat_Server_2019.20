public class ConnectedClient {
    private String mUser;
    private ConnectionListener.ClientThread mThread;

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
