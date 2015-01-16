package name.arbitrary.toytcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {
	    // Process pppProcess = new PppProcessRunner().start();

        logger.info("ToyTCP has started");

        PppFrameReader frameReader = new PppFrameReader(System.in, new Buffer.Listener() {
            @Override
            public void receive(Buffer buffer) {
                logger.info("Received frame: " + buffer);
            }
        });
        frameReader.start();

        // pppProcess.waitFor();
    }
}
