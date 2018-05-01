package de.hartz.vpn.helper;

import javax.swing.*;
import java.awt.*;

/**
 * A circle that is red or green depending on the boolean online status.
 */
public class StatusComponent extends JLabel implements ListCellRenderer<String> {

    public final static int PADDING = 5;
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
        g.translate(5*PADDING,0);
        super.paint(g);
        g.translate(-5*PADDING,0);
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
    public Component getListCellRendererComponent(JList<? extends String> list, String text, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        setOpaque(true);
        setOnline(true);
        if (isSelected) {
            setBackground(Color.lightGray);
        } else if (cellHasFocus) {
            setBackground(Color.lightGray.brighter());
        }

        setText(text);

        return this;
    }

}
