package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionQualityProtocolTest {
    @Test
    public void testCreateSuccess() {
        assertEquals(new OptionQualityProtocol(0xFFFE, new Buffer(0xA0, 0xB0)),
                OptionsReader.readOption(OptionsReader.QUALITY_PROTOCOL,
                        new Buffer(0xFF, 0xFE, 0xA0, 0xB0)));
    }

    @Test
    public void testCreationFailureBufferTooShort() {
        assertEquals(new OptionBad(OptionsReader.QUALITY_PROTOCOL, new Buffer(0x42)),
                OptionsReader.readOption(OptionsReader.QUALITY_PROTOCOL,
                        new Buffer(0x42)));
    }

    @Test
    public void testRequestIsRejected() {
        assertEquals(Option.ResponseType.REJECT,
                new OptionQualityProtocol(0xFFFE, new Buffer(0xA0, 0xB0)).getResponseType());
    }

    @Test(expected = IllegalStateException.class)
    public void testNoAcceptableAlternativeRequired() {
        new OptionQualityProtocol(0xFFFE, new Buffer(0xA0, 0xB0)).getAcceptableVersion();
    }
}
