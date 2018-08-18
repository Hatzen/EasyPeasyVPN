package de.hartz.vpn.main;

import de.hartz.vpn.main.server.ConfigState;
import de.hartz.vpn.mediation.Mediator;

import java.io.*;
import java.util.ArrayList;

import static de.hartz.vpn.utilities.Constants.USER_DATA_FILE_PATH;

/**
 * Hold the data for the current session of this user.
 */
public class UserData {
    private static UserData instance;
    private static Data data;

    /**
     * Returns the one and only {@link UserData} object. If one is saved it will loaded.
     * @returns the instance.
     */
    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
            loadUserData();
        }
        return instance;
    }

    /**
     * @returns a list of all active users in the vpn.
     */
    public UserList getUserList() {
        return data.userList;
    }

    public ArrayList<Mediator> getMediatorList() {
        return data.mediatorList;
    }

    /**
     * Indicates whether this configuration is server or client one.
     * @returns true if its a client installation.
     */
    public boolean isClientInstallation() {
        return data.clientInstallation;
    }

    public void setClientInstallation(boolean clientInstallation) {
        data.clientInstallation = clientInstallation;
    }

    /**
     * Sets the current vpn config state and saves it persistent.
     */
    public ConfigState getVpnConfigState() {
        return data.vpnConfigState;
    }

    /**
     * Sets the current vpn config state and saves it persistent.
     */
    public void setVpnConfigState(ConfigState configState) {
        data.vpnConfigState = configState;
        writeUserData();
    }

    private UserData() {
        data = new Data();
        data.clientInstallation = true;
        data.userList = new UserList();
        data.mediatorList = new ArrayList<>();
        //mediatorList.add(new Mediator("DEFAULT","192.168.2.214", -1, -1, false));
        data.mediatorList.add(new Mediator("DEFAULT","http://hartzkai.freehostia.com/thesis/", -1, -1, true));
    }

    /**
     * Loads an old user data object.
     * @returns true if the data was loaded successfully.
     */
    private static boolean loadUserData() {
        try {
            FileInputStream fin = new FileInputStream(USER_DATA_FILE_PATH);
            ObjectInputStream ois = new ObjectInputStream(fin);
            data = (Data) ois.readObject();
            return  true;
        } catch (Exception e) {
            // TODO: Check for data version.
            System.out.println("Data not loaded. File does not exist (?)");
            if (new File(USER_DATA_FILE_PATH).exists())
                e.printStackTrace();
        }
        return false;
    }

    /**
     * Saves the current user data object persistent.
     * @returns true if the data was saved successfully.
     */
    public boolean writeUserData() {
        try {
            deleteTempData();
            File configFile = new File(USER_DATA_FILE_PATH);
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
            FileOutputStream fout = new FileOutputStream(configFile);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(data);
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
        data.userList.clear();
    }

    public String getServerIp() {
        return data.serverIp;
    }

    public Integer getServerPort() {
        return data.serverPort;
    }

    public void setServerIp(String ip) {
        data.serverIp = ip;
    }

    public void setServerPort(Integer port) {
        data.serverPort = port;
    }
}
