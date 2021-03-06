package name.arbitrary.toytcp;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * A Buffer is simply a view into a byte array.
 */
public final class Buffer {
    private final byte[] data;
    private final int start;
    private final int length;

    public Buffer(byte[] data, int start, int length) {
        this.data = data;
        this.start = start;
        this.length = length;
    }

    public Buffer(int... data) {
        this.data = new byte[data.length];
        for (int i = 0 ; i < data.length; i++) {
            this.data[i] = (byte)data[i];
        }
        this.start = 0;
        this.length = data.length;
    }

    public byte get(int i) {
        return data[start + i];
    }

    public int getU8(int i) {
        return 0xFF & (int)data[start + i];
    }

    public int getU16(int i) {
        return getU8(i) << 8 | getU8(i+1);
    }

    public int getS32(int i) {
        return getU8(i) << 24 | getU8(i + 1) << 16 | getU8(i + 2) << 8 | getU8(i + 3);
    }

    // TODO: Should 'put' be on a standard buffer?

    public void put(int i, byte value) {
        data[start + i] = value;
    }

    public void putU8(int i, int value) {
        put(i, (byte)value);
    }

    public void putU16(int i, int value) {
        put(i, (byte)(value & 0xFF));
        put(i, (byte)(value >> 8));
    }

    public int length() {
        return length;
    }

    public Buffer getSubBuffer(int offset) {
        return new Buffer(data, start + offset, length - offset);
    }

    public Buffer getSubBuffer(int offset, int length) {
        return new Buffer(data, start + offset, length);
    }

    // Sub-buffer construction shares the underlying data. deepCopy removes the sharing.
    public Buffer deepCopy() {
        return new Buffer(Arrays.copyOfRange(data, start, start + length), 0, length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String format = "%02x";
        int end = start + length;
        for (int i = start; i != end; i++) {
            sb.append(String.format(format, data[i]));
            format = " %02x";
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Buffer buffer = (Buffer) o;

        if (length != buffer.length) return false;

        // Equality only covers the part of the buffer we're interested in,
        // not how it's encapsulated.
        for (int i = 0; i < length; i++) {
            if (buffer.get(i) != get(i)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (int i = 0; i < length; i++) {
            result = 31 * get(i) + result;
        }
        result = 31 * result + length;
        return result;
    }

    public interface Listener {
        void receive(Buffer buffer);
    }
}
