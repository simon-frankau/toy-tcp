package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class DemultiplexerTest {
    @Mock
    private PppLinkListener listener;

    @Test
    public void testUpPassesThrough() {
        Demultiplexer demultiplexer = new Demultiplexer();
        demultiplexer.subscribe(41, listener);
        verifyNoMoreInteractions(listener);

        demultiplexer.onLinkUp();
        verify(listener).onLinkUp();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testDownPassesThrough() {
        Demultiplexer demultiplexer = new Demultiplexer();
        demultiplexer.subscribe(41, listener);

        demultiplexer.onLinkUp();
        verify(listener).onLinkUp();
        verifyNoMoreInteractions(listener);

        demultiplexer.onLinkDown();
        verify(listener).onLinkDown();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testAlreadyUpReachesNewSubscriber() {
        Demultiplexer demultiplexer = new Demultiplexer();
        demultiplexer.onLinkUp();
        demultiplexer.subscribe(41, listener);
        verify(listener).onLinkUp();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testUnsubscribeLeadsToDown() {
        Demultiplexer demultiplexer = new Demultiplexer();
        demultiplexer.onLinkUp();
        demultiplexer.subscribe(41, listener);
        verify(listener).onLinkUp();
        verifyNoMoreInteractions(listener);

        demultiplexer.unsubscribe(41);
        verify(listener).onLinkDown();
        verifyNoMoreInteractions(listener);

        demultiplexer.onLinkDown();
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testEightBitProtocol() {
        Demultiplexer demultiplexer = new Demultiplexer();
        demultiplexer.subscribe(0x41, listener);

        demultiplexer.onLinkUp();
        verify(listener).onLinkUp();
        verifyNoMoreInteractions(listener);

        demultiplexer.onFrame(new Buffer(0x41));
        verify(listener).onFrame(new Buffer());
        verifyNoMoreInteractions(listener);

        demultiplexer.onFrame(new Buffer(0x43));
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void testSixteenBitProtocol() {
        Demultiplexer demultiplexer = new Demultiplexer();
        demultiplexer.subscribe(0xF041, listener);

        demultiplexer.onLinkUp();
        verify(listener).onLinkUp();
        verifyNoMoreInteractions(listener);

        demultiplexer.onFrame(new Buffer(0xF0, 0x41));
        verify(listener).onFrame(new Buffer());
        verifyNoMoreInteractions(listener);

        demultiplexer.onFrame(new Buffer(0xF0, 0x43));
        verifyNoMoreInteractions(listener);
    }

}