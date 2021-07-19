package hu.webarticum.miniconnect.jdbc.blob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;

// TODO: make writeout to file optional ( BlobStorage:(MemoryStorage|FileStorage) )
public class WriteableBlob implements Blob {

    private static final String FILE_ACCESS_MODE = "rw";
    
    
    private final File file;
    
    private final RandomAccessFile randomAccessFile;
    

    public WriteableBlob() {
        this.file = createTemporaryFile();
        this.randomAccessFile = createRandomAccessFile(file);
    }
    
    // FIXME see: hu.webarticum.miniconnect.tool.contentaccess.FileChargeableContentAccess
    private static File createTemporaryFile() {
        try {
            return Files.createTempFile("miniconnect-jdbc-", ".blob").toFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private static RandomAccessFile createRandomAccessFile(File file) {
        try {
            return new RandomAccessFile(file, FILE_ACCESS_MODE);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    
    @Override
    public long length() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void truncate(long len) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void free() throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
