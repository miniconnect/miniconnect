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
    

    private static final JavaTranslator INSTANCE = new JavaTranslator();
    
    
    private JavaTranslator() {
        // singleton
    }
    
    public static JavaTranslator instance() {
        return INSTANCE;
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
        try (ObjectInputStream in = new ObjectInputStream(contentAccess.inputStream())) {
            return in.readObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public MiniContentAccess encode(Object value) {
        return DynamicContentAccessBuilder.open()
                .writing(out -> new ObjectOutputStream(out).writeObject(value))
                .build();
    }
    
}
