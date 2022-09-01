package ch.zhaw.pm2.multichat.protocol;

import ch.zhaw.pm2.multichat.protocol.message.DataType;
import ch.zhaw.pm2.multichat.protocol.message.Message;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;

import static ch.zhaw.pm2.multichat.protocol.State.NEW;
import static ch.zhaw.pm2.multichat.protocol.message.DataType.ERROR;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printError;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printLine;

/**
 * This Class contains the logic for the ConnectionHandler which handles the sending and receiving of Data for every connected  client separately.
 *
 * @author fupot002, weberph5
 * @version 1.0.0
 */
public abstract class ConnectionHandler implements Runnable {

    protected final NetworkHandler.NetworkConnection<Message> connection;

    protected String userName;
    protected State state = NEW;

    public static final String USER_NONE = "";
    public static final String USER_ALL = "*";

    protected ConnectionHandler(NetworkHandler.NetworkConnection<Message> connection, String userName) {
        if (connection == null) {
            throw new NullPointerException("Connection can not be null");
        } else {
            this.connection = connection;
        }
        this.userName = (userName == null || userName.isBlank()) ? USER_NONE : userName;
    }

    @Override
    public void run() {
        printLine("Starting Connection Handler");
        try {
            printLine("Start receiving data...");
            while (connection.isAvailable()) {
                Message message = connection.receive();
                processMessage(message);
            }
            printLine("Stopped receiving data");
        } catch (SocketException e) {
            handleSocketException(e.getMessage());
        } catch (EOFException e) {
            handleEOFException(e.getMessage());
        } catch (IOException e) {
            handleIOException(e.getMessage());
        } catch (ClassNotFoundException e) {
            handleClassNotFoundException(e.getMessage());
        }
        printLine("Stopped Connection Handler");
    }

    protected abstract void handleSocketException(String exceptionMessage);

    protected abstract void handleEOFException(String exceptionMessage);

    protected abstract void handleIOException(String exceptionMessage);

    protected abstract void handleClassNotFoundException(String exceptionMessage);

    private void processMessage(Message message) {
        try {
            checkMessage(message);
            DataType type = message.getType();
            switch (type) {
                case CONNECT -> handleConnectMessage(message.getSender());
                case CONFIRM -> handleConfirmMessage(message);
                case DISCONNECT -> handleDisconnectMessage(message);
                case MESSAGE -> handleMessage(message);
                case ERROR -> handleErrorMessage(message);
                default -> printError("Unknown data type received: " + type);
            }
        } catch (ChatProtocolException e) {
            printError("Error while processing data: " + e.getMessage());
            sendError(e.getMessage());
        }
    }

    protected abstract void handleConnectMessage(String sender) throws ChatProtocolException;

    protected abstract void handleConfirmMessage(Message message);

    protected abstract void handleDisconnectMessage(Message message) throws ChatProtocolException;

    protected abstract void handleMessage(Message message) throws ChatProtocolException;

    protected abstract void handleErrorMessage(Message message);

    /**
     * This method is used to send Error messages to a user if they perform an invalid operation.
     *
     * @param text The Error Message to be sent.
     */
    public void sendError(String text) {
        sendMessage(USER_NONE, userName, ERROR, text);
    }

    /**
     * This method is used to covert input into a Message object to be sent between Clients and Server.
     *
     * @param sender   The sender of the Message.
     * @param receiver The receiver of the Message. Can be USER_ALL if the message is not private.
     * @param type     The type of the Message as in the Enum DataType.
     * @param payload  The contents of the message.
     */
    public void sendMessage(String sender, String receiver, DataType type, String payload) {
        Message message = new Message(sender, receiver, type, payload);
        sendMessage(message);
    }

    /**
     * Sends the above created Message Object.
     *
     * @param message A Message Object.
     */
    public void sendMessage(Message message) {
        if (connection.isAvailable()) {
            try {
                connection.send(message);
            } catch (SocketException e) {
                printError("Connection closed: " + e.getMessage());
            } catch (EOFException e) {
                printError("Connection terminated by remote");
            } catch (IOException e) {
                printError("Communication error: " + e.getMessage());
            }
        }
    }

    /**
     * Method to Stop Receiving Data when a connection is closed.
     */
    public synchronized void stopReceiving() {
        printLine("Closing Connection Handler for " + userName);
        try {
            printLine("Stop receiving data...");
            connection.close();
            printLine("Stopped receiving data.");
        } catch (IOException e) {
            printError("Failed to close connection." + e);
        }
        printLine("Closed Connection Handler for " + userName);
    }

    private void checkMessage(Message message) throws ChatProtocolException {
        if (message.getSender() == null) {
            throw new ChatProtocolException("No Sender found");
        }
        if (message.getReceiver() == null) {
            throw new ChatProtocolException("No Receiver found");
        }
        if (message.getType() == null) {
            throw new ChatProtocolException("No Type found");
        }
    }

    public String getUserName() {
        return userName;
    }
}
