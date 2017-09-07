package de.hartz.vpn.main.installation.client;

import de.hartz.vpn.main.UserData;
import de.hartz.vpn.main.installation.InstallationController;
import de.hartz.vpn.main.server.ConfigState;
import de.hartz.vpn.utilities.Constants;
import de.hartz.vpn.utilities.NetworkHelper;
import de.hartz.vpn.utilities.OpenVPNHelper;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static de.hartz.vpn.main.server.MetaServer.*;


/**
 * Meta client to get the network config file and the needed certificate.
 */
public class MetaClient extends Thread {

    interface ClientListener {
        void onError(Exception e);
        void onCommandFinished(String command);
        void onFinish();
    }

    public static final String FILE_PATH_CA = OpenVPNHelper.getOpenVPNInstallationPath() + "client.ca";
    public static final String FILE_PATH_CRT = OpenVPNHelper.getOpenVPNInstallationPath() + "client.crt";
    public static final String FILE_PATH_KEY = OpenVPNHelper.getOpenVPNInstallationPath() + "client.key";

    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private PrintWriter printWriter;
    private Socket socket = null;

    private ClientListener clientListener;
    private String serverIp;
    private ArrayList<String> commands;

    public MetaClient(ClientListener clientListener ) {
        this.clientListener = clientListener;
        this.serverIp = UserData.serverIp;
        commands = new ArrayList<>();
        commands.add(SEND_CONFIG);
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIp, Constants.META_SERVER_PORT);
            System.out.println("Connecting...");

            printWriter = new PrintWriter( socket.getOutputStream() );
            // Can not be for each loop. Cause of modifications on the list.
            for (int i = 0; i < commands.size(); i++) {
                String command = commands.get(i);
                printWriter.println(command);
                printWriter.flush();
                receiveAnswer(command);
            }
            clientListener.onFinish();
        } catch (IOException e) {
            clientListener.onError(e);
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
                if (bos != null) bos.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveAnswer(String command) throws IOException {
        File fileToReceive;
        System.out.println("client Starte command: " + command);
        switch (command) {
            case SEND_CONFIG:
                receiveConfigObject();
                break;
            case SEND_CRT:
                fileToReceive = new File (FILE_PATH_CRT);
                receiveFile(fileToReceive);
                break;
            case SEND_CA:
                fileToReceive = new File (FILE_PATH_CA);
                receiveFile(fileToReceive);
                break;
            case SEND_KEY:
                fileToReceive = new File (FILE_PATH_KEY);
                receiveFile(fileToReceive);
                break;
            default:
                System.out.println("UNKNOW COMMAND:" + command);
        }
        clientListener.onCommandFinished(command);
    }

    private void receiveConfigObject() throws IOException {
        ObjectInputStream oos = new ObjectInputStream(socket.getInputStream());
        ConfigState config = null;
        try {
            config = (ConfigState) oos.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot happen!");
            e.printStackTrace();
        }

        if (config.getAdapter() == ConfigState.Adapter.OpenVPN ) {
            commands.add(SEND_CA);
            commands.add(SEND_CRT);
            // TODO: Append auth data to key command.
            commands.add(SEND_KEY);
        }
        commands.add(EXIT);

        InstallationController.getInstance().setTmpConfigState(config);
    }

    private void receiveFile(File file) throws IOException {
        // TODO: Under linux there is a warning saying file is group or other accessible. Avoid it.
        InputStream is = socket.getInputStream();
        fos = new FileOutputStream(file);

        // Get byte count.
        ObjectInputStream oos = new ObjectInputStream(is);
        int byteCount = 0;
        try {
            byteCount = (Integer) oos.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Get bytes.
        byte[] byteArray = new byte[byteCount];
        is.read(byteArray);

        NetworkHelper.AdvancedEncryptionStandard aes = new NetworkHelper.AdvancedEncryptionStandard();
        try {
            byteArray = aes.decrypt(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fos.write(byteArray, 0, byteArray.length);

        fos.close();
    }
}