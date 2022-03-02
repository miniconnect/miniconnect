package hu.webarticum.miniconnect.record.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public final class Numbers {
    
    private Numbers() {
        // utility class
    }
    
    
    public static BigDecimal toBigDecimal(Number number, int scale) {
        BigDecimal rawValue;
        if (number instanceof BigDecimal) {
            rawValue = (BigDecimal) number;
        } else if (number instanceof BigInteger) {
            rawValue = new BigDecimal((BigInteger) number);
        } else if (
                number instanceof Byte ||
                number instanceof Short ||
                number instanceof Integer ||
                number instanceof Long) {
            rawValue = BigDecimal.valueOf(number.longValue());
        } else {
            rawValue = BigDecimal.valueOf(number.doubleValue());
        }
        
        return rawValue.setScale(scale, RoundingMode.HALF_UP);
    }

}
