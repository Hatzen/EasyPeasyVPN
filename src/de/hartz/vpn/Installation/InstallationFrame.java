package de.hartz.vpn.Installation;

import de.hartz.vpn.Helper.Helper;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/**
 * Created by kaiha on 28.05.2017.
 */
public class InstallationFrame extends JFrame implements ActionListener {

    private JPanel content;
    private InstallationPanel currentContentPanel;

    private JButton nextButton;
    private JButton previousButton;

    public InstallationFrame(String title) {
        setTitle(title);
        setMinimumSize(new Dimension(500,500));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            File file = Helper.getResourceAsFile("resources/icon.png");
            Image image = ImageIO.read( file );
            setIconImage(image);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        JPanel navigation = new JPanel();
        navigation.setLayout(new BorderLayout());
        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        previousButton = new JButton("Previous");
        previousButton.addActionListener(this);
        navigation.add(nextButton, BorderLayout.EAST);
        navigation.add(previousButton, BorderLayout.WEST);

        content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel padding = new JPanel();
        padding.setLayout(new BorderLayout());
        padding.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(padding, BorderLayout.CENTER);
        padding.add(content, BorderLayout.CENTER);
        padding.add(navigation, BorderLayout.SOUTH);

    }

    public void setContent(InstallationPanel content) {
        currentContentPanel = content;
        this.content.removeAll();
        this.content.add(content);
        this.content.validate();
        this.content.repaint();
    }


    public void setNextEnabled(boolean enabled) {
        nextButton.setEnabled(enabled);
    }

    public void setPreviousEnabled(boolean enabled) {
        previousButton.setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == nextButton) {
            InstallationController.getInstance().onNextClick(getCurrentContentPanel());
        } else if (actionEvent.getSource() == previousButton) {
            InstallationController.getInstance().onPreviousClick(getCurrentContentPanel());
        }
    }

    private InstallationPanel getCurrentContentPanel() {
        return currentContentPanel;
    }
}
