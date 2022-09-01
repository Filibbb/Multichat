package ch.zhaw.pm2.multichat.client;

import ch.zhaw.pm2.multichat.protocol.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.ConnectionHandler;
import ch.zhaw.pm2.multichat.protocol.NetworkHandler.NetworkConnection;
import ch.zhaw.pm2.multichat.protocol.State;
import ch.zhaw.pm2.multichat.protocol.message.Message;

import static ch.zhaw.pm2.multichat.protocol.State.*;
import static ch.zhaw.pm2.multichat.protocol.message.DataType.CONNECT;
import static ch.zhaw.pm2.multichat.protocol.message.DataType.DISCONNECT;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printError;
import static ch.zhaw.pm2.multichat.protocol.util.LogUtil.printLine;

/**
 * Is responsible for establishing and managing the network connection on the client side.
 * Allows you to connect, send and receive messages, access received messages, and disconnect.
 */
public class ClientConnectionHandler extends ConnectionHandler {

    private final ClientMessageList receivedMessages;

    /**
     * Creates a new network connection to a server as specified by the given parameters.
     *
     * @param connection       the connection handler should handle
     * @param userName         of the user
     * @param receivedMessages list containing received messages
     */
    public ClientConnectionHandler(NetworkConnection<Message> connection, String userName, ClientMessageList receivedMessages) {
        super(connection, userName);
        this.receivedMessages = receivedMessages;
    }

    /**
     * Sends a connect message and sets the status
     *
     * @throws ChatProtocolException if state does not equal {@link State NEW}
     */
    public void connect() throws ChatProtocolException {
        if (state != NEW) throw new ChatProtocolException("Illegal state for connect: " + state);
        sendMessage(userName, USER_NONE, CONNECT, null);
        setState(CONFIRM_CONNECT);
    }

    /**
     * Sends a disconnect message and sets the status
     */
    public void disconnect() {
        if (state != NEW && state != CONNECTED)
            throw new IllegalStateException("Illegal state for disconnect: " + state);
        sendMessage(userName, USER_NONE, DISCONNECT, null);
        setState(CONFIRM_DISCONNECT);
    }

    @Override
    protected void handleConnectMessage(String sender) {
        printError("Illegal connect request from server");
    }

    @Override
    protected void handleConfirmMessage(Message message) {
        String payload = message.getPayload();
        receivedMessages.addMessage(message);
        if (state == CONFIRM_CONNECT) {
            this.userName = message.getReceiver();
            printLine("CONFIRM: " + payload);
            setState(CONNECTED);
        } else if (state == CONFIRM_DISCONNECT) {
            printLine("CONFIRM: " + payload);
            setState(DISCONNECTED);
        } else {
            printError("Got unexpected confirm message: " + payload);
        }
    }

    @Override
    protected void handleDisconnectMessage(Message message) {
        String payload = message.getPayload();
        if (state == DISCONNECTED) {
            printError("DISCONNECT: Already in disconnected: " + payload);
            return;
        }
        receivedMessages.addMessage(message);
        printLine("DISCONNECT: " + payload);
        setState(DISCONNECTED);
    }

    @Override
    protected void handleMessage(Message message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();
        String payload = message.getPayload();
        if (state != CONNECTED) {
            printError("MESSAGE: Illegal state " + state + " for message: " + payload);
            return;
        }
        receivedMessages.addMessage(message);
        printLine("MESSAGE: From " + sender + " to " + receiver + ": " + payload);
    }

    @Override
    protected void handleErrorMessage(Message message) {
        receivedMessages.addMessage(message);
        printError("ERROR: " + message.getPayload());
    }

    @Override
    protected void handleSocketException(String exceptionMessage) {
        printLine("Connection terminated locally");
        setState(DISCONNECTED);
        printError("Unregistered because connection terminated " + exceptionMessage);
    }

    @Override
    protected void handleEOFException(String exceptionMessage) {
        printLine("Connection terminated by remote");
        setState(DISCONNECTED);
        printError("Unregistered because connection terminated" + exceptionMessage);
    }

    @Override
    protected void handleIOException(String exceptionMessage) {
        printError("Communication error" + exceptionMessage);
    }

    @Override
    protected void handleClassNotFoundException(String exceptionMessage) {
        printError("Received object of unknown type" + exceptionMessage);
    }

    public State getState() {
        return this.state;
    }

    public void setState(State newState) {
        this.state = newState;
        if(state == DISCONNECTED){
            stopReceiving();
        }
    }
}
