package hu.webarticum.miniconnect.transfer.fetcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.transfer.fetcher.pocket.Pocket;


public class CollectingConsumer<T> implements Consumer<T> {
    
    private final List<Notifier> notifiers = new ArrayList<>();
    
    private final Consumer<T> fallbackConsumer;
    
    
    public CollectingConsumer() {
        this(anyItem -> {});
    }

    public CollectingConsumer(Consumer<T> fallbackConsumer) {
        this.fallbackConsumer = fallbackConsumer;
    }
    

    @Override
    public void accept(T item) {
        Notifier foundNotifier = null;
        synchronized (notifiers) {
            Iterator<Notifier> iterator = notifiers.iterator();
            while (iterator.hasNext()) {
                Notifier notifier = iterator.next();
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
    
    public CollectingNotifier<T> listen(Pocket<T> pocket) {
        Notifier notifier = new Notifier(pocket);
        synchronized (notifiers) {
            notifiers.add(notifier);
        }
        return notifier;
    }
    

    private class Notifier implements CollectingNotifier<T> {
    
        private final Pocket<T> pocket;
    
    
        private Notifier(Pocket<T> pocket) {
            this.pocket = pocket;
        }
    
        
        @Override
        public Pocket<T> pocket() {
            return pocket;
        }

        @Override
        public void remove() {
            synchronized (notifiers) {
                notifiers.remove(this);
            }
        }
    
    }
    
}
