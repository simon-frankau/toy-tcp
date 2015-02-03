package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.WriteBuffer;

/**
 * Async-Control-Character-Map option.
 */
public final class OptionAsyncControlCharacterMap implements Option {
    private final int asyncMap;

    public OptionAsyncControlCharacterMap(int asyncMap) {
        this.asyncMap = asyncMap;
    }

    @Override
    public ResponseType getResponseType() {
        // We're happy to escape whatever they ask for.
        return ResponseType.ACCEPT;
    }

    @Override
    public Option getAcceptableVersion() {
        throw new IllegalStateException("No need for acceptable version - always accept");
    }

    @Override
    public void writeTo(WriteBuffer buffer) {
        buffer.append(OptionsReader.ASYNC_CONTROL_CHARACTER_MAP, (byte)6);
        buffer.appendU32(asyncMap);
    }

    @Override
    public String toString() {
        return "OptionAsyncControlCharacterMap{" +
                "asyncMap=" + asyncMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionAsyncControlCharacterMap that = (OptionAsyncControlCharacterMap) o;

        if (asyncMap != that.asyncMap) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return asyncMap;
    }
}
