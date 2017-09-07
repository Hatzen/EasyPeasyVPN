package de.hartz.vpn.helper;

import javax.swing.*;
import java.awt.*;

/**
 * A circle that is red or green depending on the boolean online status.
 */
public class StatusComponent extends JLabel implements ListCellRenderer<String> {

    private final static int PADDING = 10;
    private boolean online;
    private boolean connecting;


    public void setOnline(boolean online) {
        this.online = online;
        connecting = false;
        repaint();
    }

    public void setConnecting(boolean connecting) {
        this.connecting = connecting;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if(online) {
            g2d.setPaint(Color.green);
        } else {
            g2d.setPaint(Color.red);
        }
        if (connecting) {
            g2d.setPaint(Color.yellow);
        }
        g2d.fillOval(PADDING, PADDING, getHeight()-2*PADDING, getHeight()-2*PADDING);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String country, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setOpaque(true);
        setBackground(Color.green);
        setText(country);

        return this;
    }

}
