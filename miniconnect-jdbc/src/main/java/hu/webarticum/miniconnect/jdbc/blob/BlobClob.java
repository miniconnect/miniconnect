package hu.webarticum.miniconnect.jdbc.blob;

import java.io.IOException;
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

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedReader;
import org.apache.commons.io.input.ReaderInputStream;
import org.apache.commons.io.output.WriterOutputStream;

public class BlobClob implements NClob {

    private final Blob blob;
    
    private final Charset blobCharset;
    
    private final int blobCharWidth;
    
    private final Charset targetCharset;
    
    private final boolean lengthIsCachable;
    
    private volatile long cachedLength = -1;
    

    public BlobClob() {
        this(StandardCharsets.UTF_8);
    }
    
    public BlobClob(Charset targetCharset) {
        this(new WriteableBlob(), StandardCharsets.UTF_16BE, 2, targetCharset);
    }

    public BlobClob(Blob blob, Charset blobCharset, Charset targetCharset) {
        this(blob, blobCharset, 0, targetCharset);
    }

    public BlobClob(Blob blob, Charset blobCharset, int blobCharWidth, Charset targetCharset) {
        this(blob, blobCharset, blobCharWidth, targetCharset, false);
    }
    
    public BlobClob(
            Blob blob,
            Charset blobCharset,
            int blobCharWidth,
            Charset targetCharset,
            boolean lengthIsCachable) {
        this.blob = blob;
        this.blobCharset = blobCharset;
        this.blobCharWidth = blobCharWidth;
        this.targetCharset = targetCharset;
        this.lengthIsCachable = lengthIsCachable;
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
        if (cachedLength >= 0) {
            return cachedLength;
        } else if (!lengthIsCachable) {
            return calculateLength();
        }
        
        synchronized (this) {
            cachedLength = calculateLength();
            return cachedLength;
        }
    }
    
    private long calculateLength() throws SQLException {
        if (blobCharWidth > 0) {
            return blob.length() / blobCharWidth;
        } else {
            return countCharactersIn(getCharacterStream());
        }
    }
    
    private static long countCharactersIn(Reader reader) throws SQLException {
        try {
            return reader.skip(Long.MAX_VALUE - 1000L);
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        if (blobCharWidth > 0) {
            return getSubStringWithFixedCharWidth(pos, length);
        } else {
            return getSubStringWithVariableCharWidth(pos, length);
        }
    }

    private String getSubStringWithFixedCharWidth(long pos, int length) throws SQLException {
        long bytePos = findBytePos(pos);
        int byteLength = length * blobCharWidth;
        byte[] bytes = blob.getBytes(bytePos, byteLength);
        return new String(bytes, blobCharset);
    }

    private String getSubStringWithVariableCharWidth(long pos, int length) throws SQLException {
        try {
            return IOUtils.toString(getCharacterStreamWithVariableCharWidth(pos, length));
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        return new InputStreamReader(blob.getBinaryStream(), blobCharset);
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        if (blobCharWidth > 0) {
            return getCharacterStreamWithFixedCharWidth(pos, length);
        } else {
            return getCharacterStreamWithVariableCharWidth(pos, length);
        }
    }

    private Reader getCharacterStreamWithFixedCharWidth(long pos, long length) throws SQLException {
        long bytePos = findBytePos(pos);
        long byteLength = length * blobCharWidth;
        return new InputStreamReader(blob.getBinaryStream(bytePos, byteLength), blobCharset);
    }

    private Reader getCharacterStreamWithVariableCharWidth(
            long pos, long length) throws SQLException {
        long zeroBasedPos = pos - 1;
        long until = zeroBasedPos + length;
        if (until > Integer.MAX_VALUE) {
            throw new SQLException(
                    "Large positioning is not supported for variable-length encodings");
        }
        int zeroBasedPosAsInt = (int) zeroBasedPos;
        int untilAsInt = (int) until;
        Reader reader = new BoundedReader(
                new InputStreamReader(blob.getBinaryStream()), untilAsInt);
        try {
            reader.skip(zeroBasedPosAsInt);
        } catch (IOException e) {
            throw new SQLException(e);
        }
        return reader;
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
    
    private long findBytePos(long charPos) throws SQLException {
        if (charPos == 1L) {
            return 1L;
        }  else if (blobCharWidth > 0) {
            return ((charPos - 1) * blobCharWidth) + 1;
        } else {
            Reader reader = new InputStreamReader(blob.getBinaryStream());
            try {
                return countReaderBytesUntil(reader, charPos - 1) + 1;
            } catch (IOException e) {
                throw new SQLException();
            }
        }
    }
    
    // TODO: find a more efficient solution
    private long countReaderBytesUntil(Reader reader, long until) throws IOException {
        long result = 0L;
        long remainingChars = until;
        char[] buffer = new char[1024];
        int readLength;
        while ((readLength = reader.read(buffer)) != -1) {
            remainingChars -= readLength;
            int usefulLength =
                    remainingChars >= 0 ?
                    readLength :
                    (int) (readLength + remainingChars);
            int byteLength = blobCharset.encode(new String(buffer, 0, usefulLength)).remaining();
            result += byteLength;
        }
        return result;
    }
    
}
