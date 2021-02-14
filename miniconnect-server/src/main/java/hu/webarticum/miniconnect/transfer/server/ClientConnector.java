package hu.webarticum.miniconnect.transfer.server;

import java.io.IOException;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.protocol.message.Request;
import hu.webarticum.miniconnect.protocol.message.ResultResponse;
import hu.webarticum.miniconnect.protocol.message.SqlRequest;
import hu.webarticum.miniconnect.transfer.Block;
import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.util.result.StoredResult;

// TODO: make it attachable to Server instance
//   optionally, Server instance can listen to some events, and close attached sessions
//     question: how does Server know which session belongs to which connector?
public class ClientConnector implements Runnable {
    
    // FIXME: when to instantiate?
    private final MiniSession session;
    
    private final BlockSource source;
    
    private final BlockTarget target;
    

    public ClientConnector(
            MiniSession session,
            BlockSource source,
            BlockTarget target) {
        
        this.session = session;
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
        } catch (InterruptedException e) {
            
            // XXX
            Thread.currentThread().interrupt();
            return false;
            
        } catch (Exception e) {
            
            // XXX
            return false;
            
        }
        return true;
    }
    
    private void iterateThrowing() throws IOException, InterruptedException {
        
        // XXX
        
        Block block = source.fetch();
        Request request = Request.decode(block.content());
        System.out.println(request.getClass().getName());
        
        if (request instanceof SqlRequest) {
            SqlRequest sqlRequest = (SqlRequest) request;
            int sessionId = sqlRequest.sessionId();
            int queryId = sqlRequest.queryId();
            MiniResult result = session.execute(sqlRequest.sql());
            StoredResult storedResult = StoredResult.of(result);
            ResultResponse resultReponse = new ResultResponse(sessionId, queryId, storedResult);
            
            target.send(new Block(resultReponse.encode()));
        }
    }
    
}
