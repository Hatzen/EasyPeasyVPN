package de.hartz.vpn.Installation.Server;

import de.hartz.vpn.Utilities.RadioButtonWithDescription;
import de.hartz.vpn.Installation.InstallationPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kaiha on 28.05.2017.
 */
public class ChooseNetworkType extends InstallationPanel implements ActionListener {

    private RadioButtonWithDescription endToEnd;
    private RadioButtonWithDescription siteToEnd;
    private RadioButtonWithDescription siteToSite;

    public ChooseNetworkType() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        endToEnd = new RadioButtonWithDescription( "End-to-End", "Connect single devices over different networks." ,this);
        endToEnd.setSelected(true);

        siteToEnd = new RadioButtonWithDescription( "Site-to-End ? End-to-Site!", "Connect one device to the ", this);
        siteToEnd.setEnabled(false);

        siteToSite = new RadioButtonWithDescription( "FreeLan", "OpenSource Protocol, FUTURE FEATURE", this);
        siteToSite.setEnabled(false);

        ButtonGroup group = new ButtonGroup();
        endToEnd.addToGroup(group);
        siteToEnd.addToGroup(group);
        siteToSite.addToGroup(group);

        this.add(endToEnd);
        this.add(siteToEnd);
        this.add(siteToSite);
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
