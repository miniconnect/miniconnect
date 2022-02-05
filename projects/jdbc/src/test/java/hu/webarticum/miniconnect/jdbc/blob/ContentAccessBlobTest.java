package hu.webarticum.miniconnect.jdbc.blob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Blob;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;

class ContentAccessBlobTest {

    @Test
    void testContents() throws Exception {
        MiniContentAccess contentAccess = new StoredContentAccess(ByteString.of("abcdefghijk"));
        Blob blob = new ContentAccessBlob(contentAccess);

        assertThat(blob.length()).isEqualTo(11L);
        assertThat(blob.getBytes(1L, 11)).containsExactly(ByteString.of("abcdefghijk").extract());
        assertThat(blob.getBytes(2L, 3)).containsExactly(ByteString.of("bcd").extract());
        assertThat(blob.getBinaryStream()).hasBinaryContent(ByteString.of("abcdefghijk").extract());
        assertThat(blob.getBinaryStream(3L, 4L)).hasBinaryContent(
                ByteString.of("cdef").extract());
    }

    @Test
    void testReadOnly() throws Exception {
        MiniContentAccess contentAccess = new StoredContentAccess(ByteString.of("abcdefghijk"));
        Blob blob = new ContentAccessBlob(contentAccess);
        
        assertThatThrownBy(() -> blob.setBytes(1, ByteString.of("xyz").extract()))
                .isInstanceOf(SQLException.class);
        assertThatThrownBy(() -> blob.setBytes(1, ByteString.of("xyz").extract(), 1, 2))
                .isInstanceOf(SQLException.class);
        assertThatThrownBy(() -> blob.setBinaryStream(1)).isInstanceOf(SQLException.class);
        assertThatThrownBy(() -> blob.truncate(3L)).isInstanceOf(SQLException.class);
    }

}
