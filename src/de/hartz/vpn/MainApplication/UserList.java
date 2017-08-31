package de.hartz.vpn.MainApplication;

import java.util.ArrayList;

/**
 * Class that stores all vpn users.
 */
public class UserList extends ArrayList<UserList.User> {

    public static class User {
        private String vpnIp;
        private String commonName;

        public User(String vpnIp, String commonName) {
            this.vpnIp = vpnIp;
            this.commonName = commonName;
        }

        public String getVpnIp() {
            return vpnIp;
        }

        public String getCommonName() {
            return commonName;
        }
    }

    public void removeUserByName(String name) {
        System.out.println("find " + name);
        for (User user: this) {
            System.out.println(name + "=" + user.commonName);
            if(user.commonName.equals(name)) {
                System.out.println("found");
                remove(user);
                break;
            }
        }
    }

}
