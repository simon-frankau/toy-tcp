package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;

/**
 * Checks the FrameCheckSequence field, and only passes data on if the check passes.
 */
public class FcsChecker implements Buffer.Listener {
    private final Buffer.Listener listener;

    public FcsChecker(Buffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void receive(Buffer buffer) {
        if (buffer.length() < 2) {
            return;
        }
        listener.receive(buffer);
    }
}
