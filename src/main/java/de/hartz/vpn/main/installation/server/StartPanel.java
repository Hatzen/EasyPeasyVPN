package de.hartz.vpn.main.installation.server;

import de.hartz.vpn.helper.RadioButtonWithDescription;
import de.hartz.vpn.main.installation.InstallationPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kaiha on 28.05.2017.
 */
public class StartPanel extends InstallationPanel implements ActionListener {

    private RadioButtonWithDescription expressRadioButton;
    private RadioButtonWithDescription customRadioButton;

    private boolean isExpressInstallation;

    public StartPanel() {
        isExpressInstallation = true;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        String configString = "UDP, ";
        configString += "Using TAP Device so Broadcasts are supported, ";
        configString += "No Authentication, ";
        configString += "AES-128-CBC Encryption, ";
        configString += "RSA Key Size of 512 Bit";

        expressRadioButton = new RadioButtonWithDescription( "Express installation. Use default settings.", "For users with less experience includes following settings:" + configString, this);
        expressRadioButton.setSelected(isExpressInstallation);

        customRadioButton = new RadioButtonWithDescription( "Custom installation. Choose settings step by step.", "Customize the settings like security, performance etc.", this);
        customRadioButton.setSelected(!isExpressInstallation);
        customRadioButton.setEnabled(false);

        ButtonGroup group = new ButtonGroup();
        customRadioButton.addToGroup(group);
        expressRadioButton.addToGroup(group);

        this.add(expressRadioButton);
        this.add(customRadioButton);
    }

    public boolean isExpressInstallation() {
        return isExpressInstallation;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        isExpressInstallation = expressRadioButton.isEventSource(event.getSource());
    }

    @Override
    public void onSelect() {

    }

    @Override
    public boolean onDeselect() {
        return true;
    }
}
