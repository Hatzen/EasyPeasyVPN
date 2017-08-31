package de.hartz.vpn;

import de.hartz.vpn.Installation.InstallationController;
import de.hartz.vpn.MainApplication.MainFrame;
import de.hartz.vpn.MainApplication.UserData;

import java.awt.*;
import java.util.Arrays;

/**
 * Main class the program starts with.
 * Can be started with gui or from console.
 */
public class Main {

    public static void main(String[] args) {

        new de.hartz.vpn.MediationServer.Main();
        if (true) {
            new MainFrame();
            return;
        }

        if ( args.length > 0) {
            if (args.length == 2 && args[0].equals("server") && args[0].equals("express")) {
                UserData.clientInstallation = false;
                InstallationController.getInstance().startInstallation(false);
            } else if (args.length == 2 && args[0].equals("client")) {
                UserData.clientInstallation = true;
                UserData.serverIp = args[1];
                InstallationController.getInstance().startInstallation(false);
            }
            else {
                System.out.println("invalid parameter " + Arrays.toString(args));
            }
        } else {
            // Is GUI not possible?
            if (GraphicsEnvironment.isHeadless()) {
                InstallationController.getInstance().startInstallation(false);
            } else {
                InstallationController.getInstance().startInstallation(true);
            }
        }
    }
}
