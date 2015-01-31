package name.arbitrary.toytcp.ppp.lcp;

/**
 * Interface for the actions that the state machine spits out.
 */
public interface LcpStateActionListener {
    void onInitializeRestartCount();

    void onSendConfigureRequest();

    void onThisLayerDown();

    void onSendCodeReject();

    void onSendEchoReply();

    void onThisLayerFinished();

    void onThisLayerUp();

    void onThisLayerStarted();

    void onSendTerminateAcknowledge();

    void onSendTerminateRequest();

    void onSendConfigureAcknowledge();

    void onSendConfigureNak();

    void onZeroRestartCount();
}
