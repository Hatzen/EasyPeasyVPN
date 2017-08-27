package de.hartz.vpn.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by kaiha on 20.06.2017.
 */
public class RadioButtonWithDescription extends JPanel {

    private JRadioButton radioButton;
    private JTextArea descriptionLabel;

    public RadioButtonWithDescription(String radioButtonText, String description) {
        this(radioButtonText, description, null);
    }

    public RadioButtonWithDescription(String radioButtonText, String description, ActionListener actionListener) {
        setLayout(new GridLayout(2,1));

        radioButton = new JRadioButton(radioButtonText);
        radioButton.setMaximumSize(new Dimension(radioButton.getWidth(), 50));
        if (actionListener != null) {
            radioButton.addActionListener(actionListener);
        }
        //JPanel leftTabulator = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel leftTabulator = new JPanel(new GridLayout());
        leftTabulator.setAlignmentX(LEFT_ALIGNMENT);
        leftTabulator.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        descriptionLabel = new JTextArea(description);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setEditable(false);
        descriptionLabel.setEnabled(false);
        descriptionLabel.setBorder(null);
        descriptionLabel.setOpaque(false);
        descriptionLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));

        add(radioButton);
        add(leftTabulator);
        leftTabulator.add(descriptionLabel);
    }

    public void setEnabled(boolean enabled) {
        radioButton.setEnabled(enabled);
        descriptionLabel.setEnabled(enabled);
    }

    public void setSelected(boolean selected) {
        radioButton.setSelected(selected);
    }

    public void addToGroup(ButtonGroup group) {
        group.add(radioButton);
    }

    public boolean isEventSource(Object source) {
        return radioButton == source;
    }

}
