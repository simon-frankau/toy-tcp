package name.arbitrary.toytcp;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Not entirely sure what this should look like.
 */
public class WriteBuffer {
    private final List<Byte> buffer = new ArrayList<Byte>();

    public void write(Byte b) {
        buffer.add(b);
    }

    public void write(Byte... bs) {
        for (Byte b : bs) {
            buffer.add(b);
        }
    }

    @Override
    public String toString() {
        return "WriteBuffer{" +
                "buffer=" + buffer +
                '}';
    }
}
