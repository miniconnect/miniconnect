package hu.webarticum.miniconnect.repl;

public interface Repl {
    
    public void welcome();
    
    public boolean execute(String command);

    public void bye();
    
}
