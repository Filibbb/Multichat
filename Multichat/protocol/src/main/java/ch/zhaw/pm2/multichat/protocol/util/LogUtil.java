package ch.zhaw.pm2.multichat.protocol.util;

/**
 * A utility class for the log statements.
 */
public class LogUtil {

    /**
     * Print text to the console.
     *
     * @param printableText The text that will be printed on the console
     */
    public static void printLine(String printableText) {
        System.out.println(printableText);
    }

    /**
     * Print an error to the console.
     *
     * @param printableText The error text that will be printed on the console
     */
    public static void printError(String printableText) {
        System.err.println(printableText);
    }
}
