package hu.webarticum.miniconnect.rdmsframework.util;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: extend functionality, add escape support (XXX: escaped sql string literals?)
public class LikeMatcher implements Predicate<String> {
    
    private static final Pattern WILDCARD_PATTERN = Pattern.compile(
            "(?<fixed>.*?)(?:(?<single>_)|(?<any>%)|$)");
    
    private static final String FIXED_GROUPNAME = "fixed";
    
    private static final String SINGLE_GROUPNAME = "single";
    
    private static final String ANY_GROUPNAME = "any";
    

    private final Pattern pattern;
    
    
    public LikeMatcher(String likePattern) {
        StringBuilder patternBuilder = new StringBuilder();
        Matcher matcher = WILDCARD_PATTERN.matcher(likePattern);
        while (matcher.find()) {
            String fixedPart = matcher.group(FIXED_GROUPNAME);
            patternBuilder.append(Pattern.quote(fixedPart));
            if (matcher.group(SINGLE_GROUPNAME) != null) {
                patternBuilder.append(".");
            } else if (matcher.group(ANY_GROUPNAME) != null) {
                patternBuilder.append(".*");
            }
        }
        pattern = Pattern.compile(patternBuilder.toString());
    }


    @Override
    public boolean test(String value) {
        return pattern.matcher(value).matches();
    }
    
}
