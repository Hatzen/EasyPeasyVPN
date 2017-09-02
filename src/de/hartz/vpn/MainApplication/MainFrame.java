package de.hartz.vpn.MainApplication;

import de.hartz.vpn.Helper.Helper;
import de.hartz.vpn.Helper.NetworkHelper;
import de.hartz.vpn.Helper.UiHelper;
import de.hartz.vpn.Installation.InstallationController;
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
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static de.hartz.vpn.Helper.Statics.SOFTWARE_NAME;

/**
 * The main frame of the client. It displays the current vpn connection.
 * And can start a server or connect to an existing one.
 */
public class MainFrame extends JFrame implements ActionListener, Logger, NetworkStateInterface, InstallationController.InstallationCallback {
    private final int STATUS_HEIGHT = 50;

    private JList list;
    private JLabel ownStatusText;
    private JLabel networkStatusText;
    private StatusComponent ownStatus;
    private JTabbedPane content;
    private JTextArea outputTextArea;

    private JMenuItem aboutItem;
    private JMenuItem manualItem;
    private JMenuItem networkInfoItem;
    private JMenuItem mediationMenuItem;
    private JMenuItem createNetworkItem;
    private JMenuItem joinNetworkItem;

    public MainFrame() {
        setTitle(SOFTWARE_NAME);
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

            JPanel statusCenter = new JPanel();
            statusCenter.setLayout(new BoxLayout(statusCenter, BoxLayout.Y_AXIS));
            networkStatusText = new JLabel("Networkname");
            networkStatusText.setFont(new Font("Arial", Font.BOLD, 16));
            ownStatusText = new JLabel("ownIP and isClient/Server");
            statusCenter.add(networkStatusText);
            statusCenter.add(ownStatusText);
        statusPanel.add(statusCenter, BorderLayout.CENTER);

        JPanel padding = new JPanel();
        padding.setLayout(new BorderLayout());
        padding.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(padding, BorderLayout.CENTER);
        padding.add(content, BorderLayout.CENTER);
        padding.add(statusPanel, BorderLayout.NORTH);

        setVisible(true);
        //startVPN();
    }

    private void startVPN() {
        ownStatus.setConnecting(true);
        String configFilename = "client";
        if (!UserData.getInstance().isClientInstallation()) {
            MetaServer.getInstance().startServer();
            configFilename = "server";
        }

        new OpenVPNRunner(configFilename + Helper.getOpenVPNConfigExtension(), this);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu networkMenu = new JMenu("Network");
        JMenu extrasMenu = new JMenu("Extras");
        JMenu helpMenu = new JMenu("Help");

        createNetworkItem = new JMenuItem("Create");
        createNetworkItem.addActionListener(this);
        createNetworkItem.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        joinNetworkItem = new JMenuItem("Join");
        joinNetworkItem.addActionListener(this);
        joinNetworkItem.setAccelerator(KeyStroke.getKeyStroke('J', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        networkInfoItem = new JMenuItem("Network info");
        networkInfoItem.addActionListener(this);
        networkInfoItem.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        mediationMenuItem = new JMenuItem("Mediation settings");
        mediationMenuItem.addActionListener(this);
        mediationMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(this);
        aboutItem.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        manualItem = new JMenuItem("Manual");
        manualItem.addActionListener(this);
        manualItem.setAccelerator(KeyStroke.getKeyStroke('M', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

        add(menuBar,  BorderLayout.NORTH);
        menuBar.add(networkMenu);
            networkMenu.add(createNetworkItem);
            networkMenu.add(joinNetworkItem);
        menuBar.add(extrasMenu);
            extrasMenu.add(networkInfoItem);
            extrasMenu.add(mediationMenuItem);
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
                        // TODO: Make sure process like server and openvpn etc. are closed softly.
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
            trayIcon = new TrayIcon(image, SOFTWARE_NAME, popup);
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
        for (UserList.User user : UserData.getInstance().getUserList()) {
            model.addElement(user.getVpnIp() + " / " + user.getCommonName());
        }
        list.setModel( model );
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == createNetworkItem) {
            InstallationController.getInstance().startInstallation(true, false, this);
        } else if (actionEvent.getSource() == joinNetworkItem) {
            InstallationController.getInstance().startInstallation(true, true, this);
        } else if (actionEvent.getSource() == networkInfoItem) {
            new NetworkInfoFrame();
        } else if (actionEvent.getSource() == joinNetworkItem) {
            InstallationController.getInstance().startInstallation(true, true, this);
        } else if (actionEvent.getSource() == aboutItem) {
            JOptionPane.showMessageDialog(this,
                    new EasyHtmlComponent(SOFTWARE_NAME + " <br> Contribute under: <a href=\"https://github.com/Hatzen/EasyPeasyVPN\">https://github.com/Hatzen/EasyPeasyVPN</a>"));
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

        if (line.contains(SUCCESSFUL_INIT)) {
            // Successful connected.
            setOnlineState(true);
        }
        refreshModel();
    }

    @Override
    public void setOnlineState(boolean online) {
        ownStatus.setOnline(online);
        networkStatusText.setText(UserData.getInstance().getVpnConfigState().getNetworkName());
        String prefix = UserData.getInstance().isClientInstallation() ? "Client: " : "Server: ";
        ownStatusText.setText(prefix + "");

        if (online) {
            // Check for avaiable connections.
            final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    // TODO: Get netadress of vpn from config.
                    String netaddress = "10.0.0";
                    ArrayList<String> ips = NetworkHelper.getAllReachableClientsForNetaddress(netaddress);

                    UserData.getInstance().getUserList().clear();
                    UserList userList =  UserData.getInstance().getUserList();
                    userList.clear();
                    for (String ip : ips) {
                        userList.add(new UserList.User(ip, "-"));
                    }
                    refreshModel();
                }
            }, 0, 30, TimeUnit.SECONDS);
        }
    }

    @Override
    public void onInstallationSuccess() {
        startVPN();
    }

    @Override
    public void onInstallationCanceled() {

    }
}
