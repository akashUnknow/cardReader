package org.akash;

import javax.smartcardio.*;
import java.util.List;
import java.util.function.Consumer;

public class CardReader implements Runnable {

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

            CardTerminal terminal = terminals.get(0);
            System.out.println("Reader detected: " + terminal.getName());

            while (true) {
                terminal.waitForCardPresent(0);
                System.out.println("Card detected");

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
                    } else {
                        onUidRead.accept("READ_FAILED");
                    }

                } catch (Exception e) {
                    onUidRead.accept("ERROR");
                    e.printStackTrace();
                } finally {
                    if (card != null) {
                        card.disconnect(false);
                    }
                }

                terminal.waitForCardAbsent(0);
            }

        } catch (Exception e) {
            onUidRead.accept("INIT_ERROR");
            e.printStackTrace();
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
