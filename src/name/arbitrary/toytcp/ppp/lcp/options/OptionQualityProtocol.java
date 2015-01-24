package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;

/**
 * Created by sgf on 23/01/2015.
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
}
