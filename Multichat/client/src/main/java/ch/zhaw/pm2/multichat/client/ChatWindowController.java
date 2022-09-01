package ch.zhaw.pm2.multichat.client;

import ch.zhaw.pm2.multichat.protocol.ChatProtocolException;
import ch.zhaw.pm2.multichat.protocol.ConnectionHandler;
import ch.zhaw.pm2.multichat.protocol.NetworkHandler;
import ch.zhaw.pm2.multichat.protocol.State;
import ch.zhaw.pm2.multichat.protocol.message.Message;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.zhaw.pm2.multichat.protocol.ConnectionHandler.USER_NONE;
import static ch.zhaw.pm2.multichat.protocol.State.*;
import static ch.zhaw.pm2.multichat.protocol.message.DataType.ERROR;
import static ch.zhaw.pm2.multichat.protocol.message.DataType.MESSAGE;

/**
 * This is the controller for the chat window.
 * It takes care of the initialization of the window when it is opened and also easy interaction for the user
 */
public class ChatWindowController {

    private static final int EXECUTOR_TIMEOUT = 1;
    private static final int MATCHER_ONE = 1;
    private static final int MATCHER_TWO = 2;
    private static final String SPACE_IN_USER_NAME = " ";

    private final ExecutorService connectionHandlerExecutor = Executors.newSingleThreadExecutor();
    private final Pattern messagePattern = Pattern.compile("^(?:@(\\w*))?\\s*(.*)$");
    private final ClientMessageList receivedMessages = new ClientMessageList();
    private ClientConnectionHandler connectionHandler;

    @FXML
    private Pane rootPane;
    @FXML
    private TextField serverAddressField;
    @FXML
    private TextField serverPortField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField messageField;
    @FXML
    private TextArea messageArea;
    @FXML
    private Button connectButton;
    @FXML
    private Button sendButton;
    @FXML
    private TextField filterValue;


    /**
     * Sets the Server address and Server port text fields to their default values, specified by the {@link NetworkHandler}.
     */
    @FXML
    public void initialize() {
        serverAddressField.setText(NetworkHandler.DEFAULT_ADDRESS.getCanonicalHostName());
        serverPortField.setText(String.valueOf(NetworkHandler.DEFAULT_PORT));
        stateChanged(NEW);
    }

    @FXML
    private void message() {
        if (connectionHandler == null) {
            localConnectionHandlerError("No connection handler");
            return;
        }
        createMessage(messageField.getText().strip());
        showMessages();
    }

    private void createMessage(String messageString) {
        Matcher matcher = messagePattern.matcher(messageString);
        if (matcher.find()) {
            String receiver = matcher.group(MATCHER_ONE);
            String message = matcher.group(MATCHER_TWO);
            if (receiver == null || receiver.isBlank()) receiver = ConnectionHandler.USER_ALL;
            sendMessage(receiver, message);
        } else {
            connectionHandler.sendError("Not a valid message format.");
        }
    }

    private void sendMessage(String receiver, String message) {
        State state = connectionHandler.getState();
        if (state != CONNECTED) {
            connectionHandler.sendError("Illegal state for message: " + state);
        } else {
            connectionHandler.sendMessage(connectionHandler.getUserName(), receiver, MESSAGE, message);
        }
    }

    @FXML
    private void applyFilter() {
        Platform.runLater(() -> receivedMessages.applyFilter(filterValue.getText().strip()));
        showMessages();
    }

    @FXML
    private void toggleConnection() {
        if ((connectionHandler == null || connectionHandler.getState() != CONNECTED) && isValidUserName(userNameField.getText().trim())) {
            connect();
        } else {
            if (!isValidUserName(userNameField.getText().trim())) {
                connectionHandler.sendError("The username must not contain spaces!");
            }
            disconnect();
        }
    }

    private void connect() {
        try {
            startConnectionHandler();
            connectionHandlerExecutor.execute(connectionHandler);
            connectionHandler.connect();
            stateChanged(CONNECTED);
        } catch (ChatProtocolException | IOException e) {
            localConnectionHandlerError("Check the entered server settings");
            connectionHandler.sendError(e.getMessage());
        }
    }

    private void disconnect() {
        if (connectionHandler == null) {
            localConnectionHandlerError("No connection handler");
            return;
        }
        connectionHandler.disconnect();
        stateChanged(DISCONNECTED);
    }

    public void addShutdownRoutines() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        rootPane.getScene().getWindow().setOnCloseRequest(event -> shutdown());
    }

    private void shutdown() {
        try {
            if (connectionHandler != null && connectionHandler.getState() == CONNECTED) {
                connectionHandler.disconnect();
            }
            connectionHandlerExecutor.shutdown();
            if (!connectionHandlerExecutor.awaitTermination(EXECUTOR_TIMEOUT, TimeUnit.SECONDS)) {
                connectionHandlerExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            connectionHandlerExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void startConnectionHandler() throws IOException {
        String userName = userNameField.getText().trim();
        String serverAddress = serverAddressField.getText();
        int serverPort = Integer.parseInt(serverPortField.getText());
        connectionHandler = new ClientConnectionHandler(NetworkHandler.openConnection(serverAddress, serverPort), userName, receivedMessages);
    }

    private void stateChanged(State newState) {
        Platform.runLater(() -> connectButton.setText((newState == CONNECTED || newState == CONFIRM_DISCONNECT) ? "Disconnect" : "Connect"));
        showMessages();
        if (newState == DISCONNECTED) {
            connectionHandler = null;
        }
    }

    private void showMessages() {
        receivedMessages.getMessagesBound().addListener((observable, oldValue, newValue) -> messageArea.setText(newValue));
    }

    private boolean isValidUserName(String userName) {
        return !userName.contains(SPACE_IN_USER_NAME);
    }

    private void localConnectionHandlerError(String text) {
        receivedMessages.addMessage(new Message(USER_NONE, USER_NONE, ERROR, text));
    }
}
