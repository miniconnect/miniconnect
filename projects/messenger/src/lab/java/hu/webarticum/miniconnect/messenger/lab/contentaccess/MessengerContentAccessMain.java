package hu.webarticum.miniconnect.messenger.lab.contentaccess;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import hu.webarticum.miniconnect.tool.contentaccess.FileChargeableContentAccess;
import hu.webarticum.miniconnect.util.data.ByteString;

public class MessengerContentAccessMain {

    private static final int SUPPLIER_SLEEP_MILLIS = 1000;
    
    private static final int[] SUPPLIER_ORDER = new int[] { 3, 2, 0, 6, 1, 5, 4, 8, 7 };
    
    private static final int SUPPLIER_CHUNK_SIZE = 7;
    
    private static final int CONSUMER_CHUNK_SIZE = 10;
    
    
    public static void main(String[] args) throws IOException, InterruptedException {
        long fullLength = ((long) SUPPLIER_CHUNK_SIZE) * SUPPLIER_ORDER.length;
        try (FileChargeableContentAccess contentAccess =
            new FileChargeableContentAccess(fullLength, Files.createTempFile("LOB_", ".bin").toFile())) {
            
            Thread supplierThread = new Thread(() -> {
                for (int n : SUPPLIER_ORDER) {
                    try {
                        Thread.sleep(SUPPLIER_SLEEP_MILLIS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                        return;
                    }
                    long offset = ((long) n) * SUPPLIER_CHUNK_SIZE;
                    char letter = (char) ('a' + n);
                    StringBuilder contentBuilder = new StringBuilder();
                    for (int i = 0; i < SUPPLIER_CHUNK_SIZE; i++) {
                        contentBuilder.append(letter);
                    }
                    String contentString = contentBuilder.toString();
                    System.out.println("Write: " + contentString);
                    ByteString content = ByteString.of(contentString);
                    try {
                        contentAccess.accept(offset, content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            
            supplierThread.start();

            try (InputStream in = contentAccess.inputStream()) {
                byte[] buffer = new byte[CONSUMER_CHUNK_SIZE];
                while (true) {
                    int r = in.read(buffer);
                    if (r == (-1)) {
                        break;
                    }
                    
                    String part = new String(buffer, 0, r);
                    System.out.println(" Fetched: " + part);
                }
            }
            
            supplierThread.join();
        }
    }
    
}
