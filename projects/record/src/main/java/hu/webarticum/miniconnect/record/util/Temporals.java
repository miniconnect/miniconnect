package hu.webarticum.miniconnect.record.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

public final class Temporals {

    private Temporals() {
        // utility class
    }


    // FIXME: duplicated from TemporalUtil from minibase
    public static Temporal parse(String dateString) {
        int firstColonPos = dateString.lastIndexOf(':');
        if (firstColonPos < 0) {
            return LocalDate.parse(dateString);
        }
        int lastDashPos = dateString.lastIndexOf('-');
        int lastPlusPos = dateString.lastIndexOf('+');
        if (lastDashPos < 0 && lastPlusPos < 0) {
            return LocalTime.parse(dateString);
        }
        if (dateString.lastIndexOf('/') >= 0) {
            return ZonedDateTime.parse(normalizeDateTimeString(dateString));
        } else if (dateString.endsWith("Z") || dateString.endsWith("z")) {
            return OffsetDateTime.parse(normalizeDateTimeString(dateString));
        }
        boolean hasOffset = (lastPlusPos >= 0 || lastDashPos > firstColonPos);
        if (!hasOffset) {
            return LocalDateTime.parse(normalizeDateTimeString(dateString));
        }
        if (lastDashPos < 0 || dateString.indexOf('-') > firstColonPos) {
            return OffsetTime.parse(dateString);
        } else {
            return OffsetDateTime.parse(normalizeDateTimeString(dateString));
        }
    }

    private static String normalizeDateTimeString(String dateTimeString) {
        int pos = dateTimeString.indexOf(' ', 5);
        if (pos < 0) {
            return dateTimeString;
        }

        return dateTimeString.substring(0, pos) + 'T' + dateTimeString.substring(pos + 1);
    }

}
