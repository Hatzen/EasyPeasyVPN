package de.hartz.vpn.main.chat;

import de.hartz.vpn.main.chat.model.Message;

/**
 * Created by kaiha on 25.04.2018.
 */
public interface UserDataChangedListener {

    public void addedMessage(Message message);

    public void changedName(String name);

}
