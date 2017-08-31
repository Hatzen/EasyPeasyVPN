package de.hartz.vpn.Installation.Client;

import de.hartz.vpn.Helper.Helper;
import de.hartz.vpn.Helper.OpenVPNHelper;
import de.hartz.vpn.MainApplication.UserData;
import de.hartz.vpn.Utilities.Linux;

import java.io.*;

import static de.hartz.vpn.Installation.Server.ConfigOpenVPN.*;

/**
 * Class that writes the needed openvpn config file.
 */
public class ConfigOpenVPN {

    private static String INSTALLATION_PATH;

    public ConfigOpenVPN() {
        INSTALLATION_PATH = OpenVPNHelper.getOpenVPNInstallationPath();
        if (Helper.isWindows()) {
            String replaceSingleBackSlash = "\\\\";
            INSTALLATION_PATH = INSTALLATION_PATH.replaceAll(replaceSingleBackSlash, "/");
        } else if (Helper.isLinux()) {
            // Create needed folders.
            new File(INSTALLATION_PATH + "config/").mkdir();
            new File(INSTALLATION_PATH + "log/").mkdir();
        }

        writeOVPNFile();
        if (Helper.isLinux()) {

        }
    }

    // TODO: Should be (most) OS Independent. replace .bat to java call or something like that.
    private void writeOVPNFile() {
        // More infos about the keys and values: http://wiki.openvpn.eu/index.php/OpenVPN-Syntax
        String content = "";


        // TODO: SAME AS SERVER
        content += "proto " + DEFAULT_PROTOCOL; //TODO: Move to ConfigState. UDP or TCP.
        content += System.getProperty("line.separator");
        content += "dev " + DEFAULT_ADAPTER_NAME; // TODO: Check TUN or TAP?
        content += System.getProperty("line.separator");

        content += "cipher " + DEFAULT_CIPHER; // TODO: Move to ConfigState. And look for performance increase.
        content += System.getProperty("line.separator");

        // Verbose level.
        content += "verb " + 3;
        content += System.getProperty("line.separator");


        // TODO: Different from server.
        // Certificate location.
        content += "ca \"" + INSTALLATION_PATH + "client.ca\"";
        content += System.getProperty("line.separator");
        content += "cert \"" + INSTALLATION_PATH + "client.crt\"";
        content += System.getProperty("line.separator");
        content += "key \"" + INSTALLATION_PATH + "client.key\"";
        content += System.getProperty("line.separator");

        /*
        # The hostname/IP and port of the server.
        # You can have multiple remote entries
        # to load balance between the servers.
         */
        content += "remote " + UserData.serverIp + " " + DEFAULT_PORT; //TODO: Move to ConfigState.
        content += System.getProperty("line.separator");
        /*
        # Specify that we are a client and that we
        # will be pulling certain config file directives
        # from the server.
         */
        content += "client";
        content += System.getProperty("line.separator");
        /*
        # Keep trying indefinitely to resolve the
        # host name of the OpenVPN server.  Very useful
        # on machines which are not permanently connected
        # to the internet such as laptops.
         */
        content += "resolv-retry infinite";
        content += System.getProperty("line.separator");

        /*
        # Try to preserve some state across restarts.
         */
        content += "persist-key";
        content += System.getProperty("line.separator");
        content += "persist-tun";
        content += System.getProperty("line.separator");

        /*
        # Wireless networks often produce a lot
        # of duplicate packets.  Set this flag
        # to silence duplicate packet warnings.
        ;mute-replay-warnings
         */

        /* TODO: #1 FUTURE! Additional Secureity.
        # Verify server certificate by checking that the
        # certicate has the correct key usage set.
        # This is an important precaution to protect against
        # a potential attack discussed here:
        #  http://openvpn.net/howto.html#mitm
        #
        # To use this feature, you will need to generate
        # your server certificates with the keyUsage set to
        #   digitalSignature, keyEncipherment
        # and the extendedKeyUsage to
        #   serverAuth
        # EasyRSA can do this for you.
        remote-cert-tls server

        # If a tls-auth key is used on the server
        # then every client must also have the key.
        tls-auth ta.key 1
        */

        /* TODO: #2 FUTURE! Additional performance increase?
        # Enable compression on the VPN link.
        # Don't enable this unless it is also
        # enabled in the server config file.
        #comp-lzo
        */

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(INSTALLATION_PATH + "config/client.ovpn"), "utf-8"))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Linux
    public void configurateIptables() {
        // Maybe not needed?

        /*
        // TODO: Save persistent: iptables-save && iptables-restore.
        iptables -A INPUT -i eth0 -m state --state NEW -p udp --dport 1194 -j ACCEPT

        iptables -A INPUT -i tun+ -j ACCEPT

        iptables -A FORWARD -i tun+ -j ACCEPT
        iptables -A FORWARD -i tun+ -o eth0 -m state --state RELATED,ESTABLISHED -j ACCEPT
        iptables -A FORWARD -i eth0 -o tun+ -m state --state RELATED,ESTABLISHED -j ACCEPT

        iptables -t nat -A POSTROUTING -s 10.8.0.0/24 -o eth0 -j MASQUERADE

        iptables -A OUTPUT -o tun+ -j ACCEPT

         */

        /* DEBUG ONLY. ACCEPT ALL CONNECTIONS.
        iptables -P INPUT ACCEPT
        iptables -P OUTPUT ACCEPT
        iptables -P FORWARD ACCEPT
        iptables -F
        */


    }

}
