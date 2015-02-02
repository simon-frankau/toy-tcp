package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;
import name.arbitrary.toytcp.ppp.lcp.statemachine.LcpConfigChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    public boolean isConfigAcceptable(List<Option> options) {
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
        return nakReceivedOptions.isEmpty() && rejectReceivedOptions.isEmpty();
    }

    @Override
    public WriteBuffer getConfigNakOrReject(byte identifier) {
        // TODO: Doesn't really build responses yet!
        if (!rejectReceivedOptions.isEmpty()) {
            logger.info("Would send reject {}", rejectReceivedOptions);
            return new WriteBuffer();
        } else if (!nakReceivedOptions.isEmpty()) {
            logger.info("Would send nak {}", nakReceivedOptions);
            return new WriteBuffer();
        }
        throw new IllegalStateException("Asking for nak/reject response when should ack");
    }

    @Override
    public boolean isRejectAcceptable(Buffer rejected) {
        // We really aren't doing anything fancy, so and code or protocol reject is serious!
        return false;
    }
}
