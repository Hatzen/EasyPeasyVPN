package de.hartz.vpn.Helper;

import javax.crypto.Cipher;
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
public class NetworkHelper {

    /**
     * https://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example
     */
    public static class AdvancedEncryptionStandard
    {
        private byte[] key;

        private static final String ALGORITHM = "AES";

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
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            return cipher.doFinal(plainText);
        }

        /**
         * Decrypts the given byte array
         *
         * @param cipherText The data to decrypt
         */
        public byte[] decrypt(byte[] cipherText) throws Exception
        {
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

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
     * Get a list of all already used ip addresses.
     * @returns ArrayList of all used ipaddresses.
     */
    public static ArrayList<String> getAllUsedIpAddresses() {
        ArrayList<String> ipaddresses = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    String ip = enumIpAddr.nextElement().toString();
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
}
