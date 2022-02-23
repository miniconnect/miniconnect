package hu.webarticum.miniconnect.record.custom;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.util.ToStringBuilder;

public class CustomValue {
    
    private final Object value;
    
    
    public CustomValue(Object value) {
        this.value = value;
    }
    

    public Object get() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (!(other instanceof CustomValue)) {
            return false;
        }
        
        CustomValue otherCustomValue = (CustomValue) other;
        return Objects.equals(value, otherCustomValue.value);
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("value", value)
                .build();
    }
    
    @JsonValue
    public Object getForJson() {
        return mapForJson(value);
    }
    
    private Object mapForJson(Object value) {
        if (value instanceof ImmutableList) {
            return ((ImmutableList<?>) value).map(this::mapForJson).toArrayList();
        } else if (value instanceof ImmutableMap) {
            return ((ImmutableMap<?, ?>) value).map(this::mapForJson, this::mapForJson).toHashMap();
        } else {
            return value;
        }
    }
    
}
