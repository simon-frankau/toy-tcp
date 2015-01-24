package name.arbitrary.toytcp.ppp.lcp.options;

import name.arbitrary.toytcp.Buffer;

import java.util.LinkedList;
import java.util.List;

/**
 * Reads an option or set of options. Helper class for FrameReader.
 */
public class OptionsReader {
    public static final byte MAXIMUM_RECEIVE_UNIT = 1;
    public static final byte ASYNC_CONTROL_CHARACTER_MAP = 2;
    public static final byte AUTHENTICATION_PROTOCOL = 3;
    public static final byte QUALITY_PROTOCOL = 4;
    public static final byte MAGIC_NUMBER = 5;
    public static final byte PROTOCOL_FIELD_COMPRESSION = 7;
    public static final byte ADDRESS_AND_CONTROL_COMPRESSION_FIELD= 8;

    private OptionsReader() {
        // Static method holder class.
    }

    public static List<Option> readOptions(Buffer buffer) {
        List<Option> options = new LinkedList<Option>();
        while (buffer.length() >= 2) {
            // Extract type and length.
            byte type = buffer.get(0);
            int length = buffer.getU8(1);

            // Length sanity check.
            if (length > buffer.length() || length < 2) {
                break;
            }

            // Construct option.
            Buffer optionBuffer = buffer.getSubBuffer(2, length - 2);
            options.add(readOption(type, optionBuffer));

            // Move to next option.
            buffer = buffer.getSubBuffer(length);
        }
        return options;
    }

    public static Option readOption(byte type, Buffer buffer) {
        switch (type) {
            case MAXIMUM_RECEIVE_UNIT: {
                if (buffer.length() != 2) {
                    return new OptionBad(type, buffer);
                }
                return new OptionMaximumReceiveUnit(buffer.getU16(0));
            }
            case ASYNC_CONTROL_CHARACTER_MAP: {
                if (buffer.length() != 4) {
                    return new OptionBad(type, buffer);
                }
                return new OptionAsyncControlCharacterMap(buffer.getS32(0));
            }
            case AUTHENTICATION_PROTOCOL: {
                if (buffer.length() < 2) {
                    return new OptionBad(type, buffer);
                }
                return new OptionAuthenticationProtocol(buffer.getU16(0), buffer.getSubBuffer(2));
            }
            case QUALITY_PROTOCOL:
                if (buffer.length() < 2) {
                    return new OptionBad(type, buffer);
                }
                return new OptionQualityProtocol(buffer.getU16(0), buffer.getSubBuffer(2));
            case MAGIC_NUMBER:
                if (buffer.length() != 4) {
                    return new OptionBad(type, buffer);
                }
                return new OptionMagicNumber(buffer.getS32(0));
            case PROTOCOL_FIELD_COMPRESSION:
                if (buffer.length() != 0) {
                    return new OptionBad(type, buffer);
                }
                return OptionProtocolFieldCompression.INSTANCE;
            case ADDRESS_AND_CONTROL_COMPRESSION_FIELD:
                if (buffer.length() != 0) {
                    return new OptionBad(type, buffer);
                }
                return OptionAddressAndControlFieldCompression.INSTANCE;
            default:
                return new OptionBad(type, buffer);
        }
    }
}
