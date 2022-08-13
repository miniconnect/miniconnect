package hu.webarticum.miniconnect.repl;

import java.io.IOException;

public class RichAnsiAppendable implements AnsiAppendable {
    
    private final Appendable baseOut;
    
    
    public RichAnsiAppendable(Appendable baseOut) {
        this.baseOut = baseOut;
    }
    
    
    @Override
    public AnsiAppendable append(CharSequence text) throws IOException {
        return appendAnsi(AnsiUtil.escapeText(text));
    }

    @Override
    public AnsiAppendable appendAnsi(CharSequence ansiText) throws IOException {
        baseOut.append(ansiText);
        return this;
    }

}
