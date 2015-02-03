package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.WriteBuffer;

/**
 * Maximum-Receive-Unit option.
 */
public final class OptionMaximumReceiveUnit implements Option {
    private final int maximumReceiveUnit;

    public OptionMaximumReceiveUnit(int maximumReceiveUnit) {
        this.maximumReceiveUnit = maximumReceiveUnit;
    }

    @Override
    public ResponseType getResponseType() {
        // This one's basically advisory (says it can receive larger, or request smaller
        // but still must be able to cope with MRU, so we just say "Yeah, whatever".
        return ResponseType.ACCEPT;
    }

    @Override
    public Option getAcceptableVersion() {
        throw new IllegalStateException("No need for acceptable version - always accept");
    }

    @Override
    public void writeTo(WriteBuffer buffer) {
        buffer.append(OptionsReader.MAXIMUM_RECEIVE_UNIT, (byte)4);
        buffer.appendU16(maximumReceiveUnit);
    }

    @Override
    public String toString() {
        return "OptionMaximumReceiveUnit{" +
                "maximumReceiveUnit=" + maximumReceiveUnit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionMaximumReceiveUnit that = (OptionMaximumReceiveUnit) o;

        if (maximumReceiveUnit != that.maximumReceiveUnit) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return maximumReceiveUnit;
    }
}
