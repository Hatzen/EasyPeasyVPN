package de.hartz.vpn.Installation.Client;

// TODO: Why this doesnt work? Or at least intellij show erros...
import de.hartz.vpn.Helper.Helper;
import de.hartz.vpn.Helper.Statics;
import de.hartz.vpn.Helper.UserData;
import de.hartz.vpn.MainApplication.Server.ConfigState;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static de.hartz.vpn.MainApplication.Server.SimpleFileServer.*;


/**
 * Created by kaiha on 21.06.2017.
 */

// TODO: RENAME
public class SimpleFileClient extends Thread {

    interface ClientListener {
        void onError(Exception e);
        void onCommandFinished(String command);
        void onFinish();
    }

    public static final String FILE_PATH_CA = Helper.getOpenVPNInstallationPath() + "client.ca";
    public static final String FILE_PATH_CRT = Helper.getOpenVPNInstallationPath() + "client.crt";
    public static final String FILE_PATH_KEY = Helper.getOpenVPNInstallationPath() + "client.key";
    //public final static int FILE_SIZE = Integer.MAX_VALUE; //doesnt work because its to large..

    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private PrintWriter printWriter;
    private Socket socket = null;

    private ClientListener clientListener;
    private String serverIp;
    private ArrayList<String> commands;

    public SimpleFileClient( ClientListener clientListener ) {
        this.clientListener = clientListener;
        this.serverIp = UserData.serverIp;
        commands = new ArrayList<>();
        commands.add(SEND_CONFIG);
    }

    @Override
    public void run() {
        try {
            socket = new Socket(serverIp, Statics.MEDIATION_SERVER_PORT);
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
        System.out.println("Client Starte command: " + command);
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

        UserData.setVpnConfigState(config);
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
        byte[] bytes = new byte[byteCount];
        is.read(bytes);
        fos.write(bytes, 0, byteCount);

        fos.close();
    }
}