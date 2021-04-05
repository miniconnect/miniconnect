package hu.webarticum.miniconnect.tool.lab.dummy;

public final class QueryUtil {
    
    private QueryUtil() {
        // static class
    }


    public static String unescapeIdentifier(String identifier) {
        char firstChar = identifier.charAt(0);
        if (firstChar != '`' && firstChar != '"') {
            return identifier;
        }
        
        String innerPart = identifier.substring(1, identifier.length() - 1);
        return innerPart; // TODO
    }

}
