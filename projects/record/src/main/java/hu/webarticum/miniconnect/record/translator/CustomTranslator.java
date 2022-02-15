package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.contentaccess.dynamic.DynamicContentAccessBuilder;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.custom.CustomValue;
import hu.webarticum.miniconnect.record.custom.schema.AnySchema;
import hu.webarticum.miniconnect.record.custom.schema.Schema;

public class CustomTranslator implements ValueTranslator {
    
    private static final String SCHEMA_KEY = "schema";
    
    
    private final Schema schema;
    

    private CustomTranslator(Schema schema) {
        this.schema = schema;
    }

    public static CustomTranslator of(ImmutableMap<String, ByteString> properties) {
        Schema schema;
        if (properties.containsKey(SCHEMA_KEY)) {
            schema = Schema.readFrom(properties.get(SCHEMA_KEY).inputStream());
        } else {
            schema = AnySchema.instance();
        }
        return new CustomTranslator(schema);
    }
    

    @Override
    public int length() {
        return MiniValueDefinition.DYNAMIC_SIZE;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        Object dynamicValue = schema.readValueFrom(contentAccess.inputStream());
        return new CustomValue(dynamicValue);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        return DynamicContentAccessBuilder.open()
                .writing(out -> schema.writeValueTo(value, out))
                .build();
    }
    
}
