package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;

/**
 * Created by sgf on 23/01/2015.
 */
public class OptionBad implements Option {
    private final byte type;
    private final Buffer buffer;

    public OptionBad(byte type, Buffer buffer) {
        this.type = type;
        this.buffer = buffer;
    }

    @Override
    public String toString() {
        return "OptionBad{" +
                "type=" + type +
                ", buffer=" + buffer +
                '}';
    }
}
