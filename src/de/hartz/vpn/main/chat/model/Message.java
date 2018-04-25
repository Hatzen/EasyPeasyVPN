package de.hartz.vpn.main.chat.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kaiha on 25.04.2018.
 */
public class Message implements Serializable {
    private User from;
    private String text;
    private Date date;

    private static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm");

    public Message(User from, String text, Date date) {
        this.from = from;
        this.text = text;
        this.date = date;
    }

    public String getReprensation() {
        return "[" + sdf.format(date) + "] " + from.getName() + ":" + text + "\n";
    }
}
