package hu.webarticum.miniconnect.record.customvalue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.miniconnect.record.custom.CustomValue;

class CustomValueTest {

    @Test
    void testHashCodeAndEquals() {
        CustomValue instance1 = new CustomValue("lorem");
        CustomValue instance2 = new CustomValue("ipsum");
        CustomValue instance3 = new CustomValue("lorem");

        assertThat(instance1)
                .hasSameHashCodeAs(instance3)
                .isNotEqualTo(instance2)
                .isEqualTo(instance3);
        assertThat(instance2)
                .isNotEqualTo(instance3);
    }
    
    @Test
    void testCustomValueBinding() {
        ImmutableMap<String, Object> data = ImmutableMap.of(
                "hello", ImmutableList.of("hello", "world"),
                "sub", ImmutableMap.of(
                        "lorem", "DOLOR",
                        "ipsum", 3));
        CustomValue customValue = new CustomValue(data);
        ObjectMapper mapper = new ObjectMapper();
        MappableValue actual = mapper.convertValue(customValue, MappableValue.class);
        MappableValue expected = new MappableValue(
                new ArrayList<>(Arrays.asList("hello", "world")),
                new MappableSubValue("DOLOR", 3));
        assertThat(actual).isEqualTo(expected);
    }
    
    
    private static class MappableValue {

        private final List<String> hello;
        
        private final MappableSubValue sub;
        
        
        @JsonCreator
        public MappableValue(
                @JsonProperty("hello") List<String> hello,
                @JsonProperty("sub") MappableSubValue sub) {
            this.hello = new ArrayList<>(hello);
            this.sub = sub;
        }
        
        
        @Override
        public int hashCode() {
            return Objects.hash(hello, sub);
        }
        
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof MappableValue)) {
                return false;
            }
            
            MappableValue otherValue = (MappableValue) other;
            return
                    hello.equals(otherValue.hello) &&
                    sub.equals(otherValue.sub);
        }
        
        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("hello", hello)
                    .add("sub", sub)
                    .build();
        }
        
    }
    
    private static class MappableSubValue {
        
        private final String lorem;
        
        private final int ipsum;
        

        @JsonCreator
        public MappableSubValue(
                @JsonProperty("lorem") String lorem,
                @JsonProperty("ipsum") int ipsum) {
            this.lorem = lorem;
            this.ipsum = ipsum;
        }
        
        
        @Override
        public int hashCode() {
            return Objects.hash(lorem, ipsum);
        }
        
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (other == null) {
                return false;
            } else if (!(other instanceof MappableSubValue)) {
                return false;
            }
            
            MappableSubValue otherSubValue = (MappableSubValue) other;
            return
                    lorem.equals(otherSubValue.lorem) &&
                    ipsum == otherSubValue.ipsum;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("lorem", lorem)
                    .add("ipsum", ipsum)
                    .build();
        }
        
    }
    
}
