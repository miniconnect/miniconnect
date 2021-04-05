package hu.webarticum.miniconnect.tool.result;

import java.io.Serializable;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.util.data.ImmutableMap;

public class StoredColumnHeader implements MiniColumnHeader, Serializable {

    private static final long serialVersionUID = 1L;


    private final String name;

    private final String type;

    private final ImmutableMap<String, byte[]> properties;


    public StoredColumnHeader(String name,  String type) {
        this(name, type, new ImmutableMap<>());
    }

    public StoredColumnHeader(
            String name,
            String type,
            ImmutableMap<String, byte[]> properties) {

        this.name = name;
        this.type = type;
        this.properties = properties;
    }

    public static StoredColumnHeader of(MiniColumnHeader columnHeader) {
        return new StoredColumnHeader(
                columnHeader.name(),
                columnHeader.type(),
                columnHeader.properties());
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public ImmutableMap<String, byte[]> properties() {
        return properties;
    }

}
