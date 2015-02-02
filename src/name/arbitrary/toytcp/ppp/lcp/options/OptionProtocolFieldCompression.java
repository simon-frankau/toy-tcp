package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Protocol-Field-Compression option.
 */
public enum OptionProtocolFieldCompression implements Option {
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
        return "OptionProtocolFieldCompression{}";
    }
}
