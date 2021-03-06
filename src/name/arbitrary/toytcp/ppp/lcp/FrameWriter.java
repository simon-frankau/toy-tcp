package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.WriteBuffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;
import name.arbitrary.toytcp.ppp.lcp.statemachine.ActionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Constructs and sends the messages from the state machine.
 */
public class FrameWriter implements ActionProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FrameWriter.class);

    private final WriteBuffer.Listener listener;

    public FrameWriter(WriteBuffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onThisLayerStarted() {
        logger.info("TLS");
    }

    @Override
    public void onThisLayerFinished() {
        logger.info("TLF");
    }

    @Override
    public void sendConfigureRequest(byte identifier, List<Option> options) {
        sendConfigFrame(FrameReader.CONFIGURE_REQUEST, identifier, options);
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

    @Override
    public void sendTerminateRequest(byte identifier, WriteBuffer buffer) {
        sendOtherFrame(FrameReader.TERMINATE_REQUEST, identifier, buffer);
    }

    @Override
    public void sendTerminateAcknowledge(byte identifier, WriteBuffer buffer) {
        sendOtherFrame(FrameReader.TERMINATE_ACK, identifier, buffer);
    }

    @Override
    public void sendCodeReject(byte identifier, WriteBuffer buffer) {
        sendOtherFrame(FrameReader.CODE_REJECT, identifier, buffer);
    }

    @Override
    public void sendEchoReply(byte identifier, WriteBuffer buffer) {
        sendOtherFrame(FrameReader.ECHO_REPLY, identifier, buffer);
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
        listener.send(buffer);
    }

    private void sendOtherFrame(byte type, byte identifier, WriteBuffer buffer) {
        WriteBuffer newBuffer = new WriteBuffer();
        newBuffer.append(type, identifier);
        // Reserve the space for the length, and write in later.
        int lengthFieldOffset = newBuffer.getAppendOffset();
        newBuffer.append((byte) 0, (byte) 0);

        newBuffer.append(buffer.toByteArray());

        newBuffer.putU16(lengthFieldOffset, newBuffer.getAppendOffset());

        logger.info("{}", newBuffer);
        listener.send(newBuffer);
    }
}
