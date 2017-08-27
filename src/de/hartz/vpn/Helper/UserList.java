package de.hartz.vpn.Helper;

import java.util.ArrayList;

/**
 * Created by kaiha on 02.07.2017.
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
