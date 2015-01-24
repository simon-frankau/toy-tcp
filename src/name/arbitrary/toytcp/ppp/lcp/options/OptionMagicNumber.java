package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Created by sgf on 23/01/2015.
 */
public class OptionMagicNumber implements Option {
    private final int magicNumber;

    public OptionMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    @Override
    public String toString() {
        return "OptionMagicNumber{" +
                "magicNumber=" + magicNumber +
                '}';
    }
}
