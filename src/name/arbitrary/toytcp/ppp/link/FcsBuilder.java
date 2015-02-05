package name.arbitrary.toytcp.ppp.link;

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
        logger.info("{}", buffer);
        // TODO
        listener.send(buffer);
    }
}
