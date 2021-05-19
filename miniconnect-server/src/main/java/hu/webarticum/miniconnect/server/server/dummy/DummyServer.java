package hu.webarticum.miniconnect.server.server.dummy;

import java.util.function.Consumer;

import hu.webarticum.miniconnect.server.message.request.QueryRequest;
import hu.webarticum.miniconnect.server.message.request.Request;
import hu.webarticum.miniconnect.server.message.response.Response;
import hu.webarticum.miniconnect.server.message.response.ResultResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse;
import hu.webarticum.miniconnect.server.message.response.ResultSetRowsResponse.CellData;
import hu.webarticum.miniconnect.server.server.Server;
import hu.webarticum.miniconnect.util.data.ImmutableList;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public class DummyServer implements Server<Request, Response> {

    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        if (request instanceof QueryRequest) {
            acceptQueryRequest((QueryRequest) request, responseConsumer);
        }
    }

    // TODO
    private void acceptQueryRequest(QueryRequest request, Consumer<Response> responseConsumer) {
        long sessionId = request.sessionId();
        int queryId = request.id();
        
        ResultResponse resultResponse = new ResultResponse(
                sessionId, queryId, true, "", "", ImmutableList.empty(), true, ImmutableList.empty());
        responseConsumer.accept(resultResponse);
        
        ImmutableList<ImmutableList<CellData>> rows = new ImmutableList<>();
        
        ResultSetRowsResponse resultSetRowsResponse = new ResultSetRowsResponse(
                sessionId, queryId, 0, ImmutableList.empty(), ImmutableMap.empty(), rows);
        responseConsumer.accept(resultSetRowsResponse);
    }

}
