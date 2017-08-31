package de.hartz.vpn.Installation;

import de.hartz.vpn.Utilities.Logger;

import javax.swing.*;

/**
 * Interface for every installation panel, so it has a predefined behaviour and logger.
 */
public abstract class InstallationPanel extends JPanel implements Logger {

    public abstract void onSelect();

    /**
     *
     * @return boolean indicating whether deselecting is possible.
     */
    public abstract boolean onDeselect();

    public void addLogLine(String line) {
        System.out.println(line);
    }
}

