package ch.zhaw.pm2.multichat.server;

import ch.zhaw.pm2.multichat.protocol.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.ConnectionHandler;
import ch.zhaw.pm2.multichat.protocol.NetworkHandler.NetworkConnection;
import ch.zhaw.pm2.multichat.protocol.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.zhaw.pm2.multichat.protocol.State.*;
import static ch.zhaw.pm2.multichat.protocol.message.DataType.*;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printError;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printLine;

/**
 * This Class is the extension of the ConnectionHandler class to handle all Connection Server-side.
 *
 * @author fupat002, weberph5
 * @version 1.0.0
 */
public class ServerConnectionHandler extends ConnectionHandler {

    private static final AtomicInteger CONNECTION_COUNTER = new AtomicInteger(0);
    private static final Map<String, ServerConnectionHandler> REGISTERED_CONNECTIONS = new HashMap<>();

    /**
     * Creates a ServerConnectionHandler
     *
     * @param connection The network connection to communicate with clients
     */
    public ServerConnectionHandler(NetworkConnection<Message> connection) {
        super(connection, USER_NONE);
    }

    /**
     * This method disconnects all clients when the Server is stopped.
     */
    public static void disconnectAllClients() {
        for (Map.Entry<String, ServerConnectionHandler> connection : REGISTERED_CONNECTIONS.entrySet()) {
            connection.getValue().sendMessage(null, null, DISCONNECT, null);
        }
        REGISTERED_CONNECTIONS.clear();
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    protected void handleSocketException(String exceptionMessage) {
        printLine("Connection terminated locally");
        REGISTERED_CONNECTIONS.remove(userName);
        printError("Unregistered because client connection terminated: " + userName + " " + exceptionMessage);
    }

    @Override
    protected void handleEOFException(String exceptionMessage) {
        printLine("Connection terminated by remote");
        REGISTERED_CONNECTIONS.remove(userName);
        printLine("Unregistered because client connection terminated: " + userName + " " + exceptionMessage);
    }

    @Override
    protected void handleIOException(String exceptionMessage) {
        printError("Communication error: " + exceptionMessage);
    }

    @Override
    protected void handleClassNotFoundException(String exceptionMessage) {
        printError("Received object of unknown type: " + exceptionMessage);
    }

    @Override
    protected void handleConnectMessage(String sender) throws ChatProtocolException {
        if (this.state != NEW) {
            throw new ChatProtocolException("Illegal state for connect request: " + state);
        }
        if (REGISTERED_CONNECTIONS.containsKey(sender)) {
            throw new ChatProtocolException("User name already taken: " + sender);
        }
        connect(sender);
    }

    private void connect(String sender) {
        decideUserName(sender);
        REGISTERED_CONNECTIONS.put(userName, this);
        printLine(String.format("New Client %s with IP:Port <%s:%d>", userName, connection.getRemoteHost(), connection.getRemotePort()));
        sendMessage(USER_NONE, userName, CONFIRM, "Registration successful for " + userName);
        state = CONNECTED;
    }

    private void decideUserName(String sender) {
        if (sender == null || sender.isBlank()) {
            sender = this.userName;
        }
        if (isAnonymous(sender)) {
            this.userName = "Anonymous-" + CONNECTION_COUNTER.incrementAndGet();
        } else {
            this.userName = sender;
        }
    }

    private boolean isAnonymous(String sender) {
        return sender == null || USER_NONE.equals(sender) || sender.isBlank();
    }

    @Override
    protected void handleConfirmMessage(Message message) {
        printLine("Not expecting to receive a CONFIRM request from client");
    }

    @Override
    protected void handleDisconnectMessage(Message message) throws ChatProtocolException {
        if (state == DISCONNECTED) throw new ChatProtocolException("Illegal state for disconnect request: " + state);
        if (state == CONNECTED) {
            REGISTERED_CONNECTIONS.remove(this.userName);
        }
        sendMessage(USER_NONE, userName, CONFIRM, "Confirm disconnect of " + userName);
        this.state = DISCONNECTED;
        this.stopReceiving();
    }

    @Override
    protected void handleMessage(Message message) throws ChatProtocolException {
        if (state != CONNECTED) throw new ChatProtocolException("Illegal state for message request: " + state);
        if (USER_ALL.equals(message.getReceiver())) {
            for (ServerConnectionHandler handler : REGISTERED_CONNECTIONS.values()) {
                handler.sendMessage(message);
            }
        } else {
            ServerConnectionHandler receiverHandler = REGISTERED_CONNECTIONS.get(message.getReceiver());
            ServerConnectionHandler senderHandler = REGISTERED_CONNECTIONS.get(message.getSender());
            if (receiverHandler != null && senderHandler != null) {
                receiverHandler.sendMessage(message);
                senderHandler.sendMessage(message);
            } else {
                this.sendMessage(USER_NONE, userName, ERROR, "Unknown User: " + message.getReceiver());
            }
        }
    }

    @Override
    protected void handleErrorMessage(Message message) {
        printLine("Received error from client (" + message.getSender() + "): " + message.getPayload());
    }
}
