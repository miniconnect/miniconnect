package hu.webarticum.miniconnect.jdbc.blob;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BlobClobTest {

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testSetGetString(BlobClob clob) throws Exception {
        clob.setString(1, "abcdefg");
        assertThat(clob.length()).isEqualTo(7L);
        assertThat(clob.getSubString(1L, 7)).isEqualTo("abcdefg");
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testSetWriter(BlobClob clob) throws Exception {
        Writer writer = clob.setCharacterStream(1L);
        writer.write("uvw");
        writer.close();
        assertThat(clob.length()).isEqualTo(3L);
        assertThat(clob.getSubString(1L, 3)).isEqualTo("uvw");
        
        Writer updateWriter = clob.setCharacterStream(3L);
        updateWriter.write("nmop");
        updateWriter.close();
        assertThat(clob.length()).isEqualTo(6L);
        assertThat(clob.getSubString(1L, 6)).isEqualTo("uvnmop");
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testReader(BlobClob clob) throws Exception {
        clob.setString(1, "abcdefghi");
        assertThat(IOUtils.toString(clob.getCharacterStream())).isEqualTo("abcdefghi");
        assertThat(IOUtils.toString(clob.getCharacterStream(1L, 9L))).isEqualTo("abcdefghi");
        assertThat(IOUtils.toString(clob.getCharacterStream(3L, 4L))).isEqualTo("cdef");
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testStream(BlobClob clob) throws Exception {
        clob.setString(1, "43210");
        Charset targetCharset = clob.getTargetCharset();
        assertThat(clob.getAsciiStream()).hasBinaryContent("43210".getBytes(targetCharset));
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testBlobStream(BlobClob clob) throws Exception {
        clob.setString(1, "uvwxyz");
        Charset blobCharset = clob.getBlobCharset();
        Blob blob = clob.getBlob();
        assertThat(blob.getBinaryStream()).hasBinaryContent("uvwxyz".getBytes(blobCharset));
    }
    
    
    static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of(clobOf(StandardCharsets.ISO_8859_1, 1, StandardCharsets.ISO_8859_1)),
                Arguments.of(clobOf(StandardCharsets.ISO_8859_1, 1, StandardCharsets.UTF_16BE)),
                Arguments.of(clobOf(StandardCharsets.UTF_16BE, 2, StandardCharsets.UTF_8)),
                Arguments.of(clobOf(StandardCharsets.UTF_8, 0, StandardCharsets.UTF_8)),
                Arguments.of(clobOf(StandardCharsets.UTF_8, 0, StandardCharsets.ISO_8859_1)));
    }

    private static BlobClob clobOf(Charset blobCharset, int blobCharWidth, Charset targetCharset) {
        Blob blob = new WriteableBlob();
        return new BlobClob(blob, blobCharset, blobCharWidth, targetCharset);
    }

}
