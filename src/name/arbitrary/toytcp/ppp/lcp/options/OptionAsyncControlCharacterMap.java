package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Async-Control-Character-Map option.
 */
public final class OptionAsyncControlCharacterMap implements Option {
    private final int asyncMap;

    public OptionAsyncControlCharacterMap(int asyncMap) {
        this.asyncMap = asyncMap;
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
