package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientClass2 extends JFrame implements ActionListener {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientClass2::new);
    }
    private final JTextField usernameField;
    private final JButton loginButton;
    private final JTextArea chatArea;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton deleteButton;
    private final JButton exitButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean loggedIn = false;
    private String username;

    public ClientClass2() {
        super("Client 2");
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernameField = new JTextField(15);
        loginButton = new JButton("login");
        loginButton.setEnabled(false);
        loginButton.addActionListener(this);
        topPanel.add(usernameField);
        topPanel.add(loginButton);
        add(topPanel, BorderLayout.NORTH);
        chatArea = new JTextArea(10, 40);
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messageField = new JTextField(25);
        sendButton = new JButton("post");
        sendButton.setEnabled(false);
        sendButton.addActionListener(this);
        deleteButton = new JButton("delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(this);
        exitButton = new JButton("exit");
        exitButton.addActionListener(this);
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                    loginButton.setEnabled(!usernameField.getText().trim().isEmpty());
                } else {
                    e.consume();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == loginButton && !loggedIn) {
            username = usernameField.getText().trim();
            connectToServer();
            loginButton.setEnabled(false);
            sendButton.setEnabled(true);
            deleteButton.setEnabled(true);
            loggedIn = true;
        } else if (source == sendButton) {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                out.println(username + ": " + message);
                messageField.setText("");
            }
        } else if (source == deleteButton) {
            messageField.setText("");
        } else if (source == exitButton) {
            out.println("exit");
            disconnectFromServer();
            System.exit(0);
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 8080);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Thread readerThread = new Thread(this::readMessages);
            readerThread.start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Не удалось подключиться к серверу.", "Ошибка подключения", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void readMessages() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                chatArea.append(line + "\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void disconnectFromServer() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
