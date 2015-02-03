package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Utilities to help with testing options.
 */
public class OptionsTestUtilities {
    private OptionsTestUtilities() {
    }

    static void testOptionWriting(Option option) {
        WriteBuffer buffer = new WriteBuffer();
        option.writeTo(buffer);
        byte[] array = buffer.toByteArray();
        List<Option> options = OptionsReader.readOptions(new Buffer(array, 0, array.length));
        assertEquals(1, options.size());
        assertEquals(option, options.get(0));
    }
}
