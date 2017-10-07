package de.hartz.vpn.utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by kaiha on 31.08.2017.
 */
public final class NetworkUtilities {

    public static String OWN_VPN_IP;

    /**
     * https://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example
     * https://stackoverflow.com/questions/28172246/how-to-reset-the-cipher-to-the-state-at-the-time-of-initialization
     */
    public static class AdvancedEncryptionStandard
    {
        private byte[] key;
        private static final byte[] INITIALISATION_VECTOR = {5,23,127,9,1,1,1,1,10,123,12,44,66,99,14,15,16};

        private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";

        // TODO: Dont use a static key.. Maybe replace by creating from server ip address (key which is known by every vpn participant)
        private byte[] getDefaultKey() {
            return "AFHSAksagAOMOLL6".getBytes(StandardCharsets.UTF_8);
        }

        public AdvancedEncryptionStandard() {
            key = getDefaultKey();
        }

        public AdvancedEncryptionStandard(byte[] key)
        {
            this.key = key;
        }

        /**
         * Encrypts the given plain text
         *
         * @param plainText The plain text to encrypt
         */
        public byte[] encrypt(byte[] plainText) throws Exception
        {
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(INITIALISATION_VECTOR);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            return cipher.doFinal(plainText);
        }

        /**
         * Decrypts the given byte array
         *
         * @param cipherText The data to decrypt
         */
        public byte[] decrypt(byte[] cipherText) throws Exception
        {
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(INITIALISATION_VECTOR);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            return cipher.doFinal(cipherText);
        }
    }

    /**
     *
     * https://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java
     * @param targetURL the url that should be called with the given post parameters.
     * @param urlParameters the post paramters as one string.
     * @returns the response of the http request.
     */
    public static String executePost(String targetURL, String urlParameters) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Get the external ip of the device that is connected to the internet.
     * @return the external ip.
     */
    public static String getExternalIp() {
        try {
            URL whatIsMyIp = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIp.openStream()));
            String ip = in.readLine();
            return ip;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a list of all already used ip addresses with its subnetmask.
     * @returns ArrayList of all used ipaddresses.
     */
    public static ArrayList<String> getAllUsedIpAddresses() {
        ArrayList<String> ipaddresses = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress tmpAddress = enumIpAddr.nextElement();
                    short subnet = NetworkInterface.getByInetAddress(tmpAddress).getInterfaceAddresses().get(0).getNetworkPrefixLength();
                    String ip = tmpAddress.toString() + "/" + subnet;
                    // Only ipv4 addresses.
                    if (ip.contains(".")) {
                        // remove prefix "/"
                        ipaddresses.add(ip.substring(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipaddresses;
    }

    /**
     * Pings all clients via ICMP in the given network and returns a list with all ips that answered.
     * IMPORTANT:  Currently only ipv4 with subnetmask /24 is supported!
     * https://stackoverflow.com/questions/3345857/how-to-get-a-list-of-ip-connected-in-same-network-subnet-using-java
     * @param netaddress the netadress to check.
     * @returns a list of all reachable ips in the network.
     * TODO: On my machine spotify starts stottering shortly. Maybe because of too much traffic for short time?
     */
    public static ArrayList<String> getAllReachableClientsForNetaddress(String netaddress) {
        final ArrayList<String> ipAddresses = new ArrayList<>();

        for (int i=1;i<255;i++){
            final String host= netaddress + "." + i;
            new Thread() {
                @Override
                public void run() {
                    try {
                        // TODO: Make this more relieable. Might be problem of using udp???
                        if (InetAddress.getByName(host).isReachable(5000)) {
                            synchronized (ipAddresses) {
                                ipAddresses.add(host);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        /*
        TODO: Check why this does not work!! Unrelible and first time alwasys ends with all ips ???
        for (int i=1;i<255;i++){
            final String host= netaddress + "." + i;
            new Thread() {
                @Override
                public void run() {
                    if (NetworkUtilities.isReachable(host)) {
                        ipAddresses.add(host);
                    }
                }
            }.start();
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        return ipAddresses;
    }

    /**
     * Returns a ip netaddress that is not used by this computer.
     * @returns valid ip address.
     */
    public static String getRecommendedIp() {
        // TODO: Check getAllUsedIpAddresses and use 1) 10.0.0.0 2) 172.16.0.0 3) 192.168.0.0 the one which is not used.
        // NOTE: Maybe Openvpn is already used so ip is already used. So lookup interfaces and compare ips..
        return "10.0.0.0";
    }

    /**
     * Pings the given ip, to see if the host is reachable. This function is blocking the current thread til the result.
     * https://stackoverflow.com/questions/9922543/why-does-inetaddress-isreachable-return-false-when-i-can-ping-the-ip-address
     * @return
     */
    public static boolean isReachable(String ip) {
        boolean reachable = false;
        try {
            String param = "n";
            // in case of Linux change the 'n' to 'c'.
            if ( GeneralUtilities.isLinux() ) {
                param = "c";
            }

            ProcessBuilder pb = new ProcessBuilder( "ping", "-" + param, "1", ip );
            Process p = pb.start();
            int returnVal = p.waitFor();

            reachable = (returnVal == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reachable;
    }

    public static String getOwnVPNIP() {
        // TODO: Better way would be scanning all interfaces and checking the ip for all. Problem is that the devices are named differently on windows linux etc..
        return OWN_VPN_IP;
    }

    /**
     * Hack that removes unnecessary bytes which removes all "invisible" characters.
     * @param string The string to clean up. Remove line ending etc..
     * @returns a clean string consisting only of visible characters.
     * https://stackoverflow.com/questions/9057083/how-can-i-remove-all-control-characters-from-a-java-string
     */
    public static String getCleanString(String string) {
        return string.replaceAll("\\p{Cntrl}", "");
    }
}
