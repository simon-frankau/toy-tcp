package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PppLinkReaderThreadTest {
    @Test
    public void testUpSendOnStart() throws Exception {
        final Semaphore semaphore = new Semaphore(0);
        PppLinkListener listener = new PppLinkAdapter() {
            @Override
            public void onLinkUp() {
                semaphore.release();
            }
        };
        PppLinkReaderThread link = new PppLinkReaderThread(new ByteArrayInputStream(new byte[0]), listener);
        assertEquals(0, semaphore.availablePermits());
        link.start();
        assertTrue(semaphore.tryAcquire(3, TimeUnit.SECONDS));
    }

    @Test
    public void testReceivesDownOnStop() throws Exception {
        final Semaphore semaphore = new Semaphore(0);
        PppLinkListener listener = new PppLinkAdapter() {
            @Override
            public void onLinkDown() {
                semaphore.release();
            }
        };
        PppLinkReaderThread link = new PppLinkReaderThread(new ByteArrayInputStream(new byte[0]), listener);
        assertEquals(0, semaphore.availablePermits());
        link.start();
        // Should automatically shut down if no data's available.
        assertTrue(semaphore.tryAcquire(3, TimeUnit.SECONDS));
    }

    @Test
    public void testProcessesPackets() throws Exception {
        byte[] inputData = new byte[] {
                Unframer.FLAG_CHAR,
                (byte)0xff, 0x03, (byte)0xc0, 0x21, 0x01, 0x01, 0x00, 0x14,
                0x02, 0x06, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06,
                0x4e, 0x28, 0x19, (byte)0xbd, 0x07, 0x02, 0x08, 0x02,
                (byte)0x8f, (byte)0xbc,
                Unframer.FLAG_CHAR,
                (byte)0xff, 0x03, (byte)0xc0, 0x21, 0x01, 0x01, 0x00, 0x14,
                0x02, 0x06, 0x00, 0x00, 0x00, 0x00, 0x05, 0x06,
                0x4e, 0x28, 0x19, (byte)0xbd, 0x07, 0x02, 0x08, 0x02,
                (byte)0x8f, (byte)0xbc,
                Unframer.FLAG_CHAR
        };

        final Buffer expectedResult = new Buffer(
                0xc0, 0x21, 0x01, 0x01, 0x00, 0x14, 0x02, 0x06,
                0x00, 0x00, 0x00, 0x00, 0x05, 0x06, 0x4e, 0x28,
                0x19, 0xbd, 0x07, 0x02, 0x08, 0x02);

        final Semaphore semaphore = new Semaphore(0);
        PppLinkListener listener = new PppLinkAdapter() {
            @Override
            public void onFrame(Buffer buffer) {
                if (expectedResult.equals(buffer)) {
                    semaphore.release();
                }
            }
        };

        PppLinkReaderThread link = new PppLinkReaderThread(new ByteArrayInputStream(inputData), listener);
        assertEquals(0, semaphore.availablePermits());
        link.start();
        // Should read input and generate two packets.
        assertTrue(semaphore.tryAcquire(2, 3, TimeUnit.SECONDS));
    }

    class PppLinkAdapter implements PppLinkListener {
        @Override
        public void onFrame(Buffer buffer) {
        }

        @Override
        public void onLinkUp() {
        }

        @Override
        public void onLinkDown() {
        }
    }
}