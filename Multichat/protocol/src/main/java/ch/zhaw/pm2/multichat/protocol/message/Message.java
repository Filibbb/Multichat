package ch.zhaw.pm2.multichat.protocol.message;

import java.io.Serializable;

/**
 * This class contains the logic for the Message object which will be passed between the clients and the server.
 *
 * @author fupat002
 * @version 1.0.0
 */

public class Message implements Serializable {

    private final String sender;
    private final String receiver;
    private final DataType type;
    private final String payload;

    public Message(String sender, String receiver, DataType type, String payload) {
        this.sender = sender;
        this.receiver = receiver;
        this.type = type;
        this.payload = payload;

    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public DataType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    /**
     * This method checks if the message contains a certain keyword in any of the fields.
     *
     * @param keyword the keyword you want to match.
     * @return true if the message contains the keyword. False if not.
     */
    public boolean contains(String keyword) {
        return sender.contains(keyword) || receiver.contains(keyword) || payload.contains(keyword) || type.toString().contains(keyword);
    }
}
