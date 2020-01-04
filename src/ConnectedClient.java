public class ConnectedClient {
    private static long NEXT_ID = 0;

    private long mID;
    private String mUser;
    private ConnectionListener.ClientThread mThread;

    public ConnectedClient() {
        mID = NEXT_ID++;
    }

    public long getID() {
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
