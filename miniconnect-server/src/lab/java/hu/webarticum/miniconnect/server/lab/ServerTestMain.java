package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.protocol.block.Block;
import hu.webarticum.miniconnect.protocol.channel.in.SingleStreamBlockInputChannel;
import hu.webarticum.miniconnect.protocol.channel.out.BlockOutputChannel;
import hu.webarticum.miniconnect.protocol.channel.out.SingleStreamBlockOutputChannel;
import hu.webarticum.miniconnect.protocol.request.CloseRequest;
import hu.webarticum.miniconnect.protocol.request.ConnectRequest;
import hu.webarticum.miniconnect.protocol.request.PingRequest;
import hu.webarticum.miniconnect.protocol.request.Request;
import hu.webarticum.miniconnect.protocol.request.SqlRequest;
import hu.webarticum.miniconnect.server.Server;
import hu.webarticum.miniconnect.util.lab.dummy.DummyConnection;

public class ServerTestMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        PipedOutputStream out = new PipedOutputStream();
        InputStream in = new PipedInputStream(out);
        MiniConnection connection = new DummyConnection();
        
        Server server = new Server(
                connection,
                new SingleStreamBlockInputChannel(in),
                new SingleStreamBlockOutputChannel(System.out)); // NOSONAR
        new Thread(server).start();

        BlockOutputChannel clientOutputChannel = new SingleStreamBlockOutputChannel(out);
        send(new ConnectRequest(), clientOutputChannel);
        send(new SqlRequest("AAA"), clientOutputChannel);
        send(new SqlRequest("BBB"), clientOutputChannel);
        send(new PingRequest(), clientOutputChannel);
        send(new SqlRequest("CCC"), clientOutputChannel);
        send(new SqlRequest("Hello, Request!"), clientOutputChannel);
        send(new CloseRequest(), clientOutputChannel);
    }
    
    private static void send(Request request, BlockOutputChannel outputChannel)
            throws IOException, InterruptedException {
        
        outputChannel.send(new Block(request.encode()));
        Thread.sleep(500);
    }

}
