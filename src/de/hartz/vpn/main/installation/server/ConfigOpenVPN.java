package de.hartz.vpn.main.installation.server;

import de.hartz.vpn.helper.Linux;
import de.hartz.vpn.helper.Logger;
import de.hartz.vpn.helper.OutputStreamHandler;
import de.hartz.vpn.helper.Windows;
import de.hartz.vpn.main.server.ConfigState;
import de.hartz.vpn.utilities.Constants;
import de.hartz.vpn.utilities.GeneralUtilities;
import de.hartz.vpn.utilities.OpenVPNUtilities;

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
    public static final String DEFAULT_ADAPTER_NAME = "tap";

    public static final String DEFAULT_IP = "10.0.0.0";
    public static final String DEFAULT_SUBNETMASK = "255.255.255.0";

    public static final String DEFAULT_CIPHER = "AES-128-CBC";
    public static final int DEFAULT_KEY_SIZE = 1024;

    private ConfigState configState;
    private Logger logger;

    public ConfigOpenVPN(ConfigState configState, Logger logger) {
        this.configState = configState;
        this.logger = logger;

        INSTALLATION_PATH = OpenVPNUtilities.getOpenVPNInstallationPath();
        if (GeneralUtilities.isWindows()) {
            String replaceSingleBackSlash = "\\\\";
            INSTALLATION_PATH = INSTALLATION_PATH.replaceAll(replaceSingleBackSlash, "/");
        } else if (GeneralUtilities.isLinux()) {
            // Create needed folders.
            new File(INSTALLATION_PATH + "config/").mkdir();
            new File(INSTALLATION_PATH + "log/").mkdir();
        }

        createUserFile();
        writeOVPNFile();
        try {
            if (GeneralUtilities.isWindows()) {
                windowsEasyRSA();
            } else if (GeneralUtilities.isLinux()) {
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
        content += "proto " +  configState.getProtocol();
        content += System.getProperty("line.separator");

        content += "dev " + DEFAULT_ADAPTER_NAME; // TODO: TAP for broadcasts, TUN for performance.
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

        /*
        # Enable compression on the VPN link and push the
        # option to the client (2.4+ only, for earlier
        # versions see below)
        */
        if (configState.isCompressData()) {
            content += "compress lz4-v2";
            content += System.getProperty("line.separator");
            content += "push \"compress lz4-v2\"";
            content += System.getProperty("line.separator");
        }
        /*
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

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(INSTALLATION_PATH + "config/server" + GeneralUtilities.getOpenVPNConfigExtension()), "utf-8"))) {
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
        /*new File(INSTALLATION_PATH + "easy-rsa").mkdirs();
        String[][] commands = {
                {"cp", "-R", "/usr/share/easy-rsa/", INSTALLATION_PATH},
                {"cp", "openssl-1.0.0.cnf", "openssl.cnf"},
                {"chmod", "-R", "777", "."}, // TODO: Maybe undo after success, else EVERY user can create new certificate..

                {"./vars; ", "./clean-all"},
                {"./vars; ",  "./build-ca","--batch"},
                {"./vars; ", "./build-dh"},
                {"./vars; ", "./build-key-server", "--batch", "server"},
                {"./vars; ", "./build-key", "--batch", Constants.DEFAULT_CLIENT_NAME}
        };

        int exitValue = 0;
        boolean filesNotCopiedYet = true;
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

            if(filesNotCopiedYet) {
                filesNotCopiedYet = false;

                String content = GeneralUtilities.readFile(INSTALLATION_PATH + "easy-rsa/vars", Charset.defaultCharset());
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
        }*/
        // Prepare scripts.
        new File(INSTALLATION_PATH + "easy-rsa").mkdirs();
        String[][] commands = {
                {"cp", "-R", "/usr/share/easy-rsa/", INSTALLATION_PATH},
                {"cp", "openssl-1.0.0.cnf", "openssl.cnf"},
                {"chmod", "-R", "777", "."}, // TODO: Maybe undo after success, else EVERY user can create new certificate..
        };
        int exitValue;
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
        }

        // Prepare parameters.
        String content = GeneralUtilities.readFile(INSTALLATION_PATH + "easy-rsa/vars", Charset.defaultCharset());
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

        // Execute scripts.
        runEasyRSACommandsLinux("clean-all", 0, 0);
        runEasyRSACommandsLinux("build-ca", 8, 0);
        runEasyRSACommandsLinux("build-dh", 0, 0);
        runEasyRSACommandsLinux("build-key-server server", 10, 2);

        // TODO: Build client scripts
        ArrayList<String> input = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            input.add("" + System.getProperty("line.separator"));
        }
        input.set(5, Constants.DEFAULT_CLIENT_NAME + System.getProperty("line.separator"));
        input.set(6, Constants.DEFAULT_CLIENT_NAME + System.getProperty("line.separator"));
        runEasyRSACommandsLinux("build-key " + Constants.DEFAULT_CLIENT_NAME, input, 2);
    }

    private void runEasyRSACommandsLinux(String command, int skips, int confirmations) throws IOException {
        ArrayList<String> input = new ArrayList<>();
        for (int i = 0; i < skips; i++) {
            // Skip all settings to use default values.
            input.add("" + System.getProperty("line.separator"));
        }
        runEasyRSACommandsLinux(command, input, confirmations);
    }

    /**
     * Needed, to execute every command with vars.bat and to confirm some values.
     * @param command
     * @param inputs ArrayList of answers to give.
     * @param confirmations number of how often "y" should be entered (after the skips).
     * @throws IOException
     */
    @Windows
    private void runEasyRSACommandsLinux(String command, ArrayList<String> inputs, int confirmations) throws IOException {
        // TODO: Doesnt work. Creating files need "sudo java ..." cause of location. Because of sudo it is not possible to do "./vars; ./clean-all" cause export isnt available for "clean-all"
        ProcessBuilder pb = new ProcessBuilder( "/bin/bash", "vars", ";", "/bin/bash", command);
        pb.redirectErrorStream(true);
        pb.directory(new File(INSTALLATION_PATH + "easy-rsa/"));
        Process process = pb.start();

        OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());

        // Write commands.
        PrintWriter commandExecutor = new PrintWriter(process.getOutputStream());
        logger.addLogLine(command);
        //commandExecutor.println("vars.bat");
        //commandExecutor.println(command);


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

    @Windows
    private void windowsEasyRSA() throws IOException {

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

        String content = GeneralUtilities.readFile(INSTALLATION_PATH + "easy-rsa/vars.bat", Charset.defaultCharset());
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

        runEasyRSACommandsWindows("clean-all.bat", 0, 0);
        runEasyRSACommandsWindows("build-ca.bat", 8, 0);
        runEasyRSACommandsWindows("build-dh.bat", 0, 0);
        runEasyRSACommandsWindows("build-key-server.bat server", 10, 2);

        // TODO: Build client scripts.
        //TODO:  DO THIS ALSO WITH LINUX INSTALLATION!!!!!!!!!!!!!
        ArrayList<String> input = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            input.add("" + System.getProperty("line.separator"));
        }
        input.set(5, Constants.DEFAULT_CLIENT_NAME + System.getProperty("line.separator"));
        input.set(6, Constants.DEFAULT_CLIENT_NAME + System.getProperty("line.separator"));
        runEasyRSACommandsWindows("build-key.bat " + Constants.DEFAULT_CLIENT_NAME, input, 2);
    }

    /**
     * Needed, to execute every command with vars.bat and to confirm some values.
     * @param command
     * @param skips Number of how often enter should be entered.
     * @param confirmations number of how often "y" should be entered (after the skips).
     * @throws IOException
     */
    @Windows
    private void runEasyRSACommandsWindows(String command, int skips, int confirmations) throws IOException {
        ArrayList<String> input = new ArrayList<>();
        for (int i = 0; i < skips; i++) {
            // Skip all settings to use default values.
            input.add("" + System.getProperty("line.separator"));
        }
        runEasyRSACommandsWindows(command, input, confirmations);
    }

    /**
     * Needed, to execute every command with vars.bat and to confirm some values.
     * @param command
     * @param inputs ArrayList of answers to give.
     * @param confirmations number of how often "y" should be entered (after the skips).
     * @throws IOException
     */
    @Windows
    private void runEasyRSACommandsWindows(String command, ArrayList<String> inputs, int confirmations) throws IOException {
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
        if (OpenVPNUtilities.needsPathUpdate()) {
            commandExecutor.println("SET PATH=%PATH%;" + OpenVPNUtilities.openVPNBinPath);
            System.out.println("SET PATH=%PATH%;" + OpenVPNUtilities.openVPNBinPath);
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
        /* Commands to execute on VPN-server:
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
