package hu.webarticum.miniconnect.repl;

import java.io.IOException;

public interface Repl {

    public boolean isCommandComplete(String command);

    public void welcome(AnsiAppendable out) throws IOException;

    public void prompt(AnsiAppendable out) throws IOException;

    public void prompt2(AnsiAppendable out) throws IOException;

    public boolean execute(String command, AnsiAppendable out) throws IOException;

    public void bye(AnsiAppendable out) throws IOException;

}
