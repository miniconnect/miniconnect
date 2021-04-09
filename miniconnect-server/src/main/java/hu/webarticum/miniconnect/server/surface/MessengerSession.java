package hu.webarticum.miniconnect.server.surface;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.server.message.request.Request;
import hu.webarticum.miniconnect.server.message.request.QueryRequest;
import hu.webarticum.miniconnect.server.message.response.Response;

public class MessengerSession implements MiniSession {

    private final AtomicInteger requestIdCounter = new AtomicInteger();

    private final RequestSender<Request, Response> requestSender;


    public MessengerSession(RequestSender<Request, Response> requestSender) {
        this.requestSender = requestSender;
    }


    @Override
    public MiniResult execute(String query) throws IOException {
        int requestId = requestIdCounter.incrementAndGet();
        Request request = new QueryRequest(requestId, query, 1000L);
        Predicate<Response> filter = null; // TODO
        CloseableSource<Response> responseSource = requestSender.send(request, filter);

        // TODO fetch from responseSource
        return null;
    }

    @Override
    public String putLargeData(InputStream dataSource) throws IOException {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

}
