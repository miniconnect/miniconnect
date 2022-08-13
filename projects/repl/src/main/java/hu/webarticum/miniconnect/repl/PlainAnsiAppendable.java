package hu.webarticum.miniconnect.repl;

import java.io.IOException;
import java.util.regex.Pattern;

public class PlainAnsiAppendable implements AnsiAppendable {
    
    private static final Pattern ANSI_ESCAPE_PATTERN = Pattern.compile("\\e\\[[0-9;]*m");
    
    
    private final Appendable baseOut;
    
    
    public PlainAnsiAppendable(Appendable baseOut) {
        this.baseOut = baseOut;
    }
    

    @Override
    public PlainAnsiAppendable append(CharSequence text) throws IOException {
        baseOut.append(text);
        return this;
    }

    @Override
    public PlainAnsiAppendable appendAnsi(CharSequence ansiText) throws IOException {
        return append(cleanText(ansiText));
    }
    
    private CharSequence cleanText(CharSequence ansiText) {
        return ANSI_ESCAPE_PATTERN.matcher(ansiText).replaceAll("");
    }

}
