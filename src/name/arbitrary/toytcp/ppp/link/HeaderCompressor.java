package name.arbitrary.toytcp.ppp.link;

import name.arbitrary.toytcp.Buffer;

/**
 * Removes the address/control header, if present, from a PPP frame.
 */
public class HeaderCompressor implements Buffer.Listener {
    public final static byte ADDRESS = (byte)0xFF;
    public final static byte CONTROL = 0x03;

    private final Buffer.Listener listener;

    public HeaderCompressor(Buffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void receive(Buffer buffer) {
        // Address and control field compression support
        if (buffer.length() >= 2 && buffer.get(0) == ADDRESS && buffer.get(1) == CONTROL) {
            listener.receive(buffer.getSubBuffer(2));
        } else {
            listener.receive(buffer);
        }
    }
}
