package hu.webarticum.miniconnect.transfer.old.benchmark;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import hu.webarticum.miniconnect.transfer.old.util.ByteUtil;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class IntegerSerializationBenchmark {

    private static final int[] NUMBERS = {
            
            // small
            0, 1, 2, 3, 12, 15, 32, 55, 79, 111,

            // medium
            150, 263, 324, 1601, 3412,
            12356, 100000, 5210399, 12345678, 111111111,
            
            // large
            1378073628, 1472837062, 1506173829, 1672381386, 1736274738,
            1832745837, 1937287584, 2038274861, 2113672647, 2147483647
            
            };
    
    /*
    TODO
    
    test:
      - serialization performance
      - deserialization performance
      - result size (bytes)
    
    implementatons:
      - standard fix 4-byte integer serialization
      - dynamic-length serialization
    
    */ 

    private long fixedCounter = 0;
    
    private long fixedFullLength = 0;
    
    private long dynamicCounter = 0;
    
    private long dynamicFullLength = 0;
    
    @Setup
    public void setupBenchmark() {
        
    }

    @TearDown
    public void tearDownBenchmark() {
        // TODO: print
    }
    
    @Benchmark
    public void benchEncodingFixed(Blackhole blackhole) {
        for (int number : NUMBERS) {
            byte[] bytes = ByteUtil.intToBytes(number);
            dynamicCounter++;
            dynamicFullLength += bytes.length;
            blackhole.consume(bytes);
        }
    }

    @Benchmark
    public void benchEncodingDynamic(Blackhole blackhole) {
        // TODO
    }

}
