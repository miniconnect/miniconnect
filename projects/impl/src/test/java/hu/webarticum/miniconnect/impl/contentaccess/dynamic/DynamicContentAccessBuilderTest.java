package hu.webarticum.miniconnect.impl.contentaccess.dynamic;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.contentaccess.dynamic.DynamicContentAccessBuilder.DynamicContentAccessFinalBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

class DynamicContentAccessBuilderTest {
    
    @Test
    void testNoWrite() {
        MiniContentAccess contentAccess = DynamicContentAccessBuilder.open()
                .writing(out -> {})
                .build();
        assertThat(contentAccess.length()).isZero();
    }

    @Test
    void testSimpleWrite() {
        MiniContentAccess contentAccess = DynamicContentAccessBuilder.open()
                .writing(out -> out.write("lorem ipsum".getBytes(StandardCharsets.UTF_8)))
                .build();
        assertThat(contentAccess.get().toString(StandardCharsets.UTF_8)).isEqualTo("lorem ipsum");
    }

    @Test
    void testConcurrentWriting() {
        DynamicContentAccessBuilder builderBase = DynamicContentAccessBuilder.open();
        MiniContentAccess contentAccess1 = builderBase
                .writing(out -> out.write("aaaaa".getBytes(StandardCharsets.UTF_8)))
                .build();
        MiniContentAccess contentAccess2 = builderBase
                .writing(out -> out.write("bbbbb".getBytes(StandardCharsets.UTF_8)))
                .build();
        assertThat(contentAccess1.get().toString(StandardCharsets.UTF_8)).isEqualTo("aaaaa");
        assertThat(contentAccess2.get().toString(StandardCharsets.UTF_8)).isEqualTo("bbbbb");
    }

    @Test
    void testException() {
        DynamicContentAccessFinalBuilder builderBase = DynamicContentAccessBuilder.open()
                .writing(out -> { throw new IOException(); });
        assertThatThrownBy(() -> builderBase.build()).isInstanceOf(UncheckedIOException.class);
    }

}