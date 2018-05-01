package de.hartz.vpn.main;

import de.hartz.vpn.helper.LoadingFrame;
import de.hartz.vpn.mediation.Mediator;
import de.hartz.vpn.utilities.UiUtilities;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * A frame that displays all used ip addresses of this client. External ip, lan ip and vpn ip.
 */
public class MediatorFrame extends LoadingFrame {

    private final String HEADING_NAME = "Name";
    private final String HEADING_ADDRESS = "Address";
    private final String HEADING_PORT = "Port";
    private final String HEADING_PROXY = "Proxy";
    private final String HEADING_META_PORT = "Network Exchange Port";
    private final String HEADING_DEFAULT = "Default selection";
    private final String HEADING_REACHABLE = "Online";

    private final JTable table;


    public MediatorFrame() {
        setTitle("Mediator Overview");
        setMinimumSize(new Dimension(500,500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        UiUtilities.setLookAndFeelAndIcon(this);

        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());

        JPanel padding = new JPanel();
        padding.setLayout(new BorderLayout());
        padding.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(padding, BorderLayout.CENTER);
        padding.add(content, BorderLayout.CENTER);

        //create table with data
        table = new JTable(getTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);

        //add the table to the frame
        content.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        JButton deleteRow = new JButton("Delete row");
        deleteRow.setPreferredSize(new Dimension(150, 30));
        deleteRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = table.getSelectedRow();
                UserData.getInstance().getMediatorList().remove(index);
                UserData.getInstance().writeUserData();
                table.setModel(getTableModel());
            }
        });

        JButton addRow = new JButton("Add row");
        addRow.setPreferredSize(new Dimension(150, 30));
        addRow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMediator();
            }
        });
        content.add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(deleteRow, BorderLayout.WEST);
        bottomPanel.add(addRow, BorderLayout.EAST);

        setVisible(true);
    }

    /**
     * Creates the table model structure and data.
     * @return
     */
    private DefaultTableModel getTableModel() {
        //headers for the table
        String[] columns = new String[] {
                HEADING_NAME,
                HEADING_ADDRESS,
                HEADING_PORT,
                HEADING_PROXY,
                HEADING_META_PORT,
                HEADING_DEFAULT,
                HEADING_REACHABLE
        };
        ArrayList<Mediator> mediatorList = UserData.getInstance().getMediatorList();
        Object[][] data = new Object[mediatorList.size()][];
        for (int i = 0; i < mediatorList.size() ; i++ ) {
            data[i] = new Object[] {mediatorList.get(i).getMediatorName(),
                    mediatorList.get(i).getUrl(),
                    mediatorList.get(i).getUdpHolePunchingPort(),
                    mediatorList.get(i).isRedirectFromUrl(),
                    mediatorList.get(i).getMetaServerPort(),
                    false};
        }

        final Class[] columnClass = new Class[] {
                String.class, String.class, Integer.class, Boolean.class, Integer.class, Boolean.class, Boolean.class
        };
        //create table model with data
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                if (column == columnClass.length - 1) {
                    return false;
                }
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int rowIndex, int columnIndex)
            {
                Mediator mediator = UserData.getInstance().getMediatorList().get(rowIndex);
                if(0 == columnIndex) {
                    mediator.setMediatorName((String) aValue);
                } else if(1 == columnIndex) {
                    mediator.setUrl((String) aValue);
                } else if(2 == columnIndex) {
                    mediator.setUdpHolePunchingPort((Integer) aValue);
                } else if(3 == columnIndex) {
                    mediator.setRedirectFromUrl((Boolean) aValue);
                } else if(4 == columnIndex) {
                    mediator.setMetaServerPort((Integer) aValue);
                } else if(5 == columnIndex) {
                    // Default selection
                    //mediator.setMetaServerPort((Integer) aValue);
                }
            }

            @Override
            public Class<?> getColumnClass(int columnIndex)
            {
                return columnClass[columnIndex];
            }
        };
        return model;
    }

    @Override
    public void performTask() {

    }

    /**
     * Shows a dialog to create a new Mediator.
     */
    private void addMediator() {
        JLabel nameLabel = new JLabel(HEADING_NAME + ":");
        nameLabel.setToolTipText("The name which represents this mediation server.");
        JTextField nameTextField = new JTextField();
        nameTextField.setPreferredSize(new Dimension(150, 30));

        JLabel addressLabel = new JLabel(HEADING_ADDRESS + ":");
        addressLabel.setToolTipText("The url or ip address of the mediation server.");
        JTextField addressTextField = new JTextField();
        addressTextField.setPreferredSize(new Dimension(150, 30));

        JLabel portLabel = new JLabel(HEADING_PORT + ":");
        portLabel.setToolTipText("the port which should be used on the mediation server. Isnt needed when used URL.");
        JTextField portTextField = new JTextField();
        portTextField.setPreferredSize(new Dimension(150, 30));
        portTextField.setHorizontalAlignment(JTextField.RIGHT);

        JLabel proxyLabel = new JLabel(HEADING_PROXY + ":");
        proxyLabel.setToolTipText("If true the given URL displays only a text of the current IP:PORT of the mediation server. HINT: In most cases this will be false. It is only needed if server has a dynamic ip.");
        JCheckBox proxyCheckbox = new JCheckBox();

        JLabel metaPortLabel = new JLabel(HEADING_META_PORT + ":");
        metaPortLabel.setToolTipText("If the server hosts all network related files. And every participant can get the server role.");
        JTextField metaPortTextField = new JTextField();
        metaPortTextField.setPreferredSize(new Dimension(150, 30));
        metaPortTextField.setHorizontalAlignment(JTextField.RIGHT);

        JLabel defaultLabel = new JLabel(HEADING_DEFAULT + ":");
        defaultLabel.setToolTipText("Should this mediator be selected on default?");
        JCheckBox defaultCheckbox = new JCheckBox();

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(6,2));
        gridPanel.add(nameLabel);
        gridPanel.add(nameTextField);
        gridPanel.add(addressLabel);
        gridPanel.add(addressTextField);
        gridPanel.add(portLabel);
        gridPanel.add(portTextField);
        gridPanel.add(proxyLabel);
        gridPanel.add(proxyCheckbox);
        gridPanel.add(metaPortLabel);
        gridPanel.add(metaPortTextField);
        gridPanel.add(defaultLabel);
        gridPanel.add(defaultCheckbox);

        final JComponent[] inputs = new JComponent[] {
                gridPanel
        };
        int result = JOptionPane.showConfirmDialog(null, inputs, "add mediation server", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int metaPort = -1;
            try {
                metaPort = new Integer(metaPortTextField.getText());
            } catch(Exception e) {}
            try {
                UserData.getInstance().getMediatorList().add(new Mediator(nameTextField.getText(),
                        addressTextField.getText(),
                        metaPort,
                        new Integer(portTextField.getText()),
                        proxyCheckbox.isSelected())
                );
                UserData.getInstance().writeUserData();
                table.setModel(getTableModel());
            } catch(Exception e) {
                UiUtilities.showAlert(e.getLocalizedMessage());
            }
        }
    }

}
