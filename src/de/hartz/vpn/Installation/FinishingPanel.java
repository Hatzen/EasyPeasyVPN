package de.hartz.vpn.Installation;

import javax.swing.*;

/**
 */
public class FinishingPanel extends InstallationPanel {

    public FinishingPanel() {
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
