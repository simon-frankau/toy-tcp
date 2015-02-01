package name.arbitrary.toytcp.ppp.lcp.statemachine;

/**
 * Listener for the events from the LcpStateMachine to the upper layer.
 */
public interface LcpUpperLayerListener {
    // We're configured, you can start.
    void onThisLayerUp();

    // Layer's down, stop.
    void onThisLayerDown();

}
