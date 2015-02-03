package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.WriteBuffer;

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

    // Write the option into a buffer. Reading equivalent is in OptionsReader.
    void writeTo(WriteBuffer buffer);
}
