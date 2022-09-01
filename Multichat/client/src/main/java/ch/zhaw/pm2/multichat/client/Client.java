package ch.zhaw.pm2.multichat.client;

import javafx.application.Application;

import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printLine;

/**
 * This Class contains the main method for the Client. No arguments are expected.
 *
 * @author fupat002
 * @version 1.0.0
 */

public class Client {
    public static void main(String[] args) {
        // Start UI
        printLine("Starting Client Application");
        Application.launch(ClientUI.class, args);
        printLine("Client Application ended");
    }
}

