package name.arbitrary.toytcp;

/**
 * A Buffer is simply a view into a byte array.
 */
public class Buffer {
    private final byte[] data;
    private final int start;
    private final int end;

    public Buffer(byte[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    public byte[] getData() {
        return data;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String format = "%02x";
        for (int i = start; i != end; i++) {
            sb.append(String.format(format, data[i]));
            format = " %02x";
        }
        return sb.toString();
    }
}
