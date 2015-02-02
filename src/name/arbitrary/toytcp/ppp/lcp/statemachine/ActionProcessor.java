package name.arbitrary.toytcp.ppp.lcp.statemachine;

import name.arbitrary.toytcp.WriteBuffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;

import java.util.List;

/**
 * Interface for the messages to send out and link layer actions associated with state machine actions.
 */
public interface ActionProcessor {
    void onSendConfigureRequest();

    void onSendCodeReject();

    void onSendEchoReply();

    void onThisLayerFinished();

    void onThisLayerStarted();

    void onSendTerminateAcknowledge();

    void onSendTerminateRequest();

    void onSendConfigureAcknowledge(byte identifier, List<Option> options);

    void onSendConfigureNak(WriteBuffer configNakOrReject);
}
