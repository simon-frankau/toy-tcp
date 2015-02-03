package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionAddressAndControlFieldCompressionTest {
    private final OptionAddressAndControlFieldCompression option = OptionAddressAndControlFieldCompression.INSTANCE;

    @Test
    public void testCreateSuccess() {
        assertEquals(option,
                OptionsReader.readOption(OptionsReader.ADDRESS_AND_CONTROL_COMPRESSION_FIELD,
                        new Buffer()));
    }

    @Test
    public void testCreationFailureBufferTooLong() {
        assertEquals(new OptionBad(OptionsReader.ADDRESS_AND_CONTROL_COMPRESSION_FIELD, new Buffer(0x42)),
                OptionsReader.readOption(OptionsReader.ADDRESS_AND_CONTROL_COMPRESSION_FIELD,
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