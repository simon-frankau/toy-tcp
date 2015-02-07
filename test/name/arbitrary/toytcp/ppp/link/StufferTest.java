package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class StufferTest {
    private Stuffer stuffer;
    @Mock
    private WriteBuffer.Listener listener;

    @Before
    public void setup() {
        stuffer = new Stuffer(listener);
    }

    @Test
    public void testFlagCharacter() {
        testStuffing(Unframer.FLAG_CHAR);
    }

    @Test
    public void testEscapeCharacter() {
        testStuffing(Unstuffer.ESCAPE_CHAR);
    }

    @Test
    public void testEscapeLowChars0() {
        testStuffing((byte)0);
    }

    @Test
    public void testEscapeLowChars1F() {
        testStuffing((byte)0x1F);
    }

    @Test
    public void testSetACCM() {
        int testACCM = 0x0002;
        stuffer.setAsyncControlCharacterMap(testACCM);
        assertEquals(testACCM, stuffer.getAsyncControlCharacterMap());

        stuffer.send(new WriteBuffer(0x00, 0x01, 0x02));
        verify(listener).send(new WriteBuffer(0x00, Unstuffer.ESCAPE_CHAR, Unstuffer.ESCAPE_MASK ^ 0x01, 0x02));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testDefaultACCM() {
        assertEquals(0xFFFFFFFF, stuffer.getAsyncControlCharacterMap());
    }

    private void testStuffing(byte b) {
        stuffer.send(new WriteBuffer(0x20, b, 0xF0));
        verify(listener).send(new WriteBuffer(0x20, Unstuffer.ESCAPE_CHAR, Unstuffer.ESCAPE_MASK ^ b, 0xF0));
        verifyNoMoreInteractions(listener);
    }
}