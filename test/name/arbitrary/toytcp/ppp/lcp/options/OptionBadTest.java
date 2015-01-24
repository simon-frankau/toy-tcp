package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionBadTest {
    // Creation of OptionBad with a known type but incorrectly-formatted message tested with that type.

    @Test
    public void testCreateOnUnknownType() {
        assertEquals(new OptionBad((byte)0xA0, new Buffer()), OptionsReader.readOption((byte)0xA0, new Buffer()));
    }
}