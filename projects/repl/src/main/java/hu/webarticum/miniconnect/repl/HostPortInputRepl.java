package hu.webarticum.miniconnect.repl;

import java.io.IOException;

public class HostPortInputRepl implements Repl {
    
    private enum Status {
        
        HOST, PORT, COMPLETED
        
    }
    

    private volatile Status status = Status.HOST;
    
    private volatile String host = "";
    
    private volatile int port = 0;
    
    
    public HostPortInputRepl(String defaultHost, int defaultPort) {
        this.host = defaultHost;
        this.port = defaultPort;
    }
    

    @Override
    public boolean isCommandComplete(String command) {
        return true;
    }

    @Override
    public void welcome(AnsiAppendable out) throws IOException {
        out.append("\nPlease enter the server address!\n\n");
    }

    @Override
    public void prompt(AnsiAppendable out) throws IOException {
        if (status == Status.HOST) {
            out.append("Host [" + host + "]: ");
        } else {
            out.append("Port [" + port + "]: ");
        }
    }

    @Override
    public void prompt2(AnsiAppendable out) throws IOException {
        // nothing to do
    }

    @Override
    public boolean execute(String command, AnsiAppendable out) throws IOException {
        String trimmedInput = command.trim();
        if (status == Status.HOST) {
            if (!trimmedInput.isEmpty()) {
                host = trimmedInput;
            }
            status = Status.PORT;
            return true;
        } else if (status == Status.PORT) {
            if (!trimmedInput.isEmpty()) {
                try {
                    port = Integer.parseInt(trimmedInput);
                } catch (NumberFormatException e) {
                    out.append("Invalid port number!\n");
                    return true;
                }
            }
            status = Status.COMPLETED;
            return false;
        } else {
            throw new IllegalStateException("This repl was already completed");
        }
    }

    @Override
    public void bye(AnsiAppendable out) throws IOException {
        // nothing to do
    }
    
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
