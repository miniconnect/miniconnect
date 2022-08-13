package hu.webarticum.miniconnect.repl;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSessionManager;
import hu.webarticum.miniconnect.server.ClientMessenger;
import hu.webarticum.miniconnect.server.ServerConstants;

public class ClientMain {

    private static final String DEFAULT_HOST = "localhost";

    private static final int DEFAULT_PORT = ServerConstants.DEFAULT_PORT;
    

    public static void main(String[] args) {
        String defaultHost = argOrDefault(args, 0, DEFAULT_HOST);
        int defaultPort = Integer.parseInt(argOrDefault(args, 1, Integer.toString(DEFAULT_PORT)));
        ReplRunner replRunner = createReplRunner();
        HostPortInputRepl hostPortInputRepl = new HostPortInputRepl(defaultHost, defaultPort);
        replRunner.run(hostPortInputRepl);
        String host = hostPortInputRepl.getHost();
        int port = hostPortInputRepl.getPort();
        try (ClientMessenger clientMessenger = new ClientMessenger(host, port)) {
            MiniSessionManager sessionManager = new MessengerSessionManager(clientMessenger);
            try (MiniSession session = sessionManager.openSession()) {
                Repl repl = new SqlRepl(session);
                replRunner.run(repl);
            }
        }
    }
    
    private static String argOrDefault(String[] args, int argIndex, String defaultValue) {
        if (args.length > argIndex) {
            return args[argIndex];
        }
        return defaultValue;
    }

    private static ReplRunner createReplRunner() {
        if (System.console() != null) {
            return new RichReplRunner();
        }
        
        return new PlainReplRunner(System.in, System.out); // NOSONAR System.out is necessary
    }

}
