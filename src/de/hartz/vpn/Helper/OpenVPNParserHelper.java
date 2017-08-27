package de.hartz.vpn.Helper;

/**
 * Created by kaiha on 02.07.2017.
 */
public class OpenVPNParserHelper {

    /**
     *
     * TODO: Check if its aquivalent on linux.
     * DEMO DATA:
     Sun Jul 02 19:49:08 2017 us=339988 Notified TAP-Windows driver to set a DHCP IP/netmask of 10.0.0.1/255.255.255.0 on interface {6F249CF5-95FB-4416-AFD8-1C08B9982162} [DHCP-serv: 10.0.0.0, lease-time: 31536000]
     * @param line
     * @return
     */
    public static String getServerIpFromLine(String line) {
        final String matchBefore = "IP/netmask of ";
        final String matchAfter = " on interface";

        int indexOfMatchBefore = line.indexOf(matchBefore);
        if ( !(indexOfMatchBefore != -1 && line.contains(matchAfter)) ) {
            return null;
        }
        String serverIp = line.substring(indexOfMatchBefore + matchBefore.length(), line.indexOf("/", indexOfMatchBefore + matchBefore.length()) );
        return serverIp;
    }

    /**
     *
     * DEMO DATA:
     Sun Jul 02 19:49:22 2017 us=672550 DefaultClient/192.168.2.120 SENT CONTROL [DefaultClient]: 'PUSH_REPLY,route-gateway 10.0.0.1,ping 10,ping-restart 120,ifconfig 10.0.0.2 255.255.255.0' (status=1)
     * @param line
     * @return
     */
    public static String getClientIpFromLine(String line) {
        if (!line.contains("SENT CONTROL [")) {
            return null;
        }
        final String matchBefore = ",ifconfig ";
        final int indexOfMatchBefore = line.indexOf(matchBefore);
        String clientIp = line.substring(indexOfMatchBefore + matchBefore.length(), line.indexOf(" ", indexOfMatchBefore + matchBefore.length()) );
        return clientIp;
    }

    /**
     *
     * DEMO DATA:
     Sun Jul 02 19:49:22 2017 us=672550 DefaultClient/192.168.2.120 SENT CONTROL [DefaultClient]: 'PUSH_REPLY,route-gateway 10.0.0.1,ping 10,ping-restart 120,ifconfig 10.0.0.2 255.255.255.0' (status=1)
     * @param line
     * @return
     */
    public static String getClientNameFromLine(String line) {
        final String matchBefore = "SENT CONTROL [";
        final int indexOfMatchBefore = line.indexOf(matchBefore);
        if (indexOfMatchBefore == -1) {
            return null;
        }
        String clientName = line.substring(indexOfMatchBefore  + matchBefore.length(), line.indexOf("]", indexOfMatchBefore) );
        return clientName;
    }

    /**
     *
     * DEMO DATA:
     Sun Jul 02 20:28:26 2017 us=189108 DefaultClient/192.168.2.120 SIGUSR1[soft,connection-reset] received, client-instance restarting
     * @param line
     * @return
     */
    public static String getDisconnectedClientNameFromLine(String line) {
        final int indexOfMatchAfter = line.indexOf("SIGUSR1[soft,connection-reset] received, client-instance restarting");
        if (indexOfMatchAfter == -1) {
            return null;
        }
        int indexAfter = line.lastIndexOf("/", indexOfMatchAfter);
        int indexBefore = line.lastIndexOf(" ", indexAfter)+1;
        String clientName = line.substring(indexBefore, indexAfter );
        return clientName;
    }

}
