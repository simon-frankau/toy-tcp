package name.arbitrary.toytcp;

/**
 * A thing that receives PppFrames.
 */
public interface PppFrameListener {
    void onFrame(PppFrame frame);
}
