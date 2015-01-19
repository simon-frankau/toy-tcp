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

    public void put(int i, byte value) {
        data[start + i] = value;
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
