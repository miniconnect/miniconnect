package hu.webarticum.miniconnect.tool.repl;

import java.util.regex.Pattern;

public interface Repl {
    
    public Pattern commandPattern();
    
    public void welcome();
    
    public void prompt();
    
    public void prompt2();
    
    public boolean execute(String command);

    public void bye();
    
}
