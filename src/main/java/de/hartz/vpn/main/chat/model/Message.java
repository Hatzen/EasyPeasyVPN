package de.hartz.vpn.main.chat.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Model class to wrap a chat message.
 */
public class Message implements Serializable {
    private User from;
    private String text;
    private Date date;
    public transient boolean ownMessage;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm");

    public Message(User from, String text, Date date, boolean ownMessage) {
        this.from = from;
        this.text = text;
        this.date = date;
        this.ownMessage = ownMessage;
    }

    public String getRepresentation() {
        String representation ="[" + sdf.format(date) + "] " + from.getName() + ":" + text + "\n";
        if (ownMessage) {
            representation = "***SEND***" + representation;
        }
        return representation;
    }
}
