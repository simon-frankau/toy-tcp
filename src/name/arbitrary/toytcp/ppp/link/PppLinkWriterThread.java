package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles the sending of frames.
 */
class PppLinkWriterThread {
    private static final Logger logger = LoggerFactory.getLogger(PppLinkWriterThread.class);

    private final WriteBuffer.Listener sender;

    public PppLinkWriterThread(final OutputStream outputStream) {
        WriteBuffer.Listener enqueuer = new WriteBuffer.Listener() {
            @Override
            public void send(WriteBuffer buffer) {
                // TODO: Will enqueue the packet for a low-level sending thread.
                // Initial hack: Just write directly out.
                try {
                    outputStream.write(buffer.toByteArray());
                    outputStream.flush();
                } catch (IOException e) {
                    logger.error("IO Error", e);
                }
            }
        };
        sender = new HeaderBuilder(new FcsBuilder(new Stuffer(new Framer(enqueuer))));
    }

    public void start() {
        // TODO!
    }

    public void stop() {
        // TODO!
    }

    public void send(WriteBuffer buffer) {
        sender.send(buffer);
    }
}
