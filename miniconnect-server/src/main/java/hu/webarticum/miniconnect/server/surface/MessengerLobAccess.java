package hu.webarticum.miniconnect.server.surface;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.util.NavigableSet;
import java.util.TreeSet;

import hu.webarticum.miniconnect.api.MiniLobAccess;
import hu.webarticum.miniconnect.server.message.response.ResultSetValuePartResponse;
import hu.webarticum.miniconnect.util.data.ByteString;

// TODO: test
public class MessengerLobAccess implements MiniLobAccess {
    
    private final long fullLength;
    
    private final RandomAccessFile randomAccessFile;
    
    private final Object indexLock = new Object();
    
    private final Object fileAccessLock = new Object();
    
    private final Object closeLock = new Object();
    
    
    private NavigableSet<IndexEntry> index = new TreeSet<>();
    
    
    private volatile boolean closed = false;
    
    
    
    public MessengerLobAccess(long length, RandomAccessFile randomAccessFile) {
        this.fullLength = length;
        this.randomAccessFile = randomAccessFile;
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
    
    // TODO: use a concurrent InputStream implementation instead
    //       that can start reading even if content is not completed yet
    @Override
    public InputStream inputStream() throws IOException {
        checkClosed();
        waitAvailable(0L, fullLength);
        
        return Channels.newInputStream(randomAccessFile.getChannel());
    }
    
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
        }
        
        // FIXME: what if close occured during this write?
        
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

}
