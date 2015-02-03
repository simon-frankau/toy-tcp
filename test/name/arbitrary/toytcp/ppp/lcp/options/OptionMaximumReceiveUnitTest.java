package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionMaximumReceiveUnitTest {
    private final Option option = new OptionMaximumReceiveUnit(0x0102);

    @Test
    public void testCreateSuccess() {
        assertEquals(option,
                OptionsReader.readOption(OptionsReader.MAXIMUM_RECEIVE_UNIT,
                        new Buffer(0x01, 0x02)));
    }

    @Test
    public void testCreationFailureBufferTooShort() {
        assertEquals(new OptionBad(OptionsReader.MAXIMUM_RECEIVE_UNIT, new Buffer(0x42)),
                OptionsReader.readOption(OptionsReader.MAXIMUM_RECEIVE_UNIT,
                        new Buffer(0x42)));
    }

    @Test
    public void testCreationFailureBufferTooLong() {
        assertEquals(new OptionBad(OptionsReader.MAXIMUM_RECEIVE_UNIT, new Buffer(0x42, 0x43, 0x44)),
                OptionsReader.readOption(OptionsReader.MAXIMUM_RECEIVE_UNIT,
                        new Buffer(0x42, 0x43, 0x44)));
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