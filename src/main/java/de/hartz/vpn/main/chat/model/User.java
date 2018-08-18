package de.hartz.vpn.main.chat.model;

import de.hartz.vpn.main.chat.Connection;
import de.hartz.vpn.main.chat.UserDataChangedListener;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Model class to wrap a chat user.
 */
public class User implements Serializable {
    private String ip;
    private String name;
    private transient Connection connection;
    private transient ArrayList<UserDataChangedListener> listeners;
    private ArrayList<Message> messages;

    public User(String ip, String name) {
        this.ip = ip;
        this.name = name;
        messages = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        for (UserDataChangedListener listener : listeners) {
            listener.addedMessage(message);
        }
        messages.add(message);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        for (UserDataChangedListener listener : listeners) {
            listener.changedName(name);
        }
        this.name = name;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void addListener(UserDataChangedListener listener) {
        listeners.add(listener);
    }

    public void removeListener(UserDataChangedListener listener) {
        listeners.remove(listener);
    }
}
