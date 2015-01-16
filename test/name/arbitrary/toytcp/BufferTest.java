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

    // Utility function for tests.
    public void checkBuffer(Buffer buffer, int... values) {
        assertEquals(buffer.length(), values.length);
        for (int i = 0; i < values.length; i++) {
            assertEquals(buffer.get(i), values[i]);
        }
    }

    @Test
    public void testCheckBufferSucceedsWithRightData() {
        checkBuffer(buffer, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19);
    }
}
