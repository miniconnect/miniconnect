package hu.webarticum.miniconnect.repl;

import java.util.concurrent.Callable;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniSessionManager;
import hu.webarticum.miniconnect.messenger.adapter.MessengerSessionManager;
import hu.webarticum.miniconnect.server.ClientMessenger;
import hu.webarticum.miniconnect.server.ServerConstants;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "mini-repl")
public class ClientMain implements Callable<Integer> {

    private static final String DEFAULT_HOST = "localhost";

    private static final int DEFAULT_PORT = ServerConstants.DEFAULT_PORT;

   
    @Parameters(
            index = "0",
            description = "Server address",
            defaultValue = "")
    public String serverAddressArg;

    @Option(
            names = { "-i", "--interactive-input" },
            arity = "0..1",
            description = "Get server host and port interactively",
            defaultValue = "false")
    public boolean interactiveInputArg;
    

    public static void main(String[] args) {
        int exitCode = new CommandLine(ClientMain.class).execute(args);
        System.exit(exitCode);
    }
    
    @Override
    public Integer call() throws Exception {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        boolean runHostPortInputRepl = interactiveInputArg;
        if (serverAddressArg != null && !serverAddressArg.isEmpty()) {
            int colonPos = serverAddressArg.indexOf(':');
            if (colonPos != -1) {
                host = serverAddressArg.substring(0, colonPos);
                try {
                    port = Integer.parseInt(serverAddressArg.substring(colonPos + 1));
                } catch (NumberFormatException e) {
                    runHostPortInputRepl = true;
                }
            } else {
                host = serverAddressArg;
            }
        }
        ReplRunner replRunner = createReplRunner();
        if (runHostPortInputRepl) {
            HostPortInputRepl hostPortInputRepl = new HostPortInputRepl(host, port);
            replRunner.run(hostPortInputRepl);
            host = hostPortInputRepl.getHost();
            port = hostPortInputRepl.getPort();
        }
        try (ClientMessenger clientMessenger = new ClientMessenger(host, port)) {
            MiniSessionManager sessionManager = new MessengerSessionManager(clientMessenger);
            try (MiniSession session = sessionManager.openSession()) {
                String titleMessage = SqlRepl.DEFAULT_TITLE_MESSAGE + " - " + host + ":" + port;
                Repl repl = new SqlRepl(session, titleMessage);
                replRunner.run(repl);
            }
        }
        return 0;
    }
    
    private static ReplRunner createReplRunner() {
        if (System.console() != null) {
            return new RichReplRunner();
        }
        
        return new PlainReplRunner(System.in, System.out); // NOSONAR System.out is necessary
    }

}
