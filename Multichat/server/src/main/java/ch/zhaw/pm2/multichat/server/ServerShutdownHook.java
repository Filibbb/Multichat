package ch.zhaw.pm2.multichat.server;

import java.io.IOException;

import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printError;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printLine;

/**
 * This class contains a shutdown hook which handles the proper shutdown of the server if the Application is interrupted by user input or system fail.
 *
 * @author weberph5
 * @version 1.0.0
 */
public class ServerShutdownHook extends Thread {

    private Server server;

    public ServerShutdownHook(Server server) {
        this.server = server;
    }

    private void shutdown() {
        try {
            printLine("Shutting down server...");
            printLine("Closing all connections...");
            ServerConnectionHandler.disconnectAllClients();
            printLine("Close server port.");
            server.terminateServer();
        } catch (IOException e) {
            printError("Failed to close server connection: " + e);
        }
    }

    @Override
    public void run() {
        printLine("Shutdown initiated...");
        shutdown();
        printLine("Shutdown complete.");
    }
}
