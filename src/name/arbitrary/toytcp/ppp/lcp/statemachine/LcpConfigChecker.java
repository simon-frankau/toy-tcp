package name.arbitrary.toytcp.ppp.lcp.statemachine;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;

import java.util.List;

/**
 * Interface for something that actually handles the details of configuration.
 *
 * It has sub-interfaces to handle receiving requests, sending requests, and code/protocol rejects.
 */
public interface LcpConfigChecker {
    // TODO: Reset the internal state when going via this layer stopped.

    // Interface to do with receiving requests and producing responses:

    boolean isConfigAcceptable(List<Option> options);

    // Get an appropriate response if the config is not acceptable.
    name.arbitrary.toytcp.WriteBuffer getConfigNakOrReject(byte identifier);

    // Interface to do with sending requests and handling responses.

    // TODO

    // Interface to do with handling rejects.

    boolean isRejectAcceptable(Buffer rejected);
}
