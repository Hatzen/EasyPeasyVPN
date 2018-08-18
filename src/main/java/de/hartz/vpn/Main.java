package de.hartz.vpn;

import de.hartz.vpn.main.MainFrame;
import de.hartz.vpn.main.MediationConnector;
import de.hartz.vpn.main.UserData;
import de.hartz.vpn.main.installation.InstallationController;

import java.awt.*;
import java.util.Arrays;

import netact.modules.NetACTCommunicator;

/**
 * Main class the program starts with.
 * Can be started with gui or from console.
 */
public class Main {

    public static void main(String[] args) {
        MediationConnector.startSocket();

        NetACTCommunicator.sendState("Start");

        // TODO: TEST COMMAND LINE AGAIN! Espacially callback = null might be a problem..
        if ( args.length > 0) {
            if (args.length == 1 && args[0].equals("mediator")) {
                new de.hartz.vpn.mediation.Main();
            } else if (args.length == 2 && args[0].equals("server") && args[1].equals("express")) {
                UserData.getInstance().setClientInstallation(false);
                InstallationController.getInstance().startInstallation(false, false, null);
            } else if (args.length == 2 && args[0].equals("client")) {
                UserData.getInstance().setClientInstallation(true);
                UserData.getInstance().setServerIp(args[1]);
                InstallationController.getInstance().startInstallation(false, true, null);
            }
            else {
                System.out.println("invalid parameter " + Arrays.toString(args));
            }
        } else {
            // Is GUI not possible?
            if (GraphicsEnvironment.isHeadless()) {
                //InstallationController.getInstance().startInstallation(false, true,null);
            } else {
                new MainFrame();
            }
        }
    }
}
