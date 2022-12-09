package hu.webarticum.miniconnect.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public abstract class LargeInteger extends Number implements Comparable<LargeInteger> {
    
    private static final long serialVersionUID = 1L;
    
    
    private static final long CACHE_LOW = -128L;

    private static final long CACHE_HIGH = 129L;
    
    private static final LargeInteger[] cache = new LargeInteger[(int) (CACHE_HIGH - CACHE_LOW)];
    static {
        for (int i = 0; i < cache.length; i++) {
            cache[i] = new ImplSmall(CACHE_LOW + i);
        }
    }
    

    private static final int MAX_SAFE_LONG_STR_LENGTH = 18;
    
    private static final int MAX_SAFE_LONG_RADIX_STR_LENGTH = 12;
    
    private static final int MAX_SAFE_LONG_RADIX = 36;
    
    private static final long MAX_SMALL_ADDITIVE = 4611686018427387903L;
    
    private static final long MAX_SMALL_MULTIPLIER = 3037000499L;
    
    private static final int MAX_SMALL_POW_EXPONENT = 4;
    
    private static final long MAX_SMALL_POW_BASE_ABS = 55108L;
    
    
    public static final LargeInteger NEGATIVE_ONE = of(-1L);
    
    public static final LargeInteger ZERO = of(0L);
    
    public static final LargeInteger ONE = of(1L);
    
    public static final LargeInteger TWO = of(2L);
    
    public static final LargeInteger TEN = of(10L);


    public static LargeInteger of(byte value) {
        return of((long) value);
    }

    public static LargeInteger of(short value) {
        return of((long) value);
    }

    public static LargeInteger of(int value) {
        return of((long) value);
    }

    public static LargeInteger of(long value) {
        return ofSmall(value);
    }
    
    public static LargeInteger of(BigInteger value) {
        return isSmall(value) ? ofSmall(value.longValue()) : ofBig(value);
    }

    public static LargeInteger of(String value) {
        if (value.length() <= MAX_SAFE_LONG_STR_LENGTH) {
            return ofSmall(Long.parseLong(value));
        } else {
            return of(new BigInteger(value));
        }
    }

    public static LargeInteger of(String value, int radix) {
        if (value.length() <= MAX_SAFE_LONG_RADIX_STR_LENGTH && radix <= MAX_SAFE_LONG_RADIX) {
            return ofSmall(Long.parseLong(value, radix));
        } else {
            return of(new BigInteger(value, radix));
        }
    }

    public static LargeInteger of(byte[] bytes) {
        if (bytes.length == Long.BYTES) {
            return ofSmall(ByteBuffer.wrap(bytes).getLong());
        } else if (bytes.length == 0) {
            return ZERO;
        } else if (bytes.length < Long.BYTES) {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            buffer.position(Long.BYTES - bytes.length);
            buffer.put(bytes);
            buffer.position(0);
            long value = buffer.getLong();
            return ofSmall(value);
        } else {
            return of(new BigInteger(bytes));
        }
    }


    private static boolean isSmall(BigInteger value) {
        return value.bitLength() <= 63;
    }

    private static LargeInteger ofSmall(long value) {
        if (value < CACHE_HIGH && value >= CACHE_LOW) {
            return cache[(int) (value - CACHE_LOW)];
        }
        
        return new ImplSmall(value);
    }
    
    private static LargeInteger ofBig(BigInteger value) {
        return new ImplBig(value);
    }
    

    // BigInteger' methods
    
    public abstract long longValueExact();
    
    public abstract int intValueExact();
    
    public abstract short shortValueExact();
    
    public abstract byte byteValueExact();
    
    public abstract byte[] toByteArray();
    
    public abstract boolean isProbablePrime(int certainty);
    
    public abstract LargeInteger nextProbablePrime();
    
    public abstract LargeInteger min(LargeInteger val);
    
    public abstract LargeInteger max(LargeInteger val);
    
    public abstract LargeInteger add(LargeInteger val);
    
    public abstract LargeInteger subtract(LargeInteger val);
    
    public abstract LargeInteger multiply(LargeInteger val);
    
    public abstract LargeInteger divide(LargeInteger val);
    
    public abstract LargeInteger[] divideAndRemainder(LargeInteger val);
    
    public abstract LargeInteger remainder(LargeInteger val);
    
    public abstract LargeInteger pow(int exponent);
    
    public abstract LargeInteger gcd(LargeInteger val);
    
    public abstract LargeInteger abs();
    
    public abstract LargeInteger negate();
    
    public abstract int signum();
    
    public abstract LargeInteger mod(LargeInteger m);
    
    public abstract LargeInteger modPow(LargeInteger exponent, LargeInteger m);
    
    public abstract LargeInteger modInverse(LargeInteger m);
    
    public abstract LargeInteger shiftLeft(int n);
    
    public abstract LargeInteger shiftRight(int n);
    
    public abstract LargeInteger and(LargeInteger val);
    
    public abstract LargeInteger or(LargeInteger val);
    
    public abstract LargeInteger xor(LargeInteger val);
    
    public abstract LargeInteger not();
    
    public abstract LargeInteger andNot(LargeInteger val);
    
    public abstract boolean testBit(int n);
    
    public abstract LargeInteger setBit(int n);
    
    public abstract LargeInteger clearBit(int n);
    
    public abstract LargeInteger flipBit(int n);
    
    public abstract int getLowestSetBit();
    
    public abstract int bitLength();
    
    public abstract int bitCount();
    
    
    // additional methods

    public abstract String toString(int radix);
    
    public abstract BigInteger bigIntegerValue();

    public abstract BigDecimal bigDecimalValue();
    
    public abstract boolean isFittingInLong();
    
    public abstract boolean isFittingInInt();
    
    public abstract boolean isFittingInShort();
    
    public abstract boolean isFittingInByte();

    public boolean isZero() {
        return equals(ZERO);
    }

    public boolean isPositive() {
        return compareTo(ZERO) > 0;
    }

    public boolean isNonPositive() {
        return compareTo(ZERO) <= 0;
    }

    public boolean isNegative() {
        return compareTo(ZERO) < 0;
    }

    public boolean isNonNegative() {
        return compareTo(ZERO) >= 0;
    }

    public boolean isEven() {
        return isDivisibleBy(TWO);
    }

    public boolean isOdd() {
        return !isEven();
    }

    public boolean isDivisibleBy(LargeInteger val) {
        return remainder(val).isZero();
    }

    public boolean isEqualTo(LargeInteger val) {
        return equals(val);
    }

    public boolean isLessThan(LargeInteger val) {
        return compareTo(val) < 0;
    }

    public boolean isLessThanOrEqualTo(LargeInteger val) {
        return compareTo(val) <= 0;
    }
    
    public boolean isGreaterThan(LargeInteger val) {
        return compareTo(val) > 0;
    }

    public boolean isGreaterThanOrEqualTo(LargeInteger val) {
        return compareTo(val) >= 0;
    }
    
    public abstract LargeInteger increment();
    
    public abstract LargeInteger decrement();
    

    private static class ImplSmall extends LargeInteger {
        
        private static final long serialVersionUID = 1L;
        
        
        private final long value;
        

        private ImplSmall(long value) {
            this.value = value;
        }

        
        @Override
        public String toString() {
            return Long.toString(value);
        }

        @Override
        public String toString(int radix) {
            return Long.toString(value, radix);
        }
        
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ImplSmall)) {
                return false;
            }
            
            return value == ((ImplSmall) other).value;
        }
        
        @Override
        public int hashCode() {
            return Long.hashCode(value);
        }
        
        @Override
        public int compareTo(LargeInteger other) {
            if (other instanceof ImplBig) {
                return ((ImplBig) other).value.signum();
            }
            
            return Long.compare(value, other.longValueExact());
        }

        @Override
        public BigInteger bigIntegerValue() {
            return BigInteger.valueOf(value);
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return BigDecimal.valueOf(value);
        }
        
        @Override
        public long longValue() {
            return value;
        }

        @Override
        public int intValue() {
            return (int) value;
        }

        @Override
        public float floatValue() {
            return value;
        }

        @Override
        public double doubleValue() {
            return value;
        }

        @Override
        public long longValueExact() {
            return value;
        }

        @Override
        public int intValueExact() {
            if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
                throw new ArithmeticException("LargeInteger out of int range");
            }
            
            return intValue();
        }

        @Override
        public short shortValueExact() {
            if (value > Short.MAX_VALUE || value < Short.MIN_VALUE) {
                throw new ArithmeticException("LargeInteger out of short range");
            }
            
            return shortValue();
        }

        @Override
        public byte byteValueExact() {
            if (value > Byte.MAX_VALUE || value < Byte.MIN_VALUE) {
                throw new ArithmeticException("LargeInteger out of byte range");
            }
            
            return byteValue();
        }

        public byte[] toByteArray() {
            return bigIntegerValue().toByteArray();
        }
        
        public boolean isProbablePrime(int certainty) {
            return bigIntegerValue().isProbablePrime(certainty);
        }
        
        public LargeInteger nextProbablePrime() {
            return of(bigIntegerValue().nextProbablePrime());
        }
        
        public LargeInteger min(LargeInteger val) {
            if (val instanceof ImplBig) {
                return val.signum() > 0 ? this : val;
            }
            
            return ofSmall(Math.min(value, ((ImplSmall) val).value));
        }
        
        public LargeInteger max(LargeInteger val) {
            if (val instanceof ImplBig) {
                return val.signum() < 0 ? this : val;
            }
            
            return ofSmall(Math.max(value, ((ImplSmall) val).value));
        }
        
        public LargeInteger add(LargeInteger val) {
            if (value == 0) {
                return val;
            } else if (val instanceof ImplBig || value == Long.MIN_VALUE || Math.abs(value) > MAX_SMALL_ADDITIVE) {
                return of(bigIntegerValue().add(val.bigIntegerValue()));
            }

            ImplSmall smallVal = (ImplSmall) val;
            if (smallVal.value == Long.MIN_VALUE || Math.abs(smallVal.value) > MAX_SMALL_ADDITIVE) {
                return ofBig(bigIntegerValue().multiply(smallVal.bigIntegerValue()));
            }

            return ofSmall(value + smallVal.value);
        }
        
        public LargeInteger subtract(LargeInteger val) {
            return add(val.negate());
        }
        
        public LargeInteger multiply(LargeInteger val) {
            if (value == 0) {
                return ZERO;
            } else if (val instanceof ImplBig || value == Long.MIN_VALUE || Math.abs(value) > MAX_SMALL_MULTIPLIER) {
                return ofBig(bigIntegerValue().multiply(val.bigIntegerValue()));
            }
            
            ImplSmall smallVal = (ImplSmall) val;
            if (smallVal.value == Long.MIN_VALUE || Math.abs(smallVal.value) > MAX_SMALL_MULTIPLIER) {
                return ofBig(bigIntegerValue().multiply(smallVal.bigIntegerValue()));
            }
            
            return ofSmall(value * smallVal.value);
        }
        
        public LargeInteger divide(LargeInteger val) {
            if (val instanceof ImplBig) {
                return ZERO;
            }
            
            return ofSmall(value / ((ImplSmall) val).value);
        }
        
        public LargeInteger[] divideAndRemainder(LargeInteger val) {
            return new LargeInteger[] { divide(val), remainder(val) };
        }
        
        public LargeInteger remainder(LargeInteger val) {
            if (val instanceof ImplBig) {
                return this;
            }
            
            return ofSmall(value % ((ImplSmall) val).value);
        }
        
        public LargeInteger pow(int exponent) {
            if (exponent < 0) {
                throw new ArithmeticException("Negative exponent");
            } else if (exponent == 0) {
                return ONE;
            } else if (exponent == 1) {
                return this;
            } else if (
                    value == Long.MIN_VALUE ||
                    Math.abs(value) > MAX_SMALL_POW_BASE_ABS ||
                    exponent > MAX_SMALL_POW_EXPONENT) {
                return of(bigIntegerValue().pow(exponent));
            }
            
            long square = value * value;
            long longResult =
                    exponent == 2 ? square :
                    exponent == 3 ? square * value :
                    square * square;
            return ofSmall(longResult);
        }
        
        public LargeInteger gcd(LargeInteger val) {
            if (val instanceof ImplBig) {
                return of(bigIntegerValue().gcd(val.bigIntegerValue()));
            }
            
            return ofSmall(longGcd(value, ((ImplSmall) val).value));
        }
        
        private static long longGcd(long a, long b) {
            return b == 0 ? a : longGcd(b, a % b);
        }
        
        public LargeInteger abs() {
            if (value >= 0L) {
                return this;
            } else if (value == Long.MIN_VALUE) {
                return ofBig(bigIntegerValue().negate());
            } else {
                return ofSmall(-value);
            }
        }
        
        public LargeInteger negate() {
            if (value == Long.MIN_VALUE) {
                return ofBig(bigIntegerValue().negate());
            }
            
            return ofSmall(-value);
        }
        
        public int signum() {
            return Long.signum(value);
        }
        
        public LargeInteger mod(LargeInteger m) {
            ImplSmall remainder = (ImplSmall) remainder(m);
            if (remainder.value >= 0) {
                return remainder;
            } else {
                return m.abs().add(remainder);
            }
        }
        
        public LargeInteger modPow(LargeInteger exponent, LargeInteger m) {
            return of(bigIntegerValue().modPow(exponent.bigIntegerValue(), m.bigIntegerValue()));
        }
        
        public LargeInteger modInverse(LargeInteger m) {
            return of(bigIntegerValue().modInverse(m.bigIntegerValue()));
        }
        
        public LargeInteger shiftLeft(int n) {
            return ofSmall(value << n);
        }
        
        public LargeInteger shiftRight(int n) {
            return ofSmall(value >> n);
        }
        
        public LargeInteger and(LargeInteger val) {
            if (val instanceof ImplBig) {
                return of(bigIntegerValue().and(val.bigIntegerValue()));
            }

            return ofSmall(value & ((ImplSmall) val).value);
        }
        
        public LargeInteger or(LargeInteger val) {
            if (val instanceof ImplBig) {
                return of(bigIntegerValue().or(val.bigIntegerValue()));
            }

            return ofSmall(value | ((ImplSmall) val).value);
        }
        
        public LargeInteger xor(LargeInteger val) {
            if (val instanceof ImplBig) {
                return of(bigIntegerValue().xor(val.bigIntegerValue()));
            }

            return ofSmall(value ^ ((ImplSmall) val).value);
        }
        
        public LargeInteger not() {
            return ofSmall(~value);
        }
        
        public LargeInteger andNot(LargeInteger val) {
            if (val instanceof ImplBig) {
                return of(bigIntegerValue().andNot(val.bigIntegerValue()));
            }

            return ofSmall(value & ~(((ImplSmall) val).value));
        }
        
        public boolean testBit(int n) {
            return (value & (1 <<n )) != 0;
        }
        
        public LargeInteger setBit(int n) {
            return ofSmall(value | (1 << n));
        }
        
        public LargeInteger clearBit(int n) {
            return ofSmall(value & ~(1 << n));
        }
        
        public LargeInteger flipBit(int n) {
            return ofSmall(value ^ (1 << n));
        }
        
        public int getLowestSetBit() {
            return 63 - Long.numberOfTrailingZeros(Long.lowestOneBit(value));
        }
        
        public int bitLength() {
            return bigIntegerValue().bitLength();
        }
        
        public int bitCount() {
            return bigIntegerValue().bitCount();
        }

        public boolean isFittingInLong() {
            return true;
        }
        
        public boolean isFittingInInt() {
            return value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE;
        }
        
        public boolean isFittingInShort() {
            return value >= Short.MIN_VALUE && value <= Short.MAX_VALUE;
        }
        
        public boolean isFittingInByte() {
            return value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE;
        }

        public LargeInteger increment() {
            if (value == Long.MAX_VALUE) {
                return ofBig(bigIntegerValue().add(BigInteger.ONE));
            }
            
            return ofSmall(value + 1);
        }
        
        public LargeInteger decrement() {
            if (value == Long.MIN_VALUE) {
                return ofBig(bigIntegerValue().subtract(BigInteger.ONE));
            }
            
            return ofSmall(value - 1);
        }

    }
    
    private static class ImplBig extends LargeInteger {
        
        private static final long serialVersionUID = 1L;
        
        
        private final BigInteger value;
        

        private ImplBig(BigInteger value) {
            this.value = value;
        }


        @Override
        public String toString() {
            return value.toString();
        }

        @Override
        public String toString(int radix) {
            return value.toString(radix);
        }
        
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof ImplBig)) {
                return false;
            }
            
            return value.equals(((ImplBig) other).value);
        }
        
        @Override
        public int hashCode() {
            return value.hashCode();
        }
        
        @Override
        public int compareTo(LargeInteger other) {
            if (other instanceof ImplSmall) {
                return value.signum();
            }
            
            return value.compareTo(other.bigIntegerValue());
        }

        @Override
        public BigInteger bigIntegerValue() {
            return value;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return new BigDecimal(value);
        }
        
        @Override
        public long longValue() {
            return value.longValue();
        }

        @Override
        public int intValue() {
            return value.intValue();
        }

        @Override
        public float floatValue() {
            return value.floatValue();
        }

        @Override
        public double doubleValue() {
            return value.doubleValue();
        }

        @Override
        public long longValueExact() {
            return value.longValueExact();
        }

        @Override
        public int intValueExact() {
            return value.intValueExact();
        }

        @Override
        public short shortValueExact() {
            return value.shortValueExact();
        }

        @Override
        public byte byteValueExact() {
            return value.byteValueExact();
        }

        public byte[] toByteArray() {
            return value.toByteArray();
        }
        
        public boolean isProbablePrime(int certainty) {
            return value.isProbablePrime(certainty);
        }
        
        public LargeInteger nextProbablePrime() {
            return of(value.nextProbablePrime());
        }
        
        public LargeInteger min(LargeInteger val) {
            return of(value.min(val.bigIntegerValue()));
        }
        
        public LargeInteger max(LargeInteger val) {
            return of(value.max(val.bigIntegerValue()));
        }
        
        public LargeInteger add(LargeInteger val) {
            return of(value.add(val.bigIntegerValue()));
        }
        
        public LargeInteger subtract(LargeInteger val) {
            return of(value.subtract(val.bigIntegerValue()));
        }
        
        public LargeInteger multiply(LargeInteger val) {
            return of(value.multiply(val.bigIntegerValue()));
        }
        
        public LargeInteger divide(LargeInteger val) {
            return of(value.divide(val.bigIntegerValue()));
        }
        
        public LargeInteger[] divideAndRemainder(LargeInteger val) {
            BigInteger[] bigIntegerResults = value.divideAndRemainder(val.bigIntegerValue());
            return new LargeInteger[] { of(bigIntegerResults[0]), of(bigIntegerResults[1]) };
        }
        
        public LargeInteger remainder(LargeInteger val) {
            return of(value.remainder(val.bigIntegerValue()));
        }
        
        public LargeInteger pow(int exponent) {
            return of(value.pow(exponent));
        }
        
        public LargeInteger gcd(LargeInteger val) {
            return of(value.gcd(val.bigIntegerValue()));
        }
        
        public LargeInteger abs() {
            return of(value.abs());
        }
        
        public LargeInteger negate() {
            return of(value.negate());
        }
        
        public int signum() {
            return value.signum();
        }
        
        public LargeInteger mod(LargeInteger m) {
            return of(value.mod(m.bigIntegerValue()));
        }
        
        public LargeInteger modPow(LargeInteger exponent, LargeInteger m) {
            return of(value.modPow(exponent.bigIntegerValue(), m.bigIntegerValue()));
        }
        
        public LargeInteger modInverse(LargeInteger m) {
            return of(value.modInverse(m.bigIntegerValue()));
        }
        
        public LargeInteger shiftLeft(int n) {
            return of(value.shiftLeft(n));
        }
        
        public LargeInteger shiftRight(int n) {
            return of(value.shiftRight(n));
        }
        
        public LargeInteger and(LargeInteger val) {
            return of(value.and(val.bigIntegerValue()));
        }
        
        public LargeInteger or(LargeInteger val) {
            return of(value.or(val.bigIntegerValue()));
        }
        
        public LargeInteger xor(LargeInteger val) {
            return of(value.xor(val.bigIntegerValue()));
        }
        
        public LargeInteger not() {
            return of(value.not());
        }
        
        public LargeInteger andNot(LargeInteger val) {
            return of(value.andNot(val.bigIntegerValue()));
        }
        
        public boolean testBit(int n) {
            return value.testBit(n);
        }
        
        public LargeInteger setBit(int n) {
            return of(value.setBit(n));
        }
        
        public LargeInteger clearBit(int n) {
            return of(value.clearBit(n));
        }
        
        public LargeInteger flipBit(int n) {
            return of(value.flipBit(n));
        }
        
        public int getLowestSetBit() {
            return value.getLowestSetBit();
        }
        
        public int bitLength() {
            return value.bitLength();
        }
        
        public int bitCount() {
            return value.bitCount();
        }

        public boolean isFittingInLong() {
            return false;
        }
        
        public boolean isFittingInInt() {
            return false;
        }
        
        public boolean isFittingInShort() {
            return false;
        }
        
        public boolean isFittingInByte() {
            return false;
        }
        
        public LargeInteger increment() {
            return ofBig(value.add(BigInteger.ONE));
        }
        
        public LargeInteger decrement() {
            return ofBig(value.subtract(BigInteger.ONE));
        }

    }
    
}
