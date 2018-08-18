package de.hartz.vpn.main.installation.server;

import de.hartz.vpn.helper.RadioButtonWithDescription;
import de.hartz.vpn.main.installation.InstallationController;
import de.hartz.vpn.main.installation.InstallationPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kaiha on 28.05.2017.
 */
public class ChoosePerformancePanel extends InstallationPanel implements ActionListener {

    private boolean compress;

    private RadioButtonWithDescription compressionOnRadio;
    private RadioButtonWithDescription compressionOffRadio;

    public ChoosePerformancePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        compressionOnRadio = new RadioButtonWithDescription( "Compression on", "Needs more CPU and has less latency." ,this);
        compressionOnRadio.setSelected(true);
        compressionOffRadio = new RadioButtonWithDescription( "Compression off", "Normal latency.", this);

        ButtonGroup group = new ButtonGroup();
        compressionOnRadio.addToGroup(group);
        compressionOffRadio.addToGroup(group);

        this.add(compressionOnRadio);
        this.add(compressionOffRadio);

        compress = true;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if ( compressionOnRadio.isEventSource(event.getSource()) ) {
            compressionOnRadio.setDescriptionComponentEnabled(true);
            compressionOffRadio.setDescriptionComponentEnabled(false);
            compress = true;
        } else if( compressionOffRadio.isEventSource(event.getSource()) ) {
            compressionOnRadio.setDescriptionComponentEnabled(false);
            compressionOffRadio.setDescriptionComponentEnabled(true);
            compress = false;
        }
    }

    @Override
    public void onSelect() {

    }

    @Override
    public boolean onDeselect() {
        InstallationController.getInstance().getTmpConfigState().setCompressData(compress);
        return true;
    }
}
