package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;

/**
 * Created by sgf on 23/01/2015.
 */
public final class OptionBad implements Option {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionBad optionBad = (OptionBad) o;

        if (type != optionBad.type) return false;
        if (buffer != null ? !buffer.equals(optionBad.buffer) : optionBad.buffer != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) type;
        result = 31 * result + (buffer != null ? buffer.hashCode() : 0);
        return result;
    }
}
