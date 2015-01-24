package name.arbitrary.toytcp.ppp.lcp.options;

/**
 * Created by sgf on 23/01/2015.
 */
public class OptionMaximumReceiveUnit implements Option {
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
}
