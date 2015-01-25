package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.ppp.lcp.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Handles the finite state automaton stuff
 *
 * Perhaps could be driven by a table, but by exploding it out and coding it manually, I hope to understand the
 * intention behind the automaton, and handle it a little more neatly.
 *
 * TODO: Very messy right now, obviously.
 *
 * TODO: No timeouts yet.
 */
public class LcpStateMachine implements EventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(LcpStateMachine.class);

    private State state = State.INITIAL;

    enum State {
        INITIAL,
        STARTING,
        CLOSED,
        STOPPED,
        CLOSING,
        STOPPING,
        REQ_SENT,
        ACK_RCVD,
        ACK_SENT,
        OPENED
    }

    @Override
    public void onLinkUp() {
        logger.info("Link up");
        switch (state) {
            case INITIAL:
                state = State.CLOSED;
                break;
            case STARTING:
                // TODO: irc, scr
                state = State.REQ_SENT;
                break;
            default:
                throw new RuntimeException("Link up in state " + state);
        }
    }

    @Override
    public void onLinkDown() {
        logger.info("Link down");
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("Link down in state " + state);
            case CLOSED:
            case CLOSING:
                state = State.INITIAL;
                break;
            case STOPPED:
                // TODO: tls
                state = State.STARTING;
                break;
            case OPENED:
                // TODO: tld
                state = State.STARTING;
                break;
            case STOPPING:
            case REQ_SENT:
            case ACK_RCVD:
            case ACK_SENT:
                state = State.STARTING;
                break;
        }
    }

    @Override
    public void onOpen() {
        logger.info("Link open");
        switch (state) {
            case INITIAL:
                // TODO tls
                state = State.STARTING;
                break;
            case STARTING:
                state = State.STARTING;
                break;
            case CLOSED:
                // TODO irc,scr
                state = State.REQ_SENT;
                break;
            case STOPPED:
            case CLOSING:
            case STOPPING:
            case OPENED:
                // TODO restart option
                break;
            case REQ_SENT:
            case ACK_RCVD:
            case ACK_SENT:
                // Remains in same state.
                break;
        }
    }

    @Override
    public void onClose() {
        logger.info("Link close");
        switch (state) {
            case INITIAL:
                state = State.INITIAL;
                break;
            case STARTING:
                // TODO tlf
                state = State.INITIAL;
                break;
            case CLOSED:
            case STOPPED:
                state = State.CLOSED;
                break;
            case CLOSING:
            case STOPPING:
                state = State.CLOSING;
                break;
            case REQ_SENT:
            case ACK_RCVD:
            case ACK_SENT:
                // TODO irc,str
                state = State.CLOSING;
                break;
            case OPENED:
                // TODO tld,irc,str
                state = State.CLOSING;
                break;
        }
    }

    @Override
    public void onConfigureRequest(byte identifier, List<Option> options) {
        boolean configOk = true;
        logger.info("ConfigureRequest {} {}", identifier, options);
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("Config request in state " + state);
            case CLOSED:
                // TODO sta
                break;
            case STOPPED:
                // TODO irc,scr,sc[an]
                if (configOk) {
                    state = State.ACK_SENT;
                } else {
                    state = State.REQ_SENT;
                }
                break;
            case CLOSING:
            case STOPPING:
                break;
            case REQ_SENT:
            case ACK_SENT:
                // TODO sc[an]
                state = State.ACK_SENT;
                if (configOk) {
                    state = State.ACK_SENT;
                } else {
                    state = State.REQ_SENT;
                }
                break;
            case ACK_RCVD:
                // TODO sc[an], tlu
                state = State.ACK_SENT;
                if (configOk) {
                    state = State.OPENED;
                } else {
                    state = State.REQ_SENT;
                }
                break;
            case OPENED:
                // TODO tld, scr, sc[an]
                if (configOk) {
                    state = State.ACK_SENT;
                } else {
                    state = State.REQ_SENT;
                }
                break;
        }
    }

    @Override
    public void onConfigureAck(byte identifier, List<Option> options) {
        logger.info("ConfigureAck {} {}", identifier, options);
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("Config ack in state " + state);
            case CLOSED:
            case STOPPED:
                // TODO sta
                break;
            case CLOSING:
            case STOPPING:
                break;
            case REQ_SENT:
                // TODO irc
                state = State.ACK_RCVD;
                break;
            case ACK_RCVD:
                // TODO scr/x
                state = State.REQ_SENT;
                break;
            case ACK_SENT:
                // TODO irc, tlu
                state = State.OPENED;
                break;
            case OPENED:
                // TODO tld,  scr x
                state = State.REQ_SENT;
                break;
        }
    }

    @Override
    public void onConfigureNak(byte identifier, List<Option> options) {
        logger.info("ConfigureNak {} {}", identifier, options);
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("Config nak in state " + state);
            case CLOSED:
            case STOPPED:
                // TODO sta
                break;
            case CLOSING:
            case STOPPING:
                break;
            case REQ_SENT:
                // TODO irc, scr
                break;
            case ACK_RCVD:
                // TODO scr/x
                state = State.REQ_SENT;
                break;
            case ACK_SENT:
                // TODO irc, scr
                state = State.ACK_SENT;
                break;
            case OPENED:
                // TODO tld,  scr x
                state = State.REQ_SENT;
                break;
        }
    }

    @Override
    public void onConfigureReject(byte identifier, List<Option> options) {
        logger.info("ConfigureReject {} {}", identifier, options);
        onConfigureNak(identifier, options); // TODO
    }

    @Override
    public void onReceiveTerminateRequest(byte identifier, Buffer buffer) {
        logger.info("ReceiveTerminateRequest {} {}", identifier, buffer);
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("Terminate request in state " + state);
            case CLOSED:
            case STOPPED:
            case CLOSING:
            case STOPPING:
            case REQ_SENT:
                // sta
                break;
            case ACK_RCVD:
            case ACK_SENT:
                // sta
                state = State.REQ_SENT;
                break;
            case OPENED:
                // tld, zrc, sta
                state = State.STOPPING;
                break;
        }
    }

    @Override
    public void onReceiveTerminateAck(byte identifier, Buffer buffer) {
        logger.info("ReceiveTerminateAck {} {}", identifier, buffer);
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("Terminate request in state " + state);
            case CLOSED:
            case STOPPED:
                break;
            case CLOSING:
                // tlf
                state = State.CLOSED;
                break;
            case STOPPING:
                // tlf
                state = State.STOPPING;
                break;
            case REQ_SENT:
            case ACK_RCVD:
                state = State.REQ_SENT;
                break;
            case ACK_SENT:
                break;
            case OPENED:
                // tld, src
                state = State.REQ_SENT;
                break;
        }
    }


    @Override
    public void onUnknownCode(byte code, byte identifier, Buffer buffer) {
        logger.warn("Received unknown code: {} {} {}", code, identifier, buffer);
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("Unknown code in state " + state);
            case CLOSED:
            case STOPPED:
            case CLOSING:
            case STOPPING:
            case REQ_SENT:
            case ACK_RCVD:
            case ACK_SENT:
            case OPENED:
                // scj
                break;
        }
    }

    @Override
    public void onCodeReject(byte identifier, Buffer rejected) {
        logger.info("CodeReject {} {}", identifier, rejected);
        receiveReject(identifier, rejected);
    }

    @Override
    public void onProtocolReject(byte identifier, Buffer rejected) {
        logger.info("ProtocolReject {} {}", identifier, rejected);
        receiveReject(identifier, rejected);
    }

    private void receiveReject(byte identifier, Buffer rejected) {
        boolean isOk = true;
        if (isOk) {
            receiveAcceptableReject(identifier, rejected);
        } else {
            receiveCatastrophicReject(identifier, rejected);
        }
    }

    private void receiveAcceptableReject(byte identifier, Buffer rejected) {
        logger.info("AcceptableReject");
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("AcceptableReject in state " + state);
            case ACK_RCVD:
                state = State.REQ_SENT;
                break;
        }
    }

    private void receiveCatastrophicReject(byte identifier, Buffer rejected) {
        logger.info("CatastrophicReject");
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("CatastrophicReject in state " + state);
            case CLOSED:
            case CLOSING:
                // tlf
                state = State.CLOSED;
                break;
            case STOPPED:
            case STOPPING:
            case REQ_SENT:
            case ACK_RCVD:
            case ACK_SENT:
                // tlf
                state = State.STOPPED;
                break;
            case OPENED:
                // tld, irc, str
                state = State.STOPPING;
                break;
        }
    }

    @Override
    public void onEchoRequest(byte identifier, Buffer buffer) {
        logger.info("EchoRequest {} {}", identifier, buffer);
        switch (state) {
            case INITIAL:
            case STARTING:
                throw new RuntimeException("EchoRequest in state " + state);
            case CLOSED:
            case STOPPED:
            case CLOSING:
            case STOPPING:
            case REQ_SENT:
            case ACK_RCVD:
            case ACK_SENT:
                break;
            case OPENED:
                // ser
                break;
        }
    }

    @Override
    public void onEchoReply(byte identifier, Buffer buffer) {
        logger.info("EchoReply {} {}", identifier, buffer);
        // No need to take further action...
    }

    @Override
    public void onDiscardRequest(byte identifier, Buffer buffer) {
        logger.info("DiscardRequest {} {}", identifier, buffer);
        // No need to take further action...
    }

    // Getter and setter for testing.
    State getState() {
        return state;
    }

    void setState(State state) {
        this.state = state;
    }
}
