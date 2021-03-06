package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionAsyncControlCharacterMapTest {
    private final OptionAsyncControlCharacterMap option = new OptionAsyncControlCharacterMap(0x01020304);

    @Test
    public void testCreateSuccess() {
        assertEquals(option,
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