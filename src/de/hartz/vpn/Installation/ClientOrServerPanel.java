package de.hartz.vpn.Installation;

import de.hartz.vpn.Utilities.RadioButtonWithDescription;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TODO: Remove.
 */
public class ClientOrServerPanel extends InstallationPanel implements ActionListener {

    private RadioButtonWithDescription clientRadioButton;
    private RadioButtonWithDescription serverRadioButton;
    private boolean clientInstallation;

    public ClientOrServerPanel() {
        clientInstallation = true;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        clientRadioButton = new RadioButtonWithDescription( "Install Client", "If you want to join an existing server. (If you dont know use this)", this);
        clientRadioButton.setSelected(clientInstallation);
        serverRadioButton = new RadioButtonWithDescription( "Install Server", "If you want to host a server.", this);
        serverRadioButton.setSelected(!clientInstallation);
        ButtonGroup group = new ButtonGroup();
        clientRadioButton.addToGroup(group);
        serverRadioButton.addToGroup(group);

        add(clientRadioButton);
        add(serverRadioButton);

    }

    @Override
    public void actionPerformed(ActionEvent event) {
        clientInstallation = clientRadioButton.isEventSource(event.getSource());
    }

    @Override
    public void onSelect() {

    }

    @Override
    public boolean onDeselect() {
        return true;
    }

    public boolean isClientInstallation() {
        return clientInstallation;
    }
}
