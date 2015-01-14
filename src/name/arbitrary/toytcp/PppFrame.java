package name.arbitrary.toytcp;

import java.util.Arrays;

/**
 * TODO: A PPP frame. Currently just able to show contents...
 */
public class PppFrame {
    private final byte[] data;
    private final int start;
    private final int end;

    public PppFrame(byte[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
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
