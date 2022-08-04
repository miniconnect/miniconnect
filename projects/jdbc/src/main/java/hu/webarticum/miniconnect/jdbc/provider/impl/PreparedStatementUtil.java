package hu.webarticum.miniconnect.jdbc.provider.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniErrorException;
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.jdbc.blob.BlobClob;
import hu.webarticum.miniconnect.jdbc.provider.DatabaseProvider;
import hu.webarticum.miniconnect.jdbc.provider.ParameterValue;
import hu.webarticum.regexbee.Bee;
import hu.webarticum.regexbee.BeeFragment;
import hu.webarticum.regexbee.common.StringLiteralFragment;

public final class PreparedStatementUtil {

    private static final BeeFragment SINGLE_QUOTED_FRAGMENT = StringLiteralFragment.builder()
            .withDelimiter("'")
            .withEscaping('\\', true)
            .build();
    
    private static final BeeFragment DOUBLE_QUOTED_FRAGMENT = StringLiteralFragment.builder()
            .withDelimiter("\"")
            .withEscaping('\\', true)
            .build();
    
    private static final BeeFragment TWODOLLARS_QUOTED_FRAGMENT = StringLiteralFragment.builder()
            .withDelimiter("$$")
            .withoutAnyEscaping()
            .build();

    private static final Pattern STRING_OR_QUESTION_MARK_PATTERN =
            SINGLE_QUOTED_FRAGMENT
            .or(DOUBLE_QUOTED_FRAGMENT)
            .or(TWODOLLARS_QUOTED_FRAGMENT)
            .or(Bee.fixed("?"))
            .toPattern();
    
    
    private PreparedStatementUtil() {
        // nothing to do
    }
    
    
    public static String[] compileSql(String sql) {
        Matcher matcher = STRING_OR_QUESTION_MARK_PATTERN.matcher(sql);
        List<Integer> positions = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.group().charAt(0) == '?') {
                positions.add(matcher.start());
            }
        }
        int questionMarkCount = positions.size();
        String[] result = new String[questionMarkCount + 1];
        int continuingPosition = 0;
        for (int i = 0; i < questionMarkCount; i++) {
            int position = positions.get(i);
            result[i] = sql.substring(continuingPosition, position);
            continuingPosition = position + 1;
        }
        result[questionMarkCount] = sql.substring(continuingPosition);
        return result;
    }

    public static void closeIfNecessary(ParameterValue parameterValue) {
        if (parameterValue == null || !parameterValue.managed()) {
            return;
        }
        
        Object value = parameterValue.value();
        try {
            if (value instanceof Blob) {
                ((Blob) value).free();
            } else if (value instanceof Clob) {
                ((Clob) value).free();
            } else if (value instanceof AutoCloseable) {
                ((AutoCloseable) value).close();
            }
        } catch (Exception e) {
            // nothing to do
        }
    }

    public static void putVariable(
            MiniSession session, DatabaseProvider provider, String variableName, ParameterValue parameterValue) {
        Object value = parameterValue.value();
        if (value instanceof Blob) {
            putBlob(session, variableName, (Blob) value);
            return;
        } else if (value instanceof BlobClob) {
            putBlob(session, variableName, ((BlobClob) value).getBlob());
            return;
        } else if (value instanceof Clob) {
            throw new IllegalArgumentException("Unknown CLOB type: " + value.getClass());
        }

        String sql = "SET @" + provider.quoteIdentifier(variableName) + " = " + provider.stringifyValue(parameterValue);
        MiniResult result = session.execute(sql);
        if (!result.success()) {
            throw new MiniErrorException(result.error());
        }
    }
    
    public static void putBlob(MiniSession session, String variableName, Blob blob) {
        MiniLargeDataSaveResult result;
        try {
            long length = blob.length();
            result = session.putLargeData(variableName, length, blob.getBinaryStream());
        } catch (SQLException e) {
            throw new UncheckedIOException(new IOException(e));
        }
        if (!result.success()) {
            throw new MiniErrorException(result.error());
        }
    }
    
}
