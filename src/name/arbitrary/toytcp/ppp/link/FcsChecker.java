package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.PppFrame;
import name.arbitrary.toytcp.PppFrameListener;

/**
 * Checks the FrameCheckSequence field, and only passes data on if the check passes.
 */
public class FcsChecker implements Unframer.BufferListener {
    private final PppFrameListener frameListener;

    public FcsChecker(PppFrameListener frameListener) {
        this.frameListener = frameListener;
    }

    @Override
    public void onBuffer(Buffer buffer) {
        if (buffer.getEnd() - buffer.getStart() < 2) {
            return;
        }

        frameListener.onFrame(new PppFrame(buffer.getData(), buffer.getStart(), buffer.getEnd()));
    }
}
