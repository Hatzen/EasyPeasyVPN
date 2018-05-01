package de.hartz.vpn.main;

import de.hartz.vpn.main.server.ConfigState;
import de.hartz.vpn.mediation.Mediator;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kaiha on 26.04.2018.
 */

// Encapsulate data in own class. So it is readable after changing code.
public class Data implements Serializable {
    int invalidate;

    UserList userList;
    ArrayList<Mediator> mediatorList;

    //client only.
    String serverIp;
    Integer serverPort;
    //END OF: client only.

    boolean clientInstallation;
    ConfigState vpnConfigState;

}