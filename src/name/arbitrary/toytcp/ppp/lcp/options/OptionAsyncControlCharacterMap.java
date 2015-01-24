package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Created by sgf on 23/01/2015.
 */
public class OptionAsyncControlCharacterMap implements Option {
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
}
