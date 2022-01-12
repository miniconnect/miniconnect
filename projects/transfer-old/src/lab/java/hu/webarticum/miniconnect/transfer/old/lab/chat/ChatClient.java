package hu.webarticum.miniconnect.transfer.old.lab.chat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;

import hu.webarticum.miniconnect.transfer.old.channel.BlockSource;
import hu.webarticum.miniconnect.transfer.old.channel.BlockTarget;
import hu.webarticum.miniconnect.transfer.old.fetcher.BlockSourceFetcher;
import hu.webarticum.miniconnect.transfer.old.fetcher.DecodingBlockConsumer;

public class ChatClient implements Closeable {

    private static final String SELF_PROMPT = "you > ";
    
    private static final String FORMAT = "         %s: %s%n";
    
    
    private final BufferedReader in;
    
    private final Appendable out;
    
    private final String senderName;
    
    private final BlockSourceFetcher fetcher;
    
    private final BlockTarget target;
    
    
    private ChatClient(
            BufferedReader in,
            Appendable out,
            String senderName,
            BlockSource source,
            BlockTarget target) {
        this.in = in;
        this.out = out;
        this.senderName = senderName;
        this.target = target;
        this.fetcher = BlockSourceFetcher.start(
                source,
                new DecodingBlockConsumer<>(ChatMessage::decode, this::accept));
    }
    
    public static ChatClient start(
            BufferedReader in,
            Appendable out,
            String senderName,
            BlockSource source,
            BlockTarget target) {
        return new ChatClient(in, out, senderName, source, target);
    }
    
    
    public void loop() throws IOException {
        out.append(SELF_PROMPT);
        while (true) { // NOSONAR
            String message = in.readLine();
            send(message);
            out.append(String.format(FORMAT, senderName, message));
            out.append(SELF_PROMPT);
        }
    }
    
    public void accept(ChatMessage chatMessage) {
        try {
            out.append('\n');
            out.append(String.format(
                    FORMAT,
                    chatMessage.senderName(),
                    chatMessage.message()));
            out.append(SELF_PROMPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void send(String message) throws IOException {
        ChatMessage chatMessage = new ChatMessage(senderName, message);
        target.send(chatMessage.encode());
    }

    @Override
    public void close() throws IOException {
        fetcher.close();
    }

}
