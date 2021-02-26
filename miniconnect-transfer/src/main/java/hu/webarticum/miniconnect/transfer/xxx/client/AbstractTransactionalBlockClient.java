package hu.webarticum.miniconnect.transfer.xxx.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;

public abstract class AbstractTransactionalBlockClient<Q, R>
        extends AbstractTypedBlockClient<Q, R> {
    
    private final List<ResponseNotifier<R>> responseNotifiers = new ArrayList<>();
    

    protected AbstractTransactionalBlockClient(BlockSource source, BlockTarget target) {
        super(source, target);
    }
    

    // TODO: timeout etc.
    @Override
    protected void acceptResponseInternal(R response) {
        ResponseNotifier<R> foundNotifier = null;
        synchronized (responseNotifiers) {
            Iterator<ResponseNotifier<R>> iterator = responseNotifiers.iterator();
            while (iterator.hasNext()) {
                ResponseNotifier<R> notifier = iterator.next();
                if (notifier.acceptPredicate.test(response)) {
                    iterator.remove();
                    foundNotifier = notifier;
                    break;
                }
            }
        }
        
        if (foundNotifier == null) {
            acceptStandaloneResponseInternal(response);
            return;
        }
        
        synchronized (foundNotifier) {
            foundNotifier.response = response;
            foundNotifier.notifyAll();
        }
    }
    
    protected R sendAndWaitForResponseInternal(Q request, Predicate<R> acceptPredicate) throws IOException {
        ResponseNotifier<R> notifier = new ResponseNotifier<>(acceptPredicate);
        
        synchronized (responseNotifiers) {
            responseNotifiers.add(notifier);
        }
       
        Block requestBlock = encodeRequestInternal(request);
        sendBlockInternal(requestBlock);
        
        synchronized (notifier) {
            while (notifier.response == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    
                    // XXX
                    Thread.currentThread().interrupt();
                    
                }
            }
        }

        return notifier.response;
    }
    
    protected abstract void acceptStandaloneResponseInternal(R response);
    

    private static class ResponseNotifier<T> {

        volatile T response = null;
        
        final Predicate<T> acceptPredicate;
        
        
        ResponseNotifier(Predicate<T> acceptPredicate) {
            this.acceptPredicate = acceptPredicate;
        }

    }
    
}
