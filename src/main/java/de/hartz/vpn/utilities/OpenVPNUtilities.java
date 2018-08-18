package de.hartz.vpn.utilities;

import de.hartz.vpn.helper.OutputStreamHandler;
import de.hartz.vpn.main.UserData;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by kaiha on 31.08.2017.
 */
public final class OpenVPNUtilities {

    //TODO: If possible get rid off..
    // @Windows
    public static String openVPNBinPath;

    /**
     * TODO: TIDY UP!!!! MODULARIZE.
     * TODO: Get working under linux?! And maybe same concept to find openssl?
     * Finds the installation path of openvpn through the environment variables.
     * @return The installation path or null if not found.
     */
    public static String getOpenVPNInstallationPath() {
        if (GeneralUtilities.isWindows()) {
            String findValue = "OpenVPN";

            // After installations getenv() returns old values, missing openvpn...
            // HACK AROUND THIS;
            // https://stackoverflow.com/questions/10434065/how-to-retrieve-the-modified-value-of-an-environment-variable-which-is-modified
            ProcessBuilder pb = new ProcessBuilder( "cmd.exe");
            pb.redirectErrorStream(true);
            Process process = null;
            try {
                process = pb.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());
            PrintWriter commandExecutor = new PrintWriter(process.getOutputStream());
            commandExecutor.println("reg query \"HKLM\\SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment\"\n");
            commandExecutor.println("exit");
            commandExecutor.flush();
            commandExecutor.close();
            outputHandler.start();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String pathVar = outputHandler.getOutput().toString();
            //TODO: MODULARISE..
            if (pathVar.contains(findValue)) {
                int indexOfFindValue = pathVar.indexOf(findValue);
                // if it should be bin path..  value.indexOf(";", indexOfFindValue)
                openVPNBinPath = pathVar.substring( pathVar.lastIndexOf(";" ,indexOfFindValue)+1, pathVar.indexOf("bin", indexOfFindValue) + "bin".length() );
                String path = pathVar.substring( pathVar.lastIndexOf(";" ,indexOfFindValue)+1, indexOfFindValue + findValue.length() );
                if (path.charAt(path.length()-1) != File.separator.charAt(0)) {
                    path += File.separator;
                }
                return path;
            }
            //END OF: HACK AROUND THIS;


        } else {
            // TODO: Files in this folder might be auto started. Wanted?
            String filePath = "/etc/openvpn/";
            File installationPath = new File(filePath);
            if(installationPath.exists())
                return filePath;
            else if (installationPath.mkdirs()) {
                return filePath;
            }
            System.err.println(filePath + " cannot create directories ");
        }
        return null;
    }

    public static String getOpenVPNConfigPath() {
        String installationPath = OpenVPNUtilities.getOpenVPNInstallationPath();
        if(installationPath == null) {
            System.err.println("OpenVPN not FOUND! Was it uninstalled?");
            return null;
        }
        String configFilename = "client";
        if (!UserData.getInstance().isClientInstallation()) {
            configFilename = "server";
        }
        String configName = configFilename + GeneralUtilities.getOpenVPNConfigExtension();
        String configPath = installationPath + "config" + File.separator;

        return configPath + configName;
    }

    /**
     * Checks if OpenSSL/ OpenVPN is available on command line. Needed if openvpn is fresh installed and path is not ready yet.
     * @return true if path needs an extension.
     */
    public static boolean needsPathUpdate() {
        String findValue = "OpenVPN";
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            String value = env.get(envName);
            if (envName.contains(findValue) || value.contains(findValue) ) {
                return false;
            }
        }
        return true;
    }

}
