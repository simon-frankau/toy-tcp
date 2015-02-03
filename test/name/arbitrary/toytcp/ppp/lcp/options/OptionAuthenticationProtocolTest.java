package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionAuthenticationProtocolTest {
    private final Option option = new OptionAuthenticationProtocol(0xFFFE, new Buffer(0xA0, 0xB0));

    @Test
    public void testCreateSuccess() {
        assertEquals(option,
                OptionsReader.readOption(OptionsReader.AUTHENTICATION_PROTOCOL,
                        new Buffer(0xFF, 0xFE, 0xA0, 0xB0)));
    }

    @Test
    public void testCreationFailureBufferTooShort() {
        assertEquals(new OptionBad(OptionsReader.AUTHENTICATION_PROTOCOL, new Buffer(0x42)),
                OptionsReader.readOption(OptionsReader.AUTHENTICATION_PROTOCOL,
                        new Buffer(0x42)));
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