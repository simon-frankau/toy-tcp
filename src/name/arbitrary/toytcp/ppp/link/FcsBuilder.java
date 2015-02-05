package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructs and adds a FrameCheckSequence.
 */
public class FcsBuilder implements WriteBuffer.Listener {
    private final static Logger logger = LoggerFactory.getLogger(FcsBuilder.class);

    private final WriteBuffer.Listener listener;

    public FcsBuilder(WriteBuffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void send(WriteBuffer buffer) {
        byte[] data = buffer.toByteArray();
        Buffer asReadBuffer = new Buffer(data, 0, data.length);
        // Borrow existing FCS implementation...
        int trialFcs = FcsChecker.pppFcs16(asReadBuffer) ^ 0xFFFF;
        // LSB first, for once.
        buffer.append((byte)trialFcs);
        buffer.append((byte)(trialFcs >> 8));

        logger.info("{}", buffer);
        listener.send(buffer);
    }
}
