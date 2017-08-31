package de.hartz.vpn.Helper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by kaiha on 31.08.2017.
 */
public class UiHelper {

    /**
     * Shows an error window to inform the user.
     * https://stackoverflow.com/questions/9119481/how-to-present-a-simple-alert-message-in-java
     * @param text The text that will be displayed in the window.
     */
    public static void showAlert(String text) {
        // TODO: Respect show gui and maybe write it to the console.
        Toolkit.getDefaultToolkit().beep();
        JOptionPane optionPane = new JOptionPane(text,JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog("Error");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    /**
     * Sets the icon and look and feel of a jframe. Because its needed at serveral places.
     * @param frame the frame to setup.
     */
    public static void setLookAndFeelAndIcon(JFrame frame) {
        try {
            File file = Helper.getResourceAsFile("resources/icon.png");
            Image image = ImageIO.read( file );
            frame.setIconImage(image);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
