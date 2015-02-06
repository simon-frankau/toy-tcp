package name.arbitrary.toytcp;

import name.arbitrary.toytcp.ppp.lcp.DefaultConfigChecker;
import name.arbitrary.toytcp.ppp.lcp.FrameReader;
import name.arbitrary.toytcp.ppp.lcp.FrameWriter;
import name.arbitrary.toytcp.ppp.lcp.statemachine.*;
import name.arbitrary.toytcp.ppp.link.PppLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("ToyTCP has started");

        PppLink link = new PppLink(System.in, System.out);

        LcpUpperLayerListener listener = new LcpUpperLayerListener() {
            @Override
            public void onThisLayerUp() {
                logger.info("LCP is UP");
            }

            @Override
            public void onThisLayerDown() {
                logger.info("LCP is DOWN");
            }
        };

        ActionProcessor actionProcessor = new FrameWriter(link);
        LcpRestartCounter restartCounter = new LcpRestartCounter() {
            @Override
            public void onInitializeRestartCount() {
                logger.info("IRC");
            }

            @Override
            public void onZeroRestartCount() {
                logger.info("ZRC");
            }
        };
        LcpConfigChecker configChecker = new DefaultConfigChecker();
        LcpStateMachine stateMachine = new LcpStateMachine(listener, actionProcessor,
                                                           restartCounter, configChecker);
        link.subscribe(0xC021, new FrameReader(stateMachine));
        stateMachine.onOpen();
        link.start();
    }
}
