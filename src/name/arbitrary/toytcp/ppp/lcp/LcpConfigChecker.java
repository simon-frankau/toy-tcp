package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;

import java.util.List;

/**
 * Interface for something that actually handles the details of configuration.
 */
public interface LcpConfigChecker {
    boolean isConfigAcceptable(List<Option> options);

    boolean isRejectAcceptable(Buffer rejected);
}
