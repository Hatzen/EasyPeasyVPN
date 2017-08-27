package de.hartz.vpn.Installation.Server;

import de.hartz.vpn.Utilities.RadioButtonWithDescription;
import de.hartz.vpn.Installation.InstallationPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kaiha on 28.05.2017.
 */
public class ChoosePerformancePanel  extends InstallationPanel implements ActionListener {

    private RadioButtonWithDescription openVpnRadioButton;
    private RadioButtonWithDescription ipsecRadioButton;
    private RadioButtonWithDescription freelanRadioButton;

    public ChoosePerformancePanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        openVpnRadioButton = new RadioButtonWithDescription( "OpenVPN Adapter", "Less Performance, full open source" ,this);
        openVpnRadioButton.setSelected(true);

        ipsecRadioButton = new RadioButtonWithDescription( "IPSec", " Good Performance,  FUTURE FEATURE", this);
        ipsecRadioButton.setEnabled(false);

        freelanRadioButton = new RadioButtonWithDescription( "FreeLan", "OpenSource Protocol, FUTURE FEATURE", this);
        freelanRadioButton.setEnabled(false);

        ButtonGroup group = new ButtonGroup();
        openVpnRadioButton.addToGroup(group);
        ipsecRadioButton.addToGroup(group);
        freelanRadioButton.addToGroup(group);

        this.add(openVpnRadioButton);
        this.add(ipsecRadioButton);
        this.add(freelanRadioButton);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // TODO: Handle selection.
    }

    @Override
    public void onSelect() {

    }

    @Override
    public boolean onDeselect() {
        return true;
    }
}
