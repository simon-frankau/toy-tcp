package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;
import name.arbitrary.toytcp.ppp.lcp.statemachine.LcpConfigChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Standard implementation of the config checker.
 */
public class DefaultConfigChecker implements LcpConfigChecker {
    private static final Logger logger = LoggerFactory.getLogger(DefaultConfigChecker.class);

    private List<Option> acceptableReceivedOptions = new ArrayList<Option>();
    private List<Option> nakReceivedOptions = new ArrayList<Option>();
    private List<Option> rejectReceivedOptions = new ArrayList<Option>();

    @Override
    public Option.ResponseType processIncomingConfigRequest(List<Option> options) {
        acceptableReceivedOptions.clear();
        nakReceivedOptions.clear();
        rejectReceivedOptions.clear();

        for (Option option : options) {
            switch (option.getResponseType()) {
                case ACCEPT:
                    acceptableReceivedOptions.add(option);
                    break;
                case NAK:
                    nakReceivedOptions.add(option.getAcceptableVersion());
                    break;
                case REJECT:
                    rejectReceivedOptions.add(option);
                    break;
            }
        }

        if (!rejectReceivedOptions.isEmpty()) {
            return Option.ResponseType.REJECT;
        }
        if (!nakReceivedOptions.isEmpty()) {
            return Option.ResponseType.NAK;
        }
        return Option.ResponseType.ACCEPT;
    }

    @Override
    public List<Option> getConfigRejectOptions() {
        assert !rejectReceivedOptions.isEmpty();
        return rejectReceivedOptions;
    }

    @Override
    public List<Option> getRequestedOptions() {
        // We aren't going to request /anything/. Keep it simple!
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Option> getConfigNakOptions() {
        assert rejectReceivedOptions.isEmpty();
        assert !nakReceivedOptions.isEmpty();
        return nakReceivedOptions;
    }

    @Override
    public boolean isRejectAcceptable(Buffer rejected) {
        // We really aren't doing anything fancy, so and code or protocol reject is serious!
        return false;
    }
}
