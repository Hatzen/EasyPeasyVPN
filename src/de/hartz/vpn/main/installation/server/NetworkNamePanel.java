package de.hartz.vpn.main.installation.server;

import de.hartz.vpn.main.MediationConnector;
import de.hartz.vpn.main.UserData;
import de.hartz.vpn.main.installation.InstallationController;
import de.hartz.vpn.main.installation.InstallationPanel;
import de.hartz.vpn.mediation.Mediator;
import de.hartz.vpn.utilities.UiHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by kaiha on 02.09.2017.
 */
public class NetworkNamePanel extends InstallationPanel implements ActionListener {

    private static final String NO_SELECTION = "NONE";

    private JTextField networkNameField;
    private JComboBox mediatorBox;

    public NetworkNamePanel() {

        setLayout(new GridLayout(5, 1));

        networkNameField = new JTextField();
        networkNameField.setAlignmentX( Component.CENTER_ALIGNMENT );
        networkNameField.setMaximumSize( new Dimension( 1000, 30) );

        ArrayList<Mediator> mediatorList = UserData.getInstance().getMediatorList();
        String[] comboBoxSource = new String[mediatorList.size()+1];
        comboBoxSource[0] = NO_SELECTION;
        for (int i = 0; i < mediatorList.size(); i++ ) {
            comboBoxSource[i+1] = mediatorList.get(i).getMediatorName();
        }
        mediatorBox = new JComboBox(comboBoxSource);
        mediatorBox.setSelectedIndex(1);
        mediatorBox.setMaximumSize( new Dimension( 1000, 30) );
        mediatorBox.addActionListener(this);


        JLabel networkNameLabel = new JLabel("Enter a Networkname:");
        networkNameLabel.setMaximumSize( new Dimension( 1000, 30) );

        JLabel mediatorLabel = new JLabel("Select Mediation server:");
        mediatorLabel.setMaximumSize( new Dimension( 1000, 30) );


        add( UiHelper.getComponentWrapper(networkNameLabel));
        add( UiHelper.getComponentWrapper(networkNameField) );
        add( UiHelper.getComponentWrapper(mediatorLabel));
        add( UiHelper.getComponentWrapper(mediatorBox) );
        // Inivisible Panel to keep the other components normal sized.
        add(new JPanel());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
    }

    @Override
    public void onSelect() {

    }

    @Override
    public boolean onDeselect() {
        // TODO: Check if name is free at mediator.
        String networkName = networkNameField.getText();
        if (networkName.isEmpty()) {
            UiHelper.showAlert("Missing networkname!");
            return false;
        }
        InstallationController.getInstance().getTmpConfigState().setNetworkName(networkName);

        if (!mediatorBox.getSelectedItem().equals(NO_SELECTION)) {
            // TODO: Respect selection here.
            InstallationController.getInstance().getTmpConfigState().setMediator(MediationConnector.getDefaultMediator());
        }
        return true;
    }
}