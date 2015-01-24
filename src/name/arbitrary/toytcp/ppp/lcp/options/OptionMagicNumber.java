package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Created by sgf on 23/01/2015.
 */
public final class OptionMagicNumber implements Option {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionMagicNumber that = (OptionMagicNumber) o;

        if (magicNumber != that.magicNumber) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return magicNumber;
    }
}
