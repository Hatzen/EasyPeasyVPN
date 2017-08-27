package de.hartz.vpn.MainApplication.Server;

import java.util.HashMap;

/**
 * Created by kaiha on 22.06.2017.
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
