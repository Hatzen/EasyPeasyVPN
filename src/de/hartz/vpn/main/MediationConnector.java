package de.hartz.vpn.main;

import de.hartz.vpn.mediation.Mediator;
import de.hartz.vpn.utilities.Constants;
import de.hartz.vpn.utilities.NetworkUtilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by kaiha on 04.09.2017.
 */
public class MediationConnector {

    private final static MediationConnector INSTANCE = new MediationConnector();

    private DatagramSocket clientSocket;
    private ServerLocalRedirector slr;

    public void startSocket() {
        try {
            clientSocket = new DatagramSocket();
            slr = new ServerLocalRedirector(clientSocket, new DatagramSocket());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void shutdownSocket() {
        slr.run = false;
        clientSocket.close();
    }

    public static MediationConnector getInstance() {
        return INSTANCE;
    }

    private MediationConnector() {
    }



    /**
     * Returns the Ip and port that got hole punched via udp.
     * @param networkName
     * @returns ip:port
     */
    public String getAccessibleNetworkAddress(String networkName) {
        return getAccessibleNetworkAddressAtMediator(networkName, getDefaultMediator());
    }

    public String getAccessibleNetworkAddressAtMediator(String networkName, Mediator mediator) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData = ("JOIN:" + networkName).getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, InetAddress.getByName(mediator.getUrl()), Constants.MEDIATION_SERVER_PORT);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
            clientSocket.receive(receivePacket);
            String response = new String(receivePacket.getData());
            response = NetworkUtilities.getCleanString(response);
            System.out.println("Response: " + response);
            if(response.equals("ERROR"))
                return null;
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Mediator getDefaultMediator() {
        return UserData.getInstance().getMediatorList().get(0);
    }

    /**
     * Registers a network at the default mediator.
     * @param networkName
     */
    public void registerNetwork(String networkName) {
        registerNetworkAtMediator(networkName, getDefaultMediator());
    }

    public void registerNetworkAtMediator(String networkName, Mediator mediator) {
        try {
            byte[] sendData = ("CREATE:" + networkName).getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, InetAddress.getByName(mediator.getUrl()), Constants.MEDIATION_SERVER_PORT);

            // TODO: Remove local test mediator.

            //synchronized (clientSocket) {
                clientSocket.send(sendPacket);
                System.out.println("CREATED " + networkName + " on " + mediator.getUrl() + ":" +  Constants.MEDIATION_SERVER_PORT);
            //}
            // TODO: HOLEPUNCHING DOESNT WORK THIS WAY!!!!!! NAT PORT changes by every method call. Because of closing the socket? YES, as well indirect closing by losing reference...

        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }



    class ServerLocalRedirector extends Thread {

        /*
        // TODO: Implemnent Holepunching for tcp... FUTURE FEATURE.
        class OneDirectionTCP extends Thread {
            Socket s1;
            Socket s2;

            public OneDirectionTCP(Socket s1, Socket s2) {
                this.s1 = s1;
                this.s2 = s2;
            }

            public void run() {
                try {
                    InputStream is = s1.getInputStream();
                    OutputStream os = s2.getOutputStream();
                    for (int i; (i = is.read()) != -1; i++) {
                        os.write(i);

                        if(is.available() == 0) {
                            os.flush();
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
         */

        private int clientPort;
        private InetAddress clientAddress;
        DatagramSocket s1;
        DatagramSocket s2;
        boolean run = true;

        public ServerLocalRedirector(DatagramSocket s1, DatagramSocket s2) {
            this.s1 = s1;
            this.s2 = s2;
            start();
        }


        // TODO: Liste von allen IP:PORt von denen man recieved und immer an diese senden.. (?)
        public void run() {
            try {
                while (run) {
                    DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
                    s1.receive(receivePacket);

                    DatagramPacket sendPacket;
                    if ( receivePacket.getAddress() != InetAddress.getLoopbackAddress()) {
                        clientPort = receivePacket.getPort();
                        clientAddress = receivePacket.getAddress();
                        sendPacket = new DatagramPacket(receivePacket.getData(),
                                receivePacket.getLength(), InetAddress.getLoopbackAddress(), 1194);
                    } else {
                        sendPacket = new DatagramPacket(receivePacket.getData(),
                                receivePacket.getLength(), clientAddress, clientPort);
                    }
                    //synchronized (clientSocket) {
                        clientSocket.send(sendPacket);
                    //}
                }
            } catch (IOException e) {
                // Socket closed.
            }
        }
    }

}
