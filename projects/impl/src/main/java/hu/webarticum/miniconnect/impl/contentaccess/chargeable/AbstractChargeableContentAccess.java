package hu.webarticum.miniconnect.impl.contentaccess.chargeable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UncheckedIOException;
import java.util.NavigableSet;
import java.util.TreeSet;

import hu.webarticum.miniconnect.lang.ByteString;

public abstract class AbstractChargeableContentAccess implements ChargeableContentAccess {
    
    private static final long INDEX_LOCK_TIMEOUT_MILLIS = 60 * 1000;
    

    private final long fullLength;
    
    private final Object indexLock = new Object();
    
    private final NavigableSet<IndexEntry> index = new TreeSet<>();
    
    private volatile boolean completed = false;
    
    
    
    protected AbstractChargeableContentAccess(long length) {
        this.fullLength = length;
    }


    @Override
    public long length() {
        return fullLength;
    }

    @Override
    public ByteString get(long start, int length) {
        checkClosed();
        checkBounds(start, length);
        waitAvailable(start, length);
        
        return loadPart(start, length);
    }
    
    protected abstract ByteString loadPart(long start, int length);
    
    @Override
    public InputStream inputStream() {
        checkClosed();
        return new LobInputStream();
    }

    @Override
    public InputStream inputStream(long offset, long length) {
        checkClosed();
        return new LobInputStream(offset, length);
    }
    
    @Override
    public void accept(long start, ByteString part) {
        checkClosed();
        
        int length = part.length();
        long end = start + length;
        
        checkBounds(start, length);

        IndexEntry entry = new IndexEntry(start, end);
        
        synchronized (indexLock) {
            IndexEntry previousEntry = index.lower(entry);
            if (previousEntry != null && previousEntry.end > start) {
                throw new IllegalArgumentException(String.format(
                        "Interval %d..%d is already allocated by existing entry: %d..%d",
                        start, end, previousEntry.start, previousEntry.end));
            }
            
            IndexEntry nextEntry = index.ceiling(entry);
            if (nextEntry != null && nextEntry.start < end) {
                throw new IllegalArgumentException(String.format(
                        "Interval %d..%d is already allocated by existing entry: %d..%d",
                        start, end, nextEntry.start, nextEntry.end));
            }
            
            index.add(entry);
        }
        
        savePart(start, part);
        
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

            completed = isAvailable(0L, fullLength);
            
            indexLock.notifyAll();
        }
    }
    
    protected abstract void savePart(long start, ByteString part);
    
    private void checkBounds(long start, int length) {
        if (start < 0L || length <= 0 || (start + length) > fullLength) {
            throw new IllegalArgumentException(String.format(
                    "Invalid substring, beginIndex: %d, length: %d, content length: %d",
                    start, length, fullLength));
        }
    }

    private void waitAvailable(long start, long length) {
        synchronized (indexLock) {
            while (!isAvailable(start, length)) {
                try {
                    indexLock.wait(INDEX_LOCK_TIMEOUT_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    IOException ioException = new InterruptedIOException();
                    ioException.addSuppressed(e);
                    throw new UncheckedIOException(ioException);
                }
                checkClosed();
            }
        }
    }
    
    @Override
    public boolean isAvailable(long start, long length) {
        long end = start + length;
        IndexEntry containerEntry = index.floor(new IndexEntry(start, end));
        return (containerEntry != null && !containerEntry.pending && containerEntry.end >= end);
    }
    
    @Override
    public boolean isCompleted() {
        return completed;
    }
    
    @Override
    public void close() {
        synchronized (indexLock) {
            indexLock.notifyAll();
        }
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
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof IndexEntry)) {
                return false;
            }
            
            return start == ((IndexEntry) other).start;
        }
        
        @Override
        public String toString() {
            return start + ".." + end;
        }
        
    }
    
    
    private class LobInputStream extends InputStream {
        
        private long position;
        
        private long endPosition;
        
        private long mark = -1L;
        
        
        private LobInputStream() {
            this(0L, fullLength);
        }
        
        private LobInputStream(long offset, long length) {
            position = offset;
            endPosition = offset + length;
        }
        

        @Override
        public int read() {
            ByteString part = readPart(1);
            if (part == null || part.isEmpty()) {
                return -1;
            }
            
            return Byte.toUnsignedInt(part.byteAt(0));
        }

        @Override
        public int read(byte[] buffer) {
            return read(buffer, 0, buffer.length);
        }
        
        @Override
        public int read(byte[] buffer, int offset, int length) {
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

        private synchronized ByteString readPart(int length) {
            int safeLength =
                    position + length > endPosition ?
                    (int) (endPosition - position) :
                    length;
            if (safeLength == 0) {
                return null;
            }
            ByteString part = get(position, safeLength);
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
        public synchronized void reset() {
            position = mark;
        }

        @Override
        public int available() {
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
