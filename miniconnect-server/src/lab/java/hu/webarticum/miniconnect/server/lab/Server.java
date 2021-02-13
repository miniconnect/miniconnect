package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.channel.BlockSource;
import hu.webarticum.miniconnect.protocol.channel.BlockTarget;
import hu.webarticum.miniconnect.protocol.message.Request;
import hu.webarticum.miniconnect.protocol.message.ResultResponse;
import hu.webarticum.miniconnect.protocol.message.SqlRequest;
import hu.webarticum.miniconnect.util.result.StoredResult;

public class Server implements Runnable {
    
    // FIXME: connectionFactory? (each session is mapped to a MiniConnection / MiniSession)
    private final MiniConnection connection;
    
    private final BlockSource source;
    
    private final BlockTarget target;
    

    public Server(
            MiniConnection connection,
            BlockSource source,
            BlockTarget target) {
        
        this.connection = connection;
        this.source = source;
        this.target = target;
    }


    @Override
    public void run() {
        while (iterate());
    }
    
    private boolean iterate() {
        try {
            iterateThrowing();
        } catch (IOException e) {
            // XXX
            return false;
        }
        return true;
    }
    
    private void iterateThrowing() throws IOException {
        
        // XXX
        
        Block block = source.fetch();
        Request request = Request.decode(block.content());
        System.out.println(request.getClass().getName());
        
        if (request instanceof SqlRequest) {
            SqlRequest sqlRequest = (SqlRequest) request;
            int sessionId = sqlRequest.sessionId();
            int queryId = sqlRequest.queryId();
            MiniResult result = connection.execute(sqlRequest.sql());
            StoredResult storedResult = StoredResult.of(result);
            ResultResponse resultReponse = new ResultResponse(sessionId, queryId, storedResult);
            
            target.send(new Block(resultReponse.encode()));
        }
    }
    
}
