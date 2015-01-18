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
public class FcsCheckerTest {
    @Mock
    private Buffer.Listener listener;

    private FcsChecker fcsChecker;

    @Before
    public void setup() {
        fcsChecker = new FcsChecker(listener);
    }

    @Test
    public void testCorrectFcsPasses() {
        Buffer correctFcs = new Buffer(
            0xff, 0x03, 0xc0, 0x21, 0x01, 0x01, 0x00, 0x14,
            0x02, 0x06, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06,
            0x4e, 0x28, 0x19, 0xbd, 0x07, 0x02, 0x08, 0x02,
            0x8f, 0xbc
        );
        fcsChecker.receive(correctFcs);
        verify(listener, times(1)).receive(correctFcs.getSubBuffer(0, correctFcs.length() - 2));
    }

    @Test
    public void testDropsFcsFails() {
        Buffer incorrectFcs = new Buffer(0x01, 0x02, 0x03, 0x04, 0x05);
        fcsChecker.receive(incorrectFcs);
        verify(listener, never()).receive(any(Buffer.class));
    }

    @Test
    public void testShortFrameIsDropped() {
        Buffer emptyFrame = new Buffer();
        fcsChecker.receive(emptyFrame);
        verify(listener, never()).receive(any(Buffer.class));
    }
}