package ch.zhaw.pm2.multichat.server;

import java.io.IOException;

import static ch.zhaw.pm2.multichat.protocol.NetworkHandler.DEFAULT_PORT;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printError;

/**
 * This Class contains the main method to start a new Server. It checks the port argument for its validity and passes it to the Server Application.
 *
 * @author weberph5
 * @version 1.0.0
 */
public class ServerApplication {
    static int port = DEFAULT_PORT;
    static Server server;

    /**
     * The main method to start a new Server.
     *
     * @param args If a valid port number is provided then the server starts on that port. If something invalid is provided, the server starts on the default port.
     * @throws IOException When an I/O operation fails.
     */
    public static void main(String[] args) throws IOException {
        checkArgs(args);
        initializeServer(port);
    }

    private static void initializeServer(int port) throws IOException {
        server = new Server(port);
        server.start();
    }

    private static void checkArgs(String[] args) {
        if (args.length == 1) {
            port = tryToParseArgs(args);
        } else if (args.length > 1) {
            printError("Illegal number of arguments. Starting server on default port: " + DEFAULT_PORT);
        }
    }

    private static int tryToParseArgs(String[] args) {
        try {
            int arg = Integer.parseInt(args[0]);
            if (arg >= 0 && arg <= 65535) {
                return arg;
            } else {
                printError("The selected Port is out of range. Starting server on default port: " + DEFAULT_PORT);
                return DEFAULT_PORT;
            }
        } catch (NumberFormatException ex) {
            printError("Invalid argument. Starting server on default port: " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }
}

