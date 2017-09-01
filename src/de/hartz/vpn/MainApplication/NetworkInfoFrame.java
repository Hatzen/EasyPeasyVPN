package de.hartz.vpn.MainApplication;

import de.hartz.vpn.Helper.NetworkHelper;
import de.hartz.vpn.Helper.UiHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by kaiha on 02.09.2017.
 */
public class NetworkInfoFrame extends JFrame {

    public NetworkInfoFrame() {
        setTitle("NetworkInfo");
        setMinimumSize(new Dimension(500,500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        UiHelper.setLookAndFeelAndIcon(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel padding = new JPanel();
        padding.setLayout(new BorderLayout());
        padding.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(padding, BorderLayout.CENTER);
        padding.add(content, BorderLayout.CENTER);

        JTextArea outputTextArea = new JTextArea(getContent());
        outputTextArea.setEnabled(false);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        content.add(scrollPane);
        setVisible(true);
    }

    private String getContent() {
        String content = "";

        content += "External Ip:" + NetworkHelper.getExternalIp();
        content += "\n \n";

        ArrayList<String> ips = NetworkHelper.getAllUsedIpAddresses();
        for (int i = 0; i < ips.size(); i++) {
            content += "Used Ip " + i + ": " + ips.get(i) + "\n";
        }
        content += "\n \n";

        return content;
    }

}
