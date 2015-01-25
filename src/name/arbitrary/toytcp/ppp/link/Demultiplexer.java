package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles subscriptions (one per protocol) and demultiplexes frames based on protocol.
 *
 * Subscriptions must not be changed once the link is up, as I don't want to have to deal race conditions where I
 * don't need to.
 */
class Demultiplexer implements PppLinkListener {
    private static final Logger logger = LoggerFactory.getLogger(Demultiplexer.class);

    private final Map<Integer, PppLinkListener> listeners = new ConcurrentHashMap<Integer, PppLinkListener>();

    private boolean isLinkUp;

    public void subscribe(int protocol, PppLinkListener listener) {
        assert !isLinkUp;
        assert !listeners.containsKey(protocol);
        listeners.put(protocol, listener);
    }

    public void unsubscribe(int protocol) {
        assert !isLinkUp;
        assert listeners.containsKey(protocol);
        listeners.remove(protocol);
    }

    @Override
    public void onFrame(Buffer buffer) {
        if (buffer.length() < 1) {
            logger.warn("Buffer too short to read protocol field (byte 1)");
            return;
        }
        int startOffset = 1;
        int protocol = buffer.getU8(0);
        if ((protocol & 1) == 0) {
            // It's a 16-bit protocol.
            if (buffer.length() < 2) {
                logger.warn("Buffer too short to read protocol field (byte 2)");
                return;
            }
            protocol = buffer.getU16(0);
            startOffset++;
        }
        PppLinkListener listener = listeners.get(protocol);
        if (listener == null) {
            logger.warn(String.format("No listener subscribed for protocol 0x%04x", protocol));
            return;
        }
        listener.onFrame(buffer.getSubBuffer(startOffset));
    }

    @Override
    public void onLinkUp() {
        assert !isLinkUp;
        isLinkUp = true;
        for (PppLinkListener listener : listeners.values()) {
            listener.onLinkUp();
        }
    }

    @Override
    public void onLinkDown() {
        assert isLinkUp;
        isLinkUp = false;
        for (PppLinkListener listener : listeners.values()) {
            listener.onLinkDown();
        }
    }
}
