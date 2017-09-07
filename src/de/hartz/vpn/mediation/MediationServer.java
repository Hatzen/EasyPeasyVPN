package de.hartz.vpn.mediation;

import de.hartz.vpn.utilities.NetworkUtilities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import static de.hartz.vpn.utilities.Constants.MEDIATION_SERVER_PORT;

/**
 * Created by kaiha on 04.09.2017.
 * https://github.com/lklacar/java-hole-punching/blob/master/server/src/UDPHolePunchingServer.java
 */
public class MediationServer extends Thread {

    class Network {
        public String name;
        public String ip;
        public int port;


        public Network(String name, String ip, int port) {
            this.name = name;
            this.ip = ip;
            this.port = port;
        }
    }

    private static final String CREATE_NETWORK_COMMAND = "CREATE:";
    private static final String JOIN_NETWORK_COMMAND = "JOIN:";

    private ArrayList<Network> registeredNetworks;
    private DatagramSocket mediationSocket;

    public MediationServer() {
        System.out.println("Initialize Mediator");
        registeredNetworks = new ArrayList<>();
        start();
    }

    @Override
    public void run() {
        System.out.println("Run Mediator");
        try {
            mediationSocket = new DatagramSocket(MEDIATION_SERVER_PORT);
            System.out.println("Successful listen on Port " + MEDIATION_SERVER_PORT + " for UDP Packets");
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            System.out.println("Waiting for packets..");
            DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
            try {
                mediationSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String command = null;
            try {
                command = new String(receivePacket.getData(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            InetAddress clientIP = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            processCommand(command, clientIP, clientPort);
        }

    }

    private void processCommand(String command, InetAddress clientIP, int clientPort) {
        System.out.println(command + " from " + clientIP.getHostAddress() + ":" + clientPort);
        if (command.contains(CREATE_NETWORK_COMMAND)) {
            String name = command.substring(CREATE_NETWORK_COMMAND.length());
            name = NetworkUtilities.getCleanString(name);
            System.out.println("Created Network " + name);
            Network n = new Network(name, clientIP.getHostAddress(), clientPort);
            registeredNetworks.add(n);
        } else if (command.contains(JOIN_NETWORK_COMMAND)) {
            String name = command.substring(JOIN_NETWORK_COMMAND.length());
            name = NetworkUtilities.getCleanString(name);
            Network network = getNetworkByName(name);
            System.out.println("JOIN Network: " + name);

            String response = "ERROR";
            if (network != null) {
                response = network.ip + ":" + network.port;
            } else {
                System.out.println("Network: " + name + " does not exists yet.");
            }
            try {
                mediationSocket.send(new DatagramPacket(response.getBytes("UTF-8"),
                        response.getBytes("UTF-8").length, clientIP, clientPort));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Network getNetworkByName(String name) {
        for(Network n : registeredNetworks) {
            System.out.println(n.name + "=" + name + "=" + n.name.equals(name));
            if (n.name.equals(name)) {
                return n;
            }
        }
        return null;
    }

}
