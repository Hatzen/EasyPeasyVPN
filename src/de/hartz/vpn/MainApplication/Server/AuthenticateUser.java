package de.hartz.vpn.MainApplication.Server;

import java.util.HashMap;

/**
 * Script to get users authenticated. if needed openvpn will call it.
 */
public class AuthenticateUser {

    public static void main(String[] args) {
        String username = args[0];
        String hashedPassword = args[1];

        if(readAllUsersFromFile().get(username).equals(hashedPassword)) {
            System.exit(0);
        }
        System.exit(1);
    }

    private static HashMap<String, String> readAllUsersFromFile() {
        HashMap<String, String> userPasswordMap = new HashMap<>();

        // TODO: Readfile.

        return userPasswordMap;
    }

}
