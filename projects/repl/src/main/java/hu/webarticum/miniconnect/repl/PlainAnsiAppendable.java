package hu.webarticum.miniconnect.repl;

import java.io.IOException;

public class PlainAnsiAppendable implements AnsiAppendable {
    
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
        return append(AnsiUtil.cleanAnsiText(ansiText));
    }
    
}
