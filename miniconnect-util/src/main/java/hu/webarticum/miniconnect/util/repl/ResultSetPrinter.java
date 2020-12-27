package hu.webarticum.miniconnect.util.repl;

import java.io.PrintStream;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;

public class ResultSetPrinter {
    
    private static final String NULL_PLACEHOLDER = "[NULL]";
    

    public void print(MiniResultSet resultSet, PrintStream out) {


        // TODO
        

        out.println();

        out.println("---------------------");
        
        out.println(resultSet.columnNames());

        out.println("---------------------");
        
        for (List<MiniValue> row : resultSet) {
            for (MiniValue value : row) {
                out.print(stringifyValue(value));
                out.print(", ");
            }
            out.println();
        }
        
        out.println();
        
    }
    
    public String stringifyValue(MiniValue value) {
        if (!value.isNull()) {
            return value.asString();
        } else {
            return NULL_PLACEHOLDER;
        }
    }
    
}
