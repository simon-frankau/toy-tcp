package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionAsyncControlCharacterMapTest {
    @Test
    public void testCreateSuccess() {
        assertEquals(new OptionAsyncControlCharacterMap(0x01020304),
                OptionsReader.readOption(OptionsReader.ASYNC_CONTROL_CHARACTER_MAP,
                        new Buffer(0x01, 0x02, 0x03, 0x04)));
    }

    @Test
    public void testCreationFailureBufferTooShort() {
        assertEquals(new OptionBad(OptionsReader.ASYNC_CONTROL_CHARACTER_MAP, new Buffer(0x42, 0x43, 0x44)),
                OptionsReader.readOption(OptionsReader.ASYNC_CONTROL_CHARACTER_MAP,
                        new Buffer(0x42, 0x43, 0x44)));
    }

    @Test
    public void testCreationFailureBufferTooLong() {
        assertEquals(new OptionBad(OptionsReader.ASYNC_CONTROL_CHARACTER_MAP, new Buffer(0x42, 0x43, 0x44, 0x45, 0x46)),
                OptionsReader.readOption(OptionsReader.ASYNC_CONTROL_CHARACTER_MAP,
                        new Buffer(0x42, 0x43, 0x44, 0x45, 0x46)));
    }

    @Test
    public void testRequestIsAccepted() {
        assertEquals(Option.ResponseType.ACCEPT,
                new OptionAsyncControlCharacterMap(0x01020304).getResponseType());
    }

    @Test(expected = IllegalStateException.class)
    public void testNoAcceptableAlternativeRequired() {
        new OptionAsyncControlCharacterMap(0x01020304).getAcceptableVersion();
    }
}