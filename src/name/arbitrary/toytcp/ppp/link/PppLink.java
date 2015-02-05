package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.WriteBuffer;

import java.io.InputStream;

/**
 * Represents the PPP link layer. Mostly delegates.
 */
public class PppLink implements WriteBuffer.Listener {
    private final PppLinkReaderThread readerThread;
    private final PppLinkWriterThread writerThread;
    private final Demultiplexer demultiplexer;

    public PppLink(InputStream inputStream) {
        demultiplexer = new Demultiplexer();
        readerThread = new PppLinkReaderThread(inputStream, demultiplexer);
        writerThread = new PppLinkWriterThread();
    }

    public void start() {
        readerThread.start();
        writerThread.start();
    }

    public void stop() {
        readerThread.stop();
        writerThread.stop();
    }

    public void subscribe(int protocol, PppLinkListener listener) {
        demultiplexer.subscribe(protocol, listener);
    }

    public void unsubscribe(int protocol) {
        demultiplexer.unsubscribe(protocol);
    }

    public void send(WriteBuffer buffer) {
        writerThread.send(buffer);
    }
}
