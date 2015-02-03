package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionBadTest {
    // Creation of OptionBad with a known type but incorrectly-formatted message is tested with that type.

    private final Option option = new OptionBad((byte)0xA0, new Buffer((byte)0x01));

    @Test
    public void testCreateOnUnknownType() {
        assertEquals(option, OptionsReader.readOption((byte)0xA0, new Buffer((byte)(0x01))));
    }

    @Test
    public void testRequestIsRejected() {
        assertEquals(Option.ResponseType.REJECT, option.getResponseType());
    }

    @Test(expected = IllegalStateException.class)
    public void testNoAcceptableAlternativeRequired() {
        option.getAcceptableVersion();
    }

    @Test
    public void testWriting() {
        OptionsTestUtilities.testOptionWriting(option);
    }
}