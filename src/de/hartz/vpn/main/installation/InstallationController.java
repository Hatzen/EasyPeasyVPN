package de.hartz.vpn.main.installation;

import de.hartz.vpn.main.UserData;
import de.hartz.vpn.main.installation.client.ConnectToServerPanel;
import de.hartz.vpn.main.installation.server.*;
import de.hartz.vpn.main.server.ConfigState;
import de.hartz.vpn.utilities.GeneralUtilities;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The installation controller that can show a given order of panels.
 */
public class InstallationController {

    /**
     * Interface to notify the caller after installation.
     */
    public interface InstallationCallback {

        void onInstallationSuccess();
        void onInstallationCanceled();

    }

    private static InstallationController instance = new InstallationController();
    private InstallationFrame mainFrame;
    private InstallationCallback callback;

    private ConfigState tmpConfigState;
    private boolean clientInstallation;

    private static boolean hasGUI;

    private ArrayList<InstallationPanel> currentPanelOrder;
    private ArrayList<InstallationPanel> clientPanelOrder;
    private ArrayList<InstallationPanel> expressPanelOrder;
    private ArrayList<InstallationPanel> customPanelOrder;

    /**
     * Returns the singleton instance of this class
     * @return
     */
    public static InstallationController getInstance() {
        return instance;
    }

    /**
     * Getter to see if the user has a graphical UI.
     * @returns true if it is graphical.
     */
    public static boolean hasGUI() {
        return hasGUI;
    }

    /**
     * Listener that gets called when the next button was clicked.
     * @param currentPanel The current panel on which the next button was clicked.
     */
    public void onNextClick(InstallationPanel currentPanel) {
        if (currentPanel instanceof StartPanel) {
            currentPanelOrder.clear();
            if (((StartPanel) currentPanel).isExpressInstallation()) {
                currentPanelOrder.addAll(expressPanelOrder);
            } else {
                currentPanelOrder.addAll(customPanelOrder);
            }
        }

        showNextPanel(currentPanel);
    }

    /**
     * Listener that gets called when the back button was clicked.
     * @param currentPanel
     */
    public void onPreviousClick(JPanel currentPanel) {
        showPreviousPanel(currentPanel);
    }

    /**
     * Returns whether a panel is the first panel to show.
     * @param currentPanel The panel to check for.
     * @return
     */
    public boolean isFirst(JPanel currentPanel) {
        return currentPanelOrder.indexOf(currentPanel) == 0;
    }

    /**
     * Returns whether a panel is the last panel to show.
     * @param currentPanel The panel to check for.
     * @return
     */
    public boolean isLast(JPanel currentPanel) {
        return currentPanelOrder.indexOf(currentPanel) == currentPanelOrder.size()-1;
    }

    /**
     * Starts the installation process.
     * @param showGUI Boolean indicating whether the application is started via console.
     */
    public void startInstallation(boolean showGUI, boolean client, InstallationCallback callback) {
        tmpConfigState = new ConfigState();
        clientInstallation = client;
        hasGUI = showGUI;
        this.callback = callback;
        if (showGUI) {
            // TODO: GUI doesnt react some time. Because of extracting files etc.. Create loading screen. Also might be initalization of all the panels!!
            initGUI();

            if(client) {
                System.out.println("client installation");
                currentPanelOrder.clear();
                currentPanelOrder.addAll(clientPanelOrder);
            } else {
                System.out.println("server installation");
                currentPanelOrder.clear();
                currentPanelOrder.addAll(expressPanelOrder);
            }


            showPanel(0);
        } else {
            drawLogoWithoutGUI();
            if (client)
                new ExpressInstallationPanel().startExternalInstallation();
        }
    }

    /**
     * Sets the installation frames visibility. Not possible if it was started without GUI.
     * @param visible Boolean indicating whether the frame should be visible.
     */
    public void setMainFrameVisible(boolean visible) {
        if (!hasGUI)
            return;
        mainFrame.setVisible(visible);
    }

    /**
     * Adds a specific panel to the client panel order. After the config file has loaded, so it can decide which adapter to install.
     */
    public void addClientPanel() {
        if (getTmpConfigState().getAdapter() == ConfigState.Adapter.OpenVPN) {
            int i = 0;
            while( i < currentPanelOrder.size()) {
                if (currentPanelOrder.get(i) instanceof ConnectToServerPanel) {
                    break;
                }
                i++;
            }
            currentPanelOrder.add(i+1, new ExpressInstallationPanel());
        }
    }

    /**
     * Can be called to force going to the next panel.
     * Can produce errors. Should only be called if there is work to be done before going to the next panel.
     */
    public void forceNextPanel(InstallationPanel currentPanel) {
        int nextIndex = currentPanelOrder.indexOf(currentPanel)+1;
        showPanel(nextIndex);
    }

    /**
     * Returns the temporarily {@link ConfigState} of the network. After successfull installation it will be moved
     * to {@link UserData}
     * @returns the tmpConfigState.
     */
    public ConfigState getTmpConfigState() {
        return tmpConfigState;
    }

    public void setTmpConfigState(ConfigState tmpConfigState) {
        this.tmpConfigState = tmpConfigState;
    }

    public boolean isClientInstallation() {
        return clientInstallation;
    }

    private void initGUI() {
        hasGUI = true;
        mainFrame = new InstallationFrame("installation");
        mainFrame.setVisible(true);

        InstallationPanel startPanel = new StartPanel();

        // ClientPanels
        clientPanelOrder.add(new ConnectToServerPanel());
        clientPanelOrder.add(new FinishingPanel());

        // ExpressServer Panels
        expressPanelOrder.add(startPanel);
        expressPanelOrder.add(new NetworkNamePanel());
        expressPanelOrder.add(new ExpressInstallationPanel());
        expressPanelOrder.add(new FinishingPanel());

        // CustomServer panels
        customPanelOrder.add(startPanel);
        customPanelOrder.add(new ChooseAdapterPanel());
        customPanelOrder.add(new ChooseNetworkTypePanel());
        customPanelOrder.add(new ChooseProtocolPanel());
        customPanelOrder.add(new ChoosePerformancePanel());
        customPanelOrder.add(new NetworkNamePanel());
        customPanelOrder.add(new ExpressInstallationPanel());
        customPanelOrder.add(new FinishingPanel());
        // Anonymisieren?
        // Encryption
        // Authentification Panel (Add user panel)
            // --> Nachirchten, Onlinestatus etc
        // Mediation server
            // How to forward ips / access router
        // "How secure/ fast is this" panel
    }

    private void showPanel(int index) {
        InstallationPanel panel = currentPanelOrder.get(index);
        panel.onSelect();
        mainFrame.setContent(panel);
        mainFrame.setNextButtonTextToFinish(false);
        mainFrame.setNextEnabled(true);
        mainFrame.setPreviousEnabled(true);
        if (isFirst(panel)) {
            mainFrame.setPreviousEnabled(false);
        }
        if (panel.isFinishingPanel()) {
            mainFrame.setNextButtonTextToFinish(true);
        } else if (isLast(panel)) {
            mainFrame.setNextEnabled(false);
        }
    }

    private InstallationController() {
        clientPanelOrder = new ArrayList<>();
        currentPanelOrder = new ArrayList<>();
        expressPanelOrder = new ArrayList<>();
        customPanelOrder = new ArrayList<>();
    }

    private void drawLogoWithoutGUI() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(GeneralUtilities.getResourceAsFile("resources/icon.txt")));
            String line = in.readLine();
            while(line != null)
            {
                System.out.println(line);
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void showNextPanel(InstallationPanel currentPanel) {
        if (currentPanel.isFinishingPanel()) {
            // OnFinish setup config etc.
            UserData.getInstance().setClientInstallation(clientInstallation);
            callback.onInstallationSuccess();
            UserData.getInstance().setVpnConfigState(tmpConfigState);
            mainFrame.dispose();
        }

        boolean panelCanBeDeselected = currentPanel.onDeselect();
        if ( !panelCanBeDeselected) {
            return;
        }

        if (isLast(currentPanel)) {
            // TODO: Finish installation and start program.
            return;
        }
        int nextIndex = currentPanelOrder.indexOf(currentPanel)+1;
        showPanel(nextIndex);
    }

    private void showPreviousPanel(JPanel currentPanel) {
        if (isFirst(currentPanel)) {
            return;
        }
        int nextIndex = currentPanelOrder.indexOf(currentPanel)-1;
        showPanel(nextIndex);
    }
}
