package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.PppFrameListener;

/**
 * Removes the address/control header, if present, from a PPP frame.
 */
public class HeaderCompressor implements Unframer.BufferListener {
    public final static byte ADDRESS = (byte)0xFF;
    public final static byte CONTROL = 0x03;

    private final Unframer.BufferListener listener;

    public HeaderCompressor(Unframer.BufferListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBuffer(Buffer buffer) {
        // Address and control field compression support
        int start = buffer.getStart();
        byte[] data = buffer.getData();
        if (data[start] == ADDRESS && data[start+1] == CONTROL) {
            start += 2;
        }

        listener.onBuffer(new Buffer(data, start, buffer.getEnd()));
    }
}
