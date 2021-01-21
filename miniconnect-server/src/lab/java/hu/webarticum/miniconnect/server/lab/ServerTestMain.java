package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.io.source.SingleStreamBlockSource;
import hu.webarticum.miniconnect.protocol.io.target.BlockTarget;
import hu.webarticum.miniconnect.protocol.io.target.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.protocol.message.CloseRequest;
import hu.webarticum.miniconnect.protocol.message.ConnectRequest;
import hu.webarticum.miniconnect.protocol.message.PingRequest;
import hu.webarticum.miniconnect.protocol.message.Request;
import hu.webarticum.miniconnect.protocol.message.SqlRequest;
import hu.webarticum.miniconnect.server.Server;
import hu.webarticum.miniconnect.util.lab.dummy.DummyConnection;

public class ServerTestMain {

    public static void main(String[] args) throws IOException {
        PipedOutputStream out = new PipedOutputStream();
        InputStream in = new PipedInputStream(out);
        MiniConnection connection = new DummyConnection();
        
        Server server = new Server(
                connection,
                new SingleStreamBlockSource(in),
                new SingleStreamBlockTarget(new PrintableOutputStream(
                        System.out, 16))); // NOSONAR
        new Thread(server).start();

        BlockTarget target = new SingleStreamBlockTarget(out);
        send(new ConnectRequest(), target);
        send(new SqlRequest(1, "AAA"), target);
        send(new SqlRequest(1, "BBB"), target);
        send(new PingRequest(1), target);
        send(new SqlRequest(1, "CCC"), target);
        send(new SqlRequest(1, "Hello, Request!"), target);
        send(new SqlRequest(1, "xxx\\yyy"), target);
        send(new CloseRequest(1), target);
    }
    
    private static void send(Request request, BlockTarget target)
            throws IOException {
        
        target.send(new Block(request.encode()));
    }

}
