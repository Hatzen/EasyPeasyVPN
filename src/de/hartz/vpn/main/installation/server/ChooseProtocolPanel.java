package de.hartz.vpn.main.installation.server;

import de.hartz.vpn.helper.RadioButtonWithDescription;
import de.hartz.vpn.main.installation.InstallationController;
import de.hartz.vpn.main.installation.InstallationPanel;
import de.hartz.vpn.main.server.ConfigState;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kaiha on 28.05.2017.
 */
public class ChooseProtocolPanel extends InstallationPanel implements ActionListener {

    private boolean useUDP;

    private RadioButtonWithDescription useUDPRadio;
    private RadioButtonWithDescription useTCPRadio;

    public ChooseProtocolPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        useUDPRadio = new RadioButtonWithDescription( "UDP", "Unreliable connection, a few packets might be loss." ,this);
        useUDPRadio.setSelected(true);
        useTCPRadio = new RadioButtonWithDescription( "TCP", "Reliable connection, every data sent will be received. Has bad performance impact. WARNING: HOLE PUNCHING NOT SUPPORTED.", this);

        ButtonGroup group = new ButtonGroup();
        useUDPRadio.addToGroup(group);
        useTCPRadio.addToGroup(group);

        this.add(useUDPRadio);
        this.add(useTCPRadio);

        useUDP = true;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if ( useUDPRadio.isEventSource(event.getSource()) ) {
            useUDPRadio.setDescriptionComponentEnabled(true);
            useTCPRadio.setDescriptionComponentEnabled(false);
            useUDP = true;
        } else if( useTCPRadio.isEventSource(event.getSource()) ) {
            useUDPRadio.setDescriptionComponentEnabled(false);
            useTCPRadio.setDescriptionComponentEnabled(true);
            useUDP = false;
        }
    }

    @Override
    public void onSelect() {

    }

    @Override
    public boolean onDeselect() {
        ConfigState.Protocol protocol = ConfigState.Protocol.UDP;
        if (!useUDP) {
            protocol = ConfigState.Protocol.TCP;
        }

        InstallationController.getInstance().getTmpConfigState().setProtocol(protocol);
        return true;
    }
}
