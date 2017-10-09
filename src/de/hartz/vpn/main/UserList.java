package de.hartz.vpn.main;

import java.util.ArrayList;

/**
 * Class that stores all vpn users.
 */
public class UserList extends ArrayList<UserList.User> {

    public static class User {
        // TODO: Add timestamp from moment of adding. if larger than 1 minute set to "offline".
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

    @Override
    public boolean add(UserList.User user) {
        removeUserByIp(user.getVpnIp());
        return super.add(user);
    }

    public void removeUserByIp(String ip) {
        for (User user: this) {
            if(user.getVpnIp().equals(ip)) {
                remove(user);
                break;
            }
        }
    }

}
