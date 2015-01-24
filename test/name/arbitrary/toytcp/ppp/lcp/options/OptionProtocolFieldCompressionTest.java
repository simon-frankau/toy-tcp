package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionProtocolFieldCompressionTest {
    @Test
    public void testCreateSuccess() {
        assertEquals(OptionProtocolFieldCompression.INSTANCE,
                OptionsReader.readOption(OptionsReader.PROTOCOL_FIELD_COMPRESSION,
                        new Buffer()));
    }

    @Test
    public void testCreationFailureBufferTooLong() {
        assertEquals(new OptionBad(OptionsReader.PROTOCOL_FIELD_COMPRESSION, new Buffer(0x42)),
                OptionsReader.readOption(OptionsReader.PROTOCOL_FIELD_COMPRESSION,
                        new Buffer(0x42)));
    }
}