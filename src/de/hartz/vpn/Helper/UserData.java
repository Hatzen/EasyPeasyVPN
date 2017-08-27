package de.hartz.vpn.Helper;

import de.hartz.vpn.MainApplication.Server.ConfigState;

import java.io.Serializable;

/**
 * Created by kaiha on 02.07.2017.
 */
public class UserData implements Serializable{

    // TODO: What about temp data holding?

    public static UserList userList = new UserList();


    // TODO: SAVE IT PERSISTENT!!

    //Client only.
    public static String serverIp;
    //END OF: Client only.

    public static boolean clientInstallation = true;
    public static ConfigState VPN_CONFIG_STATE;

    public static boolean isClientInstallation() {
        return clientInstallation;
    }

    /**
     * Sets the current vpn config state and saves it persistent.
     */
    public static ConfigState getVpnConfigState() {
        // TODO: load the configstate from hdd if not set yet. Or initalize a default..
        return VPN_CONFIG_STATE;
    }

    /**
     * Sets the current vpn config state and saves it persistent.
     */
    public static void setVpnConfigState(ConfigState configState) {
        // TODO: Save the configstate persistent.
        VPN_CONFIG_STATE = configState;
    }
}
