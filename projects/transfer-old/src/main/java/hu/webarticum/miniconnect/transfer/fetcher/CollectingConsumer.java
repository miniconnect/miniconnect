package hu.webarticum.miniconnect.transfer.fetcher;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.transfer.fetcher.pocket.Pocket;
import hu.webarticum.miniconnect.transfer.util.ExceptionUtil;


public class CollectingConsumer<T> implements Consumer<T> {
    
    private final List<Notifier<?>> notifiers = new ArrayList<>();
    
    private final Consumer<T> fallbackConsumer;
    
    
    public CollectingConsumer() {
        this(anyItem -> {});
    }

    public CollectingConsumer(Consumer<T> fallbackConsumer) {
        this.fallbackConsumer = fallbackConsumer;
    }
    

    @Override
    public void accept(T item) {
        Notifier<?> foundNotifier = null;
        synchronized (notifiers) {
            Iterator<Notifier<?>> iterator = notifiers.iterator();
            while (iterator.hasNext()) {
                Notifier<?> notifier = iterator.next();
                Pocket.AcceptStatus acceptStatus = notifier.pocket.accept(item);
                if (acceptStatus == Pocket.AcceptStatus.COMPLETED) {
                    iterator.remove();
                }
                if (acceptStatus != Pocket.AcceptStatus.DECLINED) {
                    foundNotifier = notifier;
                    break;
                }
            }
        }
        
        if (foundNotifier != null) {
            synchronized (foundNotifier) {
                foundNotifier.notifyAll();
            }
            return;
        }
        
        fallbackConsumer.accept(item);
    }
    
    public <U> CollectingNotifier<T, U> listen(Pocket<T, U> pocket) {
        Notifier<U> notifier = new Notifier<>(pocket);
        synchronized (notifiers) {
            notifiers.add(notifier);
        }
        return notifier;
    }
    

    private class Notifier<U> implements CollectingNotifier<T, U> {
    
        private final Pocket<T, U> pocket;
    
    
        private Notifier(Pocket<T, U> pocket) {
            this.pocket = pocket;
        }
    
        
        @Override
        public Pocket<T, U> pocket() {
            return pocket;
        }

        @Override
        public synchronized U await() throws InterruptedIOException {
            while (!pocket.hasMore()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw ExceptionUtil.combine(new InterruptedIOException(), e);
                }
            }
            return pocket.get();
        }

        @Override
        public void remove() {
            synchronized (notifiers) {
                notifiers.remove(this);
            }
        }
    
    }
    
}
