package hu.webarticum.miniconnect.record.lob;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

class DynamicCharWidthClobValueTest {

    @Test
    void testEmpty() throws IOException {
        DynamicCharWidthClobValue emptyClob = new DynamicCharWidthClobValue(
                        new StoredContentAccess(ByteString.empty()),
                        StandardCharsets.UTF_8);
        assertThat(emptyClob.length()).isZero();
        assertThat(emptyClob.get(0L, 0)).isEmpty();
        assertThat(IOUtils.toString(emptyClob.reader())).isEmpty();
    }
    
    @Test
    void testLength() throws IOException {
        DynamicCharWidthClobValue utf8Clob = createUtf8Clob("árvíztűrő");
        assertThat(utf8Clob.length()).isEqualTo(9L);
    }

    @Test
    void testGetFromStart() throws IOException {
        DynamicCharWidthClobValue utf8Clob = createUtf8Clob("árvíztűrő");
        assertThat(utf8Clob.get(0L, 4)).isEqualTo("árví");
    }

    @Test
    void testGetInMiddle() throws IOException {
        DynamicCharWidthClobValue utf8Clob = createUtf8Clob("árvíztűrő");
        assertThat(utf8Clob.get(3L, 2)).isEqualTo("íz");
    }

    @Test
    void testReaderInMiddle() throws IOException {
        DynamicCharWidthClobValue utf8Clob = createUtf8Clob("árvíztűrő");
        assertThat(IOUtils.toString(utf8Clob.reader(2L, 3L))).isEqualTo("víz");
    }

    @Test
    void testFullReader() throws IOException {
        DynamicCharWidthClobValue utf8Clob = createUtf8Clob("árvíztűrő");
        assertThat(IOUtils.toString(utf8Clob.reader())).isEqualTo("árvíztűrő");
    }
    
    @Test
    void testContentInSequence() throws IOException {
        DynamicCharWidthClobValue utf8Clob = createUtf8Clob("árvíztűrő");
        assertThat(utf8Clob.length()).isEqualTo(9L);
        assertThat(utf8Clob.get(0L, 4)).isEqualTo("árví");
        assertThat(utf8Clob.get(3L, 2)).isEqualTo("íz");
        assertThat(IOUtils.toString(utf8Clob.reader(2L, 3L))).isEqualTo("víz");
        assertThat(IOUtils.toString(utf8Clob.reader())).isEqualTo("árvíztűrő");
    }
    
    private DynamicCharWidthClobValue createUtf8Clob(String content) {
        return new DynamicCharWidthClobValue(
                new StoredContentAccess(ByteString.of("árvíztűrő", StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8,
                3);
    }
    
}
