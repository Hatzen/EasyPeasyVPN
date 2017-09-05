package de.hartz.vpn.Installation.Server;

import de.hartz.vpn.Helper.UiHelper;
import de.hartz.vpn.Installation.InstallationController;
import de.hartz.vpn.Installation.InstallationPanel;
import de.hartz.vpn.MainApplication.Client.MediationConnector;
import de.hartz.vpn.MainApplication.UserData;
import de.hartz.vpn.MediationServer.Mediator;

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

        JLabel mediatorLabel = new JLabel("Select Mediation Server:");
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