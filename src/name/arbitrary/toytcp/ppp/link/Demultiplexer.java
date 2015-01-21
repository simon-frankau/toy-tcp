package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles subscriptions (one per protocol) and demultiplexes frames based on protocol.
 *
 * Thread model is a bit funny. Up/down messages can come from the underlying connection, or from (un)subscription,
 * but if (un)subscription comes on a different thread than the usual frame processing, you might get frames arriving
 * before/after the associate (un)subscription.
 */
public class Demultiplexer implements PppLinkListener {
    private static final Logger logger = LoggerFactory.getLogger(Demultiplexer.class);

    private final Map<Integer, PppLinkListener> listeners = new ConcurrentHashMap<Integer, PppLinkListener>();

    private boolean isLinkUp;

    public synchronized void subscribe(int protocol, PppLinkListener listener) {
        assert !listeners.containsKey(protocol);
        listeners.put(protocol, listener);
        if (isLinkUp) {
            listener.onLinkUp();
        }
    }

    public synchronized void unsubscribe(int protocol) {
        assert listeners.containsKey(protocol);
        PppLinkListener listener = listeners.remove(protocol);
        if (isLinkUp) {
            listener.onLinkDown();
        }
    }

    @Override
    public void onFrame(Buffer buffer) {
        if (buffer.length() < 1) {
            logger.warn("Buffer too short to read protocol field (byte 1)");
            return;
        }
        int startOffset = 1;
        int protocol = buffer.get(0) & 0xFF;
        if ((protocol & 1) == 0) {
            // That was the most significant octet. Read the least significant.
            if (buffer.length() < 2) {
                logger.warn("Buffer too short to read protocol field (byte 2)");
                return;
            }
            protocol = (protocol << 8) | (buffer.get(1) & 0xFF);
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
    public synchronized void onLinkUp() {
        assert !isLinkUp;
        isLinkUp = true;
        for (PppLinkListener listener : listeners.values()) {
            listener.onLinkUp();
        }
    }

    @Override
    public synchronized void onLinkDown() {
        assert isLinkUp;
        isLinkUp = false;
        for (PppLinkListener listener : listeners.values()) {
            listener.onLinkDown();
        }
    }
}
