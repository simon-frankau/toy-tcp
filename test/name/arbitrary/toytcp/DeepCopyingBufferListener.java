package name.arbitrary.toytcp;

/**
 * As the buffer may be mutated, a deep-copying buffer listener is useful when verifying interactions.
 */
public class DeepCopyingBufferListener implements Buffer.Listener {
    private final Buffer.Listener listener;

    public DeepCopyingBufferListener(Buffer.Listener listener) {
        this.listener = listener;
    }

    @Override
    public void receive(Buffer buffer) {
        listener.receive(buffer.deepCopy());
    }
}
