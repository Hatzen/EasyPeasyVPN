package de.hartz.vpn.Installation;

import de.hartz.vpn.MainApplication.Server.MetaServer;
import de.hartz.vpn.MainApplication.UserData;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 */
public class ExternalIpPanel extends InstallationPanel implements ActionListener {

    private JTextArea logArea;

    public ExternalIpPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    }

    // TODO: RENAME
    private void startMediationServer() {
        if (!UserData.isClientInstallation()) {
            new MetaServer().start();
        }
    }

    // TODO: Normally not needed? Only external ip needed?
    private void listAllIpAddresses() {
        logArea = new JTextArea();
        logArea.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane);
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            appendLine(" IP Addr: " + localhost.getHostAddress());
            // Just in case this host has multiple IP addresses....
            InetAddress[] allMyIps = InetAddress.getAllByName(localhost.getCanonicalHostName());
            if (allMyIps != null && allMyIps.length > 1) {
                appendLine(" Full list of IP addresses:");
                for (int i = 0; i < allMyIps.length; i++) {
                    appendLine("    " + allMyIps[i]);
                }
            }
        } catch (UnknownHostException e) {
            appendLine(" (error retrieving server host name)");
        }

        try {
            appendLine("Full list of Network Interfaces:");
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                appendLine("    " + intf.getName() + " " + intf.getDisplayName());
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    appendLine("        " + enumIpAddr.nextElement().toString());
                }
            }
        } catch (SocketException e) {
            appendLine(" (error retrieving network interface list)");
        }
    }

    private void appendLine(String string) {
        logArea.append(string + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent event) {
    }

    @Override
    public void onSelect() {
        listAllIpAddresses();
        startMediationServer();
    }

    @Override
    public boolean onDeselect() {
        return true;
    }
}
