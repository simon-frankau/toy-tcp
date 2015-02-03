package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.WriteBuffer;

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
    public void writeTo(WriteBuffer buffer) {
        buffer.append(OptionsReader.PROTOCOL_FIELD_COMPRESSION, (byte)2);
    }

    @Override
    public String toString() {
        return "OptionProtocolFieldCompression{}";
    }
}
