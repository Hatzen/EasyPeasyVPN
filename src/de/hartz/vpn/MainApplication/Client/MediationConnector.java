package de.hartz.vpn.MainApplication.Client;

import de.hartz.vpn.Helper.Constants;
import de.hartz.vpn.MainApplication.UserData;

import java.io.IOException;
import java.net.*;

/**
 * Created by kaiha on 04.09.2017.
 */
public class MediationConnector {

    public MediationConnector() {
        String mediatorIp = UserData.getInstance().getMediatorList().get(0).getUrl();


        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        byte[] sendData = new byte[0];
        sendData = "CREATE:Cooles Netzwerk".getBytes();

        DatagramPacket sendPacket = null;
        try {
            sendPacket = new DatagramPacket(sendData,
                    sendData.length, InetAddress.getByName(mediatorIp), Constants.MEDIATION_SERVER_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // TEST JOIN
        sendData = "JOIN:Cooles Netzwerk".getBytes();
        try {
            sendPacket = new DatagramPacket(sendData,
                    sendData.length, InetAddress.getByName(mediatorIp), Constants.MEDIATION_SERVER_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }





        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
        try {
            clientSocket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = new String(receivePacket.getData());
        System.out.println("Response: " + response);
    }

}
