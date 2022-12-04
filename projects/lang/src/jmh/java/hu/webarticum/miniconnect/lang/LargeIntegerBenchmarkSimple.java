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
 * Compares performance of LargeInteger to Long, BigInteger, BigInt in a simple case.
 * 
 * <p>Based on the following expression:</p>
 * 
 * <pre>
 * ((A * B) + C) / D
 * </pre>
 */
@State(Scope.Benchmark)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 3, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class LargeIntegerBenchmarkSimple {

    private Random random = new Random();
    

    private long[] primitiveLongValues;

    private Long[] longValues;
    
    private BigInteger[] bigIntegerValues;
    
    private BigInt[] scalaBigIntValues;
    
    private LargeInteger[] largeIntegerValues;
    
    
    @Setup
    public void setup() {
        primitiveLongValues = new long[] {
                random.nextInt(1000) + 200L,
                random.nextInt(20) + 10L,
                random.nextInt(10000) + 2000L,
                random.nextInt(100) + 50L,
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
        blackhole.consume(
                ((primitiveLongValues[0] * primitiveLongValues[1]) + primitiveLongValues[2]) / primitiveLongValues[3]);
    }

    @Benchmark
    public void benchmarkLong(Blackhole blackhole) {
        blackhole.consume(((longValues[0] * longValues[1]) + longValues[2]) / longValues[3]);
    }

    @Benchmark
    public void benchmarkBigInteger(Blackhole blackhole) {
        blackhole.consume(
                bigIntegerValues[0]
                        .multiply(bigIntegerValues[1])
                        .add(bigIntegerValues[2])
                        .divide(bigIntegerValues[3]));
    }

    @Benchmark
    public void benchmarkScalaBigInt(Blackhole blackhole) {
        blackhole.consume(
                scalaBigIntValues[0]
                        .$times(scalaBigIntValues[1])
                        .$plus(scalaBigIntValues[2])
                        .$div(scalaBigIntValues[3]));
    }

    @Benchmark
    public void benchmarkLargeInteger(Blackhole blackhole) {
        blackhole.consume(
                largeIntegerValues[0]
                        .multiply(largeIntegerValues[1])
                        .add(largeIntegerValues[2])
                        .divide(largeIntegerValues[3]));
    }

}
