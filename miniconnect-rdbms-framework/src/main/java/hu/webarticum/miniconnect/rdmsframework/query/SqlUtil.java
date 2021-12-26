package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqlUtil {
    
    // TODO: regexbee?
    private static final Pattern TO_QUOTE_PATTERN = Pattern.compile(
            "[\"\\\\]");

    private static final Pattern TO_UNQUOTE_PATTERN = Pattern.compile(
            "\\\\(.)");
    

    private SqlUtil() {
        // utility class
    }
    
    
    // FIXME: context dependent?
    public static String quoteIdentifier(String identifier) {
        Matcher matcher = TO_QUOTE_PATTERN.matcher(identifier);
        return "\"" + matcher.replaceAll("\\\\$0") + "\"";
    }
    
    public static String unquoteIdentifier(String quotedIdentifier) {
        int length = quotedIdentifier.length();
        String innerPart = quotedIdentifier.substring(1, length - 1);
        Matcher matcher = TO_UNQUOTE_PATTERN.matcher(innerPart);
        return matcher.replaceAll("$1");
    }

    public static String unbacktickIdentifier(String backtickedIdentifier) {
        int length = backtickedIdentifier.length();
        return backtickedIdentifier.substring(1, length - 1).replace("``", "`");
    }
    
}
