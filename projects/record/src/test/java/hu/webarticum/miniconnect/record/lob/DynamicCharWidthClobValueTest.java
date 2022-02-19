package hu.webarticum.miniconnect.record.lob;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

// TODO
class DynamicCharWidthClobValueTest {

    @Test
    void testEmpty() throws IOException {
        DynamicCharWidthClobValue emptyClob = new DynamicCharWidthClobValue(
                        new StoredContentAccess(ByteString.empty()),
                        StandardCharsets.UTF_8);
        assertThat(emptyClob.length()).isZero();
        //assertThat(emptyClob.get(0L, 0)).isEmpty();
        //assertThat(IOUtils.toString(emptyClob.reader())).isEmpty();
    }
    
    @Test
    void testContent() throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        DynamicCharWidthClobValue emptyClob = new DynamicCharWidthClobValue(
                        new StoredContentAccess(ByteString.of("árvíztűrő", charset)),
                        charset);
        //assertThat(asciiClob.length()).isEqualTo(9L);
        //assertThat(asciiClob.get(0L, 4)).isEqualTo("árví");
        //assertThat(asciiClob.get(3L, 2)).isEqualTo("íz");
        //assertThat(IOUtils.toString(asciiClob.reader(2L, 3L))).isEqualTo("víz");
        //assertThat(IOUtils.toString(asciiClob.reader())).isEqualTo("árvíztűrő");
    }
    
}
