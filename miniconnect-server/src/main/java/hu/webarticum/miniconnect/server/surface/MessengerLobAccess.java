package hu.webarticum.miniconnect.server.surface;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.NavigableSet;
import java.util.TreeSet;

import hu.webarticum.miniconnect.api.MiniLobAccess;
import hu.webarticum.miniconnect.server.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.util.data.ByteString;

public class MessengerLobAccess implements MiniLobAccess {
    
    private static final String FILE_ACCESS_MODE = "rw";
    
    
    private final long fullLength;
    
    private final File file;
    
    private final RandomAccessFile randomAccessFile;
    
    private final Object indexLock = new Object();
    
    private final Object fileAccessLock = new Object();
    
    private final Object closeLock = new Object();
    
    
    private NavigableSet<IndexEntry> index = new TreeSet<>();
    
    
    private volatile boolean closed = false;
    
    
    
    public MessengerLobAccess(long length, File file) throws IOException {
        this.fullLength = length;
        this.file = file;
        this.randomAccessFile = new RandomAccessFile(file, FILE_ACCESS_MODE);
    }


    @Override
    public long length() {
        return fullLength;
    }

    @Override
    public ByteString part(long start, int length) throws IOException {
        checkClosed();
        checkBounds(start, length);
        waitAvailable(start, (long) length);
        
        byte[] bytes = new byte[length];
        synchronized (fileAccessLock) {
            randomAccessFile.seek(start);
            randomAccessFile.readFully(bytes);
        }
        return ByteString.wrap(bytes);
    }
    
    @Override
    public InputStream inputStream() throws IOException {
        checkClosed();
        return new LobInputStream();
    }

    // FIXME: what if close occured during this write?
    // FIXME: what if other error occured (close with storing the exception? 'closeReason' or something)
    public void accept(ResultSetValuePartResponse partResponse) throws IOException {
        checkClosed();
        
        long start = partResponse.offset();
        ByteString part = partResponse.content();
        int length = part.length();
        long end = start + length;
        
        checkBounds(start, length);

        IndexEntry entry = new IndexEntry(start, end);
        
        synchronized (indexLock) {
            IndexEntry previousEntry = index.lower(entry);
            if (previousEntry != null && previousEntry.end > start) {
                throw new IllegalArgumentException(
                        "Location is already allocated (start: " + start + "), " +
                        "previous entry ends at: " + previousEntry.end);
            }
            
            IndexEntry nextEntry = index.ceiling(entry);
            if (nextEntry != null && nextEntry.start < end) {
                    throw new IllegalArgumentException(
                            "Location is already allocated (end: " + end + "), "+
                            "next entry starts at: " + nextEntry.start);
            }
            
            index.add(entry);
        }
        
        synchronized (fileAccessLock) {
            randomAccessFile.seek(start);
            randomAccessFile.write(part.extract());
        }
        
        synchronized (indexLock) {
            entry.pending = false;
            
            IndexEntry previousEntry = index.lower(entry);
            boolean previousConnected = (previousEntry != null && previousEntry.end == start);
            
            IndexEntry nextEntry = index.higher(entry);
            boolean nextConnected = (nextEntry != null && nextEntry.start == end);
            
            if (previousConnected && nextConnected) {
                index.remove(entry);
                index.remove(nextEntry);
                previousEntry.end = nextEntry.end;
            } else if (previousConnected) {
                index.remove(entry);
                previousEntry.end = end;
            } else if (nextConnected) {
                index.remove(nextEntry);
                entry.end = nextEntry.end;
            }

            indexLock.notifyAll();
        }
    }
    
    private void checkBounds(long start, int length) {
        if (start < 0L || length <= 0 || (start + length) > fullLength) {
            throw new IllegalArgumentException(String.format(
                    "Invalid substring, beginIndex: %d, length: %d, content length: %d",
                    start, length, fullLength));
        }
    }

    private void waitAvailable(long start, long length) throws IOException {
        synchronized (indexLock) {
            while (!checkAvailable(start, length)) {
                try {
                    // TODO: timeout?
                    indexLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedIOException(
                            "Interrupt occured before the requested content whould became available");
                }
                checkClosed();
            }
        }
    }
    
    private boolean checkAvailable(long start, long length) {
        long end = start + length;
        synchronized (indexLock) {
            IndexEntry containerEntry = index.floor(new IndexEntry(start, end));
            return (containerEntry != null && !containerEntry.pending && containerEntry.end >= end);
        }
    }

    private void checkClosed() {
        if (closed) {
            throw new IllegalArgumentException("This LOB access was already closed");
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        
        synchronized (indexLock) {
            indexLock.notifyAll();
        }
        
        randomAccessFile.close();
        Files.delete(file.toPath());
    }
    
    
    private static class IndexEntry implements Comparable<IndexEntry> {
        
        boolean pending = true;
        
        long start;
        
        long end;
        
        
        IndexEntry(long start, long end) {
            this.start = start;
            this.end = end;
        }
        
        
        @Override
        public int compareTo(IndexEntry other) {
            return Long.compare(start, other.start);
        }
        
        @Override
        public int hashCode() {
            return Long.hashCode(start);
        }
        
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof IndexEntry)) {
                return false;
            }
            
            return start == ((IndexEntry) other).start;
        }
        
    }
    
    
    private class LobInputStream extends InputStream {
        
        private long position = 0L;
        
        private long mark = -1L;
        

        @Override
        public int read() throws IOException {
            ByteString part = readPart(1);
            if (part.isEmpty()) {
                return -1;
            }
            
            return (int) part.byteAt(0);
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            return read(buffer, 0, buffer.length);
        }
        
        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            if (length == 0) {
                return 0;
            }
            
            ByteString part = readPart(length);
            if (part == null) {
                return -1;
            }
            
            int partLength = part.length();
            part.extractTo(buffer, offset, 0, partLength);
            
            return partLength;
        }

        private synchronized ByteString readPart(int length) throws IOException {
            int safeLength = position + length > fullLength ? (int) (fullLength - position) : length;
            if (safeLength == 0) {
                return null;
            }
            ByteString part = part(position, safeLength);
            position += safeLength;
            return part;
        }
        
        @Override
        public boolean markSupported() {
            return true;
        }
        
        @Override
        public synchronized void mark(int readlimit) {
            mark = position;
        }
        
        @Override
        public synchronized void reset() throws IOException {
            position = mark;
        }

        @Override
        public int available() throws IOException {
            IndexEntry firstEntry;
            synchronized (indexLock) {
                if (index.isEmpty()) {
                    return 0;
                }
                firstEntry = index.first();
            }
            long longAvailable = firstEntry.end - position;
            
            return longAvailable > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) longAvailable;
        }
        
    }
    
}
