package org.akash;

import java.util.logging.*;
import java.text.MessageFormat;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        // ---- Logger configuration ----
        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                handler.setFormatter(new Formatter() {
                    @Override
                    public synchronized String format(LogRecord record) {
                        String msg = record.getMessage();
                        if (record.getParameters() != null) {
                            msg = MessageFormat.format(msg, record.getParameters());
                        }
                        return msg + System.lineSeparator();
                    }
                });
            }
        }
        // --------------------------------

        CardReader cardReader = new CardReader(uid ->
                logger.log(Level.INFO, "Card UID: {0}", uid)
        );

        Thread readerThread = new Thread(cardReader);
        readerThread.start();

        logger.info("Waiting for card...");

        try {
            readerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warning("Main thread interrupted");
        }
    }
}
