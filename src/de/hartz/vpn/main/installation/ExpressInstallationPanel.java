package de.hartz.vpn.main.installation;

import de.hartz.vpn.helper.Linux;
import de.hartz.vpn.helper.OutputStreamHandler;
import de.hartz.vpn.helper.Windows;
import de.hartz.vpn.main.installation.server.ConfigOpenVPN;
import de.hartz.vpn.utilities.Helper;
import de.hartz.vpn.utilities.OpenVPNHelper;
import de.hartz.vpn.utilities.UiHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Class that installs the openvpn adapter.
 *
 * https://examples.javacodegeeks.com/core-java/lang/processbuilder/java-lang-processbuilder-example/
 */
public class ExpressInstallationPanel extends InstallationPanel {

    private final static String LINUX_DOWNLOAD_LINK = "http://build.openvpn.net/downloads/releases/latest/openvpn-latest-stable.tar.gz";
    private final static String WINDOWS_DOWNLOAD_LINK = "http://build.openvpn.net/downloads/releases/latest/openvpn-install-latest-stable.exe";

    private boolean isInstalled;
    private JTextArea outputTextArea;

    public ExpressInstallationPanel() {
        isInstalled = false;

        setLayout( new BorderLayout());
        outputTextArea = new JTextArea();
        outputTextArea.setEnabled(false);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void onSelect() {
        startExternalInstallation();
    }

    public void startExternalInstallation() {
        addLineToOutput("Start OpenVPN Installer");
        if (isAlreadyInstalled()) {
            addLineToOutput("Already installed, skipping installation.");
            onInstallationSuccess();
            return;
        }
        // TODO: Check for admin rights only on windows? No, apt-get usually needs sudo.
        if ( !Helper.isAdmin() ) {
            addLineToOutput("Program not launched as admin. Please start again with permissions. They are needed to install the vpn adapter");
            if(InstallationController.hasGUI())
                UiHelper.showAlert("Program not launched as admin. Please start again with permissions. They are needed to install the vpn adapter");
            return;
        }
        if (Helper.isWindows()) {
            startWindowsInstallation();
        } else if (Helper.isLinux()) {
            startLinuxInstallation();
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }

    private String getTarCreatedFolder(String tarFolder) {
        File[] files = new File(tarFolder).listFiles();
        for (File file: files) {
            System.out.println(file.getName());
            if (file.getName().contains("openvpn") && file.isDirectory()) {
                return file.getName();
            }
        }
        return null;
    }

    @Linux
    private void startLinuxInstallation() {
        try {
            // TODO: Check for different distributions and use their package manager.
            if (Helper.hasAPT()) {
                installViaAPT();
            } else {
                installOnLinuxWithoutPackageManager();
            }
        } catch (Exception e) {
            addLineToOutput("Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        finally {
            Helper.deleteTempDirectory();
        }
    }

    @Linux
    private void installViaAPT() throws Exception {
        ProcessBuilder pb = new ProcessBuilder("apt-get", "update");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());
        outputHandler.start();
        process.waitFor();
        addLineToOutput(outputHandler.getOutput().toString());

        pb = new ProcessBuilder("apt-get", "--yes", "--force-yes", "install", "openvpn");
        pb.redirectErrorStream(true);
        process = pb.start();
        outputHandler = new OutputStreamHandler(process.getInputStream());
        outputHandler.start();
        process.waitFor();
        addLineToOutput(outputHandler.getOutput().toString());
        int exitValue = process.exitValue();

        if (exitValue == 0) {
            onInstallationSuccess();
        } else {
            addLineToOutput("Error by installation, exitcode: " + exitValue);
        }
    }

    @Linux
    private void installOnLinuxWithoutPackageManager() throws Exception {
        // TODO: Fix dependency hell, openssl have to be installed too..
        // Currently openvpn is installed, but openssl is missing. So this is quite useless until openssl is added..

        boolean notCompletedYet = true;
        if (notCompletedYet) {
            addLineToOutput("Error: Currently only apt is supported on linux. installation aborted.");
            return;
        }

        File source = Helper.getResourceAsFile("resources/installer/openvpn-latest-stable.tar.gz");
        File dest = new File(Helper.getTempDirectory(), source.getName());
        String tarFolder = dest.getParent();
        copyFile(source, dest);

        String[][] commands = {{"tar", "-xzf", dest.getName()}, {"./configure"}, {"make"}, {"make install"}}; //
        int exitValue = 0;
        isInstalled = true;
        for (String[] command : commands) {
            System.out.println(Arrays.toString(command));
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(tarFolder));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());
            outputHandler.start();
            process.waitFor();
            addLineToOutput(outputHandler.getOutput().toString());
            exitValue = process.exitValue();
            System.out.println("" + exitValue);
            System.out.println("---");

            if (exitValue != 0) {
                isInstalled = false;
                addLineToOutput("Error by installation, exitcode: " + exitValue);
                break;
            }
            tarFolder = dest.getParent() + "/" + getTarCreatedFolder(tarFolder) + "/";
        }

        if (isInstalled) {
            onInstallationSuccess();
        } else {
            addLineToOutput("Error by installation, exitcode: " + exitValue);
        }
    }

    private boolean isAlreadyInstalled() {
        return (OpenVPNHelper.getOpenVPNInstallationPath() != null);
    }

    @Windows
    private void startWindowsInstallation() {
        try {
            /*
            //TODO: Download to have current version.
            URL website = new URL(WINDOWS_DOWNLOAD_LINK);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(temp);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            // write to source.
            */

            File source = Helper.getResourceAsFile("resources/installer/openvpn-install-2.4.2-I601.exe");
            File dest = new File(Helper.getTempDirectory(), source.getName());
            String filePath = dest.getPath();
            copyFile(source ,dest);

            ProcessBuilder pb = new ProcessBuilder( filePath );
            Process process = pb.start();
            OutputStreamHandler outputHandler = new OutputStreamHandler(process.getInputStream());
            outputHandler.start();

            InstallationController.getInstance().setMainFrameVisible(false);
            process.waitFor();
            InstallationController.getInstance().setMainFrameVisible(true);
            addLineToOutput(outputHandler.getOutput().toString());
            int exitValue = process.exitValue();

            if (exitValue == 0) {
                onInstallationSuccess();
            } else {
                addLineToOutput("Error by installation, exitcode: " + exitValue);
            }
        } catch (Exception e) {
            addLineToOutput("Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        finally {
            Helper.deleteTempDirectory();
        }
    }

    private void onInstallationSuccess() {
        addLineToOutput("installation successful.");
        isInstalled = true;

        if (InstallationController.getInstance().isClientInstallation()) {
            new de.hartz.vpn.main.installation.client.ConfigOpenVPN();
        } else {
            new ConfigOpenVPN(InstallationController.getInstance().getTmpConfigState(), this);
        }
    }

    public void addLineToOutput(String line) {
        // Discard empty lines.
        if (line.length() == 0)
            return;
        String logLine = "> " + line + System.getProperty("line.separator");
        if (InstallationController.hasGUI()) {
            outputTextArea.append(logLine);
        } else {
            System.out.print(logLine);
        }
    }

    @Override
    public boolean onDeselect() {
        if (!isInstalled) {
            UiHelper.showAlert("installation failed. See the log for the error and try again.");
            return false;
        }
        return true;
    }

    @Override
    public void addLogLine(String line) {
        addLineToOutput(line);
    }
}
