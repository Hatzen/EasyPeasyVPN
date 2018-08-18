package de.hartz.vpn.main;

import de.hartz.vpn.helper.Logger;
import de.hartz.vpn.helper.OutputStreamHandler;
import de.hartz.vpn.utilities.GeneralUtilities;
import de.hartz.vpn.utilities.OpenVPNUtilities;

import java.io.File;
import java.io.IOException;

/**
 * This class executes a openvpn process with a given config file.
 */
public class OpenVPNRunner extends Thread {
    private String configName;
    private Logger logger;

    private boolean running;
    private Process openVPNProcess;

    public OpenVPNRunner(String configName, Logger logger) {
        this.configName = configName;
        this.logger = logger;
        start();
    }

    @Override
    public void run() {
        String installationPath = OpenVPNUtilities.getOpenVPNInstallationPath();
        if(installationPath == null) {
            System.err.println("OpenVPN not FOUND! Was it uninstalled?");
            return;
        }
        running = true;
        String configPath = installationPath + "config" + File.separator;

        System.out.println(installationPath + "bin" + File.separator + "openvpn" + " & " + configPath + configName);

        String command = "openvpn";
        if (GeneralUtilities.isWindows()) {
            command = installationPath + "bin" + File.separator + "openvpn";
        }

        ProcessBuilder pb = new ProcessBuilder( command, configPath + configName );
        pb.redirectErrorStream(true);
        pb.directory(new File(configPath));
        try {
            openVPNProcess = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStreamHandler outputHandler = new OutputStreamHandler(openVPNProcess.getInputStream(), logger);
        outputHandler.start();
        try {
            openVPNProcess.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int exitValue = openVPNProcess.exitValue();
        logger.addLogLine("OpenVPN exit: " + exitValue );

        running = false;
    }

    public void exitProcess() {
        if(openVPNProcess != null)
            openVPNProcess.destroy();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
