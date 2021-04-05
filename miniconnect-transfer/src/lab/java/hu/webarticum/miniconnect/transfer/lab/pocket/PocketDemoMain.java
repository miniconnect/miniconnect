package hu.webarticum.miniconnect.transfer.lab.pocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.transfer.channel.singlestream.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.transfer.fetcher.BlockSourceFetcher;
import hu.webarticum.miniconnect.transfer.fetcher.CollectingConsumer;
import hu.webarticum.miniconnect.transfer.fetcher.CollectingNotifier;
import hu.webarticum.miniconnect.transfer.fetcher.DecodingBlockConsumer;
import hu.webarticum.miniconnect.transfer.fetcher.pocket.Pocket;
import hu.webarticum.miniconnect.transfer.fetcher.pocket.SingleItemPocket;
import hu.webarticum.miniconnect.transfer.lab.util.BlockUtil;

public class PocketDemoMain {

    public static void main(String[] args) throws Exception {
        PipedOutputStream out = new PipedOutputStream();
        InputStream in = new PipedInputStream(out);

        BlockTarget target = new SingleStreamBlockTarget(out);
        BlockSource source = new SingleStreamBlockSource(in);
        
        CollectingConsumer<String> collectingConsumer = new CollectingConsumer<>(
                item -> System.out.println(String.format("FALLBACK: %s", item)));
        
        try (BlockSourceFetcher fetcher = createFetcher(source, collectingConsumer)) {
            SingleItemPocket<String> bbbPocket = new SingleItemPocket<>(item -> item.equals("bbb"));
            CollectingNotifier<String, String> bbbNotifier = collectingConsumer.listen(bbbPocket);
            
            FirstNFilteredPocket firstTwoNumbersPocket =
                    new FirstNFilteredPocket(2, item -> item.matches("\\d+"));
            CollectingNotifier<String, List<String>> firstTwoNumbersNotifier =
                    collectingConsumer.listen(firstTwoNumbersPocket);
            
            Thread thread = sendMessagesOnOtherThread(target);
            
            String bbb = bbbNotifier.await();
            System.out.println(String.format("FOUND BBB: %s", bbb));

            List<String> firstTwoNumbers = firstTwoNumbersNotifier.await();
            System.out.println(String.format("FOUND NUMBERS: %s", firstTwoNumbers));
            
            thread.join();
        }
    }
    
    private static Thread sendMessagesOnOtherThread(BlockTarget target) {
        Thread thread = new Thread(() -> sendMessages(target));
        thread.start();
        return thread;
    }

    private static void sendMessages(BlockTarget target) {
        try {
            target.send(BlockUtil.dataBlockOf("111"));
            target.send(BlockUtil.dataBlockOf("aaa"));
            target.send(BlockUtil.dataBlockOf("222"));
            target.send(BlockUtil.dataBlockOf("bbb"));
            target.send(BlockUtil.dataBlockOf("333"));
            target.send(BlockUtil.dataBlockOf("ccc"));
            target.send(BlockUtil.dataBlockOf("444"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static BlockSourceFetcher createFetcher(
            BlockSource source, CollectingConsumer<String> collectingConsumer) {

        return BlockSourceFetcher.start(
                source,
                new DecodingBlockConsumer<>(
                        BlockUtil::stringOf,
                        collectingConsumer));
    }

    
    private static class FirstNFilteredPocket implements Pocket<String, List<String>> {
        
        private final int n;
        
        private final Predicate<String> filter;
        
        private final List<String> items = new ArrayList<>();
        
        
        private FirstNFilteredPocket(int n, Predicate<String> filter) {
            this.n = n;
            this.filter = filter;
        }


        @Override
        public AcceptStatus accept(String item) {
            int size;
            synchronized (this) {
                size = items.size();
            }
            
            if (size >= n || !filter.test(item)) {
                return Pocket.AcceptStatus.DECLINED;
            }
            
            synchronized (this) {
                items.add(item);
            }
            
            return size == n - 1 ?
                    Pocket.AcceptStatus.COMPLETED :
                    Pocket.AcceptStatus.ACCEPTED;
        }

        @Override
        public boolean hasMore() {
            return isCompleted();
        }

        @Override
        public synchronized List<String> get() {
            return new ArrayList<>(items);
        }
        
        public synchronized boolean isCompleted() {
            return (items.size() == n);
        }

    }

}
