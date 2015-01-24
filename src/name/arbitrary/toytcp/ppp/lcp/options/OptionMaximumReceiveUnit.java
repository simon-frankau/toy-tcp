package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Created by sgf on 23/01/2015.
 */
public final class OptionMaximumReceiveUnit implements Option {
    private final int maximumReceiveUnit;

    public OptionMaximumReceiveUnit(int maximumReceiveUnit) {
        this.maximumReceiveUnit = maximumReceiveUnit;
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
