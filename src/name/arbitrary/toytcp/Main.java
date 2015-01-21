package name.arbitrary.toytcp;

import name.arbitrary.toytcp.ppp.link.PppLink;
import name.arbitrary.toytcp.ppp.link.PppLinkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("ToyTCP has started");

        PppLink link = new PppLink(System.in);
        link.subscribe(0xC021, new PppLinkListener() {
            @Override
            public void onFrame(Buffer buffer) {
                logger.info("Received frame: {}", buffer);
            }

            @Override
            public void onLinkUp() {
                logger.info("Link UP");
            }

            @Override
            public void onLinkDown() {
                logger.info("Link DOWN");
            }
        });
        link.start();
    }
}
