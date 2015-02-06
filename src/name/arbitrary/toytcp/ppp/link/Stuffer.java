package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.WriteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Applies byte stuffing.
 */
public class Stuffer implements WriteBuffer.Listener {
    private final static Logger logger = LoggerFactory.getLogger(Stuffer.class);

    private final WriteBuffer.Listener listener;

    public Stuffer(WriteBuffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void send(WriteBuffer buffer) {
        // TODO: Conservatively, we'll stuff escape, mask and everything <= 0x20
        byte[] data = buffer.toByteArray();
        WriteBuffer newBuffer = new WriteBuffer();
        for (byte b : data) {
            if (b == Unstuffer.ESCAPE_CHAR || b == Unframer.FLAG_CHAR ||
                    (0 <= b && b < 0x20)) {
                newBuffer.append(Unstuffer.ESCAPE_CHAR);
                newBuffer.append((byte)(b ^ Unstuffer.ESCAPE_MASK));
            } else {
                newBuffer.append(b);
            }
        }

        logger.info("{}", newBuffer);
        listener.send(newBuffer);
    }
}
