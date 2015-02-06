package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.WriteBuffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FramerTest {
    private Framer framer;
    @Mock
    private WriteBuffer.Listener listener;

    @Before
    public void setup() {
        framer = new Framer(listener);
    }

    @Test
    public void testWrapsInFlagCharacters() {
        framer.send(new WriteBuffer(1, 2, 3, 4, 5));
        verify(listener).send(new WriteBuffer(Unframer.FLAG_CHAR, 1, 2, 3, 4, 5, Unframer.FLAG_CHAR));
        verifyNoMoreInteractions(listener);
    }
}