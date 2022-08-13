package hu.webarticum.miniconnect.repl;

import java.io.IOException;

public interface AnsiAppendable extends Appendable {

    public AnsiAppendable append(CharSequence text) throws IOException;
    
    public AnsiAppendable appendAnsi(CharSequence ansiText) throws IOException;

    @Override
    public default AnsiAppendable append(CharSequence text, int start, int end) throws IOException {
        return append(text.subSequence(start, end));
    }

    @Override
    public default AnsiAppendable append(char c) throws IOException {
        return append(Character.toString(c));
    }

}
