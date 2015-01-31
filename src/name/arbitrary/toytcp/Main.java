package name.arbitrary.toytcp;

import name.arbitrary.toytcp.ppp.lcp.FrameReader;
import name.arbitrary.toytcp.ppp.lcp.LcpStateMachine;
import name.arbitrary.toytcp.ppp.link.PppLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("ToyTCP has started");

        PppLink link = new PppLink(System.in);
        /*
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
        */
        // TODO: State machine has changed and needs some new toys before it'll integrate correctly...
        // link.subscribe(0xC021, new FrameReader(new LcpStateMachine(listener, configChecker)));
        link.start();
    }
}
