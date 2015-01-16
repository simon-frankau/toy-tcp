package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;

/**
 * Undo byte stuffing.
 *
 * TODO: Remove async control characters too?
 */
public class Unstuffer implements Buffer.Listener {
    public final static byte ESCAPE_CHAR = 0x7D;
    public final static byte ESCAPE_MASK = 0x20;
    public final static byte ADDRESS = (byte)0xFF;
    public final static byte CONTROL = 0x03;

    private final Buffer.Listener listener;

    public Unstuffer(Buffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void receive(Buffer buffer) {
        // Undo byte stuffing, in-place
        int dst = 0;
        for (int src = 0; src != buffer.length(); src++, dst++) {
            byte value = buffer.get(src);
            if (value == ESCAPE_CHAR) {
                if (++src == buffer.length()) {
                    // Escape character at end, abort frame.
                    return;
                }
                value = (byte)(buffer.get(src) ^ ESCAPE_MASK);
            }
            buffer.put(dst, value);
        }
        listener.receive(buffer.getSubBuffer(0, dst));
    }
}
