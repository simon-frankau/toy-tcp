package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;

/**
 * Quality-Protocol option.
 */
public class OptionQualityProtocol implements Option {
    private final int qualityProtocol;
    private final Buffer data;

    public OptionQualityProtocol(int qualityProtocol, Buffer data) {

        this.qualityProtocol = qualityProtocol;
        this.data = data;
    }

    @Override
    public String toString() {
        return "OptionQualityProtocol{" +
                "qualityProtocol=" + qualityProtocol +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionQualityProtocol that = (OptionQualityProtocol) o;

        if (qualityProtocol != that.qualityProtocol) return false;
        if (!data.equals(that.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qualityProtocol;
        result = 31 * result + data.hashCode();
        return result;
    }
}
