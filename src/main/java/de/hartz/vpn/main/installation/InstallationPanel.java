package de.hartz.vpn.main.installation;

import de.hartz.vpn.helper.Logger;

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

    /**
     * Makes it possible to do heavy task in background with showing loading animation.
     */
    public void performTask() {

    }

    /**
     * Method indicating whether the panel is the last installation step.
     * @return
     */
    public boolean isFinishingPanel() {
        return false;
    }

    public void addLogLine(String line) {
        System.out.println(line);
    }
}

