package org.akash;

public class Main {

    public static void main(String[] args) {

        CardReader cardReader = new CardReader(uid -> {
            System.out.println("Card UID: " + uid);
        });

        Thread readerThread = new Thread(cardReader);
        readerThread.start();

        System.out.println("Waiting for card...");

        try {
            readerThread.join(); // ⭐ THIS KEEPS JVM ALIVE
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
