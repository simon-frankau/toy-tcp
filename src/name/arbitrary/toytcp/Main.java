package name.arbitrary.toytcp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
	    // Process pppProcess = new PppProcessRunner().start();

        // TODO: Use a proper logger.
        final PrintStream out = new PrintStream(new FileOutputStream("toytcp.log"));
        out.println("ToyTCP has started");
        out.flush();

        PppFrameReader frameReader = new PppFrameReader(System.in, new PppFrameListener() {
            @Override
            public void onFrame(PppFrame frame) {
                out.println("Received frame: " + frame);
                out.flush();
            }
        }, out);
        frameReader.start();

        // pppProcess.waitFor();
    }
}
