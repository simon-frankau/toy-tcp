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
 * Yes, I'm one of those people who prefer recursive descent parsers to table-driven ones. :)
 *
 * TODO: No timeouts yet.
 * TODO: Still somewhat messy.
 */
public class LcpStateMachine implements EventProcessor {
    private static final Logger logger = LoggerFactory.getLogger(LcpStateMachine.class);

    private final LcpStateActionListener listener;
    private final LcpConfigChecker configChecker;

    private State state = State.INITIAL;

    public LcpStateMachine(LcpStateActionListener listener, LcpConfigChecker configChecker) {
        this.listener = listener;
        this.configChecker = configChecker;
    }

    enum State {
        INITIAL,  // Down, closed
        STARTING, // Down, open
        CLOSED,   // Up, closed
        STOPPED,  // Up, open
        CLOSING,
        STOPPING,
        REQ_SENT, // Up, open, sending
        ACK_RCVD,
        ACK_SENT,
        OPENED
    }

    @Override
    public void onLinkUp() {
        logger.info("Link up");
        if (state == State.INITIAL) {
            setState(State.CLOSED);
        } else if (state == State.STARTING) {
            initialRequest();
        } else {
            throw new RuntimeException("Should not happen: LinkUp when already up");
        }
    }

    @Override
    public void onLinkDown() {
        logger.info("Link down");
        checkNotDown(); // Shouldn't be down yet!
        if (state == State.CLOSED || state == State.CLOSING) {
            setState(State.INITIAL);
        } else {
            setState(State.STARTING);
        }
    }

    @Override
    public void onOpen() {
        logger.info("Link open");
        switch (state) {
            case INITIAL:
                setState(State.STARTING);
                break;
            case CLOSED:
                initialRequest();
                break;
            case CLOSING:
                setState(State.STOPPING);
                break;
        }
    }

    @Override
    public void onClose() {
        logger.info("Link close");
        switch (state) {
            case STARTING:
                setState(State.INITIAL);
                break;
            case STOPPED:
                setState(State.CLOSED);
                break;
            case STOPPING:
                setState(State.CLOSING);
                break;
            case REQ_SENT:
            case ACK_RCVD:
            case ACK_SENT:
            case OPENED:
                listener.onInitializeRestartCount();
                listener.onSendTerminateRequest();
                setState(State.CLOSING);
                break;
        }
    }

    @Override
    public void onConfigureRequest(byte identifier, List<Option> options) {
        logger.info("ConfigureRequest {} {}", identifier, options);
        checkNotDown();
        boolean configOk = configChecker.isConfigAcceptable(options);
        switch (state) {
            case CLOSED:
                // Sorry, we're closed.
                listener.onSendTerminateAcknowledge();
                break;
            case STOPPED:
                listener.onInitializeRestartCount();
                // Fall through
            case OPENED:
                listener.onSendConfigureRequest();
                // Fall through...
            case REQ_SENT:
            case ACK_SENT:
                if (configOk) {
                    listener.onSendConfigureAcknowledge();
                    setState(State.ACK_SENT);
                } else {
                    listener.onSendConfigureNak();
                    setState(State.REQ_SENT);
                }
                break;
            case ACK_RCVD:
                if (configOk) {
                    listener.onSendConfigureAcknowledge();
                    setState(State.OPENED);
                } else {
                    listener.onSendConfigureNak();
                    setState(State.ACK_RCVD);
                }
                break;
        }
    }

    @Override
    public void onConfigureAck(byte identifier, List<Option> options) {
        logger.info("ConfigureAck {} {}", identifier, options);
        checkNotDown();
        switch (state) {
            case CLOSED:
            case STOPPED:
                // Hmmm. Not right.
                listener.onSendTerminateAcknowledge();
                break;
            case ACK_RCVD:
            case OPENED:
                // Hmm. Already had ack. Return to base configuring state.
                listener.onSendConfigureRequest();
                setState(State.REQ_SENT);
                break;
            case REQ_SENT:
                listener.onInitializeRestartCount();
                setState(State.ACK_RCVD);
                break;
            case ACK_SENT:
                listener.onInitializeRestartCount();
                setState(State.OPENED);
                break;
        }
    }

    @Override
    public void onConfigureNak(byte identifier, List<Option> options) {
        logger.info("ConfigureNak {} {}", identifier, options);
        checkNotDown();
        switch (state) {
            case CLOSED:
            case STOPPED:
                // Hmmm. Not right.
                listener.onSendTerminateAcknowledge();
                break;
            case REQ_SENT:
                listener.onInitializeRestartCount();
                // Fall through.
            case ACK_RCVD:
            case OPENED:
                listener.onSendConfigureRequest();
                setState(State.REQ_SENT);
                break;
            case ACK_SENT:
                listener.onInitializeRestartCount();
                listener.onSendConfigureRequest();
                setState(State.ACK_SENT);
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
        checkNotDown();
        listener.onSendTerminateAcknowledge();
        switch (state) {
            case ACK_RCVD:
            case ACK_SENT:
                setState(State.REQ_SENT);
                break;
            case OPENED:
                listener.onZeroRestartCount();
                setState(State.STOPPING);
                break;
        }
    }

    @Override
    public void onReceiveTerminateAck(byte identifier, Buffer buffer) {
        logger.info("ReceiveTerminateAck {} {}", identifier, buffer);
        checkNotDown();
        switch (state) {
            case CLOSING:
                setState(State.CLOSED);
                break;
            case STOPPING:
                setState(State.STOPPED);
                break;
            case OPENED:
                listener.onSendConfigureRequest();
                // Fall through
            case REQ_SENT:
            case ACK_RCVD:
                setState(State.REQ_SENT);
                break;
            case ACK_SENT:
                break;
        }
    }

    @Override
    public void onUnknownCode(byte code, byte identifier, Buffer buffer) {
        logger.warn("Received unknown code: {} {} {}", code, identifier, buffer);
        checkNotDown();
        listener.onSendCodeReject();
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
        boolean isOk = configChecker.isRejectAcceptable(rejected);
        if (isOk) {
            receiveAcceptableReject(identifier, rejected);
        } else {
            receiveCatastrophicReject(identifier, rejected);
        }
    }

    private void receiveAcceptableReject(byte identifier, Buffer rejected) {
        logger.info("AcceptableReject");
        checkNotDown();
        if (state == State.ACK_RCVD) {
            setState(State.REQ_SENT);
        }
    }

    private void receiveCatastrophicReject(byte identifier, Buffer rejected) {
        logger.info("CatastrophicReject");
        checkNotDown();
        switch (state) {
            case CLOSING:
                setState(State.CLOSED);
                break;
            case OPENED:
                listener.onInitializeRestartCount();
                listener.onSendTerminateRequest();
                setState(State.STOPPING);
                break;
            case STOPPED:
            case STOPPING:
            case REQ_SENT:
            case ACK_RCVD:
            case ACK_SENT:
                setState(State.STOPPED);
                break;
        }
    }

    @Override
    public void onEchoRequest(byte identifier, Buffer buffer) {
        logger.info("EchoRequest {} {}", identifier, buffer);
        checkNotDown();
        if (state == State.OPENED) {
            listener.onSendEchoReply();
        }
    }

    @Override
    public void onEchoReply(byte identifier, Buffer buffer) {
        logger.info("EchoReply {} {}", identifier, buffer);
        checkNotDown();
        // No need to take further action...
    }

    @Override
    public void onDiscardRequest(byte identifier, Buffer buffer) {
        logger.info("DiscardRequest {} {}", identifier, buffer);
        checkNotDown();
        // No need to take further action...
    }

    private void checkNotDown() {
        // Most events should not be possible in a link-down state.
        if (state == State.INITIAL || state == State.STARTING) {
            throw new RuntimeException("Should not happen: Event received while down");
        }
    }

    void setState(State newState) {
        if (!isUpState(state) && isUpState(newState)) {
            listener.onThisLayerUp();
        }
        if (isUpState(state) && !isUpState(newState)) {
            listener.onThisLayerDown();
        }

        // Hmmm. I disagree with the state machine!
        if (!isFinishingState(state) && isFinishingState(newState)) {
            listener.onThisLayerFinished();
        }
        if (isFinishingState(state) && !isFinishingState(newState)) {
            listener.onThisLayerStarted();
        }

        this.state = newState;
    }

    // Initial move into Open and Up.
    private void initialRequest() {
        listener.onInitializeRestartCount();
        listener.onSendConfigureRequest();
        setState(State.REQ_SENT);
    }

    private boolean isUpState(State state) {
        return state == State.OPENED;
    }

    private boolean isFinishingState(State state) {
        return state == State.INITIAL || state == State.CLOSED || state == State.STOPPED;
    }

    // Getter and setter for testing.

    // Transition to state without ceremony.
    void forceState(State state) {
        this.state = state;
    }

    State getState() {
        return state;
    }
}
