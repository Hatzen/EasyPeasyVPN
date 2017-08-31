package de.hartz.vpn.MainApplication.Server;

import de.hartz.vpn.Helper.OpenVPNHelper;
import de.hartz.vpn.Helper.Statics;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class represents a meta server that handles the exchange of the necessary config and certificates to get
 * communication working.
 * Based on http://www.rgagnon.com/javadetails/java-0542.html
 */
public class MetaServer extends Thread {

    private static final String CONFIG_FOLDER = OpenVPNHelper.getOpenVPNInstallationPath() + "easy-rsa\\keys\\";

    // TODO: Replace. (?)
    public static final String SEND_CONFIG = "SEND_CONFIG";
    public static final String SEND_CRT = "SEND_CRT";
    public static final String SEND_KEY = "SEND_KEY";
    public static final String SEND_CA = "SEND_CA";
    public static final String EXIT = "EXIT";

    private FileInputStream fis;
    private BufferedInputStream bis = null;
    private OutputStream os = null;
    private ServerSocket serverSocket = null;
    private BufferedReader bufferedReader;
    private Socket socket = null;

    private String filePathCa;
    private String filePathCert;
    private String filePathKey;

    public MetaServer() {

    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Statics.MEDIATION_SERVER_PORT);
            while (true) {
                System.out.println("Waiting...");
                try {
                    // TODO: Check if accept has to run in own thread. So multipy clients can connect at same time..
                    // TODO: MAYBE NOT, THIS AVOIDS DOS Attacks. SOLALA, IT CAN BLOCK OTHER CLIENTS..
                    socket = serverSocket.accept();
                    System.out.println("Accepted connection : " + socket);
                    bufferedReader = new BufferedReader(new InputStreamReader( socket.getInputStream() ));
                    String command = bufferedReader.readLine();
                    initClient(command);
                    do {
                        answerCommand(command);
                        command = bufferedReader.readLine();
                    }
                    while(!isExitCommand(command));

                    System.out.println("Done.");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) bis.close();
                    if (os != null) os.close();
                    if (socket != null) socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getClientName() {
        // TODO: Get Client name from authentification.
        return Statics.DEFAULT_CLIENT_NAME;
    }

    private void initClient(String command) {
        // TODO: Check authentification from command.

        filePathCa = CONFIG_FOLDER + "ca.crt";
        filePathCert = CONFIG_FOLDER + getClientName() + ".crt";
        filePathKey = CONFIG_FOLDER + getClientName() + ".key";
    }

    private boolean isExitCommand(String command) {
        return command.equals(EXIT);
    }

    private void answerCommand(String command) throws IOException {
        File fileToSend;
        System.out.println("Server Starte command: " + command);
        switch (command) {
            case SEND_CONFIG:
                sendConfigObject();
                break;
            case SEND_KEY:
                // TODO: Get right file for client. And implement authentication..
                fileToSend = new File (filePathKey);
                sendFile(fileToSend);
                break;
            case SEND_CRT:
                fileToSend = new File (filePathCert);
                sendFile(fileToSend);
                break;
            case SEND_CA:
                fileToSend = new File (filePathCa);
                sendFile(fileToSend);
                break;
            default:
                System.out.println("UNKNOW COMMAND:" + command);
        }
    }

    private void sendConfigObject() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(new ConfigState());
        oos.flush();
    }

    private void sendFile(File file) throws IOException {
        // TODO: IMPORTANT , ADD ENCRYPTION HERE!!
        byte [] byteArray  = new byte [(int)file.length()];
        fis = new FileInputStream(file);
        bis = new BufferedInputStream(fis);
        bis.read(byteArray,0,byteArray.length);
        os = socket.getOutputStream();

        // Send size, because EOF only will be sent on closing socket.
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(byteArray.length);
        oos.flush();

        System.out.println("Sending " + file.getName() + "(" + byteArray.length + " bytes)");
        os.write(byteArray,0,byteArray.length);
        os.flush();
        System.out.println("Sent");
        //socket.shutdownOutput();


        /*byte[] b = new byte[1024];
        int len = 0;
        int bytcount = 1024;
        FileOutputStream inFile = new FileOutputStream(file);
        InputStream is = socket.getInputStream();
        BufferedInputStream in2 = new BufferedInputStream(is, 1024);
        while ((len = in2.read(b, 0, 1024)) != -1) {
            bytcount = bytcount + 1024;
            inFile.write(b, 0, len);
        }*/
    }
}