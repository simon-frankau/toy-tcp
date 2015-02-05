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
        logger.info("{}", buffer);
        // TODO
        listener.send(buffer);
    }
}
