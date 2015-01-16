package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Unframer reads from an InputStream, and breaks the input into PPP frames.
 */
public class Unframer {
    private static final Logger logger = LoggerFactory.getLogger(Unframer.class);

    // Maximum Receive Unit informs the receive buffer size we set up
    public static final int MRU = 1500;
    // Allow some buffer overhead for flags, escaping, extra fields etc.
    private static final int BUFFER_SLACK = 32;
    // The character representing the start/end of frames.
    private static final byte FLAG_CHAR = 0x7E;

    // Allow worst-case space for all characters being escaped!
    private final byte[] buffer = new byte[2 * MRU + BUFFER_SLACK];

    private final InputStream inputStream;
    private final Buffer.Listener listener;
    private boolean synced = false;
    private int readOffset = 0;

    public Unframer(InputStream inputStream, Buffer.Listener listener) {
        this.inputStream = inputStream;
        this.listener = listener;
    }

    public void process() throws IOException {
        int spaceLeft = buffer.length - readOffset;
        if (spaceLeft == 0) {
            logger.trace("Buffer full looking for end-of-frame");
            throw new IOException("Input buffer full without receiving end-of-frame");
        }

        int bytesRead = inputStream.read(buffer, readOffset, buffer.length - readOffset);
        if (bytesRead == -1) {
            logger.trace("End of file reading input");
            throw new EOFException();
        }
        int newReadOffset = readOffset + bytesRead;

        // Split what we've received into frames.
        int frameStart = 0;
        for (int i = readOffset; i < newReadOffset; i++) {
            if (buffer[i] == FLAG_CHAR) {
                if (!synced) {
                    logger.trace("Synced on initial flag character");
                    synced = true;
                } else {
                    logger.trace("Frame received");
                    listener.receive(new Buffer(buffer, frameStart, i - frameStart));
                }
                frameStart = i + 1;
            }
        }

        // Move any incomplete frames to the start of the buffer.
        if (synced) {
            if (frameStart != 0) {
                for (int src = frameStart, dst = 0; dst != newReadOffset; src++, dst++) {
                    buffer[dst] = buffer[src];
                }
                readOffset = newReadOffset - frameStart;
            }
        } else {
            // Not yet sync'd, drop everything.
            readOffset = 0;
        }
    }
}
