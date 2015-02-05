package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.WriteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds an address/control header.
 */
public class HeaderBuilder implements WriteBuffer.Listener {
    private final static Logger logger = LoggerFactory.getLogger(HeaderBuilder.class);

    private final WriteBuffer.Listener listener;

    public HeaderBuilder(WriteBuffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void send(WriteBuffer buffer) {
        // TODO: Use pre-prepared header space.
        WriteBuffer newBuffer = new WriteBuffer();
        newBuffer.append(HeaderCompressor.ADDRESS);
        newBuffer.append(HeaderCompressor.CONTROL);
        byte[] contents = buffer.toByteArray();
        for (int i = 0; i < contents.length; i++) {
            newBuffer.append(contents[i]);
        }
        logger.info("{}", newBuffer);
        listener.send(newBuffer);
    }
}
