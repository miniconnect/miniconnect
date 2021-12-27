package hu.webarticum.miniconnect.jdbc.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Similar to BoundedReader from apache-commons-io, but supports long limit
 */
public class LongBoundedReader extends Reader {

    private final Reader target;

    private final long limit;

    private int position = 0;

    
    public LongBoundedReader(Reader target, long limit) {
        this.target = target;
        this.limit = limit;
    }

    @Override
    public void close() throws IOException {
        target.close();
    }

    @Override
    public void reset() throws IOException {
        target.reset();
        position = 0;
    }

    @Override
    public void mark(int readAheadLimit) throws IOException {
        throw new IOException("Mark is not supported");
    }

    @Override
    public int read() throws IOException {
        if (position >= limit) {
            return -1;
        }

        position++;
        return target.read();
    }

    @Override
    public int read(char[] buffer, int offset, int length) throws IOException {
        int c;
        for (int i = 0; i < length; i++) {
            c = read();
            if (c == -1) {
                return i == 0 ? -1 : i;
            }
            buffer[offset + i] = (char) c;
        }
        return length;
    }
}
