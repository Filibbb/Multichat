package ch.zhaw.pm2.multichat.client;

import ch.zhaw.pm2.multichat.protocol.message.Message;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

import static ch.zhaw.pm2.multichat.protocol.message.DataType.MESSAGE;

/**
 * Holds a list of {@link Message}.
 * Provides bound to allow a GUI to listen to changes.
 * Filters can be applied in order to display only messages containing a certain keyword.
 */
public class ClientMessageList {

    private static final String NO_FILTER = "";
    private static final String NO_MESSAGE = "";

    private final List<Message> messageList;
    private final StringProperty messagesBound;
    private String filter;

    /**
     * Creates a new message list.
     */
    public ClientMessageList() {
        messageList = new ArrayList<>();
        messagesBound = new SimpleStringProperty();
        messagesBound.set(NO_MESSAGE);
        filter = NO_FILTER;
    }

    /**
     * Adds a {@link Message} to the list and updates the messagesBound
     *
     * @param message which is to be added to the list
     */
    public void addMessage(Message message) {
        messageList.add(message);
        messagesBound.set(messagesBound.getValue().concat(formatMessage(message)));
    }

    /**
     * Searches for the specified keyword in all messages contained in the list and
     * updates the messagesBound to show only the messages containing the keyword.
     *
     * @param filterKeyword to look for in all messages
     */
    public void applyFilter(String filterKeyword) {
        filter = filterKeyword;
        StringBuilder output = new StringBuilder();
        for (Message message : messageList) {
            if (message.contains(filter)) {
                output.append(formatMessage(message));
            }
        }
        messagesBound.set(output.toString());
    }

    private String formatMessage(Message message) {
        if (message.getType().equals(MESSAGE)) {
            return String.format("[%s -> %s] %s\n", message.getSender(), message.getReceiver(), message.getPayload());
        } else {
            return String.format("[%s] %s\n", message.getType(), message.getPayload());
        }
    }

    public StringProperty getMessagesBound() {
        return messagesBound;
    }
}
