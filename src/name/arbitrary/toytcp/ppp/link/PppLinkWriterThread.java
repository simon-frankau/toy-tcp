package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;

/**
 * Handles the sending of frames.
 */
class PppLinkWriterThread {
    private final WriteBuffer.Listener sender;

    public PppLinkWriterThread() {
        WriteBuffer.Listener enqueuer = new WriteBuffer.Listener() {
            @Override
            public void send(WriteBuffer buffer) {
                // TODO: Will enqueue the packet for a low-level sending thread.
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
