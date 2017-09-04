package de.hartz.vpn.MainApplication;

import de.hartz.vpn.MainApplication.Server.ConfigState;
import de.hartz.vpn.MediationServer.Mediator;

import java.io.*;
import java.util.ArrayList;

import static de.hartz.vpn.Helper.Constants.USER_DATA_FILE_PATH;

/**
 * Hold the data for the current session of this user.
 */
public class UserData implements Serializable{

    private static UserData instance;

    private UserList userList = new UserList();
    private ArrayList<Mediator> mediatorList = new ArrayList<>();

    //Client only.
    public static String serverIp;
    //END OF: Client only.

    private boolean clientInstallation = true;
    private ConfigState vpnConfigState;

    /**
     * Returns the one and only {@link UserData} object. If one is saved it will loaded.
     * @returns the instance.
     */
    public static UserData getInstance() {
        if (instance == null && !loadUserData()) {
            instance = new UserData();
        }
        return instance;
    }

    /**
     * @returns a list of all active users in the vpn.
     */
    public UserList getUserList() {
        return userList;
    }

    public ArrayList<Mediator> getMediatorList() {
        return mediatorList;
    }

    /**
     * Indicates whether this configuration is server or client one.
     * @returns true if its a client installation.
     */
    public boolean isClientInstallation() {
        return clientInstallation;
    }

    public void setClientInstallation(boolean clientInstallation) {
        this.clientInstallation = clientInstallation;
    }

    /**
     * Sets the current vpn config state and saves it persistent.
     */
    public ConfigState getVpnConfigState() {
        return vpnConfigState;
    }

    /**
     * Sets the current vpn config state and saves it persistent.
     */
    public void setVpnConfigState(ConfigState configState) {
        vpnConfigState = configState;
        writeUserData();
    }

    private UserData() {
        if (mediatorList.size() == 0) {
            mediatorList.add(new Mediator("DEFAULT","http://hartzkai.freehostia.com/thesis/", -1, -1, true));
        }
    }

    /**
     * Loads an old user data object.
     * @returns true if the data was loaded successfully.
     */
    private static boolean loadUserData() {
        try {
            FileInputStream fin = new FileInputStream(USER_DATA_FILE_PATH);
            ObjectInputStream ois = new ObjectInputStream(fin);
            instance = (UserData) ois.readObject();
            return  true;
        } catch (Exception e) {
            // TODO: Check for data version.
            System.out.println("UserData not loaded. File does not exist (?)");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Saves the current user data object persistent.
     * @returns true if the data was saved successfully.
     */
    private boolean writeUserData() {
        try {
            deleteTempData();
            File configFile = new File(USER_DATA_FILE_PATH);
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
            FileOutputStream fout = new FileOutputStream(configFile);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(instance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes all data that are cached but should not be persistent.
     */
    private void deleteTempData() {
        userList.clear();
    }
}
