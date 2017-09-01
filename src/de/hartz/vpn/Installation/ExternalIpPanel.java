package de.hartz.vpn.Installation;

import javax.swing.*;

/**
 */
public class ExternalIpPanel extends InstallationPanel {

    public ExternalIpPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JLabel("Successful installed. Finish to start client."));
    }

    @Override
    public boolean isFinishingPanel() {
        return true;
    }

    @Override
    public void onSelect() {
    }

    @Override
    public boolean onDeselect() {
        return false;
    }
}
