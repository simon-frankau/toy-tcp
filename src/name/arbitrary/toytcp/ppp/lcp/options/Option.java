package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Interface to represent an LCP option.
 */
public interface Option {
    enum ResponseType {
        ACCEPT,
        NAK,
        REJECT
    }

    ResponseType getResponseType();

    Option getAcceptableVersion();
}
