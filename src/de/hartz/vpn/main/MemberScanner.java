package de.hartz.vpn.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Created by kaiha on 07.10.2017.
 * https://demey.io/network-discovery-using-udp-broadcast/
 */
public class MemberScanner {

    /**
     * Listeners become notify when a member broadcasts.
     */
    interface Listener {
        void addMember(String ip, String name);
    }

    private static final String MEMBER_PREFIX = "EASY_PEASY_VPN:";
    private static final int PORT = 12894;
    private static final int SECONDS_TO_WAIT_FOR_PROPAGATE =  10;
    private static MemberScanner INSTANCE;
    
    private String ownUserName;
    private InetAddress broadcast;
    private static boolean run = false;
    private ArrayList<Listener> listeners;

    // Deny access.
    private MemberScanner(){}

    /**
     * Initialises this object with the necessary data.
     * @param ownUserName
     * @param broadcast
     */
    public static void init(String ownUserName, InetAddress broadcast) {
        if (INSTANCE == null) {
            INSTANCE = new MemberScanner(ownUserName, broadcast);
        } else {
            INSTANCE.ownUserName = ownUserName;
            INSTANCE.broadcast = broadcast;
        }

        if (!run) {
            run = true;
            new Thread(new Server()).start();
            new Thread(new Client()).start();
        }
    }

    /**
     * Returns the singelton instance. It needs to be initialised before!
     * @return
     */
    public static MemberScanner getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("MemberScanner not initialised.");
        }
        return INSTANCE;
    }

    /**
     * Adds a listener that gets called every time a member broadcasts.
     * @param listener
     */
    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    /**
     * Shutdown all sockets and threads.
     */
    public void shutdown() {
        if(run) {
            run = false;
            Server.socket.close();
            Client.socket.close();
        }
    }

    private MemberScanner(String ownUserName, InetAddress broadcast) {
        this.ownUserName = ownUserName;
        this.broadcast = broadcast;
        listeners = new ArrayList<>();
    }

    private void receivedMember(String ip, String name) {
        for (Listener c : listeners) {
            c.addMember(ip,name);
        }
    }

    private static class Server implements Runnable {
        static DatagramSocket socket;

        @Override
        public void run() {
            try {
                //Keep a socket open to listen to all the UDP trafic that is destined for this port
                socket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
                socket.setBroadcast(true);

                // TODO: Make clean stoppable.
                while (INSTANCE.run) {

                    //Wait for a response
                    byte[] recvBuf = new byte[15000];
                    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                    socket.receive(receivePacket);

                    //Check if the message is correct
                    String message = new String(receivePacket.getData()).trim();
                    if (message.contains(MEMBER_PREFIX)) {
                        String memberName = message.substring(MEMBER_PREFIX.length());
                        INSTANCE.receivedMember(receivePacket.getAddress().getHostAddress(), memberName);
                    }
                }
            } catch (SocketException ex) {
                // Do nothing. Normally is exited because socket closed
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class Client implements Runnable {
        static DatagramSocket socket;

        @Override
        public void run() {
            // Find the server using UDP broadcast
            try {
                //Open a random port to send the package
                socket = new DatagramSocket();
                socket.setBroadcast(true);
                byte[] sendData = (MEMBER_PREFIX + getInstance().ownUserName).getBytes(StandardCharsets.UTF_8);

                while(INSTANCE.run) {
                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, INSTANCE.broadcast, PORT);
                        socket.send(sendPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(SECONDS_TO_WAIT_FOR_PROPAGATE * 1000);
                }

                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
