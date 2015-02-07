package name.arbitrary.toytcp.ppp.lcp.statemachine;

import name.arbitrary.toytcp.WriteBuffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;

import java.util.List;

/**
 * Interface for the messages to send out and link layer actions associated with state machine actions.
 */
public interface ActionProcessor {
    void onThisLayerStarted();
    void onThisLayerFinished();

    void sendConfigureRequest(byte identifier, List<Option> options);
    void sendConfigureAcknowledge(byte identifier, List<Option> options);
    void sendConfigureNak(byte identifier, List<Option> options);
    void sendConfigureReject(byte identifier, List<Option> options);

    void sendTerminateRequest(byte identifier, WriteBuffer buffer);
    void sendTerminateAcknowledge(byte identifier, WriteBuffer buffer);

    void sendCodeReject(byte identifier, WriteBuffer buffer);

    void sendEchoReply(byte identifier, WriteBuffer buffer);
}
