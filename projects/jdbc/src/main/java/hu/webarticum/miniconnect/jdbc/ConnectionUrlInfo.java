package hu.webarticum.miniconnect.jdbc;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.regexbee.Bee;
import hu.webarticum.regexbee.BeeFragment;

public class ConnectionUrlInfo {
    
    public static final String URL_PREFIX = "jdbc:miniconnect://";
    
    
    private static final String HOST_GROUPNAME = "host";

    private static final String PORT_GROUPNAME = "port";

    private static final String SCHEMA_GROUPNAME = "schema";

    private static final String PROPERTIES_GROUPNAME = "properties";

    private static final String KEY_GROUPNAME = "key";

    private static final String VALUE_GROUPNAME = "value";
    
    private static final Pattern URL_PROPERTY_GROUPED_PATTERN = Bee
            .then(Bee.checked("[^&=]+").as(KEY_GROUPNAME))
            .then(Bee.fixedChar('='))
            .then(Bee.checked("[^&]*").as(VALUE_GROUPNAME))
            .toPattern();
    
    private static final BeeFragment URL_PROPERTY_FRAGMENT = Bee
            .then(Bee.checked("[^&=]+"))
            .then(Bee.fixedChar('='))
            .then(Bee.checked("[^&]*"));
    
    private static final Pattern URL_PATTERN = Bee
            .then(Bee.BEGIN)
            .then(Bee.fixed(URL_PREFIX))
            .then(Bee.checked("[^:/]+").as(HOST_GROUPNAME))
            .then(Bee.fixedChar(':')
                    .then(Bee.UNSIGNED_INT.as(PORT_GROUPNAME)))
            .then(Bee.fixedChar('/')
                    .then(Bee.checked("[^/\\?]+").as(SCHEMA_GROUPNAME))
                    .then(Bee.fixedChar('/').optional())
                    .optional())
            .then(Bee.fixedChar('?').then(
                    URL_PROPERTY_FRAGMENT
                            .then(Bee.fixedChar('&').then(URL_PROPERTY_FRAGMENT).any())
                            .as(PROPERTIES_GROUPNAME)
                            .optional()
                    ).optional())
            .then(Bee.END)
            .toPattern();
    

    private final String host;
    
    private final int port;

    private final String schema;

    private final ImmutableMap<String, String> properties;
    

    public static boolean isUrlSupported(String url) {
        return url.startsWith(URL_PREFIX);
    }
    

    private ConnectionUrlInfo(String host, int port, String schema, ImmutableMap<String, String> properties) {
        this.host = host;
        this.port = port;
        this.schema = schema;
        this.properties = properties;
    }
    
    
    public static ConnectionUrlInfo parse(String url) {
        return parse(url, null);
    }
    
    public static ConnectionUrlInfo parse(String url, Map<?, ?> properties) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid connection url");
        }
        
        String host = matcher.group(HOST_GROUPNAME);
        int port = Integer.parseInt(matcher.group(PORT_GROUPNAME));
        String schema = matcher.group(SCHEMA_GROUPNAME);
        String propertiesPart = matcher.group(PROPERTIES_GROUPNAME);
        Map<String, String> propertiesBuilder = new HashMap<>();
        if (properties != null) {
            properties.forEach((k, v) -> propertiesBuilder.put(k.toString(), v.toString()));
        }
        if (propertiesPart != null) {
            Matcher propertyMatcher = URL_PROPERTY_GROUPED_PATTERN.matcher(propertiesPart);
            while (propertyMatcher.find()) {
                String key = urldecode(propertyMatcher.group(KEY_GROUPNAME));
                String value = urldecode(propertyMatcher.group(VALUE_GROUPNAME));
                propertiesBuilder.put(key, value);
            }
        }
        return new ConnectionUrlInfo(host, port, schema, ImmutableMap.fromMap(propertiesBuilder));
    }
    
    private static String urldecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // could not be occured
            return value;
        }
    }
    

    public String host() {
        return host;
    }
    
    public int port() {
        return port;
    }
    
    public String schema() {
        return schema;
    }

    public ImmutableMap<String, String> properties() {
        return properties;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("host", host)
                .add("port", port)
                .add("schema", schema)
                .add("properties", properties)
                .build();
    }
    
}
