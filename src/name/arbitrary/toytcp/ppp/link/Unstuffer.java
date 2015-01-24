package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Undo byte stuffing.
 *
 * TODO: Remove async control characters too?
 */
class Unstuffer implements Buffer.Listener {
    private static final Logger logger = LoggerFactory.getLogger(Unstuffer.class);

    public static final byte ESCAPE_CHAR = (byte)0x7D;
    public static final byte ESCAPE_MASK = (byte)0x20;

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
                    logger.warn("Escape character at end of frame - aborting frame");
                    return;
                }
                value = (byte)(buffer.get(src) ^ ESCAPE_MASK);
            }
            buffer.put(dst, value);
        }
        listener.receive(buffer.getSubBuffer(0, dst));
    }
}
