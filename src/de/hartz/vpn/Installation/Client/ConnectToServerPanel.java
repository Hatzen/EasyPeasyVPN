package de.hartz.vpn.Installation.Client;

import de.hartz.vpn.Helper.UiHelper;
import de.hartz.vpn.Installation.InstallationController;
import de.hartz.vpn.Installation.InstallationPanel;
import de.hartz.vpn.MainApplication.UserData;

import javax.swing.*;
import java.awt.*;

/**
 * Panel that connects a client to a network.
 */
public class ConnectToServerPanel extends InstallationPanel implements MetaClient.ClientListener {

    private JTextField serverAddress;
    private boolean successfullConnected = false;

    public ConnectToServerPanel() {
        // TODO: Maybe ask 3 options: 1) connect to server 2) (Super express) connect to network via (hardcoded) mediation server 3) connect to network with custom mediation server.

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new GridLayout(2,1));
        wrapper.setPreferredSize(new Dimension(400,100));

        // TODO: Remove debug code.
        serverAddress = new JTextField("192.168.2.118");

        wrapper.add(new JLabel("Insert server ip or url:"));
        wrapper.add(serverAddress);
        add(wrapper);
    }

    @Override
    public void onSelect() {

    }

    @Override
    public boolean onDeselect() {
        if (!successfullConnected) {
            UserData.serverIp = serverAddress.getText();
            new MetaClient(this).start();
        }
        return false;
    }

    @Override
    public void onError(Exception e) {
        successfullConnected = false;
        UiHelper.showAlert(e.getMessage());
    }

    @Override
    public void onCommandFinished(String command) {

    }

    @Override
    public void onFinish() {
        successfullConnected = true;
        InstallationController.getInstance().addClientPanel();
        InstallationController.getInstance().forceNextPanel(this);
    }
}
