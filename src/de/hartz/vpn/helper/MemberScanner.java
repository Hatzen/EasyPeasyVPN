package de.hartz.vpn.helper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * Created by kaiha on 07.10.2017.
 * https://demey.io/network-discovery-using-udp-broadcast/
 */
public class MemberScanner {

    private static final String MEMBER_PREFIX = "EASY_PEASY_VPN:";

    private static MemberScanner INSTANCE;
    private String ownUserName;
    private InetAddress broadcast;

    private static boolean run = false;

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

    private MemberScanner(String ownUserName, InetAddress broadcast) {
        this.ownUserName = ownUserName;
        this.broadcast = broadcast;
    }

    private void receivedMember(String ip, String name) {
        // TODO: Notify callbacks..
    }

    private static class Server implements Runnable {

        @Override
        public void run() {
            try {
                //Keep a socket open to listen to all the UDP trafic that is destined for this port
                DatagramSocket socket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
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
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class Client implements Runnable {

        @Override
        public void run() {
            // TODO: Loop and send it every 30 secs

            // Find the server using UDP broadcast
            try {
                //Open a random port to send the package
                DatagramSocket c = new DatagramSocket();
                c.setBroadcast(true);
                byte[] sendData = getInstance().ownUserName.getBytes(StandardCharsets.UTF_8);

                while(INSTANCE.run) {
                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, INSTANCE.broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Thread.sleep(30000);
                }

                c.close();
                //Close the port!
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
