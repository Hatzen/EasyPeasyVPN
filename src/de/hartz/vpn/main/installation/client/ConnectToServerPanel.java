package de.hartz.vpn.main.installation.client;

import de.hartz.vpn.helper.RadioButtonWithDescription;
import de.hartz.vpn.main.UserData;
import de.hartz.vpn.main.installation.InstallationController;
import de.hartz.vpn.main.installation.InstallationPanel;
import de.hartz.vpn.mediation.Mediator;
import de.hartz.vpn.utilities.Constants;
import de.hartz.vpn.utilities.UiUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Panel that connects a client to a network.
 */
public class ConnectToServerPanel extends InstallationPanel implements MetaClient.ClientListener, ActionListener {

    private JTextField serverAddress;
    private JTextField serverPort;

    private boolean successfulConnected = false;
    private boolean directConnect = false;

    private RadioButtonWithDescription mediatorChoice;
    private RadioButtonWithDescription ipChoice;

    private JTextField networkNameField;
    private JComboBox mediatorBox;


    public ConnectToServerPanel() {
        // TODO: Add mediator connection..


        // TODO: Maybe ask 3 options: 1) connect to server 2) (Super express) connect to network via (hardcoded) mediation server 3) connect to network with custom mediation server.

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel mediatorConnectionWrapper = new JPanel();
        mediatorConnectionWrapper.setLayout(new GridLayout(5, 1));

        networkNameField = new JTextField();
        networkNameField.setAlignmentX( Component.CENTER_ALIGNMENT );
        networkNameField.setMaximumSize( new Dimension( 1000, 30) );

        ArrayList<Mediator> mediatorList = UserData.getInstance().getMediatorList();
        String[] comboBoxSource = new String[mediatorList.size()+1];
        for (int i = 0; i < mediatorList.size(); i++ ) {
            comboBoxSource[i] = mediatorList.get(i).getMediatorName();
        }
        mediatorBox = new JComboBox(comboBoxSource);
        mediatorBox.setSelectedIndex(0);
        mediatorBox.setMaximumSize( new Dimension( 1000, 30) );


        JLabel networkNameLabel = new JLabel("Enter a Networkname:");
        networkNameLabel.setMaximumSize( new Dimension( 1000, 30) );

        JLabel mediatorLabel = new JLabel("Select Mediation server:");
        mediatorLabel.setMaximumSize( new Dimension( 1000, 30) );


        mediatorConnectionWrapper.add( UiUtilities.getComponentWrapper(networkNameLabel));
        mediatorConnectionWrapper.add( UiUtilities.getComponentWrapper(networkNameField) );
        mediatorConnectionWrapper.add( UiUtilities.getComponentWrapper(mediatorLabel));
        mediatorConnectionWrapper.add( UiUtilities.getComponentWrapper(mediatorBox) );
        // Inivisible Panel to keep the other components normal sized.
        mediatorConnectionWrapper.add(new JPanel());


        JPanel ipConnectionWrapper = new JPanel();
        ipConnectionWrapper.setPreferredSize(new Dimension(400,100));
        serverAddress = new JTextField("192.168.2.118");
        serverAddress.setPreferredSize(new Dimension(300,30));
        serverPort = new JTextField("" + Constants.MEDIATION_SERVER_PORT);
        serverPort.setPreferredSize(new Dimension(100,30));
        ipConnectionWrapper.add(serverAddress);
        ipConnectionWrapper.add(serverPort);


        mediatorChoice = new RadioButtonWithDescription("Connect via Mediator", mediatorConnectionWrapper, this);
        mediatorChoice.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        mediatorChoice.addToGroup(group);
        ipChoice = new RadioButtonWithDescription("Connect via IP", ipConnectionWrapper, this);
        ipChoice.addToGroup(group);

        add(mediatorChoice);
        add(ipChoice);

        ipChoice.setDescriptionComponentEnabled(false);
    }

    @Override
    public void onSelect() {

    }

    @Override
    public boolean onDeselect() {
        if (directConnect && !successfulConnected) {
            UserData.serverIp = serverAddress.getText();
            new MetaClient(this).start();
        }
        return false;
    }

    @Override
    public void onError(Exception e) {
        successfulConnected = false;
        UiUtilities.showAlert(e.getMessage());
    }

    @Override
    public void onCommandFinished(String command) {

    }

    @Override
    public void onFinish() {
        successfulConnected = true;
        InstallationController.getInstance().addClientPanel();
        InstallationController.getInstance().forceNextPanel(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( ipChoice.isEventSource(e.getSource()) ) {
            ipChoice.setDescriptionComponentEnabled(true);
            mediatorChoice.setDescriptionComponentEnabled(false);
            directConnect = true;
        } else if( mediatorChoice.isEventSource(e.getSource()) ) {
            ipChoice.setDescriptionComponentEnabled(false);
            mediatorChoice.setDescriptionComponentEnabled(true);
            directConnect = false;
        }
    }
}
