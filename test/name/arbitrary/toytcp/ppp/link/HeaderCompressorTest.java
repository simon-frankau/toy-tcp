package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HeaderCompressorTest {
    @Mock
    private Buffer.Listener listener;

    private HeaderCompressor headerCompressor;

    @Before
    public void setup() {
        headerCompressor = new HeaderCompressor(listener);
    }

    @Test
    public void testHeaderIsRemoved() {
        Buffer bufferWithHeader = new Buffer(0xff, 0x03, 0x01, 0x02, 0x03);
        headerCompressor.receive(bufferWithHeader);
        verify(listener, times(1)).receive(bufferWithHeader.getSubBuffer(2));
    }

    @Test
    public void testNoHeaderIsPassedThrough() {
        Buffer bufferWithHeader = new Buffer(0xff, 0x01, 0x02, 0x03);
        headerCompressor.receive(bufferWithHeader);
        verify(listener, times(1)).receive(bufferWithHeader.getSubBuffer(0));

    }

    @Test
    public void testShortHeaderIsPassedThrough() {
        Buffer emptyFrame = new Buffer();
        headerCompressor.receive(emptyFrame);
        verify(listener, times(1)).receive(emptyFrame);
    }
}
