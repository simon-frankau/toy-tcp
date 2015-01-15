package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.PppFrame;
import name.arbitrary.toytcp.PppFrameListener;

/**
 * Undo byte stuffing.
 *
 * TODO: Remove async control characters too?
 */
public class Unstuffer implements Unframer.BufferListener {
    public final static byte ESCAPE_CHAR = 0x7D;
    public final static byte ESCAPE_MASK = 0x20;
    public final static byte ADDRESS = (byte)0xFF;
    public final static byte CONTROL = 0x03;

    private final Unframer.BufferListener listener;

    public Unstuffer(Unframer.BufferListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBuffer(Buffer buffer) {
        // Undo byte stuffing, in-place
        byte[] data = buffer.getData();
        int dst = buffer.getStart();
        for (int src = buffer.getStart(); src != buffer.getEnd(); src++, dst++) {
            if (data[src] == ESCAPE_CHAR) {
                // TODO: If at buffer end, abort frame.
                data[dst] = (byte)(data[++src] ^ ESCAPE_MASK);
            } else {
                data[dst] = data[src];
            }
        }
        int end = dst;

        // Address and control field compression support
        int start = buffer.getStart();
        if (data[start] == ADDRESS && data[start+1] == CONTROL) {
            start += 2;
        }

        listener.onBuffer(new Buffer(data, start, end));
    }
}
