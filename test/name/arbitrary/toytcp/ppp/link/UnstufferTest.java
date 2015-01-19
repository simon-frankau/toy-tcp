package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class UnstufferTest {
    @Mock
    private Buffer.Listener listener;

    private Unstuffer unstuffer;

    @Before
    public void setup() {
        unstuffer = new Unstuffer(listener);
    }

    @Test
    public void testSimpleValuesPassThrough() {
        unstuffer.receive(new Buffer(0xA0, 0xB0, 0xC0));
        verify(listener).receive(new Buffer(0xA0, 0xB0, 0xC0));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testEscapeWorks() {
        unstuffer.receive(new Buffer(0xA0, Unstuffer.ESCAPE_CHAR, 0x10, 0xC0));
        verify(listener).receive(new Buffer(0xA0, Unstuffer.ESCAPE_MASK ^ 0x10, 0xC0));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testEscapeAtEndAborts() {
        unstuffer.receive(new Buffer(0xA0, Unstuffer.ESCAPE_CHAR));
        verifyNoMoreInteractions(listener);
    }
}
