package de.hartz.vpn.Installation;

import de.hartz.vpn.Helper.UserData;
import de.hartz.vpn.Installation.Client.ConnectToServerPanel;
import de.hartz.vpn.Helper.Helper;
import de.hartz.vpn.MainApplication.Server.ConfigState;
import de.hartz.vpn.Installation.Server.ChooseNetworkType;
import de.hartz.vpn.Installation.Server.ChoosePerformancePanel;
import de.hartz.vpn.Installation.Server.StartPanel;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The installation controller that can show a given order of panels.
 */
public class InstallationController {

    public enum NeededPanels {
        CLIENT,
        SERVER,
        SERVER_EXPRESS,
        SERVER_CUSTOM
    }

    private static InstallationController instance = new InstallationController();
    private InstallationFrame mainFrame;

    private static boolean hasGUI;

    private ArrayList<InstallationPanel> currentPanelOrder;

    private ArrayList<InstallationPanel> clientPanelOrder;

    private ArrayList<InstallationPanel> expressPanelOrder;
    private ArrayList<InstallationPanel> customPanelOrder;

    // TODO: Check if this is more beautiful.
    public void setUpNeededPanels(NeededPanels neededPanel) {
        switch (neededPanel) {
            case CLIENT:
                break;
            case SERVER:
                break;
            case SERVER_EXPRESS:
                break;
            case SERVER_CUSTOM:
                break;
        }
    }

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
        if (currentPanel instanceof ClientOrServerPanel) {
            currentPanelOrder.clear();
            if (((ClientOrServerPanel) currentPanel).isClientInstallation()) {
                currentPanelOrder.addAll(clientPanelOrder);
                System.out.println("Client installation");
                UserData.clientInstallation = true;
            } else {
                System.out.println("Server installation");
                currentPanelOrder.addAll(expressPanelOrder);
                UserData.clientInstallation = false;
            }
        } else if (currentPanel instanceof StartPanel) {
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
    public void startInstallation(boolean showGUI) {
        hasGUI = showGUI;
        if (showGUI) {
            // TODO: GUI doesnt react some time. Because of extracting files etc.. Create loading screen. Also might be initalization of all the panels!!
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            initGUI();
            showPanel(0);
        } else {
            drawLogoWithoutGUI();
            if (UserData.isClientInstallation())
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
        if (UserData.getVpnConfigState().getAdapter() == ConfigState.Adapter.OpenVPN) {
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

    private void initGUI() {
        hasGUI = true;
        mainFrame = new InstallationFrame("Installation");
        mainFrame.setVisible(true);

        ClientOrServerPanel clientOrServerPanel = new ClientOrServerPanel();
        InstallationPanel startPanel = new StartPanel();

        // ClientPanels
        clientPanelOrder.add(clientOrServerPanel);
        clientPanelOrder.add(new ConnectToServerPanel());
        clientPanelOrder.add(new ExternalIpPanel());

        // ExpressServer Panels
        expressPanelOrder.add(clientOrServerPanel);
        expressPanelOrder.add(startPanel);
        expressPanelOrder.add(new ExpressInstallationPanel());
        expressPanelOrder.add(new ExternalIpPanel());

        // CustomServer panels
        customPanelOrder.add(clientOrServerPanel);
        customPanelOrder.add(startPanel);
        customPanelOrder.add(new ChoosePerformancePanel());
        customPanelOrder.add(new ChooseNetworkType());
        // Anonymisieren?
        // Encryption
        // Authentification Panel (Add user panel)
            // --> Nachirchten, Onlinestatus etc
        // Mediation Server
            // How to forward ips / access router
        // "How secure/ fast is this" panel


        currentPanelOrder.addAll(clientPanelOrder);
    }

    private void showPanel(int index) {
        InstallationPanel panel = currentPanelOrder.get(index);
        panel.onSelect();
        mainFrame.setContent(panel);
        mainFrame.setNextEnabled(true);
        mainFrame.setPreviousEnabled(true);
        if (isFirst(panel)) {
            mainFrame.setPreviousEnabled(false);
        }
        if (isLast(panel)) {
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
            BufferedReader in = new BufferedReader(new FileReader(Helper.getResourceAsFile("resources/icon.txt")));
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
