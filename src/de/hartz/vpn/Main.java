package de.hartz.vpn;

import de.hartz.vpn.Helper.UserData;
import de.hartz.vpn.Installation.InstallationController;
import de.hartz.vpn.MainApplication.MainFrame;

import java.awt.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        // TODO: If already configurated start mainframe..
        // TODO: Save config files persistent.
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
