package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;

/**
 * Created by sgf on 23/01/2015.
 */
public final class OptionAuthenticationProtocol implements Option {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionAuthenticationProtocol that = (OptionAuthenticationProtocol) o;

        if (authenticationProtocol != that.authenticationProtocol) return false;
        if (!data.equals(that.data)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = authenticationProtocol;
        result = 31 * result + data.hashCode();
        return result;
    }
}
