package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;
import name.arbitrary.toytcp.ppp.lcp.options.OptionProtocolFieldCompression;
import name.arbitrary.toytcp.ppp.lcp.options.OptionsReader;
import name.arbitrary.toytcp.ppp.lcp.statemachine.EventProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FrameReaderTest {
    private static final byte IDENTIFIER = 42;
    private static final List<Option> EXPECTED_OPTIONS =
            Collections.<Option>singletonList(OptionProtocolFieldCompression.INSTANCE);
    // Buffer equivalent of EXPECTED_OPTIONS.
    private static final Buffer EXPECTED_BUFFER =
            new Buffer(OptionsReader.PROTOCOL_FIELD_COMPRESSION, 2);

    private FrameReader frameReader;
    @Mock
    private EventProcessor eventProcessor;

    @Before
    public void setup() {
        frameReader = new FrameReader(eventProcessor);
    }

    @Test
    public void testLinkUp() {
        frameReader.onLinkUp();
        verify(eventProcessor).onLinkUp();
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testLinkDown() {
        frameReader.onLinkDown();
        verify(eventProcessor).onLinkDown();
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testShortPacketDoesNothing() {
        // NB: Not necessarily right, but documents current behaviour.
        frameReader.onFrame(new Buffer(FrameReader.CODE_REJECT, IDENTIFIER, 0));
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testShortLengthDoesNothing() {
        // NB: Not necessarily right, but documents current behaviour.
        frameReader.onFrame(new Buffer(FrameReader.CODE_REJECT, IDENTIFIER, 0, 3));
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testTooLongLengthFieldDoesNothing() {
        // NB: Not necessarily right, but documents current behaviour.
        frameReader.onFrame(new Buffer(FrameReader.CODE_REJECT, IDENTIFIER, 1, 0, 23));
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testTrailingJunkIsTrimmed() {
        frameReader.onFrame(new Buffer(FrameReader.CODE_REJECT, IDENTIFIER, 0, 6, 5, 6, 7, 8));
        verify(eventProcessor).onCodeReject(IDENTIFIER, new Buffer(5, 6));
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testConfigureRequest() {
        frameReader.onFrame(buildDemoPacket(FrameReader.CONFIGURE_REQUEST));
        verify(eventProcessor).onConfigureRequest(IDENTIFIER, EXPECTED_OPTIONS);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testConfigureAck() {
        frameReader.onFrame(buildDemoPacket(FrameReader.CONFIGURE_ACK));
        verify(eventProcessor).onConfigureAck(IDENTIFIER, EXPECTED_OPTIONS);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testConfigureNak() {
        frameReader.onFrame(buildDemoPacket(FrameReader.CONFIGURE_NAK));
        verify(eventProcessor).onConfigureNak(IDENTIFIER, EXPECTED_OPTIONS);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testConfigureReject() {
        frameReader.onFrame(buildDemoPacket(FrameReader.CONFIGURE_REJECT));
        verify(eventProcessor).onConfigureReject(IDENTIFIER, EXPECTED_OPTIONS);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testTerminateRequest() {
        frameReader.onFrame(buildDemoPacket(FrameReader.TERMINATE_REQUEST));
        verify(eventProcessor).onReceiveTerminateRequest(IDENTIFIER, EXPECTED_BUFFER);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testTerminateAck() {
        frameReader.onFrame(buildDemoPacket(FrameReader.TERMINATE_ACK));
        verify(eventProcessor).onReceiveTerminateAck(IDENTIFIER, EXPECTED_BUFFER);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testCodeReject() {
        frameReader.onFrame(buildDemoPacket(FrameReader.CODE_REJECT));
        verify(eventProcessor).onCodeReject(IDENTIFIER, EXPECTED_BUFFER);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testProtocolReject() {
        frameReader.onFrame(buildDemoPacket(FrameReader.PROTOCOL_REJECT));
        verify(eventProcessor).onProtocolReject(IDENTIFIER, EXPECTED_BUFFER);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testEchoRequest() {
        frameReader.onFrame(buildDemoPacket(FrameReader.ECHO_REQUEST));
        verify(eventProcessor).onEchoRequest(IDENTIFIER, EXPECTED_BUFFER);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testEchoReply() {
        frameReader.onFrame(buildDemoPacket(FrameReader.ECHO_REPLY));
        verify(eventProcessor).onEchoReply(IDENTIFIER, EXPECTED_BUFFER);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testDiscardRequest() {
        frameReader.onFrame(buildDemoPacket(FrameReader.DISCARD_REQUEST));
        verify(eventProcessor).onDiscardRequest(IDENTIFIER, EXPECTED_BUFFER);
        verifyNoMoreInteractions(eventProcessor);
    }

    @Test
    public void testUnknownCode() {
        byte UNKNOWN_CODE = 42;
        frameReader.onFrame(buildDemoPacket(UNKNOWN_CODE));
        verify(eventProcessor).onUnknownCode(UNKNOWN_CODE, IDENTIFIER, EXPECTED_BUFFER);
        verifyNoMoreInteractions(eventProcessor);
    }

    private Buffer buildDemoPacket(byte code) {
        byte length = 6; // 4 bytes header, 2 bytes option.
        byte optionLength = 2;
        return new Buffer(code, IDENTIFIER, 0, length, OptionsReader.PROTOCOL_FIELD_COMPRESSION, optionLength);
    }
}
