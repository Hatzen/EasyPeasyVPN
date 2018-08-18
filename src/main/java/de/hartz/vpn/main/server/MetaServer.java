package de.hartz.vpn.main.server;

import de.hartz.vpn.main.UserData;
import de.hartz.vpn.utilities.Constants;
import de.hartz.vpn.utilities.NetworkUtilities;
import de.hartz.vpn.utilities.OpenVPNUtilities;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * This class represents a meta server that handles the exchange of the necessary config and certificates to get
 * communication working.
 * Based on http://www.rgagnon.com/javadetails/java-0542.html
 */
public class MetaServer extends Thread {
    private static final String CONFIG_FOLDER = OpenVPNUtilities.getOpenVPNInstallationPath() + "easy-rsa" + File.separator + "keys" + File.separator;
    private static MetaServer instance;
    private static boolean run;
    private static boolean running;

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

    private MetaServer() {

    }

    /**
     * Singelton cause only one server can listen to that port.
     * @returns the one and only server.
     */
    public static MetaServer getInstance() {
        if (instance == null) {
            instance = new MetaServer();
            run = true;
        }
        return instance;
    }

    /**
     * Starts the server if needed.
     */
    public void startServer() {
        // Check if already started
        /* Thread.State currState = Thread.currentThread().getState();
        if (currState != Thread.State.NEW && currState != Thread.State.TERMINATED) {
            return;
        } */

        if(running) {
            return;
        }
        super.start();
    }

    /**
     * Deny access to thread method to ensure that {@link MetaServer#startServer()} is used.
     */
    @Override
    public void start() {
        throw new RuntimeException("Start the server with startServer Method!");
    }

    /**
     * Function to stop the server from listening.
     */
    public void stopServer() {
        run = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        instance = null;
        running = false;
    }

    /**
     * Check whether the server socket has crashed.
     * @returns true if the meta server is still running.
     */
    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Constants.META_SERVER_PORT);
            running = true;
            while (run) {
                System.out.println("Waiting...");
                try {
                    // TODO: Check if accept has to run in own thread. So multipy clients can connect at same time..
                    // TODO: MAYBE NOT, THIS AVOIDS DOS Attacks. SOLALA, IT CAN BLOCK OTHER CLIENTS..
                    try {
                        socket = serverSocket.accept();
                    } catch (SocketException e) {
                        if (!run) {
                            // Socket closed.
                            break;
                        } else {
                            System.err.println("Error while listening");
                        }
                    }
                    System.out.println("Accepted connection : " + socket);
                    bufferedReader = new BufferedReader(new InputStreamReader( socket.getInputStream() ));
                    String command = bufferedReader.readLine();
                    initClient(command);
                    do {
                        answerCommand(command);
                        command = bufferedReader.readLine();
                        if(command == null) {
                            System.err.print("client connection interruption");
                        }
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
            running = false;
            if (serverSocket != null) try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getClientName() {
        // TODO: Get client name from authentification.
        return Constants.DEFAULT_CLIENT_NAME;
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
        System.out.println("server Starte command: " + command);
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
        // TODO: Use encryption here too. But its not so important.
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.reset();
        oos.flush();
        oos.writeObject(UserData.getInstance().getVpnConfigState());
        oos.flush();
    }

    private void sendFile(File file) throws IOException {
        if (file.length() > Integer.MAX_VALUE) {
            throw new IOException("File size too large.");
        }
        byte [] byteArray  = new byte [(int)file.length()];
        fis = new FileInputStream(file);
        bis = new BufferedInputStream(fis);
        bis.read(byteArray);

        int rest = (16 - (byteArray.length % 16));
        if(rest != 0) {
            byteArray = Arrays.copyOf(byteArray, byteArray.length + rest);
        }
        NetworkUtilities.AdvancedEncryptionStandard aes = new NetworkUtilities.AdvancedEncryptionStandard();
        try {
            byteArray = aes.encrypt(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        os = socket.getOutputStream();
        //os.flush();

        // Send size, because EOF only will be sent on closing socket.
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(new Integer(byteArray.length));
        oos.flush();
        oos.writeObject(new Integer(rest));
        oos.flush();

        System.out.println("Sending " + file.getName() + "(" + byteArray.length + " bytes)");
        os.write(byteArray);
        os.flush();
        System.out.println("Sent");
    }
}