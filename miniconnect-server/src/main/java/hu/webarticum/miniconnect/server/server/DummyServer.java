package hu.webarticum.miniconnect.server.server;

import java.util.function.Consumer;

import hu.webarticum.miniconnect.server.message.request.Request;
import hu.webarticum.miniconnect.server.message.response.Response;

public class DummyServer implements Server<Request, Response> {

    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        
        // TODO
        
    }

}
