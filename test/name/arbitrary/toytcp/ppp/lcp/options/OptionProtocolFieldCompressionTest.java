package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionProtocolFieldCompressionTest {
    private final Option option = OptionProtocolFieldCompression.INSTANCE;

    @Test
    public void testCreateSuccess() {
        assertEquals(option,
                OptionsReader.readOption(OptionsReader.PROTOCOL_FIELD_COMPRESSION,
                        new Buffer()));
    }

    @Test
    public void testCreationFailureBufferTooLong() {
        assertEquals(new OptionBad(OptionsReader.PROTOCOL_FIELD_COMPRESSION, new Buffer(0x42)),
                OptionsReader.readOption(OptionsReader.PROTOCOL_FIELD_COMPRESSION,
                        new Buffer(0x42)));
    }

    @Test
    public void testRequestIsAccepted() {
        assertEquals(Option.ResponseType.ACCEPT, option.getResponseType());
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