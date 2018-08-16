package de.hartz.vpn.main;

import de.hartz.vpn.mediation.Mediator;
import de.hartz.vpn.utilities.Constants;
import de.hartz.vpn.utilities.NetworkUtilities;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by kaiha on 04.09.2017.
 */
public class MediationConnector {


    static DatagramSocket clientSocket;

    public static void startSocket() {
        // TODO: Remove as soon as mediation is working..
        if (true) {
            return;
        }

        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
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

        // TODO: Remove as soon as mediation is working..
        if (true) {
            return "0:0";
        }

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
    public static void registerNetwork(String networkName) {
        // TODO: Remove as soon as mediation is working..
        if (true) {
            return;
        }

        registerNetworkAtMediator(networkName, getDefaultMediator());
    }

    public static void registerNetworkAtMediator(String networkName, Mediator mediator) {
        try {
            byte[] sendData = ("CREATE:" + networkName).getBytes("UTF-8");
            DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, InetAddress.getByName(mediator.getUrl()), Constants.MEDIATION_SERVER_PORT);

            clientSocket.send(sendPacket);
            // TODO: HOLEPUNCHING DOESNT WORK THIS WAY!!!!!! NAT PORT changes by every method call. Because of closing the socket? YES, as well indirect closing by losing reference...
            //clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
