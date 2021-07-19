package hu.webarticum.miniconnect.jdbc.blob;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;

public class BlobClob implements NClob {

    private final Blob blob;
    
    private final Charset blobCharset;
    
    private final int blobCharWidth;
    
    private final Charset targetCharset;
    

    public BlobClob(
            Blob blob,
            Charset blobCharset,
            int blobCharWidth,
            Charset targetCharset) {
        this.blob = blob;
        this.blobCharset = blobCharset;
        this.blobCharWidth = blobCharWidth;
        this.targetCharset = targetCharset;
    }
    
    
    @Override
    public long length() throws SQLException {
        return blob.length() / blobCharWidth;
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        long bytePos = ((pos - 1) * blobCharWidth) + 1;
        int byteLength = length * blobCharWidth;
        byte[] bytes = blob.getBytes(bytePos, byteLength);
        return new String(bytes, blobCharset);
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        return new InputStreamReader(blob.getBinaryStream(), blobCharset);
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        long bytePos = ((pos - 1) * blobCharWidth) + 1;
        long byteLength = length * blobCharWidth;
        return new InputStreamReader(blob.getBinaryStream(bytePos, byteLength), blobCharset);
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        if (blobCharset == targetCharset) {
            return blob.getBinaryStream();
        } else {
            return new ReaderInputStream(
                    new InputStreamReader(blob.getBinaryStream(), blobCharset),
                    targetCharset);
        }
    }

    @Override
    public long position(String searchstr, long start) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long position(Clob searchstr, long start) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        long bytePos = ((pos - 1) * blobCharWidth) + 1;
        int charCount = blob.setBytes(bytePos, str.getBytes(blobCharset));
        return charCount / blobCharWidth;
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        long bytePos = ((pos - 1) * blobCharWidth) + 1;
        int byteOffset = offset * blobCharWidth;
        int byteLen = len * blobCharWidth;
        int charCount = blob.setBytes(bytePos, str.getBytes(blobCharset), byteOffset, byteLen);
        return charCount / blobCharWidth;
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        long bytePos = ((pos - 1) * blobCharWidth) + 1;
        if (blobCharset == targetCharset) {
            return blob.setBinaryStream(bytePos);
        } else {
            OutputStream byteStream = blob.setBinaryStream(bytePos);
            return new WriterOutputStream(
                    new OutputStreamWriter(byteStream, blobCharset),
                    targetCharset);
        }
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
        long bytePos = ((pos - 1) * blobCharWidth) + 1;
        OutputStream byteStream = blob.setBinaryStream(bytePos);
        return new OutputStreamWriter(byteStream, blobCharset);
    }

    @Override
    public void truncate(long len) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void free() throws SQLException {
        blob.free();
    }
    
}
