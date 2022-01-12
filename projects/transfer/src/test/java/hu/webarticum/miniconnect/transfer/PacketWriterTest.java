package hu.webarticum.miniconnect.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.webarticum.miniconnect.util.data.ByteString;

class PacketWriterTest {
    
    private PacketWriter packetWriter;

    
    @BeforeEach
    void init() {
        packetWriter = new PacketWriter();
    }

    @Test
    void testEmpty() {
        Packet packet = Packet.of(ByteString.empty(), ByteString.empty());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        packetWriter.write(packet, out);
        assertThat(out.toByteArray()).containsExactly(21, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    void testNoHeader() {
        Packet packet = Packet.of(ByteString.empty(), ByteString.of("lorem"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        packetWriter.write(packet, out);
        assertThat(out.toByteArray()).containsExactly(
                21,
                0, 0, 0, 0,
                0, 0, 0, 5,
                108, 111, 114, 101, 109);
    }

    @Test
    void testNoPayload() {
        Packet packet = Packet.of(ByteString.of("24"), ByteString.empty());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        packetWriter.write(packet, out);
        assertThat(out.toByteArray()).containsExactly(
                21,
                0, 0, 0, 2,
                50, 52,
                0, 0, 0, 0);
    }

    @Test
    void testFullyFilled() {
        Packet packet = Packet.of(ByteString.of("9"), ByteString.of("sit"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        packetWriter.write(packet, out);
        assertThat(out.toByteArray()).containsExactly(
                21,
                0, 0, 0, 1,
                57,
                0, 0, 0, 3,
                115, 105, 116);
    }
    
}
