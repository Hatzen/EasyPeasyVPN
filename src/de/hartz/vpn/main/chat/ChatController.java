package de.hartz.vpn.main.chat;

import de.hartz.vpn.main.chat.model.Message;
import de.hartz.vpn.main.chat.model.User;
import de.hartz.vpn.main.chat.view.ChatFrame;
import de.hartz.vpn.utilities.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by kaiha on 25.04.2018.
 */
public class ChatController {

    class ConnectionListener extends Thread {

        @Override
        public void run() {
            boolean run = true;
            try {
                serverSocket = new ServerSocket(Constants.CHAT_SERVER_PORT);
                while (run) {
                    System.out.println("Waiting for Chats...");
                    try {
                        users.add( getUser(serverSocket.accept()) );
                    } catch (SocketException e) {
                        System.err.println("Error while listening");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (serverSocket != null) try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private User getUser(Socket socket) {
            User user = new User(socket.getInetAddress().getHostAddress(), null);
            user.setConnection(new Connection(user, socket));

            return user;
        }
    }

    private ServerSocket serverSocket;
    private ArrayList<User> users;

    public ChatController() {
        users = new ArrayList<>();
        new ConnectionListener().start();
    }

    public boolean chatWith(String ip) {
        try {
            User user = getUserByIp(ip);
            if ( user == null ) {
                user = new User(ip, "");
                users.add(user);
            }

            if (user.getConnection() != null) {
                Socket socket = new Socket(user.getIp(), Constants.CHAT_SERVER_PORT);
                user.setConnection(new Connection(user, socket));
            }
            new ChatFrame(user, this).setVisible(true);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendMessage(String text, User destination) {
        destination.addMessage(new Message(getOwnUser(), text, Calendar.getInstance().getTime()));
    }

    public User getOwnUser() {
        // TODO: Get the real own user object.
        return new User("192.168.2.111", "banan");
    }

    public User getUserByIp(String ip) {
        for (User user: users) {
            if(user.getIp().equals(ip)) {
                return user;
            }
        }
        return null;
    }
}
