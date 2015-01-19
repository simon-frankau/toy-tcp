package name.arbitrary.toytcp;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class BufferTest {
    private static final byte[] example = { 99, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 99 };

    private Buffer buffer;

    @Before
    public void setup() {
        // Use a view into the middle of the example buffer.
        buffer = new Buffer(Arrays.copyOf(example, example.length), 1, 10);
    }

    @Test
    public void testGet() {
        assertEquals(15, buffer.get(5));
    }

    @Test
    public void testPut() {
        buffer.put(1, (byte)42);
        assertEquals(42, buffer.get(1));
    }

    @Test
    public void testLength() {
        assertEquals(10, buffer.length());
    }

    @Test
    public void testGetSubBufferChangingStart() {
        Buffer subBuf = buffer.getSubBuffer(3);
        assertEquals(subBuf.get(4), buffer.get(4 + 3));
        assertEquals(subBuf.length(), buffer.length() - 3);
    }

    @Test
    public void testGetSubBufferChangingStartAndLength() {
        Buffer subBuf = buffer.getSubBuffer(3, 6);
        assertEquals(subBuf.get(4), buffer.get(4 + 3));
        assertEquals(subBuf.length(), 6);
    }

    @Test
    public void testEqualsAndHashCodeSucceed() {
        Buffer buffer2 = new Buffer(10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
        assertEquals(buffer, buffer2);
        assertEquals(buffer.hashCode(), buffer2.hashCode());
    }

    @Test
    public void testEqualsAndHashCodeFailIfLengthsDiffer() {
        Buffer buffer2 = new Buffer(10, 11, 12, 13, 14, 15, 16, 17, 18);
        assertNotEquals(buffer, buffer2);
        assertNotEquals(buffer.hashCode(), buffer2.hashCode());
    }

    @Test
    public void testEqualsAndHashCodeFailIfValuesDiffer() {
        Buffer buffer2 = new Buffer(10, 11, 12, 13, 14, 15, 16, 17, 18, 20);
        assertNotEquals(buffer, buffer2);
        assertNotEquals(buffer.hashCode(), buffer2.hashCode());
    }

    @Test
    public void testDeepCopyIsEqual() {
        Buffer buffer2 = buffer.deepCopy();
        assertEquals(buffer, buffer2);
    }

    @Test
    public void testDeepCopyDoesNotShowUpdates() {
        Buffer buffer2 = buffer.deepCopy();
        buffer.put(0, (byte)180);
        assertNotEquals(buffer, buffer2);
    }

    @Test
    public void testPutVisibleAcrossShallowCopies() {
        Buffer buffer2 = buffer.getSubBuffer(0);
        buffer.put(0, (byte)180);
        assertEquals(buffer, buffer2);
    }
}
