package de.hartz.vpn.Installation.Server;

import de.hartz.vpn.Helper.Helper;
import de.hartz.vpn.Helper.OpenVPNHelper;
import de.hartz.vpn.Helper.Statics;
import de.hartz.vpn.MainApplication.Server.ConfigState;
import de.hartz.vpn.Utilities.Linux;
import de.hartz.vpn.Utilities.Logger;
import de.hartz.vpn.Utilities.OutputStreamHandler;
import de.hartz.vpn.Utilities.Windows;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kaiha on 02.06.2017.
 */
public class ConfigOpenVPN {
    //http://www.andysblog.de/openvpn-server-unter-windows-einrichten

    private static String INSTALLATION_PATH;

    // Default parameters.
    public static final String DEFAULT_PORT = "1194";
    public static final String DEFAULT_PROTOCOL = "udp";
    public static final String DEFAULT_ADAPTER_NAME = "tap";

    public static final String DEFAULT_IP = "10.0.0.0";
    public static final String DEFAULT_SUBNETMASK = "255.255.255.0";

    public static final String DEFAULT_CIPHER = "AES-128-CBC";
    public static final int DEFAULT_KEY_SIZE = 512;

    private ConfigState configState;
    private Logger logger;

    public ConfigOpenVPN(ConfigState configState, Logger logger) {
        this.configState = configState;
        this.logger = logger;

        INSTALLATION_PATH = OpenVPNHelper.getOpenVPNInstallationPath();
        if (Helper.isWindows()) {
            String replaceSingleBackSlash = "\\\\";
            INSTALLATION_PATH = INSTALLATION_PATH.replaceAll(replaceSingleBackSlash, "/");
        } else if (Helper.isLinux()) {
            // Create needed folders.
            new File(INSTALLATION_PATH + "config/").mkdir();
            new File(INSTALLATION_PATH + "log/").mkdir();
        }

        createUserFile();
        writeOVPNFile();
        try {
            if (Helper.isWindows()) {
                windowsEasyRSA();
            } else if (Helper.isLinux()) {
                linuxEasyRSA();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ( configState.getNetworkType() == ConfigState.NetworkType.SITE_TO_END) {
            setSiteToEndVPN();
        }
    }

    // TODO: Should be (most) OS Independent. replace .bat to java call or something like that.
    private void writeOVPNFile() {
        // More infos about the keys and values: http://wiki.openvpn.eu/index.php/OpenVPN-Syntax
        String content = "";

        content += "port " + DEFAULT_PORT; //TODO: Move to ConfigState.
        content += System.getProperty("line.separator");
        content += "proto " + DEFAULT_PROTOCOL; //TODO: Move to ConfigState. UDP or TCP.
        content += System.getProperty("line.separator");

        // tun = routing
        // tap = bridging ; tap supports broadcast (important for games)
        /*
        // TODO: Maybe just use bridging if
        if (configState.getNetworkType() == ConfigState.NetworkType.END_TO_END) {
            tun
            // Need different subnets.
        } else {
            tap
            // Needs same subnet.
            // Could also be END_TO_END. Maybe useful for broadcasts. For End_TO_END OWN SUBNET is useful?
        }
        http://wiki.openvpn.eu/index.php/Vergleich_TUN/TAP
         */
        content += "dev " + DEFAULT_ADAPTER_NAME; // TODO: Check TUN or TAP?
        content += System.getProperty("line.separator");

        // Certificate location.
        String easyRSAPath = "easy-rsa/keys/";
        content += "ca \"" + INSTALLATION_PATH + easyRSAPath + "ca.crt\"";
        content += System.getProperty("line.separator");
        content += "cert \"" + INSTALLATION_PATH + easyRSAPath + "server.crt\"";
        content += System.getProperty("line.separator");
        content += "key \"" + INSTALLATION_PATH + easyRSAPath + "server.key\"";
        content += System.getProperty("line.separator");

        content += "dh \"" + INSTALLATION_PATH + easyRSAPath + "dh" + DEFAULT_KEY_SIZE + ".pem\"";
        content += System.getProperty("line.separator");

        content += "server " + DEFAULT_IP + " " + DEFAULT_SUBNETMASK; //TODO: Move to ConfigState.
        content += System.getProperty("line.separator");

        // Only needed when the clients should keep their ip.
        // ifconfig-pool-persist "C:\\Program Files\\OpenVPN\\log\\ipp.txt"

        // TODO: Use if its site to site network.
        /*String currentRealIp = "192.168.2.0"; // TODO: Get Netaddress from currently used lan.
        String currentRealNetmask = "255.255.255.0"; // TODO: Get Netaddress from currently used lan.
        // TODO: This might not be needed for site to site network?
        content += "route " + currentRealIp + " " + currentRealNetmask;
        content += System.getProperty("line.separator");*/

        content += "keepalive " + 10 + " " + 120; // Needed to hold connection.
        content += System.getProperty("line.separator");

        content += "cipher " + DEFAULT_CIPHER; // TODO: Move to ConfigState. And look for performance increase.
        content += System.getProperty("line.separator");

        content += "persist-key";
        content += System.getProperty("line.separator");
        content += "persist-tun";
        content += System.getProperty("line.separator");

        // Log file.
        content += "status " + INSTALLATION_PATH + "log/openvpn-status.log";
        content += System.getProperty("line.separator");

        // Verbose level?
        content += "verb " + 3;
        content += System.getProperty("line.separator");


        /* TODO: Anonymes surfen..
        # If enabled, this directive will configure
        # all clients to redirect their default
        # network gateway through the VPN, causing
        # all IP traffic such as web browsing and
        # and DNS lookups to go through the VPN
        # (The OpenVPN server machine may need to NAT
        # or bridge the TUN/TAP interface to the internet
        # in order for this to work properly).
        */
        //;push "redirect-gateway def1 bypass-dhcp"

        /* TODO: Active as default. But disable if anonym vpn.
        # Uncomment this directive to allow different
        # clients to be able to "see" each other.
        # By default, clients will only see the server.
        # To force clients to only see the server, you
        # will also need to appropriately firewall the
        # server's TUN/TAP interface.
        */
        content += "client-to-client";
        content += System.getProperty("line.separator");

        /* TODO: Very unsecure. Implemnt.
        # Uncomment this directive if multiple clients
        # might connect with the same certificate/key
        # files or common names.  This is recommended
        # only for testing purposes.  For production use,
        # each client should have its own certificate/key
        # pair.
        #
        # IF YOU HAVE NOT GENERATED INDIVIDUAL
        # CERTIFICATE/KEY PAIRS FOR EACH CLIENT,
        # EACH HAVING ITS OWN UNIQUE "COMMON NAME",
        # UNCOMMENT THIS LINE OUT.
        */
        // TODO: This doesnt work and ends with error: connection refused. Dont know why. Not needed at the moment.
        //content += "duplicate-cn";
        //content += System.getProperty("line.separator");


        /* TODO: #1 FUTURE! Additional Secureity.
        # For extra security beyond that provided
        # by SSL/TLS, create an "HMAC firewall"
        # to help block DoS attacks and UDP port flooding.
        #
        # Generate with:
        #   openvpn --genkey --secret ta.key
        #
        # The server and each client must have
        # a copy of this key.
        # The second parameter should be '0'
        # on the server and '1' on the clients.
        tls-auth ta.key 0 # This file is secret
        */

        /* TODO: #2 FUTURE! Additional performance increase?
        # Enable compression on the VPN link and push the
        # option to the client (2.4+ only, for earlier
        # versions see below)
        ;compress lz4-v2
        ;push "compress lz4-v2"
        # For compression compatible with older clients use comp-lzo
        # If you enable it here, you must also
        # enable it in the client config file.
        ;comp-lzo
        */


        if (configState.isNeedsAuthentication()) {
            //auth-user-pass-verify "C:\\Program Files\\OpenVPN\\config\\auth.bat" via-env
            //script-security 3
        } else {
            // TODO: Maybe useful for performance increase? Needs authentication parameter.
            //--client-cert-not-required
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(INSTALLATION_PATH + "config/server.ovpn"), "utf-8"))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createUserFile() {
        // TODO: Implement.
        // Save password HASH. Not cleartext.
        // INSTALLATION_PATH + "/config/users.txt"
        /*
            username1 password1;
            username2 password2;
        */
    }

    @Linux
    private void linuxEasyRSA() throws IOException {
        //TODO:  Cannot run .vars permission denied.. CHmod -> executable?
        // Easy rsa path was an other than in the tutorial.
        /*String[][] commands = {
                {"cp", "-R", "/usr/share/easy-rsa/", INSTALLATION_PATH + "/easy-rsa"}, // TODO: Check maybe mkdir needed.
                {"./vars"},
                {"./clean-all"},
                {"./build-ca", System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator")},
                {"./build-key-server --batch server", System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator"),System.getProperty("line.separator")}
            }; //cp -R /usr/share/easy-rsa/* /etc/openvpn/easy-rsa/
        */
        String[][] commands = {
                {"cp", "-R", "/usr/share/easy-rsa/", INSTALLATION_PATH + "/easy-rsa"}, // TODO: Check maybe mkdir needed.
                {"./vars"},
                {"./clean-all"},
                {"./build-ca"},
                {"./build-key-server --batch server"}
        }; //cp -R /usr/share/easy-rsa/* /etc/openvpn/easy-rsa/
        int exitValue = 0;

        // TODO: Get rid off. Just needed because files first have to be copied..
        boolean temp = true;
        for (String[] command : commands) {
            System.out.println(Arrays.toString(command));
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(INSTALLATION_PATH + "/easy-rsa/"));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());
            outputHandler.start();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.addLogLine(outputHandler.getOutput().toString());
            exitValue = process.exitValue();
            System.out.println("" + exitValue);
            System.out.println("---");

            if(temp) {
                temp = false;

                String content = Helper.readFile(INSTALLATION_PATH + "easy-rsa/vars", Charset.defaultCharset());
                content = replaceParameter(content, "HOME=", "\"${0%/*}\"");
                content = replaceParameter(content, "KEY_SIZE=", "" + DEFAULT_KEY_SIZE);

                content = replaceParameter(content, "KEY_COUNTRY=", "DE");
                content = replaceParameter(content, "KEY_PROVINCE=", "NRW");
                content = replaceParameter(content, "KEY_CITY=", "MS");
                content = replaceParameter(content, "KEY_ORG=", "EasyPeasyVPN");
                content = replaceParameter(content, "KEY_EMAIL=", "dummy@email.de");
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(INSTALLATION_PATH + "easy-rsa/vars"), "utf-8"))) {
                    writer.write(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Windows
    private void windowsEasyRSA() throws IOException {
        // TODO: Make OS independent.

        // Creates vars.bat.
        try {
            logger.addLogLine("Execute " + "init-config.bat");
            ProcessBuilder pb = new ProcessBuilder( INSTALLATION_PATH + "easy-rsa/" + "init-config.bat");
            pb.directory(new File(INSTALLATION_PATH + "easy-rsa/"));
            Process process = pb.start();
            OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());
            outputHandler.start();
            process.waitFor();
            logger.addLogLine(outputHandler.getOutput().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String content = Helper.readFile(INSTALLATION_PATH + "easy-rsa/vars.bat", Charset.defaultCharset());
        content = replaceParameter(content, "HOME=", "%cd%");
        content = replaceParameter(content, "KEY_SIZE=", "" + DEFAULT_KEY_SIZE);

        content = replaceParameter(content, "KEY_COUNTRY=", "DE");
        content = replaceParameter(content, "KEY_PROVINCE=", "NRW");
        content = replaceParameter(content, "KEY_CITY=", "MS");
        content = replaceParameter(content, "KEY_ORG=", "EasyPeasyVPN");
        content = replaceParameter(content, "KEY_EMAIL=", "dummy@email.de");
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(INSTALLATION_PATH + "easy-rsa/vars.bat"), "utf-8"))) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        runEasyRSACommands("clean-all.bat", 0, 0);
        runEasyRSACommands("build-ca.bat", 8, 0);
        runEasyRSACommands("build-dh.bat", 0, 0);
        runEasyRSACommands("build-key-server.bat server", 10, 2);

        // TODO: Build client scripts.
        //TODO:  DO THIS ALSO WITH LINUX INSTALLATION!!!!!!!!!!!!!
        ArrayList<String> input = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            input.add("" + System.getProperty("line.separator"));
        }
        input.set(5, Statics.DEFAULT_CLIENT_NAME + System.getProperty("line.separator"));
        input.set(6, Statics.DEFAULT_CLIENT_NAME + System.getProperty("line.separator"));
        runEasyRSACommands("build-key.bat " + Statics.DEFAULT_CLIENT_NAME, input, 2);
    }

    /**
     * Needed, to execute every command with vars.bat and to confirm some values.
     * @param command
     * @param skips Number of how often enter should be entered.
     * @param confirmations number of how often "y" should be entered (after the skips).
     * @throws IOException
     */
    private void runEasyRSACommands(String command, int skips, int confirmations) throws IOException {
        ArrayList<String> input = new ArrayList<>();
        for (int i = 0; i < skips; i++) {
            // Skip all settings to use default values.
            input.add("" + System.getProperty("line.separator"));
        }
        runEasyRSACommands(command, input, confirmations);
    }

    /**
     * Needed, to execute every command with vars.bat and to confirm some values.
     * @param command
     * @param inputs ArrayList of answers to give.
     * @param confirmations number of how often "y" should be entered (after the skips).
     * @throws IOException
     */
    @Windows
    private void runEasyRSACommands(String command, ArrayList<String> inputs, int confirmations) throws IOException {
        ProcessBuilder pb = new ProcessBuilder( "cmd.exe");
        pb.redirectErrorStream(true);
        pb.directory(new File(INSTALLATION_PATH + "easy-rsa/"));
        Process process = pb.start();
        /*
        TODO: Check if Path environnement contains openssl after installation, otherwise it needs a computer restart..
        Map<String, String> envs = pb.environment();
        System.out.println(envs.get("Path"));
        envs.put("openssl", INSTALLATION_PATH + "/bin/opensll.exe");
        */
        OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());

        // Write commands.
        PrintWriter commandExecutor = new PrintWriter(process.getOutputStream());
        logger.addLogLine(command);
        if (OpenVPNHelper.needsPathUpdate()) {
            commandExecutor.println("SET PATH=%PATH%;" + OpenVPNHelper.openVPNBinPath);
            System.out.println("SET PATH=%PATH%;" + OpenVPNHelper.openVPNBinPath);
        }
        commandExecutor.println("vars.bat");
        commandExecutor.println(command);


        for (String input :inputs) {
            commandExecutor.print(input);
        }
        for (int i = 0; i < confirmations; i++) {
            // Confirm all config settings..
            // Very strange behaviour. It needs a delay regarding stackoverflow comment and the exact same command twice.
            // https://stackoverflow.com/questions/39913424/how-to-execute-batch-script-which-takes-multiple-inputs-by-using-java-process-bu
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            commandExecutor.print("y" + "\r\n");
            commandExecutor.print("y" + "\r\n");
            commandExecutor.flush();
        }

        commandExecutor.println("exit");
        commandExecutor.flush();
        commandExecutor.close();
        outputHandler.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.addLogLine(outputHandler.getOutput().toString());
    }

    private String replaceParameter(String source, String key, String value) {
        int startIndex = source.indexOf(key) + key.length();
        int endIndex = source.indexOf(System.getProperty("line.separator") ,startIndex);
        return source.substring(0, startIndex) + value + source.substring(endIndex, source.length());
    }

    private void setSiteToEndVPN() {
        // TODO: Implement.
        /* Commands to execute on VPN-Server:
        Windows XP and previous registry change is needed:
            HKEY_LOCAL_MACHINE\System\CurrentControlSet\Services\Tcpip\Parameters
            IPEnableRouter=1

        Newer windows versions:
            netsh interface ipv4 set int "LAN-Verbindung" forwarding=enabled
            netsh interface ipv4 set int "LAN-Verbindung 2" forwarding=enabled

         // Also set forwarding in the gateway/router.
         Pseudo: route add 10.0.0.0 mask 255.255.255.0 192.168.0.2 -p
         route add VPN-NETZWERK mask SUBNETZMASKE OPENVPNSERVER -p
         */
    }

}
