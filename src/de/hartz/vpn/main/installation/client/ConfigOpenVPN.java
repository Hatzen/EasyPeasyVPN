package de.hartz.vpn.main.installation.client;

import de.hartz.vpn.helper.Linux;
import de.hartz.vpn.main.UserData;
import de.hartz.vpn.main.installation.InstallationController;
import de.hartz.vpn.utilities.GeneralUtilities;
import de.hartz.vpn.utilities.OpenVPNUtilities;

import java.io.*;

import static de.hartz.vpn.main.installation.server.ConfigOpenVPN.DEFAULT_ADAPTER_NAME;
import static de.hartz.vpn.main.installation.server.ConfigOpenVPN.DEFAULT_CIPHER;

/**
 * Class that writes the needed openvpn config file.
 */
public class ConfigOpenVPN {

    private static String INSTALLATION_PATH;

    public ConfigOpenVPN() {
        INSTALLATION_PATH = OpenVPNUtilities.getOpenVPNInstallationPath();
        if (GeneralUtilities.isWindows()) {
            String replaceSingleBackSlash = "\\\\";
            INSTALLATION_PATH = INSTALLATION_PATH.replaceAll(replaceSingleBackSlash, "/");
        } else if (GeneralUtilities.isLinux()) {
            // Create needed folders.
            new File(INSTALLATION_PATH + "config/").mkdir();
            new File(INSTALLATION_PATH + "log/").mkdir();
        }

        writeOVPNFile();
        if (GeneralUtilities.isLinux()) {

        }
    }

    // TODO: Should be (most) OS Independent. replace .bat to java call or something like that.
    private void writeOVPNFile() {
        // More infos about the keys and values: http://wiki.openvpn.eu/index.php/OpenVPN-Syntax
        String content = "";

        content += "proto " + InstallationController.getInstance().getTmpConfigState().getProtocol();
        content += System.getProperty("line.separator");
        content += "dev " + DEFAULT_ADAPTER_NAME; // TODO: TAP for broadcasts, TUN for performance.
        content += System.getProperty("line.separator");

        content += "cipher " + DEFAULT_CIPHER; // TODO: Move to ConfigState. And look for performance increase.
        content += System.getProperty("line.separator");

        // Verbose level.
        content += "verb " + 3;
        content += System.getProperty("line.separator");

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
        content += "remote " + UserData.getInstance().getServerIp() + " " + UserData.getInstance().getServerPort();
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

        /*
        # Enable compression on the VPN link.
        # Don't enable this unless it is also
        # enabled in the server config file.
        #comp-lzo
        */

        /*
        Allow remote peer to change its IP address and/or port number, such as due to DHCP (this is the default if --remote is not used). --float when specified with --remote allows an OpenVPN session to initially connect to a peer at a known address, however if packets arrive from a new address and pass all authentication tests, the new address will take control of the session. This is useful when you are connecting to a peer which holds a dynamic address such as a dial-in user or DHCP client.
        Essentially, --float tells OpenVPN to accept authenticated packets from any address, not only the address which was specified in the --remote option.
         */
        content += "float";
        content += System.getProperty("line.separator");

        String filePath = INSTALLATION_PATH + "config" + File.separator + "client" + GeneralUtilities.getOpenVPNConfigExtension();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Linux
    public void configurateIptables() {
        // https://arashmilani.com/post?id=53
        // Raspian works without it. Maybe not needed?

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


        // iptables -F // TODO: Why flush at the end? It will delete all rules!?
        */


    }

}
