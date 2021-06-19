package hu.webarticum.miniconnect.tool.repl;

import java.io.IOException;
import java.util.regex.Pattern;

// FIXME: more clean method names?
public interface Repl {

    public Pattern commandPattern();

    public void welcome() throws IOException;

    public void prompt() throws IOException;

    public void prompt2() throws IOException;

    public boolean execute(String command) throws IOException;

    public void bye() throws IOException;

}
