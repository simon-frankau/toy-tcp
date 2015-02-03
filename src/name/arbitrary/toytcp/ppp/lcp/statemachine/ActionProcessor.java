package name.arbitrary.toytcp.ppp.lcp.statemachine;

import name.arbitrary.toytcp.ppp.lcp.options.Option;

import java.util.List;

/**
 * Interface for the messages to send out and link layer actions associated with state machine actions.
 */
public interface ActionProcessor {
    void onThisLayerStarted();
    void onThisLayerFinished();

    void sendConfigureRequest();
    void sendConfigureAcknowledge(byte identifier, List<Option> options);
    void sendConfigureNak(byte identifier, List<Option> options);
    void sendConfigureReject(byte identifier, List<Option> options);

    void sendTerminateRequest();
    void sendTerminateAcknowledge();

    void sendCodeReject();

    void sendEchoReply();
}
