package hu.webarticum.miniconnect.record.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.lang.ToStringBuilder;
import hu.webarticum.miniconnect.record.custom.CustomValue;

class DefaultConverterTest {

    @Test
    void testCustomMapping() {
        ImmutableMap<String, Object> data = ImmutableMap.of(
                "lorem", "dolor",
                "ipsum", 9);
        CustomValue customValue = new CustomValue(data);
        Converter converter = new DefaultConverter();
        Object actual = converter.convert(customValue, SimpleMappableValue.class);
        assertThat(actual).isEqualTo(new SimpleMappableValue("dolor", 9));
    }
    

    private static class SimpleMappableValue {

        private final String lorem;
        
        private final int ipsum;


        @JsonCreator
        public SimpleMappableValue(
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
            } else if (!(other instanceof SimpleMappableValue)) {
                return false;
            }
            
            SimpleMappableValue otherSimpleMappableValue = (SimpleMappableValue) other;
            return
                    lorem.equals(otherSimpleMappableValue.lorem) &&
                    ipsum == otherSimpleMappableValue.ipsum;
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
