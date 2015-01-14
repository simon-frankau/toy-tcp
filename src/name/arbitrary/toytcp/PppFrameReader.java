package name.arbitrary.toytcp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * The PppFrameReader runs a thread that will listen to an InputStream and pull the input into frames which it
 * will transmit to the provided listener.
 *
 * http://tools.ietf.org/html/rfc1662
 */
public class PppFrameReader {
    // Maximum Receive Unit informs the receive buffer size we set up
    private final static int MRU = 1500;
    // Allow some buffer overhead for flags, escaping, extra fields etc.
    private final static int BUFFER_SLACK = 32;

    private final static byte FLAG_CHAR = 0x7E;
    private final static byte ESCAPE_CHAR = 0x7D;
    private final static byte ESCAPE_MASK = 0x20;
    private final static byte ADDRESS = (byte)0xFF;
    private final static byte CONTROL = 0x03;

    private final InputStream inputStream;
    private final PppFrameListener listener;
    private final PrintStream logStream;

    private Thread readerThread;

    public PppFrameReader(InputStream inputStream, PppFrameListener listener, PrintStream logStream) {
        this.inputStream = inputStream;
        this.listener = listener;
        this.logStream = logStream;
    }

    public void start() {
        readerThread = new Thread(new PppBufferProcessor());
        readerThread.start();
    }

    public void stop() {
        throw new UnsupportedOperationException();
    }

    class PppBufferProcessor implements Runnable {
        // Allow worst-case space for all characters being escaped!
        private final byte[] buffer = new byte[2 * MRU + BUFFER_SLACK];
        private boolean synced = false;

        @Override
        public void run() {
            try {
                int readOffset = 0;
                while (true) {
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
                            processFrame(frameStart, i);
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
            } catch (IOException e) {
                e.printStackTrace(logStream);
                logStream.flush();
            }
        }

        private void processFrame(int start, int end) {
            if (!synced) {
                synced = true;
                return;
            }

            // Undo byte stuffing, in-place
            int dst = start;
            for (int src = start; src != end; src++, dst++) {
                if (buffer[src] == ESCAPE_CHAR) {
                    buffer[dst] = (byte)(buffer[++src] ^ ESCAPE_MASK);
                } else {
                    buffer[dst] = buffer[src];
                }
            }
            end = dst;

            // Address and control field compression support
            if (buffer[start] == ADDRESS && buffer[start+1] == CONTROL) {
                start += 2;
            }

            listener.onFrame(new PppFrame(buffer, start, end));
        }
    }
}
