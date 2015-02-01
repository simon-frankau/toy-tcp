package name.arbitrary.toytcp;

import name.arbitrary.toytcp.ppp.lcp.FrameReader;
import name.arbitrary.toytcp.ppp.lcp.options.Option;
import name.arbitrary.toytcp.ppp.lcp.statemachine.*;
import name.arbitrary.toytcp.ppp.link.PppLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

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
        ActionProcessor actionProcessor = new ActionProcessor() {
            @Override
            public void onSendConfigureRequest() {
                logger.info("SCR");
            }

            @Override
            public void onSendCodeReject() {
                logger.info("SCJ");
            }

            @Override
            public void onSendEchoReply() {
                logger.info("SER");
            }

            @Override
            public void onThisLayerFinished() {
                logger.info("TLF");
            }

            @Override
            public void onThisLayerStarted() {
                logger.info("TLS");
            }

            @Override
            public void onSendTerminateAcknowledge() {
                logger.info("STA");
            }

            @Override
            public void onSendTerminateRequest() {
                logger.info("STR");
            }

            @Override
            public void onSendConfigureAcknowledge() {
                logger.info("SCA");
            }

            @Override
            public void onSendConfigureNak() {
                logger.info("SCN");
            }
        };
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
        LcpConfigChecker configChecker = new LcpConfigChecker() {
            @Override
            public boolean isConfigAcceptable(List<Option> options) {
                return true;
            }

            @Override
            public boolean isRejectAcceptable(Buffer rejected) {
                return false;
            }
        };
        LcpStateMachine stateMachine = new LcpStateMachine(listener, actionProcessor,
                                                           restartCounter, configChecker);
        link.subscribe(0xC021, new FrameReader(stateMachine));
        stateMachine.onOpen();
        link.start();
    }
}
