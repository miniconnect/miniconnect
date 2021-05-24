package hu.webarticum.miniconnect.server;

import java.util.function.Consumer;

import hu.webarticum.miniconnect.server.message.request.Request;
import hu.webarticum.miniconnect.server.message.response.Response;

public interface Server {

    public void accept(Request request, Consumer<Response> responseConsumer);

    public default void accept(Request request) {
        accept(request, null);
    }
    
}
