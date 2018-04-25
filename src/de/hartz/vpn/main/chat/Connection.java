package de.hartz.vpn.main.chat;

import de.hartz.vpn.main.chat.model.Message;
import de.hartz.vpn.main.chat.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by kaiha on 25.04.2018.
 */
public class Connection {

    class InputHandler extends Thread {
        private ObjectInputStream ois;

        public InputHandler(Socket socket) throws IOException {
            ois = new ObjectInputStream( socket.getInputStream() );
            start();
        }

        public void run() {
            while (true) {
                try {
                     user.addMessage( (Message) ois.readObject() );
                } catch (ClassNotFoundException e) {
                    System.err.println("Cannot happen!");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private User user;
    private Socket socket;

    public Connection(User user, Socket socket) {
        try {
            new InputHandler(socket);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Doesnt make sense in constructor.
            user.setConnection(null);
        }
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
        user.addMessage( message );
        oos.flush();
    }




}
