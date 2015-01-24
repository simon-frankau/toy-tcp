package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;

/**
 * Created by sgf on 23/01/2015.
 */
public class OptionAuthenticationProtocol implements Option {
    private final int authenticationProtocol;
    private final Buffer data;

    public OptionAuthenticationProtocol(int authenticationProtocol, Buffer data) {

        this.authenticationProtocol = authenticationProtocol;
        this.data = data;
    }

    @Override
    public String toString() {
        return "OptionAuthenticationProtocol{" +
                "authenticationProtocol=" + authenticationProtocol +
                ", data=" + data +
                '}';
    }
}
