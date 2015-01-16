package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Removes the address/control header, if present, from a PPP frame.
 */
public class HeaderCompressor implements Buffer.Listener {
    private static final Logger logger = LoggerFactory.getLogger(HeaderCompressor.class);

    public static final byte ADDRESS = (byte)0xFF;
    public static final byte CONTROL = 0x03;

    private final Buffer.Listener listener;

    public HeaderCompressor(Buffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void receive(Buffer buffer) {
        // Address and control field compression support
        if (buffer.length() >= 2 && buffer.get(0) == ADDRESS && buffer.get(1) == CONTROL) {
            logger.trace("Received frame has header compresssion");
            listener.receive(buffer.getSubBuffer(2));
        } else {
            logger.trace("Received frame does not have header compresssion");
            listener.receive(buffer);
        }
    }
}
