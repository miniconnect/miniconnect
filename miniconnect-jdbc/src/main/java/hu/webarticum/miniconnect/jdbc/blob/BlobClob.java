package hu.webarticum.miniconnect.jdbc.blob;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    

    public BlobClob() {
        this(StandardCharsets.UTF_8);
    }
    
    public BlobClob(Charset targetCharset) {
        this(new WriteableBlob(), StandardCharsets.UTF_16BE, 2, targetCharset);
    }
    
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
    

    public Blob getBlob() {
        return blob;
    }

    public Charset getBlobCharset() {
        return blobCharset;
    }

    public int getBlobCharWidth() {
        return blobCharWidth;
    }

    public Charset getTargetCharset() {
        return targetCharset;
    }
    
    @Override
    public long length() throws SQLException {
        return blob.length() / blobCharWidth;
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        long bytePos = findBytePos(pos);
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
        long bytePos = findBytePos(pos);
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
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        return setString(pos, str.substring(offset, offset + len));
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        long bytePos = findBytePos(pos);
        byte[] bytes = str.getBytes(blobCharset);
        blob.setBytes(bytePos, bytes);
        return bytes.length;
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        long bytePos = findBytePos(pos);
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
        long bytePos = findBytePos(pos);
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
    
    private long findBytePos(long charPos) {
        return ((charPos - 1) * blobCharWidth) + 1;
    }
    
}
