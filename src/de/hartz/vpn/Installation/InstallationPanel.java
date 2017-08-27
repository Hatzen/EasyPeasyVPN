package de.hartz.vpn.Installation;

import de.hartz.vpn.Utilities.Logger;

import javax.swing.*;

/**
 * Created by kaiha on 01.06.2017.
 */
public abstract class InstallationPanel extends JPanel implements PanelInterface, Logger {

    public void addLogLine(String line) {
        System.out.println(line);
    }
}

