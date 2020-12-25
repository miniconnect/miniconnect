package hu.webarticum.miniconnect.repl;

public interface Repl {
    
    public void welcome();
    
    public void prompt();
    
    public void prompt2();
    
    public boolean execute(String command);

    public void bye();
    
}
