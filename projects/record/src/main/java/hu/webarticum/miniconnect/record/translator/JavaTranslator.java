package hu.webarticum.miniconnect.record.translator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.contentaccess.dynamic.DynamicContentAccessBuilder;

public class JavaTranslator implements ValueTranslator {

    public static final String NAME = "JAVA"; // NOSONAR same name is OK
    

    private static final JavaTranslator UNBOUND_INSTANCE = new JavaTranslator(null);
    
    
    private final String assuredClazzName;
    

    private JavaTranslator(String assuredClazzName) {
        this.assuredClazzName = assuredClazzName;
    }
    
    public static JavaTranslator unboundInstance() {
        return UNBOUND_INSTANCE;
    }

    public static JavaTranslator of(String assuredClazzName) {
        return new JavaTranslator(assuredClazzName);
    }

    public static JavaTranslator of(Class<?> assuredClazz) {
        return new JavaTranslator(assuredClazz.getName());
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return MiniValueDefinition.DYNAMIC_LENGTH;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        Object value;
        try (ObjectInputStream in = new ObjectInputStream(contentAccess.inputStream())) {
            value = in.readObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        checkAssuredType(value);
        return value;
    }

    @Override
    public MiniContentAccess encode(Object value) {
        checkAssuredType(value);
        return DynamicContentAccessBuilder.open()
                .writing(out -> new ObjectOutputStream(out).writeObject(value))
                .build();
    }
    
    private void checkAssuredType(Object value) {
        if (assuredClazzName == null && value == null) {
            return;
        }

        Class<?> expectedClazz;
        try {
            expectedClazz = Class.forName(assuredClazzName);
        } catch (ClassNotFoundException e) {
            throw new UncheckedIOException("Unknown type: " + assuredClazzName, new IOException(e));
        }
        
        Class<?> actualClazz = value.getClass();
        if (expectedClazz.isAssignableFrom(actualClazz)) {
            throw new IllegalArgumentException(String.format(
                    "Unexpected type: %s (expected superclass: %s)",
                    actualClazz.getName(),
                    assuredClazzName));
        }
    }

    @Override
    public String assuredClazzName() {
        return assuredClazzName == null ? Object.class.getName() : assuredClazzName;
    }
    
}
