package name.arbitrary.toytcp.ppp.lcp.statemachine;

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

    void onSendConfigureAcknowledge();

    void onSendConfigureNak();
}
