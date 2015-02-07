package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.WriteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tacks the protocol on the front of a buffer.
 */
public class Multiplexer implements WriteBuffer.Listener {
    private static final Logger logger = LoggerFactory.getLogger(Multiplexer.class);

    private final int protocol;
    private final WriteBuffer.Listener listener;

    public Multiplexer(int protocol, WriteBuffer.Listener listener) {
        this.protocol = protocol;
        this.listener = listener;
    }

    @Override
    public void send(WriteBuffer buffer) {
        WriteBuffer newBuffer = new WriteBuffer();
        newBuffer.appendU16(protocol);
        newBuffer.append(buffer.toByteArray());
        logger.info("{}", newBuffer);
        listener.send(newBuffer);
    }
}
