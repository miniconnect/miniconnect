package hu.webarticum.miniconnect.messenger;

import java.util.function.Consumer;

import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;

@FunctionalInterface
public interface Messenger {

    /**
     * Accepts a request, and, optionally, start listening for responses.
     * 
     * <p>If <code>responseConsumer</code> is not null,
     * the caller must ensure that it's reachable as long as necessary
     * (the easiest way to do so is keeping a hard reference to it).
     * This will allow the <code>Messenger</code> implementation
     * to store its consumers as weak references.</p>
     * 
     * <p>If <code>responseConsumer</code> is {@link AutoCloseable}
     * or requires any finalization process,
     * these must be handled by the caller.</p>
     */
    public void accept(Request request, Consumer<Response> responseConsumer);

    /**
     * Accepts a request, doesn't listen for responses.
     */
    public default void accept(Request request) {
        accept(request, null);
    }
    
}
