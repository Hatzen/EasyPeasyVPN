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
        private int timeouts;

        public User(String vpnIp, String commonName) {
            this.vpnIp = vpnIp;
            this.commonName = commonName;
            timeouts = 0;
        }

        public String getVpnIp() {
            return vpnIp;
        }

        public String getCommonName() {
            return commonName;
        }

        public boolean incrementTimeout() {
            if (++timeouts > 3) {
                UserData.getInstance().getUserList().removeUserByIp(vpnIp);
                return true;
            }
            return false;
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
