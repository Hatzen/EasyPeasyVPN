package de.hartz.vpn.main.chat;

import de.hartz.vpn.main.chat.model.Message;
import de.hartz.vpn.main.chat.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Wrapper class that handles the communication with a vpn partner. It can parallel write and read messages from a socket.
 */
public class Connection {

    class InputHandler extends Thread {
        private ObjectInputStream ois;

        public void run() {
            try {
                ois = new ObjectInputStream( socket.getInputStream() );
                while (true) {
                    Message m = (Message) ois.readObject();
                    user.addMessage( m );
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            closeConnection();
        }
    }

    private User user;
    private Socket socket;

    private Connection(User user, Socket socket) {
        this.socket = socket;
        this.user = user;
        new InputHandler().start();
    }

    /**
     *
     * Needs to be synchronized to chat with yourself. Because user and its connection will be created for starting and
     * receiving connection.
     * @param user
     * @param socket
     * @return
     */
    public synchronized static Connection createConnection(User user, Socket socket) {
        Connection con = new Connection(user, socket);
        return con;
    }

    public void sendMessage(Message message) throws IOException {
        // TODO: Use encryption here too. But its not so important.
        /*NetworkUtilities.AdvancedEncryptionStandard aes = new NetworkUtilities.AdvancedEncryptionStandard();
        try {
            byteArray = aes.encrypt(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.reset();
        oos.flush();
        oos.writeObject(message);
        oos.flush();
    }

    public void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setConnection(null);
    }


}
