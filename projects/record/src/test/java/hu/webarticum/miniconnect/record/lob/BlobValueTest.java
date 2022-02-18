package hu.webarticum.miniconnect.record.lob;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

class BlobValueTest {

    @Test
    void testEmpty() {
        BlobValue blobValue = new BlobValue(new StoredContentAccess(ByteString.empty()));
        assertThat(blobValue.length()).isZero();
        assertThat(blobValue.inputStream()).isEmpty();
    }

    @Test
    void testFullContent() {
        BlobValue blobValue = new BlobValue(new StoredContentAccess(ByteString.of("abcdefghi")));
        assertThat(blobValue.length()).isEqualTo(9L);
        assertThat(blobValue.get(0L, 9)).isEqualTo(ByteString.of("abcdefghi"));
        assertThat(blobValue.inputStream()).hasBinaryContent(ByteString.of("abcdefghi").extract());
    }

    @Test
    void testContentPart() {
        BlobValue blobValue = new BlobValue(new StoredContentAccess(ByteString.of("12345678")));
        assertThat(blobValue.get(0L, 3)).isEqualTo(ByteString.of("123"));
        assertThat(blobValue.get(2L, 5)).isEqualTo(ByteString.of("34567"));
        assertThat(blobValue.inputStream(0L, 4L)).hasBinaryContent(ByteString.of("1234").extract());
        assertThat(blobValue.inputStream(5L, 3L)).hasBinaryContent(ByteString.of("678").extract());
    }
    
}
