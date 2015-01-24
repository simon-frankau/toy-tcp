package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OptionsReaderTest {
    @Test
    public void testSingleOption() {
        List<Option> options = new ArrayList<Option>();
        options.add(new OptionMagicNumber(0x01020304));
        assertEquals(options,
                 OptionsReader.readOptions(new Buffer(
                         OptionsReader.MAGIC_NUMBER, 6, 0x01, 0x02, 0x03, 0x04)));
    }

    @Test
    public void testMultipleOptions() {
        List<Option> options = new ArrayList<Option>();
        options.add(OptionAddressAndControlFieldCompression.INSTANCE);
        options.add(OptionProtocolFieldCompression.INSTANCE);
        assertEquals(options,
                OptionsReader.readOptions(new Buffer(
                        OptionsReader.ADDRESS_AND_CONTROL_COMPRESSION_FIELD, 2,
                        OptionsReader.PROTOCOL_FIELD_COMPRESSION, 2)));
    }

    @Test
    public void testGivesUpIfTooShort() {
        List<Option> options = new ArrayList<Option>();
        options.add(OptionAddressAndControlFieldCompression.INSTANCE);
        assertEquals(options,
                OptionsReader.readOptions(new Buffer(
                        OptionsReader.ADDRESS_AND_CONTROL_COMPRESSION_FIELD, 2,
                        OptionsReader.PROTOCOL_FIELD_COMPRESSION, 1)));

    }

    @Test
    public void testGivesUpIfTooLong() {
        List<Option> options = new ArrayList<Option>();
        options.add(OptionAddressAndControlFieldCompression.INSTANCE);
        assertEquals(options,
                OptionsReader.readOptions(new Buffer(
                        OptionsReader.ADDRESS_AND_CONTROL_COMPRESSION_FIELD, 2,
                        OptionsReader.MAGIC_NUMBER, 6)));

    }
}