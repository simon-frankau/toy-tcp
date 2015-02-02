package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Magic-Number option.
 */
public final class OptionMagicNumber implements Option {
    private final int magicNumber;

    public OptionMagicNumber(int magicNumber) {
        this.magicNumber = magicNumber;
    }

    @Override
    public ResponseType getResponseType() {
        // I can't really be bothered with implementing this correctly. So, not at all.
        return ResponseType.REJECT;
    }

    @Override
    public Option getAcceptableVersion() {
        throw new IllegalStateException("No need for acceptable version - always reject");
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
