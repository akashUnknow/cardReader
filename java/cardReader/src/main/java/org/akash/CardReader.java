package org.akash;

import javax.smartcardio.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.*;

public class CardReader implements Runnable {


    private static final Logger logger = Logger.getLogger(CardReader.class.getName());

    private final Consumer<String> onUidRead;

    public CardReader(Consumer<String> onUidRead) {
        this.onUidRead = onUidRead;
    }


    @Override
    public void run() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                onUidRead.accept("NO_READER");
                return;
            }

            CardTerminal terminal = terminals.getFirst();
            logger.log(Level.INFO, "Reader detected: {0}", terminal.getName());

            while (!Thread.currentThread().isInterrupted()) {
                terminal.waitForCardPresent(0);
//                logger.info("Card detected");

                try {
                    readCardUID(terminal);
                } catch (CardException e) {
                    // Already logged in readCardUID
                }

                terminal.waitForCardAbsent(0);
//                logger.info("Card removed");
            }

        } catch (Exception e) {
            onUidRead.accept("INIT_ERROR");
            logger.log(Level.SEVERE, "Error initializing card reader", e);
        }
    }

    private void readCardUID(CardTerminal terminal) throws CardException {
        Card card = null;
        try {
            card = terminal.connect("*");
            CardChannel channel = card.getBasicChannel();

            CommandAPDU getUid = new CommandAPDU(
                    new byte[]{(byte) 0xFF, (byte) 0xCA, 0x00, 0x00, 0x00}
            );

            ResponseAPDU response = channel.transmit(getUid);

            if (response.getSW() == 0x9000) {
                String uid = bytesToHex(response.getData());
                onUidRead.accept(uid);
//                logger.log(Level.INFO, "Card UID: {0}", uid);
            } else {
                onUidRead.accept("READ_FAILED");
                logger.warning("Failed to read card UID. SW=" + Integer.toHexString(response.getSW()));
            }

        } catch (CardException e) {
            logger.log(
                    Level.WARNING,
                    "Card communication error on terminal: {0}",
                    terminal.getName()
            );
        } finally {
            if (card != null) {
                try {
                    card.disconnect(false);
                } catch (CardException ex) {
                    logger.log(Level.WARNING, "Error disconnecting card", ex);
                }
            }
        }
    }

    private String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();

    }
}
