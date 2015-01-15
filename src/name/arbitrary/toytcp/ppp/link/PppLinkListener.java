package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;

/**
 * Interface to receive messages from the PPP link layer
 *
 * TODO: Should represent the external interface to the link layer. To replace PppFrameReader?
 */
public interface PppLinkListener {
    void onFrame(Buffer buffer);
    void onLinkUp();
    void onLinkDown();
}
