package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import static org.junit.Assert.*;

public class OptionMaximumReceiveUnitTest {
    @Test
    public void testCreateSuccess() {
        assertEquals(new OptionMaximumReceiveUnit(0x0102),
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

}