package de.hartz.vpn.main.chat.view;

import de.hartz.vpn.main.chat.ChatController;
import de.hartz.vpn.main.chat.UserDataChangedListener;
import de.hartz.vpn.main.chat.model.Message;
import de.hartz.vpn.main.chat.model.User;
import de.hartz.vpn.utilities.UiUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by kaiha on 25.04.2018.
 */
public class ChatFrame extends JFrame implements UserDataChangedListener {
    private JTextArea outputTextArea;
    private JTextArea inputTextArea;

    public ChatFrame(final User user, final ChatController chatController) {
        setSize(new Dimension(400,500));
        setLocationRelativeTo(null);
        setLayout( new BorderLayout() );
        UiUtilities.setLookAndFeelAndIcon(this);

        outputTextArea = new JTextArea();
        outputTextArea.setEnabled(false);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(inputTextArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JButton sendText = new JButton("Send");
        sendText.setPreferredSize(new Dimension(90,60));
        sendText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputTextArea.getText();
                inputTextArea.setText("");
                chatController.sendMessage(text, user);
                inputTextArea.requestFocus();
            }
        });
        bottomPanel.add(sendText, BorderLayout.EAST);

        changedName(user.getName());
        for (Message message: user.getMessages()) {
            outputTextArea.append(message.getReprensation());
        }
        user.addListener(this);
    }

    @Override
    public void addedMessage(Message message) {
        outputTextArea.append(message.getReprensation());
    }

    @Override
    public void changedName(String name) {
        setTitle(name);
    }
}
