package name.arbitrary.toytcp.ppp.lcp.statemachine;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;

import java.util.List;

/**
 * An interface to represent something that can process LCP frames/events.
 */
public interface EventProcessor {
    void onLinkUp();

    void onLinkDown();

    void onOpen();

    void onClose();

    void onConfigureRequest(byte identifier, List<Option> options);

    void onConfigureAck(byte identifier, List<Option> options);

    void onConfigureNak(byte identifier, List<Option> options);

    void onConfigureReject(byte identifier, List<Option> options);

    void onReceiveTerminateRequest(byte identifier, Buffer buffer);

    void onReceiveTerminateAck(byte identifier, Buffer buffer);

    void onCodeReject(byte identifier, Buffer rejected);

    void onProtocolReject(byte identifier, Buffer rejected);

    void onEchoRequest(byte identifier, Buffer buffer);

    void onEchoReply(byte identifier, Buffer buffer);

    void onDiscardRequest(byte identifier, Buffer buffer);

    void onUnknownCode(byte code, byte identifier, Buffer buffer);
}
