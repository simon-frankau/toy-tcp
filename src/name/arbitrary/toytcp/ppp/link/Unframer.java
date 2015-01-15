package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Unframer reads from an InputStream, and breaks the input into PPP frames.
 */
public class Unframer {
    // Maximum Receive Unit informs the receive buffer size we set up
    public final static int MRU = 1500;
    // Allow some buffer overhead for flags, escaping, extra fields etc.
    private final static int BUFFER_SLACK = 32;
    // The character representing the start/end of frames.
    private final static byte FLAG_CHAR = 0x7E;

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
            throw new IOException("Input buffer full without receiving end-of-frame");
        }

        int bytesRead = inputStream.read(buffer, readOffset, buffer.length - readOffset);
        if (bytesRead == -1) {
            throw new EOFException();
        }
        int newReadOffset = readOffset + bytesRead;

        // Split what we've received into frames.
        int frameStart = 0;
        for (int i = readOffset; i < newReadOffset; i++) {
            if (buffer[i] == FLAG_CHAR) {
                if (!synced) {
                    synced = true;
                } else {
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
