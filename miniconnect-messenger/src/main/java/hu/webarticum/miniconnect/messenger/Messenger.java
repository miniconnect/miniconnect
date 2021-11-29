package hu.webarticum.miniconnect.messenger;

import java.util.function.Consumer;

import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;

@FunctionalInterface
public interface Messenger {

    public void accept(Request request, Consumer<Response> responseConsumer);

    public default void accept(Request request) {
        accept(request, null);
    }
    
}
