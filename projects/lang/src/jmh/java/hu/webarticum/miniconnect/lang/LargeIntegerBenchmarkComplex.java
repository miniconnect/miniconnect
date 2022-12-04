package hu.webarticum.miniconnect.lang;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import scala.math.BigInt;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Compares performance of LargeInteger to Long, BigInteger, BigInt in a complex case.
 * 
 * <p>Based on the following expression:</p>
 * 
 * <pre>
 * abs(
 *   ((((A + B) * (A - B)) + 1) ** 3)
 *     %
 *   ((C & D) | E)
 * )
 *   +
 * -max(B, D + min(A, E / A))
 * </pre>
 */
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class LargeIntegerBenchmarkComplex {
    
    private Random random = new Random();
    

    private long[] primitiveLongValues;

    private Long[] longValues;
    
    private BigInteger[] bigIntegerValues;
    
    private BigInt[] scalaBigIntValues;
    
    private LargeInteger[] largeIntegerValues;
    
    
    @Setup
    public void setup() {
        primitiveLongValues = new long[] {
                random.nextInt(30) + 100L,
                random.nextInt(20) + 10L,
                random.nextInt(5000) + 2000L,
                random.nextInt(70) + 50L,
                random.nextInt(50) + 90L,
        };

        longValues = new Long[primitiveLongValues.length];
        for (int i = 0; i < primitiveLongValues.length; i++) {
            longValues[i] = Long.valueOf(primitiveLongValues[i]);
        }
        
        bigIntegerValues = new BigInteger[longValues.length];
        for (int i = 0; i < longValues.length; i++) {
            bigIntegerValues[i] = BigInteger.valueOf(longValues[i]);
        }
        
        scalaBigIntValues = new BigInt[longValues.length];
        for (int i = 0; i < longValues.length; i++) {
            scalaBigIntValues[i] = BigInt.apply(longValues[i]);
        }
        
        largeIntegerValues = new LargeInteger[longValues.length];
        for (int i = 0; i < longValues.length; i++) {
            largeIntegerValues[i] = LargeInteger.of(longValues[i]);
        }
    }
    

    @Benchmark
    public void benchmarkPrimitiveLong(Blackhole blackhole) {
        long a = primitiveLongValues[0];
        long b = primitiveLongValues[1];
        long c = primitiveLongValues[2];
        long d = primitiveLongValues[3];
        long e = primitiveLongValues[4];
        long result =
                Math.abs(
                        (long) Math.pow((((a + b) * (a - b)) + 1L), 3)
                            %
                        ((c & d) | e))
                    +
                -Math.max(b, d + Math.min(a, e / a));
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkLong(Blackhole blackhole) {
        Long a = longValues[0];
        Long b = longValues[1];
        Long c = longValues[2];
        Long d = longValues[3];
        Long e = longValues[4];
        Long result =
                Math.abs(
                        (Long) (long) Math.pow((((a + b) * (a - b)) + 1L), 3)
                            %
                        ((c & d) | e))
                    +
                -Math.max(b, d + Math.min(a, e / a));
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkBigInteger(Blackhole blackhole) {
        BigInteger a = bigIntegerValues[0];
        BigInteger b = bigIntegerValues[1];
        BigInteger c = bigIntegerValues[2];
        BigInteger d = bigIntegerValues[3];
        BigInteger e = bigIntegerValues[4];
        BigInteger result =
                a.add(b)
                        .multiply(a.subtract(b))
                        .add(BigInteger.ONE)
                        .pow(3)
                        .remainder(c.and(d).or(e)).abs()
                        .add(b.max(d.add(a.min(e.divide(a)))).negate());
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkScalaBigInt(Blackhole blackhole) {
        BigInt a = scalaBigIntValues[0];
        BigInt b = scalaBigIntValues[1];
        BigInt c = scalaBigIntValues[2];
        BigInt d = scalaBigIntValues[3];
        BigInt e = scalaBigIntValues[4];
        BigInt result =
                a.$plus(b)
                        .$times(a.$minus(b))
                        .$plus(BigInt.apply(1L))
                        .pow(3)
                        .$percent(c.$amp(d).$bar(e)).abs()
                        .$plus(b.max(d.$plus(a.min(e.$div(a)))).unary_$minus());
        blackhole.consume(result);
    }

    @Benchmark
    public void benchmarkLargeInteger(Blackhole blackhole) {
        LargeInteger a = largeIntegerValues[0];
        LargeInteger b = largeIntegerValues[1];
        LargeInteger c = largeIntegerValues[2];
        LargeInteger d = largeIntegerValues[3];
        LargeInteger e = largeIntegerValues[4];
        LargeInteger result =
                a.add(b)
                        .multiply(a.subtract(b))
                        .increment()
                        .pow(3)
                        .remainder(c.and(d).or(e)).abs()
                        .add(b.max(d.add(a.min(e.divide(a)))).negate());
        blackhole.consume(result);
    }

}
