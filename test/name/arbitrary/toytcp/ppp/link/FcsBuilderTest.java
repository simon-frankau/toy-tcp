package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FcsBuilderTest {
    private FcsBuilder fcsBuilder;
    @Mock
    private WriteBuffer.Listener listener;

    @Before
    public void setup() {
        fcsBuilder = new FcsBuilder(listener);
    }

    @Test
    public void testGeneratesCorrectFcs() {
        WriteBuffer frameWithoutFcs = new WriteBuffer(
                0xff, 0x03, 0xc0, 0x21, 0x01, 0x01, 0x00, 0x14,
                0x02, 0x06, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06,
                0x4e, 0x28, 0x19, 0xbd, 0x07, 0x02, 0x08, 0x02
        );

        WriteBuffer frameWithFcs = new WriteBuffer(
                0xff, 0x03, 0xc0, 0x21, 0x01, 0x01, 0x00, 0x14,
                0x02, 0x06, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06,
                0x4e, 0x28, 0x19, 0xbd, 0x07, 0x02, 0x08, 0x02,
                0x8f, 0xbc
        );

        fcsBuilder.send(frameWithoutFcs);
        verify(listener).send(frameWithFcs);
        verifyNoMoreInteractions(listener);
    }

    // TODO: A round-trip test might be good.
}