package hu.webarticum.miniconnect.jdbc.blob;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WriteableBlobTest {

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testSetGetBytes(WriteableBlob blob) throws Exception {
        Charset charset = StandardCharsets.ISO_8859_1;
        String content = "abcdef";
        
        blob.setBytes(1, content.getBytes(charset));

        assertThat(blob.length()).isEqualTo(6L);
        assertThat(blob.getBytes(1L, 6)).asString(charset).isEqualTo("abcdef");
        assertThat(blob.getBytes(1L, 3)).asString(charset).isEqualTo("abc");
        assertThat(blob.getBytes(3L, 2)).asString(charset).isEqualTo("cd");
        
        blob.free();
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testUpdate(WriteableBlob blob) throws Exception {
        Charset charset = StandardCharsets.ISO_8859_1;
        String content = "abcdef";
        String updateContent = "1234567";
        
        blob.setBytes(1, content.getBytes(charset));
        blob.setBytes(4, updateContent.getBytes(charset));

        assertThat(blob.length()).isEqualTo(10L);
        assertThat(blob.getBytes(1L, 10)).asString(charset).isEqualTo("abc1234567");

        blob.free();
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testSetAndUpdateStream(WriteableBlob blob) throws Exception {
        Charset charset = StandardCharsets.ISO_8859_1;
        String content = "abcdef";
        String updateContent = "1234567";
        
        blob.setBinaryStream(1).write(content.getBytes(charset));

        assertThat(blob.length()).isEqualTo(6L);
        assertThat(blob.getBytes(1L, 6)).asString(charset).isEqualTo("abcdef");

        blob.setBinaryStream(5).write(updateContent.getBytes(charset));

        assertThat(blob.length()).isEqualTo(11L);
        assertThat(blob.getBytes(1L, 11)).asString(charset).isEqualTo("abcd1234567");

        blob.free();
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testGetStream(WriteableBlob blob) throws Exception {
        Charset charset = StandardCharsets.ISO_8859_1;
        String content = "abcdef";

        blob.setBytes(1, content.getBytes(charset));

        assertThat(blob.getBinaryStream()).hasBinaryContent("abcdef".getBytes(charset));
        assertThat(blob.getBinaryStream()).hasBinaryContent("abcdef".getBytes(charset));

        assertThat(blob.getBinaryStream(2, 3)).hasBinaryContent("bcd".getBytes(charset));
        assertThat(blob.getBinaryStream(2, 3)).hasBinaryContent("bcd".getBytes(charset));
        
        blob.free();
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    void testTruncate(WriteableBlob blob) throws Exception {
        Charset charset = StandardCharsets.ISO_8859_1;
        String content = "abcdef";
        
        blob.setBytes(1, content.getBytes(charset));
        blob.truncate(4);

        assertThat(blob.length()).isEqualTo(4L);
        assertThat(blob.getBytes(1L, 4)).asString(charset).isEqualTo("abcd");

        blob.free();
    }


    static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of(new WriteableBlob()),
                Arguments.of(new WriteableBlob(true)));
    }
    
}
