package hu.webarticum.miniconnect.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.util.data.ByteString;

class PacketReaderTest {
    
    private PacketReader packetReader;

    
    @BeforeEach
    void init() {
        packetReader = new PacketReader();
    }

    @Test
    void testUnexpectedEof() {
        InputStream in = new ByteArrayInputStream(new byte[0]);
        assertThatThrownBy(() -> packetReader.read(in)).isInstanceOf(UncheckedIOException.class);
    }

    @Test
    void testInvalidFirstByte() {
        byte nonMagicByte = TransferConstants.MAGIC_BYTE - 1;
        InputStream in = new ByteArrayInputStream(new byte[] {
                nonMagicByte, 0, 0, 0, 0, 0, 0, 0, 0 });
        assertThatThrownBy(() -> packetReader.read(in)).isInstanceOf(UncheckedIOException.class);
    }

    @Test
    void testEmpty() {
        Packet expected = Packet.of(ByteString.empty(), ByteString.empty());
        InputStream in = new ByteArrayInputStream(new byte[] {
                TransferConstants.MAGIC_BYTE, 0, 0, 0, 0, 0, 0, 0, 0 });
        assertThat(packetReader.read(in)).isEqualTo(expected);
    }

    @Test
    void testNoHeader() {
        Packet expected = Packet.of(ByteString.empty(), ByteString.of("abc"));
        InputStream in = new ByteArrayInputStream(new byte[] {
                TransferConstants.MAGIC_BYTE, 0, 0, 0, 0, 0, 0, 0, 3, 97, 98, 99 });
        assertThat(packetReader.read(in)).isEqualTo(expected);
    }

    @Test
    void testNoPayload() {
        Packet expected = Packet.of(ByteString.of("12"), ByteString.empty());
        InputStream in = new ByteArrayInputStream(new byte[] {
                TransferConstants.MAGIC_BYTE, 0, 0, 0, 2, 49, 50, 0, 0, 0, 0 });
        assertThat(packetReader.read(in)).isEqualTo(expected);
    }

    @Test
    void testFullyFilled() {
        Packet expected = Packet.of(ByteString.of("42"), ByteString.of("xyz"));
        InputStream in = new ByteArrayInputStream(new byte[] {
                TransferConstants.MAGIC_BYTE, 0, 0, 0, 2, 52, 50, 0, 0, 0, 3, 120, 121, 122 });
        assertThat(packetReader.read(in)).isEqualTo(expected);
    }
    
}
