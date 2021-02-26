package hu.webarticum.miniconnect.transfer.lab.clientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import hu.webarticum.miniconnect.transfer.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.channel.singlestream.SingleStreamBlockSource;
import hu.webarticum.miniconnect.transfer.channel.singlestream.SingleStreamBlockTarget;
import hu.webarticum.miniconnect.transfer.xxx.server.ClientConnector;

public class ClientServerMain {

    public static void main(String[] args) throws IOException {
        PipedOutputStream innerRequestOut = new PipedOutputStream();
        InputStream innerRequestIn = new PipedInputStream(innerRequestOut);

        BlockTarget innerRequestBlockTarget = new SingleStreamBlockTarget(innerRequestOut);
        BlockSource innerRequestBlockSource = new SingleStreamBlockSource(innerRequestIn);

        PipedOutputStream innerResponseOut = new PipedOutputStream();
        InputStream innerResponseIn = new PipedInputStream(innerResponseOut);

        BlockTarget innerResponseBlockTarget = new SingleStreamBlockTarget(innerResponseOut);
        BlockSource innerResponseBlockSource = new SingleStreamBlockSource(innerResponseIn);

        try (DemoClient client = new DemoClient(innerResponseBlockSource, innerRequestBlockTarget)) {
            DemoServer server = new DemoServer(String::toUpperCase);
            try (ClientConnector connector = new ClientConnector(
                    server, innerRequestBlockSource, innerResponseBlockTarget)) {

                runQuery(client, "Lorem");
                runQuery(client, "ipsum");
                runQuery(client, "XXX yyy");
                runQuery(client, "Aaa Bbb Ccc");
            }
        }
    }
    
    public static void runQuery(DemoClient client, String query) throws IOException {
        System.out.print(String.format("%s --> ", query));
        
        String result = client.query(query);
        
        System.out.println(result);
    }

}
