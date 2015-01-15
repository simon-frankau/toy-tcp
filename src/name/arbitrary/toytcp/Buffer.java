package name.arbitrary.toytcp;

/**
 * A Buffer is simply a view into a byte array.
 */
public class Buffer {
    private final byte[] data;
    private final int start;
    private final int length;

    public Buffer(byte[] data, int start, int length) {
        this.data = data;
        this.start = start;
        this.length = length;
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

    public interface Listener {
        void receive(Buffer buffer);
    }
}
