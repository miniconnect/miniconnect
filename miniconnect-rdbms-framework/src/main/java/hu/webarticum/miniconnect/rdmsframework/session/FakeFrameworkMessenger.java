package hu.webarticum.miniconnect.rdmsframework.session;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.messenger.Messenger;
import hu.webarticum.miniconnect.messenger.message.request.QueryRequest;
import hu.webarticum.miniconnect.messenger.message.request.Request;
import hu.webarticum.miniconnect.messenger.message.response.Response;
import hu.webarticum.miniconnect.messenger.message.response.ResultResponse;
import hu.webarticum.miniconnect.rdmsframework.execution.ParsingSqlExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.SqlExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeQueryExecutor;
import hu.webarticum.miniconnect.rdmsframework.execution.fake.FakeSqlParser;
import hu.webarticum.miniconnect.tool.result.StoredError;
import hu.webarticum.miniconnect.tool.result.StoredResult;

public class FakeFrameworkMessenger implements Messenger {

    @Override
    public void accept(Request request, Consumer<Response> responseConsumer) {
        if (!(request instanceof QueryRequest)) {
            throw new IllegalArgumentException("oops");
        }

        QueryRequest queryRequest = (QueryRequest) request;
        long sessionId = queryRequest.sessionId();
        int exchangeId = queryRequest.exchangeId();
        String sql = queryRequest.query();
        SqlExecutor sqlExecutor =
                new ParsingSqlExecutor(new FakeSqlParser(), new FakeQueryExecutor());
        Future<Object> future = sqlExecutor.execute(sql); // TODO
        Exception exception = null;
        Object executionResult = null;
        try {
            executionResult = future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            exception = e;
        } catch (ExecutionException e) {
            exception = (Exception) e.getCause();
        } catch (Exception e) {
            exception = e;
        }
        MiniResult result;
        if (exception != null) {
            result = new StoredResult(new StoredError(1, "00001", exception.getMessage()));
        } else {
            result = new StoredResult(new StoredError(99, "00099", "Nincs hiba sajnos..."));
        }
        Response response = ResultResponse.of(result, sessionId, exchangeId);
        responseConsumer.accept(response);
    }

}
