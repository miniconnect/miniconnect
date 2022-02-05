package hu.webarticum.miniconnect.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Global id generator for generating unique but varied ids fast.
 * 
 * @see #generate()
 */
public class GlobalIdGenerator {
    
    private static final int ID_LENGTH = Long.toUnsignedString(-1L, 16).length();
    
    private static final long PRIME_FACTOR = 6904825739060485001L;
    

    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    
    /**
     * Conditionally returns with a generated id
     * 
     * @see #generate()
     * @param enabled
     * @return generated id if {@code enabled} is  {@code true}, 0 otherwise
     */
    public static String generate(boolean enabled) {
        return enabled ? generate() : "";
    }
    
    /**
     * Returns with a generated id.
     * 
     * <p>This generator id is based on a counter
     * starting from the unix timestamp at initialization.
     * It is confuscated by a prime factor to make it
     * easily distinguishable.</p>
     * 
     * <p>The resulting id is unique until the long range is
     * fully traversed (which is unlikely).
     * The id is vary and probably unique between
     * different JVM runs (because of the timestamp based start value).</p>
     * 
     * @return the generated id
     */
    public static String generate() {
        long numericValue = counter.incrementAndGet() * PRIME_FACTOR;
        String stringValue = Long.toUnsignedString(numericValue, 16);
        return padWithZeros(stringValue);
    }

    private static String padWithZeros(String stringValue) {
        int length = stringValue.length();
        if (length == ID_LENGTH) {
            return stringValue;
        }
        
        StringBuilder resultBuilder = new StringBuilder();
        for (int i = length; i < ID_LENGTH; i++) {
            resultBuilder.append('0');
        }
        resultBuilder.append(stringValue);
        return resultBuilder.toString();
    }

}
