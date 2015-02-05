package name.arbitrary.toytcp.ppp.link;

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
public class HeaderBuilderTest {
    private HeaderBuilder headerBuilder;
    @Mock
    private WriteBuffer.Listener listener;

    @Before
    public void setup() {
        headerBuilder = new HeaderBuilder(listener);
    }

    @Test
    public void testHeaderIsApplied() {
        headerBuilder.send(new WriteBuffer(1, 2, 3, 4, 5));
        verify(listener).send(new WriteBuffer(HeaderCompressor.ADDRESS, HeaderCompressor.CONTROL,
                1, 2, 3, 4, 5));
        verifyNoMoreInteractions(listener);
    }
}