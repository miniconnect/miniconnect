package hu.webarticum.miniconnect.rdmsframework.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqlUtil {
    
    // FIXME/TODO: regexbee?
    
    private static final Pattern TO_QUOTE_IDENTIFIER_PATTERN = Pattern.compile(
            "[\"\\\\]");

    private static final Pattern TO_QUOTE_STRING_PATTERN = Pattern.compile(
            "[\'\\\\]");

    private static final Pattern TO_UNQUOTE_PATTERN = Pattern.compile(
            "\\\\(.)");
    

    private SqlUtil() {
        // utility class
    }
    
    
    // FIXME: context dependent quoting?
    
    public static String quoteIdentifier(String identifier) {
        Matcher matcher = TO_QUOTE_IDENTIFIER_PATTERN.matcher(identifier);
        return "\"" + matcher.replaceAll("\\\\$0") + "\"";
    }
    
    public static String unquoteIdentifier(String quotedIdentifier) {
        return unquote(quotedIdentifier);
    }

    public static String unbacktickIdentifier(String backtickedIdentifier) {
        int length = backtickedIdentifier.length();
        return backtickedIdentifier.substring(1, length - 1).replace("``", "`");
    }

    public static String quoteString(String string) {
        Matcher matcher = TO_QUOTE_STRING_PATTERN.matcher(string);
        return "'" + matcher.replaceAll("\\\\$0") + "'";
    }

    public static String unquoteString(String stringLiteral) {
        return unquote(stringLiteral);
    }
    
    private static String unquote(String token) {
        int length = token.length();
        String innerPart = token.substring(1, length - 1);
        Matcher matcher = TO_UNQUOTE_PATTERN.matcher(innerPart);
        return matcher.replaceAll("$1");
    }

}
