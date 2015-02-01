package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.ppp.lcp.options.OptionsReader;
import name.arbitrary.toytcp.ppp.lcp.statemachine.EventProcessor;
import name.arbitrary.toytcp.ppp.link.PppLinkListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapts the LCP packets into specific requests that get fed to an EventProcessor.
 */
public class FrameReader implements PppLinkListener {
    private static final Logger logger = LoggerFactory.getLogger(FrameReader.class);

    public static final byte CONFIGURE_REQUEST = 1;
    public static final byte CONFIGURE_ACK = 2;
    public static final byte CONFIGURE_NAK = 3;
    public static final byte CONFIGURE_REJECT = 4;
    public static final byte TERMINATE_REQUEST = 5;
    public static final byte TERMINATE_ACK = 6;
    public static final byte CODE_REJECT = 7;
    public static final byte PROTOCOL_REJECT = 8;
    public static final byte ECHO_REQUEST = 9;
    public static final byte ECHO_REPLY = 10;
    public static final byte DISCARD_REQUEST = 11;

    private static final int LCP_HEADER_LENGTH = 4;

    private final EventProcessor eventProcessor;

    public FrameReader(EventProcessor eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @Override
    public void onFrame(Buffer buffer) {
        // TODO: Correct behaviour for failure cases.
        if (buffer.length() < LCP_HEADER_LENGTH) {
            logger.warn("Frame too short");
            return;
        }
        int length = buffer.getU16(2);
        if (length > buffer.length()) {
            logger.warn("LCP frame length field length, {}, longer than frame size, {}.", length, buffer.length());
            return;
        }
        if (length < LCP_HEADER_LENGTH) {
            logger.warn("LCP frame field, {}, too short.", length);
            return;
        }
        length -= LCP_HEADER_LENGTH;
        Buffer body = buffer.getSubBuffer(LCP_HEADER_LENGTH, length);

        byte code = buffer.get(0);
        byte identifier = buffer.get(1);

        switch (code) {
            case CONFIGURE_REQUEST:
                eventProcessor.onConfigureRequest(identifier, OptionsReader.readOptions(body));
                break;
            case CONFIGURE_ACK:
                eventProcessor.onConfigureAck(identifier, OptionsReader.readOptions(body));
                break;
            case CONFIGURE_NAK:
                eventProcessor.onConfigureNak(identifier, OptionsReader.readOptions(body));
                break;
            case CONFIGURE_REJECT:
                eventProcessor.onConfigureReject(identifier, OptionsReader.readOptions(body));
                break;
            case TERMINATE_REQUEST:
                eventProcessor.onReceiveTerminateRequest(identifier, body);
                break;
            case TERMINATE_ACK:
                eventProcessor.onReceiveTerminateAck(identifier, body);
                break;
            case CODE_REJECT:
                eventProcessor.onCodeReject(identifier, body);
                break;
            case PROTOCOL_REJECT:
                eventProcessor.onProtocolReject(identifier, body);
                break;
            case ECHO_REQUEST:
                eventProcessor.onEchoRequest(identifier, body);
                break;
            case ECHO_REPLY:
                eventProcessor.onEchoReply(identifier, body);
                break;
            case DISCARD_REQUEST:
                eventProcessor.onDiscardRequest(identifier, body);
                break;
            default:
                eventProcessor.onUnknownCode(code, identifier, body);
                break;
        }
    }

    @Override
    public void onLinkUp() {
        eventProcessor.onLinkUp();
    }

    @Override
    public void onLinkDown() {
        eventProcessor.onLinkDown();
    }
}
