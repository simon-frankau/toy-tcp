package name.arbitrary.toytcp.ppp.lcp.statemachine;

import name.arbitrary.toytcp.Buffer;
import name.arbitrary.toytcp.WriteBuffer;
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
    private static final byte TODO_ID = 0;
    private static final WriteBuffer TODO = null;

    private final LcpUpperLayerListener upperListener;
    private final ActionProcessor listener;
    private final LcpRestartCounter restartCounter;
    private final LcpConfigChecker configChecker;

    private State state = State.INITIAL;

    public LcpStateMachine(LcpUpperLayerListener upperListener,
                           ActionProcessor listener,
                           LcpRestartCounter restartCounter,
                           LcpConfigChecker configChecker) {
        this.upperListener = upperListener;
        this.listener = listener;
        this.restartCounter = restartCounter;
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
                restartCounter.onInitializeRestartCount();
                listener.sendTerminateRequest(TODO_ID, TODO);
                setState(State.CLOSING);
                break;
        }
    }

    @Override
    public void onConfigureRequest(byte identifier, List<Option> options) {
        logger.info("ConfigureRequest {} {}", identifier, options);
        checkNotDown();
        Option.ResponseType type = configChecker.processIncomingConfigRequest(options);
        switch (state) {
            case CLOSED:
                // Sorry, we're closed.
                listener.sendTerminateAcknowledge(TODO_ID, TODO);
                break;
            case STOPPED:
                restartCounter.onInitializeRestartCount();
                // Fall through
            case OPENED:
                listener.sendConfigureRequest(TODO_ID, null); // TODO
                // Fall through...
            case REQ_SENT:
            case ACK_SENT:
                switch (type) {
                    case ACCEPT:
                        listener.sendConfigureAcknowledge(identifier, options);
                        setState(State.ACK_SENT);
                        break;
                    case NAK:
                        listener.sendConfigureNak(identifier, configChecker.getConfigNakOptions());
                        setState(State.REQ_SENT);
                        break;
                    case REJECT:
                        listener.sendConfigureReject(identifier, configChecker.getConfigRejectOptions());
                        setState(State.REQ_SENT);
                        break;
                }
                break;
            case ACK_RCVD:
                switch (type) {
                    case ACCEPT:
                        listener.sendConfigureAcknowledge(identifier, options);
                        setState(State.OPENED);
                        break;
                    case NAK:
                        listener.sendConfigureNak(identifier, configChecker.getConfigNakOptions());
                        setState(State.ACK_RCVD);
                        break;
                    case REJECT:
                        listener.sendConfigureReject(identifier, configChecker.getConfigRejectOptions());
                        setState(State.ACK_RCVD);
                        break;
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
                listener.sendTerminateAcknowledge(TODO_ID, TODO);
                break;
            case ACK_RCVD:
            case OPENED:
                // Hmm. Already had ack. Return to base configuring state.
                listener.sendConfigureRequest(TODO_ID, null); // TODO
                setState(State.REQ_SENT);
                break;
            case REQ_SENT:
                restartCounter.onInitializeRestartCount();
                setState(State.ACK_RCVD);
                break;
            case ACK_SENT:
                restartCounter.onInitializeRestartCount();
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
                listener.sendTerminateAcknowledge(TODO_ID, TODO);
                break;
            case REQ_SENT:
                restartCounter.onInitializeRestartCount();
                // Fall through.
            case ACK_RCVD:
            case OPENED:
                listener.sendConfigureRequest(TODO_ID, null); // TODO
                setState(State.REQ_SENT);
                break;
            case ACK_SENT:
                restartCounter.onInitializeRestartCount();
                listener.sendConfigureRequest(TODO_ID, null); // TODO
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
        listener.sendTerminateAcknowledge(identifier, TODO);
        switch (state) {
            case ACK_RCVD:
            case ACK_SENT:
                setState(State.REQ_SENT);
                break;
            case OPENED:
                restartCounter.onZeroRestartCount();
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
                listener.sendConfigureRequest(TODO_ID, null); // TODO
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
        listener.sendCodeReject(TODO_ID, TODO);
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
                restartCounter.onInitializeRestartCount();
                listener.sendTerminateRequest(TODO_ID, TODO);
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
            listener.sendEchoReply(TODO_ID, TODO);
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
        logger.trace("Switching to state {}", newState);

        if (!isUpState(state) && isUpState(newState)) {
            upperListener.onThisLayerUp();
        }
        if (isUpState(state) && !isUpState(newState)) {
            upperListener.onThisLayerDown();
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
        restartCounter.onInitializeRestartCount();
        listener.sendConfigureRequest(TODO_ID, configChecker.getRequestedOptions());
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
