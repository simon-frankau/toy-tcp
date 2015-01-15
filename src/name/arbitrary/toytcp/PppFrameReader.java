package name.arbitrary.toytcp;

import name.arbitrary.toytcp.ppp.link.FcsChecker;
import name.arbitrary.toytcp.ppp.link.HeaderCompressor;
import name.arbitrary.toytcp.ppp.link.Unframer;
import name.arbitrary.toytcp.ppp.link.Unstuffer;

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
        @Override
        public void run() {
            try {
                Unframer unframer =
                        new Unframer(inputStream, new Unstuffer(new HeaderCompressor(new FcsChecker(listener))));
                while (true) {
                    unframer.process();
                }
            } catch (IOException e) {
                e.printStackTrace(logStream);
                logStream.flush();
            }
        }
    }
}
