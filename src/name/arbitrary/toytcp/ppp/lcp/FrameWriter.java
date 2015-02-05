package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.WriteBuffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;
import name.arbitrary.toytcp.ppp.lcp.statemachine.ActionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Constructs and sends the messages from the state machine.
 *
 * TODO: Needs tests?
 */
public class FrameWriter implements ActionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FrameWriter.class);

    @Override
    public void sendConfigureRequest() {
        logger.info("SCR");
    }

    @Override
    public void sendCodeReject() {
        logger.info("SCJ");
    }

    @Override
    public void sendEchoReply() {
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
    public void sendTerminateAcknowledge() {
        logger.info("STA");
    }

    @Override
    public void sendTerminateRequest() {
        logger.info("STR");
    }

    @Override
    public void sendConfigureAcknowledge(byte identifier, List<Option> options) {
        sendConfigFrame(FrameReader.CONFIGURE_ACK, identifier, options);
    }

    @Override
    public void sendConfigureNak(byte identifier, List<Option> options) {
        sendConfigFrame(FrameReader.CONFIGURE_NAK, identifier, options);
    }

    @Override
    public void sendConfigureReject(byte identifier, List<Option> options) {
        sendConfigFrame(FrameReader.CONFIGURE_REJECT, identifier, options);
    }

    private void sendConfigFrame(byte type, byte identifier, List<Option> options) {
        WriteBuffer buffer = new WriteBuffer();
        buffer.append(type, identifier);
        // Reserve the space for the length, and write in later.
        int lengthFieldOffset = buffer.getAppendOffset();
        buffer.append((byte)0, (byte)0);

        for (Option option : options) {
            option.writeTo(buffer);
        }

        buffer.putU16(lengthFieldOffset, buffer.getAppendOffset());

        logger.info("{}", buffer);
    }
}
