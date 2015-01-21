package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.DeepCopyingBufferListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UnframerTest {
    @Mock
    private Buffer.Listener listener;

    @Test
    public void testInitialDataIsSkipped() throws Exception {
        byte[] data = new byte[Unframer.MRU * 10];
        Arrays.fill(data, (byte)0x42);
        InputStream inputStream = new ByteArrayInputStream(data);
        Unframer unframer = new Unframer(inputStream, listener);
        while (unframer.process()) {
        }
        verify(listener, never()).receive(any(Buffer.class));
    }

    @Test
    public void testFrameIsReceived() throws Exception{
        byte[] data = new byte[] { 0x01, Unframer.FLAG_CHAR, 0x02, Unframer.FLAG_CHAR, 0x03 };
        InputStream inputStream = new ByteArrayInputStream(data);
        Unframer unframer = new Unframer(inputStream, new DeepCopyingBufferListener(listener));
        while (unframer.process()) {
        }
        verify(listener, times(1)).receive(new Buffer(0x02));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testMultipleFramesAreReceived() throws Exception {
        byte[] data = new byte[] { 0x01, Unframer.FLAG_CHAR, 0x02, Unframer.FLAG_CHAR, 0x03, Unframer.FLAG_CHAR, 0x04 };
        InputStream inputStream = new ByteArrayInputStream(data);
        Unframer unframer = new Unframer(inputStream, new DeepCopyingBufferListener(listener));
        while (unframer.process()) {
        }
        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener, times(1)).receive(new Buffer(0x02));
        inOrder.verify(listener, times(1)).receive(new Buffer(0x03));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testEmptyFrame() throws Exception {
        byte[] data = new byte[] { Unframer.FLAG_CHAR, Unframer.FLAG_CHAR, Unframer.FLAG_CHAR };
        InputStream inputStream = new ByteArrayInputStream(data);
        Unframer unframer = new Unframer(inputStream, new DeepCopyingBufferListener(listener));
        while (unframer.process()) {
        }
        verify(listener, times(2)).receive(new Buffer());
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testFrameSplitAcrossReads() throws Exception {
        InputStream inputStream = mock(InputStream.class);
        Unframer unframer = new Unframer(inputStream, new DeepCopyingBufferListener(listener));

        // Messy testing of individual, separated reads. Both reads return the same 'new' data.
        when(inputStream.read(Matchers.<byte[]>anyObject(), anyInt(), anyInt())).then(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                byte[] data = (byte[])args[0];
                int idx = (Integer)args[1];
                data[idx++] = 0x42;
                data[idx++] = Unframer.FLAG_CHAR;
                data[idx++] = 0x43;
                data[idx++] = Unframer.FLAG_CHAR;
                data[idx++] = 0x44;
                return 5;
            }
        });
        unframer.process();
        verify(listener, times(1)).receive(new Buffer(0x43));
        verifyNoMoreInteractions(listener);

        unframer.process();
        InOrder inOrder = inOrder(listener);
        inOrder.verify(listener, times(1)).receive(new Buffer(0x44, 0x42));
        inOrder.verify(listener, times(1)).receive(new Buffer(0x43));
        inOrder.verifyNoMoreInteractions();
    }
}
