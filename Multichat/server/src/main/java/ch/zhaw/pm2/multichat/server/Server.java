package ch.zhaw.pm2.multichat.server;

import ch.zhaw.pm2.multichat.protocol.message.Message;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ch.zhaw.pm2.multichat.protocol.NetworkHandler.*;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printError;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printLine;

/**
 * This class contains the logic and runtime for The Server. The Server handles all connections.
 *
 * @author weberph5
 * @version 1.0.0
 */
public class Server {

    private final ExecutorService connectionThreadExecutor;
    // Server connection
    private final NetworkServer<Message> networkServer;
    private boolean running = true;

    /**
     * Creates a Server Object.
     *
     * @param serverPort Port to listen for new connection attempts.
     * @throws IOException When an I/O operation fails.
     */
    public Server(int serverPort) throws IOException {
        connectionThreadExecutor = Executors.newCachedThreadPool();
        printLine("Create server connection");
        networkServer = createServer(serverPort);
        printLine("Listening on " + networkServer.getHostAddress() + ":" + networkServer.getHostPort());
        printLine("Server created");
    }

    /**
     * This method starts the server and runs it until it is interrupted by an external operation like aborting the process or setting "running" to false through the terminateServer method.
     */
    public void start() {
        printLine("Server started.");
        Runtime.getRuntime().addShutdownHook(new ServerShutdownHook(this));
        try {
            while (running) {
                NetworkConnection<Message> connection = networkServer.waitForConnection();
                ServerConnectionHandler connectionHandler = new ServerConnectionHandler(connection);
                connectionThreadExecutor.execute(connectionHandler);
            }
        } catch (IOException e) {
            printError("Communication error " + e);
        }
        // close server
        printLine("Server Stopped.");
    }

    /**
     * This method terminates the server and closes the connections.
     *
     * @throws IOException When an I/O operation fails.
     */
    public void terminateServer() throws IOException {
        networkServer.close();
        running = false;
    }
}
