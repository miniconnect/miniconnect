package hu.webarticum.miniconnect.repl;

import java.io.IOException;
import java.util.regex.Pattern;

public class RichAnsiAppendable implements AnsiAppendable {
    
    private static final Pattern CONTROL_CHAR_PATTERN = Pattern.compile("[\\x{0000}-\\x{0008}\\x{000E}-\\x{001F}]");
    

    private final Appendable baseOut;
    
    
    public RichAnsiAppendable(Appendable baseOut) {
        this.baseOut = baseOut;
    }
    
    
    @Override
    public AnsiAppendable append(CharSequence text) throws IOException {
        return appendAnsi(escapeText(text));
    }

    @Override
    public AnsiAppendable appendAnsi(CharSequence ansiText) throws IOException {
        baseOut.append(ansiText);
        return this;
    }

    private CharSequence escapeText(CharSequence text) {
        return CONTROL_CHAR_PATTERN.matcher(text).replaceAll("");
    }

}
