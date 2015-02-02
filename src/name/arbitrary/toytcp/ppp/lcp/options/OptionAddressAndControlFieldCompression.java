package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Address-And-Control-Field-Compression option.
 */
public enum OptionAddressAndControlFieldCompression implements Option {
    INSTANCE;

    @Override
    public ResponseType getResponseType() {
        return ResponseType.ACCEPT;
    }

    @Override
    public Option getAcceptableVersion() {
        throw new IllegalStateException("No need for acceptable version - always accept");
    }

    @Override
    public String toString() {
        return "OptionAddressAndControlFieldCompression{}";
    }
}
