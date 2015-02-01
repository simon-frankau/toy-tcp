package name.arbitrary.toytcp.ppp.lcp.statemachine;

/**
 * Interface to handle restart timeouts
 */
public interface LcpRestartCounter {
    void onInitializeRestartCount();

    void onZeroRestartCount();
}
