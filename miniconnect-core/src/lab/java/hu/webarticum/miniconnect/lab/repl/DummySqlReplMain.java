package hu.webarticum.miniconnect.lab.repl;

import java.io.IOException;

import hu.webarticum.miniconnect.repl.SqlReplMain;

public class DummySqlReplMain {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        Class.forName("hu.webarticum.miniconnect.dummy.DummyDriver");
        SqlReplMain.main(new String[] { "dummy" });
    }
    
}
