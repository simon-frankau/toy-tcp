package name.arbitrary.toytcp.ppp.link;

import java.io.InputStream;

/**
 * Represents the PPP link layer. Mostly delegates.
 */
public class PppLink {
    private final PppLinkReaderThread readerThread;
    private final Demultiplexer demultiplexer;

    public PppLink(InputStream inputStream) {
        demultiplexer = new Demultiplexer();
        readerThread = new PppLinkReaderThread(inputStream, demultiplexer);
    }

    public void start() {
        readerThread.start();
    }

    public void stop() {
        readerThread.stop();
    }

    public void subscribe(int protocol, PppLinkListener listener) {
        demultiplexer.subscribe(protocol, listener);
    }

    public void unsubscribe(int protocol) {
        demultiplexer.unsubscribe(protocol);
    }
}
