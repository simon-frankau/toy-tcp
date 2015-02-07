package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;
import name.arbitrary.toytcp.ppp.lcp.options.OptionsReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

// NB: Reuses infrastructure from FrameReaderTest
@RunWith(MockitoJUnitRunner.class)
public class FrameWriterTest {
    public static final WriteBuffer EXPECTED_BUFFER = new WriteBuffer(OptionsReader.PROTOCOL_FIELD_COMPRESSION, 2);

    private FrameWriter frameWriter;
    @Mock
    private WriteBuffer.Listener listener;

    @Before
    public void setUp() throws Exception {
        frameWriter = new FrameWriter(listener);
    }

    @Test
    public void testOnThisLayerStarted() throws Exception {
        frameWriter.onThisLayerStarted();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testOnThisLayerFinished() throws Exception {
        frameWriter.onThisLayerFinished();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendConfigureRequest() throws Exception {
        frameWriter.sendConfigureRequest(FrameReaderTest.IDENTIFIER, FrameReaderTest.EXPECTED_OPTIONS);
        verify(listener).send(buildDemoPacket(FrameReader.CONFIGURE_REQUEST));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendConfigureAcknowledge() throws Exception {
        frameWriter.sendConfigureAcknowledge(FrameReaderTest.IDENTIFIER, FrameReaderTest.EXPECTED_OPTIONS);
        verify(listener).send(buildDemoPacket(FrameReader.CONFIGURE_ACK));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendConfigureNak() throws Exception {
        frameWriter.sendConfigureNak(FrameReaderTest.IDENTIFIER, FrameReaderTest.EXPECTED_OPTIONS);
        verify(listener).send(buildDemoPacket(FrameReader.CONFIGURE_NAK));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendConfigureReject() throws Exception {
        frameWriter.sendConfigureReject(FrameReaderTest.IDENTIFIER, FrameReaderTest.EXPECTED_OPTIONS);
        verify(listener).send(buildDemoPacket(FrameReader.CONFIGURE_REJECT));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendTerminateRequest() throws Exception {
        frameWriter.sendTerminateRequest(FrameReaderTest.IDENTIFIER, EXPECTED_BUFFER);
        verify(listener).send(buildDemoPacket(FrameReader.TERMINATE_REQUEST));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendTerminateAcknowledge() throws Exception {
        frameWriter.sendTerminateAcknowledge(FrameReaderTest.IDENTIFIER, EXPECTED_BUFFER);
        verify(listener).send(buildDemoPacket(FrameReader.TERMINATE_ACK));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendCodeReject() throws Exception {
        frameWriter.sendCodeReject(FrameReaderTest.IDENTIFIER, EXPECTED_BUFFER);
        verify(listener).send(buildDemoPacket(FrameReader.CODE_REJECT));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSendEchoReply() throws Exception {
        frameWriter.sendEchoReply(FrameReaderTest.IDENTIFIER, EXPECTED_BUFFER);
        verify(listener).send(buildDemoPacket(FrameReader.ECHO_REPLY));
        verifyNoMoreInteractions(listener);
    }

    public static WriteBuffer buildDemoPacket(byte code) {
        Buffer buffer = FrameReaderTest.buildDemoPacket(code);
        WriteBuffer writeBuffer = new WriteBuffer();
        for (int i = 0; i < buffer.length(); i++) {
            writeBuffer.append(buffer.get(i));
        }
        return writeBuffer;
    }
}