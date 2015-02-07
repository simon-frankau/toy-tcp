package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.WriteBuffer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents the PPP link layer. Mostly delegates.
 */
public class PppLink {
    private final PppLinkReaderThread readerThread;
    private final PppLinkWriterThread writerThread;
    private final Demultiplexer demultiplexer;

    public PppLink(InputStream inputStream, OutputStream outputStream) {
        demultiplexer = new Demultiplexer();
        readerThread = new PppLinkReaderThread(inputStream, demultiplexer);
        writerThread = new PppLinkWriterThread(outputStream);
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

    public WriteBuffer.Listener getProtocolSender(int protocol) {
        return new Multiplexer(protocol, writerThread);
    }
}
