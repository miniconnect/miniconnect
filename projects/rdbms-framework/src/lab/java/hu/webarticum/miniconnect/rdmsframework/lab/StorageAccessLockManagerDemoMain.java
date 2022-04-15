package hu.webarticum.miniconnect.rdmsframework.lab;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import hu.webarticum.miniconnect.rdmsframework.CheckableCloseable;
import hu.webarticum.miniconnect.rdmsframework.storage.StorageAccessLockManager;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.SimpleStorageAccessLockManager;

public class StorageAccessLockManagerDemoMain {

    public static void main(String[] args) throws Exception {
        StorageAccessLockManager lockManager = new SimpleStorageAccessLockManager();
        int j = 0;
        for (int i = 0; i < 30; i++) {
            int sharedIndex = i;
            Thread.sleep(500);
            new Thread(() -> {
                System.out.println(now() + "  + shared[" + sharedIndex + "]");
                try (CheckableCloseable lock = lockManager.lockShared()) {
                    System.out.println(now() + "  > shared[" + sharedIndex + "]");
                    Thread.sleep(3000);
                    System.out.println(now() + "  < shared[" + sharedIndex + "]");
                } catch (InterruptedException e) {
                    System.out.println(now() + "  - shared[" + sharedIndex + "]");
                }
            }).start();
            if (i == 3 || i == 10 || i == 11 || i == 12 || i == 13 || i == 25) {
                int exclusiveIndex = j;
                Thread.sleep(200);
                new Thread(() -> {
                    System.out.println(now() + "  + exclusive[" + exclusiveIndex + "]");
                    try (CheckableCloseable lock = lockManager.lockExclusively()) {
                        System.out.println(now() + "  > exclusive[" + exclusiveIndex + "]");
                        Thread.sleep(4000);
                        System.out.println(now() + "  < exclusive[" + exclusiveIndex + "]");
                    } catch (InterruptedException e) {
                        System.out.println(now() + "  - exclusive[" + exclusiveIndex + "]");
                    }
                }).start();
                j++;
            }
        }
    }
    
    private static String now() {
        return LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }
    
}
