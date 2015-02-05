package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.WriteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Attaches the frame bytes.
 */
public class Framer implements WriteBuffer.Listener {
    private final static Logger logger = LoggerFactory.getLogger(Framer.class);

    private final WriteBuffer.Listener listener;

    public Framer(WriteBuffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void send(WriteBuffer buffer) {
        logger.info("{}", buffer);
        // TODO
        listener.send(buffer);
    }
}
