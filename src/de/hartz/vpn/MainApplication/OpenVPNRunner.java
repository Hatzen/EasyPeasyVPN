package de.hartz.vpn.MainApplication;

import de.hartz.vpn.Helper.Helper;
import de.hartz.vpn.Utilities.Logger;
import de.hartz.vpn.Utilities.OutputStreamHandler;

import java.io.File;
import java.io.IOException;

/**
 * Created by kaiha on 02.07.2017.
 */
public class OpenVPNRunner extends Thread {
    private String configName;
    private Logger logger;

    public OpenVPNRunner(String configName, Logger logger) {
        this.configName = configName;
        this.logger = logger;
        start();
    }

    @Override
    public void run() {
        String configPath = Helper.getOpenVPNInstallationPath() + "config" + File.separator;
        ProcessBuilder pb = new ProcessBuilder( "openvpn ", configPath + configName );
        pb.redirectErrorStream(true);
        pb.directory(new File(configPath));
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream(), logger);
        outputHandler.start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int exitValue = process.exitValue();
        logger.addLogLine("OpenVPN exit: " + exitValue );
    }
}
