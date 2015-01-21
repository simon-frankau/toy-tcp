package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * PPP link layer reader thread
 */
public class PppLinkReaderThread {
    private static final Logger logger = LoggerFactory.getLogger(PppLinkReaderThread.class);

    private final InputStream inputStream;
    private final PppLinkListener listener;

    private Thread readerThread;

    public PppLinkReaderThread(InputStream inputStream, PppLinkListener listener) {
        this.inputStream = inputStream;
        this.listener = listener;
    }

    public void start() {
        readerThread = new Thread(new PppBufferProcessor());
        readerThread.start();
    }

    public void stop() {
        // TODO: Support interrupt-based shut down.
        throw new UnsupportedOperationException();
    }

    class PppBufferProcessor implements Runnable {
        @Override
        public void run() {
            listener.onLinkUp();
            try {
                Unframer unframer =
                        new Unframer(inputStream,
                                new Unstuffer(
                                        new FcsChecker(
                                                new HeaderCompressor(
                                                        new Buffer.Listener() {
                                                            @Override
                                                            public void receive(Buffer buffer) {
                                                                listener.onFrame(buffer);
                                                            }
                                                        }))));
                while (unframer.process()) {
                }
            } catch (IOException e) {
                logger.error("Exception in main processing loop", e);
            }
            listener.onLinkDown();
        }
    }
}
