package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionMagicNumberTest {
    @Test
    public void testCreateSuccess() {
        assertEquals(new OptionMagicNumber(0x01020304),
                OptionsReader.readOption(OptionsReader.MAGIC_NUMBER,
                        new Buffer(0x01, 0x02, 0x03, 0x04)));
    }

    @Test
    public void testCreationFailureBufferTooShort() {
        assertEquals(new OptionBad(OptionsReader.MAGIC_NUMBER, new Buffer(0x42, 0x43, 0x44)),
                OptionsReader.readOption(OptionsReader.MAGIC_NUMBER,
                        new Buffer(0x42, 0x43, 0x44)));
    }

    @Test
    public void testCreationFailureBufferTooLong() {
        assertEquals(new OptionBad(OptionsReader.MAGIC_NUMBER, new Buffer(0x42, 0x43, 0x44, 0x45, 0x46)),
                OptionsReader.readOption(OptionsReader.MAGIC_NUMBER,
                        new Buffer(0x42, 0x43, 0x44, 0x45, 0x46)));
    }

    @Test
    public void testRequestIsRejected() {
        assertEquals(Option.ResponseType.REJECT,
                new OptionMagicNumber(0x01020304).getResponseType());
    }

    @Test(expected = IllegalStateException.class)
    public void testNoAcceptableAlternativeRequired() {
        new OptionMagicNumber(0x01020304).getAcceptableVersion();
    }
}