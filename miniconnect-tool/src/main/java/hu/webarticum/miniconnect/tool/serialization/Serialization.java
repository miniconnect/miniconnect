package hu.webarticum.miniconnect.tool.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class Serialization {

    private Serialization() {
        // static utility
    }
    
    
    public static <T extends Serializable> T deserialize(byte[] bytes) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
            try (ObjectInputStream objectIn = new ObjectInputStream(in)) {
                @SuppressWarnings("unchecked")
                T result = (T) objectIn.readObject();
                return result;
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public static byte[] serialize(Serializable object) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (ObjectOutputStream objectOut = new ObjectOutputStream(out)) {
                objectOut.writeObject(object);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
}
