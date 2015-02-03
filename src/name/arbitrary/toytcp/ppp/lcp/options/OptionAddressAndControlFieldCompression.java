package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.WriteBuffer;

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
    public void writeTo(WriteBuffer buffer) {
        buffer.append(OptionsReader.ADDRESS_AND_CONTROL_COMPRESSION_FIELD, (byte)2);
    }

    @Override
    public String toString() {
        return "OptionAddressAndControlFieldCompression{}";
    }
}
