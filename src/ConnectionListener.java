import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListener extends Thread {

    private Server mServer;
    private ServerSocket mServerSocket;

    private final int PORT = 6789;

    public ConnectionListener(Server server) {
        mServer = server;

        try {
            mServerSocket = new ServerSocket(PORT);
        }
        catch (Exception e) {
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
    }

    public void stopServer() {
        try {
            mServerSocket.close();
        }
        catch (Exception e) {
            System.out.println("stopServer(): " + e.getMessage());
        }
    }

    class ClientThread extends Thread {

        private DataInputStream mInputStream;
        private DataOutputStream mOutputStream;
        private ConnectedClient mCurrentClient;

        public ClientThread(Socket clientSocket) {
            try {
                mInputStream = new DataInputStream(clientSocket.getInputStream());
                mOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            }
            catch (Exception ex) {
                // TODO:
                System.out.println("ClientThread(): " + ex.getMessage());
            }

            mCurrentClient = new ConnectedClient();

            mServer.addConnectedClient(mCurrentClient);

            try {
                mCurrentClient.setUsername(mInputStream.readUTF());
                mCurrentClient.setThread(this);
            }
            catch (IOException ex) {
                // TODO:
                System.out.println("ClientThread(): " + ex.getMessage());
            }
        }

        @Override
        public void run() {
            String text;
            while (true) { // TODO: temp
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
                    mServer.appendLiveChatText("You", mCurrentClient.getUsername() + " left the chat");
                    mServer.sendToAllClients("SERVER", mCurrentClient.getUsername() + " left the chat");
                    break;
                }
            }
        }

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
