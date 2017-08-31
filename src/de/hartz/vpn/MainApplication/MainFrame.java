package de.hartz.vpn.MainApplication;

import de.hartz.vpn.Helper.Helper;
import de.hartz.vpn.Helper.OpenVPNParserHelper;
import de.hartz.vpn.Helper.UiHelper;
import de.hartz.vpn.MainApplication.Server.MetaServer;
import de.hartz.vpn.Utilities.EasyHtmlComponent;
import de.hartz.vpn.Utilities.Logger;
import de.hartz.vpn.Utilities.StatusComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * The main frame of the client. It displays the current vpn connection.
 * And can start a server or connect to an existing one.
 */
public class MainFrame extends JFrame implements ActionListener, Logger, NetworkStateInterface {
    private final int STATUS_HEIGHT = 50;

    private JList list;
    private JLabel ownStatusText;
    private StatusComponent ownStatus;
    private JTabbedPane content;
    private JTextArea outputTextArea;

    private JMenuItem aboutItem;
    private JMenuItem manualItem;
    private JMenuItem createNetworkItem;
    private JMenuItem joinNetworkItem;

    public MainFrame() {
        setTitle("EasyPeasy");
        setMinimumSize(new Dimension(500,500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        initTray();
        UiHelper.setLookAndFeelAndIcon(this);
        initMenuBar();

        // Overview tab.
        content = new JTabbedPane();
        JPanel overviewPanel = new JPanel();
        overviewPanel.setLayout( new BorderLayout());
        list = new JList();
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setCellRenderer(new StatusComponent());
        JScrollPane scrollPane = new JScrollPane(list);
        overviewPanel.add(scrollPane);
        content.addTab("Overview", overviewPanel);

        // Log tab.
        JPanel logPanel = new JPanel();
        logPanel.setLayout( new BorderLayout());
        outputTextArea = new JTextArea();
        outputTextArea.setEnabled(false);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        DefaultCaret caret = (DefaultCaret)outputTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        logPanel.add(scrollPane);
        content.addTab("VPN Log", logPanel);

        // NORTH LAYOUT. Own Status.
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setPreferredSize(new Dimension(statusPanel.getWidth(), STATUS_HEIGHT));
        ownStatus = new StatusComponent();
        ownStatus.setPreferredSize(new Dimension(STATUS_HEIGHT,STATUS_HEIGHT));
        statusPanel.add(ownStatus, BorderLayout.WEST);
        ownStatusText = new JLabel("ownIP and isClient/Server");
        statusPanel.add(ownStatusText, BorderLayout.CENTER);

        JPanel padding = new JPanel();
        padding.setLayout(new BorderLayout());
        padding.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(padding, BorderLayout.CENTER);
        padding.add(content, BorderLayout.CENTER);
        padding.add(statusPanel, BorderLayout.NORTH);

        setVisible(true);
        startVPN();
    }

    private void startVPN() {
        if (UserData.isClientInstallation()) {
            new MetaServer().start();
        }
        new OpenVPNRunner("server" + Helper.getOpenVPNConfigExtension(), this);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu networkMenu = new JMenu("Network");
        JMenu helpMenu = new JMenu("Help");
        createNetworkItem = new JMenuItem("Create");
        createNetworkItem.addActionListener(this);
        joinNetworkItem = new JMenuItem("Join");
        joinNetworkItem.addActionListener(this);
        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);
        manualItem = new JMenuItem("Manual");
        manualItem.addActionListener(this);
        add(menuBar,  BorderLayout.NORTH);
        menuBar.add(networkMenu);
            networkMenu.add(createNetworkItem);
            networkMenu.add(joinNetworkItem);
        menuBar.add(helpMenu);
            helpMenu.add(manualItem);
            helpMenu.add(aboutItem);
    }

    private void initTray() {
        TrayIcon trayIcon = null;
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            File file = Helper.getResourceAsFile("resources/icon.png");
            Image image = null;
            try {
                image = ImageIO.read( file );
            } catch (IOException e) {
                e.printStackTrace();
            }
            PopupMenu popup = new PopupMenu();
            final MenuItem openItem = new MenuItem("Open");
            final MenuItem exitItem = new MenuItem("Exit");
            ActionListener listener = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getSource() == exitItem) {
                        System.exit(0);
                    } else { // Source == openItem
                        setVisible(true);
                    }
                }
            };
            openItem.addActionListener(listener);
            exitItem.addActionListener(listener);
            popup.add(openItem);
            popup.add(exitItem);
            trayIcon = new TrayIcon(image, "EasyPeasyVPN", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(listener);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println(e);
            }
        }
    }

    private void refreshModel() {
        DefaultListModel model = new DefaultListModel();
        for (UserList.User user : UserData.userList) {
            model.addElement(user.getVpnIp() + " / " + user.getCommonName());
        }
        list.setModel( model );
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == createNetworkItem) {

        } else if (actionEvent.getSource() == joinNetworkItem) {

        } else if (actionEvent.getSource() == aboutItem) {
            JOptionPane.showMessageDialog(this,
                    new EasyHtmlComponent("EasyPeasyVPN <br> Contribute under: <a href=\"https://github.com/Hatzen/EasyPeasyVPN\">https://github.com/Hatzen/EasyPeasyVPN</a>"));
        } else if (actionEvent.getSource() == manualItem) {
            try {
                EasyHtmlComponent.openURLInBrowser(new URL("https://github.com/Hatzen/EasyPeasyVPN/wiki"));
            } catch (Exception e) {}
        }
    }

    @Override
    public void addLogLine(String line) {
        System.out.println("" + line);
        checkLine(line);
        outputTextArea.append(line + System.getProperty("line.separator"));
    }

    // TODO: Move this to a controller, so it can be used by every kind of adapter..
    // TODO: REFACTOR THIS!!!
    public void checkLine(String line) {
        final String SUCCESSFUL_INIT = "Initialization Sequence Completed";

        String tmp;
        if (line.contains(SUCCESSFUL_INIT)) {
            System.out.println("Online");
            setOnlineState(true);
        } else if ((tmp = OpenVPNParserHelper.getServerIpFromLine(line)) != null) {
            UserData.userList.add(new UserList.User(tmp, "The Server"));
            // TODO: Only set text if this is the server.
            ownStatusText.setText(tmp);
        } else if((tmp = OpenVPNParserHelper.getClientIpFromLine(line)) != null) {
            String clientIp = tmp;
            String clientName = OpenVPNParserHelper.getClientNameFromLine(line);
            UserData.userList.add(new UserList.User(clientIp, clientName));
        } else if((tmp = OpenVPNParserHelper.getDisconnectedClientNameFromLine(line)) != null) {
            System.out.println(tmp);
            UserData.userList.removeUserByName(tmp);
        }
        refreshModel();
    }

    @Override
    public void setOnlineState(boolean online) {
        ownStatus.setOnline(online);
    }
}
