package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.protocol.Block;
import hu.webarticum.miniconnect.protocol.ByteString;
import hu.webarticum.miniconnect.server.Server;
import hu.webarticum.miniconnect.util.lab.dummy.DummyConnection;

public class ServerTestMain {

    public static void main(String[] args) throws IOException {
        PipedOutputStream out = new PipedOutputStream();
        InputStream in = new PipedInputStream(out);
        MiniConnection connection = new DummyConnection();
        
        Server server = new Server(connection, in, new HexOutputStream(System.out));
        new Thread(server).start();
        
        String content = "Hello, Block!";
        
        new Block(new ByteString(content.getBytes(StandardCharsets.UTF_8))).writeTo(out);
    }

}
