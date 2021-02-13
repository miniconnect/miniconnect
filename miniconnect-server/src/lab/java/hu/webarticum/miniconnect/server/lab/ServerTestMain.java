package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.channel.BlockSource;
import hu.webarticum.miniconnect.protocol.channel.BlockTarget;
import hu.webarticum.miniconnect.protocol.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.protocol.channel.singlestream.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.protocol.message.CloseRequest;
import hu.webarticum.miniconnect.protocol.message.ConnectRequest;
import hu.webarticum.miniconnect.protocol.message.PingRequest;
import hu.webarticum.miniconnect.protocol.message.Request;
import hu.webarticum.miniconnect.protocol.message.SqlRequest;
import hu.webarticum.miniconnect.util.lab.dummy.DummySession;
import hu.webarticum.miniconnect.util.repl.Repl;
import hu.webarticum.miniconnect.util.repl.ReplRunner;
import hu.webarticum.miniconnect.util.repl.SqlRepl;

public class ServerTestMain {

    public static void main(String[] args) throws IOException {
        MiniSession session = new DummySession();
        
        PipedOutputStream clientOut = new PipedOutputStream();
        InputStream serverIn = new PipedInputStream(clientOut);

        PipedOutputStream serverOut = new PipedOutputStream();
        InputStream clientIn = new PipedInputStream(serverOut);

        BlockSource clientBlockSource = new SingleStreamBlockSource(clientIn);
        BlockTarget clientBlockTarget = new SingleStreamBlockTarget(clientOut);
        Client client = new Client(clientBlockSource, clientBlockTarget);

        BlockSource serverBlockSource = new SingleStreamBlockSource(serverIn);
        BlockTarget serverBlockTarget = new SingleStreamBlockTarget(serverOut);
        Server server = new Server(session, serverBlockSource, serverBlockTarget);
        new Thread(server).start();
        
        ClientSession clientSession = client.openSession();
        Repl repl = new SqlRepl(clientSession, System.out, System.err);
        
        ReplRunner replRunner = new ReplRunner(repl, System.in);
        replRunner.run();
    }
    
    public static void main_XXX(String[] args) throws IOException {
        PipedOutputStream out = new PipedOutputStream();
        InputStream in = new PipedInputStream(out);
        MiniSession session = new DummySession();
        
        Server server = new Server(
                session,
                new SingleStreamBlockSource(in),
                new SingleStreamBlockTarget(new PrintableOutputStream(
                        System.out, 16))); // NOSONAR
        new Thread(server).start();

        BlockTarget target = new SingleStreamBlockTarget(out);
        send(new ConnectRequest(), target);
        send(new SqlRequest(1, 1, "AAA"), target);
        send(new SqlRequest(1, 2, "BBB"), target);
        send(new PingRequest(1), target);
        send(new SqlRequest(1, 3, "CCC"), target);
        send(new SqlRequest(1, 4, "Hello, Request!"), target);
        send(new SqlRequest(1, 5, "xxx\\yyy"), target);
        send(new CloseRequest(1), target);
    }
    
    private static void send(Request request, BlockTarget target)
            throws IOException {
        
        target.send(new Block(request.encode()));
    }

}
