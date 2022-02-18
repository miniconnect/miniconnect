package hu.webarticum.miniconnect.record.lob;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import hu.webarticum.miniconnect.api.MiniContentAccess;

public class FixedCharWidthClobValue implements ClobValue {

    private final MiniContentAccess contentAccess;
    
    private final Charset charset;
    
    private final int charWidth;
    

    public FixedCharWidthClobValue(
            MiniContentAccess contentAccess, Charset charset, int charWidth) {
        if (charWidth <= 0) {
            throw new IllegalArgumentException("Invalid character width: " + charWidth);
        }
        
        this.contentAccess = contentAccess;
        this.charset = charset;
        this.charWidth = charWidth;
    }
    

    @Override
    public MiniContentAccess contentAccess() {
        return contentAccess;
    }

    @Override
    public Charset charset() {
        return charset;
    }

    @Override
    public long length() {
        return contentAccess.length() / charWidth;
    }

    @Override
    public String get(long start, int length) {
        return contentAccess.get(start * charWidth, length * charWidth).toString(charset);
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(contentAccess.inputStream(), charset);
    }

    @Override
    public Reader reader(long start, long length) {
        InputStream inputStream = contentAccess.inputStream(start * charWidth, length * charWidth);
        return new InputStreamReader(inputStream, charset);
    }

}
