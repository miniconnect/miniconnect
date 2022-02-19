package hu.webarticum.miniconnect.record.lob;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

class FixedCharWidthClobValueTest {

    @Test
    void testEmpty() throws IOException {
        FixedCharWidthClobValue emptyClob = new FixedCharWidthClobValue(
                        new StoredContentAccess(ByteString.empty()),
                        StandardCharsets.US_ASCII,
                        1);
        assertThat(emptyClob.length()).isZero();
        assertThat(emptyClob.get(0L, 0)).isEmpty();
        assertThat(IOUtils.toString(emptyClob.reader())).isEmpty();
    }
    
    @Test
    void testAscii() throws IOException {
        Charset charset = StandardCharsets.US_ASCII;
        FixedCharWidthClobValue asciiClob = new FixedCharWidthClobValue(
                new StoredContentAccess(ByteString.of("abcdefghijk", charset)),
                charset,
                1);
        assertThat(asciiClob.length()).isEqualTo(11L);
        assertThat(asciiClob.get(0L, 4)).isEqualTo("abcd");
        assertThat(asciiClob.get(2L, 3)).isEqualTo("cde");
        assertThat(IOUtils.toString(asciiClob.reader(3L, 3L))).isEqualTo("def");
        assertThat(IOUtils.toString(asciiClob.reader())).isEqualTo("abcdefghijk");
    }

    @Test
    void testUtf16() throws IOException {
        Charset charset = StandardCharsets.UTF_16BE;
        FixedCharWidthClobValue asciiClob = new FixedCharWidthClobValue(
                new StoredContentAccess(ByteString.of("árvíztűrő", charset)),
                charset,
                2);
        assertThat(asciiClob.length()).isEqualTo(9L);
        assertThat(asciiClob.get(0L, 4)).isEqualTo("árví");
        assertThat(asciiClob.get(3L, 2)).isEqualTo("íz");
        assertThat(IOUtils.toString(asciiClob.reader(2L, 3L))).isEqualTo("víz");
        assertThat(IOUtils.toString(asciiClob.reader())).isEqualTo("árvíztűrő");
    }
    
}
