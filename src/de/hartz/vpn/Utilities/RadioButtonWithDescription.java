package de.hartz.vpn.Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * GUI class to be able to have title and description to a radio button.
 */
public class RadioButtonWithDescription extends JPanel {

    private JRadioButton radioButton;
    private Component descriptionComponent;

    public RadioButtonWithDescription(String radioButtonText, String description) {
        this(radioButtonText, description, null);
    }

    public RadioButtonWithDescription(String radioButtonText, String description, ActionListener actionListener) {
        this(radioButtonText, getDescriptionLabel(description) , actionListener);
    }

    public RadioButtonWithDescription(String radioButtonText, Component descriptionComponent, ActionListener actionListener) {
        this.descriptionComponent = descriptionComponent;
        setLayout(new GridLayout(2,1));

        radioButton = new JRadioButton(radioButtonText);
        radioButton.setMaximumSize(new Dimension(radioButton.getWidth(), 50));
        if (actionListener != null) {
            radioButton.addActionListener(actionListener);
        }
        JPanel leftTabulator = new JPanel(new GridLayout());
        leftTabulator.setAlignmentX(LEFT_ALIGNMENT);
        leftTabulator.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        add(radioButton);
        add(leftTabulator);
        leftTabulator.add(descriptionComponent);
    }

    public void setEnabled(boolean enabled) {
        radioButton.setEnabled(enabled);
        setDescriptionComponentEnabled(enabled);
    }

    public void setDescriptionComponentEnabled(boolean enabled) {
        setSubComponentsEnabled(descriptionComponent, enabled);
    }

    private void setSubComponentsEnabled(Component component,boolean enabled) {
        if (component instanceof JPanel) {
            Component[] components = ((JPanel) component).getComponents();

            for (Component c: components) {
                if ( c instanceof JPanel) {
                    setSubComponentsEnabled(c, enabled);
                }
                c.setEnabled(enabled);
            }
        }
        component.setEnabled(false);
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

    private static JTextArea getDescriptionLabel(String description) {
        JTextArea descriptionLabel = new JTextArea(description);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setEditable(false);
        descriptionLabel.setEnabled(false);
        descriptionLabel.setBorder(null);
        descriptionLabel.setOpaque(false);
        descriptionLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));

        return descriptionLabel;
    }

}
