package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handles the finite state automaton stuff
 */
public class LcpStateMachine implements EventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(LcpStateMachine.class);

    public void onConfigureRequest(byte identifier, List<Option> options) {
        logger.info("ConfigureRequest {} {}", identifier, options);
    }

    public void onConfigureAck(byte identifier, List<Option> options) {
        logger.info("ConfigureAck {} {}", identifier, options);
    }

    @Override
    public void onConfigureNak(byte identifier, List<Option> options) {
        logger.info("ConfigureNak {} {}", identifier, options);
    }

    @Override
    public void onConfigureReject(byte identifier, List<Option> options) {
        logger.info("ConfigureReject {} {}", identifier, options);
    }

    @Override
    public void onReceiveTerminateRequest(byte identifier, Buffer buffer) {
        logger.info("ReceiveTerminateRequest {} {}", identifier, buffer);
    }

    @Override
    public void onReceiveTerminateAck(byte identifier, Buffer buffer) {
        logger.info("ReceiveTerminateAck {} {}", identifier, buffer);
    }

    @Override
    public void onCodeReject(byte identifier, Buffer rejected) {
        logger.info("CodeReject {} {}", identifier, rejected);
    }

    @Override
    public void onProtocolReject(byte identifier, Buffer rejected) {
        logger.info("ProtocolReject {} {}", identifier, rejected);
    }

    @Override
    public void onEchoRequest(byte identifier, Buffer buffer) {
        logger.info("EchoRequest {} {}", identifier, buffer);
    }

    @Override
    public void onEchoReply(byte identifier, Buffer buffer) {
        logger.info("EchoReply {} {}", identifier, buffer);
    }

    @Override
    public void onDiscardRequest(byte identifier, Buffer buffer) {
        logger.info("DiscardRequest {} {}", identifier, buffer);
    }

    @Override
    public void onUnknownCode(byte code, byte identifier, Buffer buffer) {
        logger.warn("Unrecognised code field: {} {} {}", code, identifier, buffer);
        // TODO: Should issue code reject.
    }

    @Override
    public void onLinkUp() {
        logger.info("Link up");
    }

    @Override
    public void onLinkDown() {
        logger.info("Link down");
    }

    /*

   RCR+ = Receive-Configure-Request (Good)
   RCR- = Receive-Configure-Request (Bad)
   RCA  = Receive-Configure-Ack
   RCN  = Receive-Configure-Nak/Rej

   RTR  = Receive-Terminate-Request
   RTA  = Receive-Terminate-Ack

   RUC  = Receive-Unknown-Code
   RXJ+ = Receive-Code-Reject (permitted)
       or Receive-Protocol-Reject
   RXJ- = Receive-Code-Reject (catastrophic)
       or Receive-Protocol-Reject
   RXR  = Receive-Echo-Request
       or Receive-Echo-Reply
       or Receive-Discard-Request
       */

}
