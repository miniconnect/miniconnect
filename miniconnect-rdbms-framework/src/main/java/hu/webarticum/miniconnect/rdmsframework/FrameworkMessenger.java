package hu.webarticum.miniconnect.rdmsframework;

import java.util.function.Consumer;

import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;

public class FrameworkMessenger implements Messenger {

    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        
        // TODO
        
    }

}
