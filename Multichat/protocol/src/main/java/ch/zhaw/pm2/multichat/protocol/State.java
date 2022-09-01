package ch.zhaw.pm2.multichat.protocol;

/**
 * An enum that defines the status.
 */
public enum State {
    NEW,
    CONFIRM_CONNECT,
    CONNECTED,
    CONFIRM_DISCONNECT,
    DISCONNECTED;
}
