package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.Buffer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static name.arbitrary.toytcp.ppp.lcp.LcpStateMachine.State.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/*
 * Exercises the state machine against RFC 1661.
 *
 * Note that our state machine is slightly different, basically in how it send TLS/TLF, so that you should never
 * get repeated TLS, TLS or TLF, TLF - i.e. to make it represent a clear running/finished state transition.
 *
 * TODO: No timeouts.
 */

@RunWith(MockitoJUnitRunner.class)
public class LcpStateMachineTest {
    @Mock
    private LcpConfigChecker configChecker;

    private final LcpStateActionListener stateActionListener = new ActionRecorder();
    private final Set<Actions> actions = EnumSet.noneOf(Actions.class);

    private LcpStateMachine stateMachine;
    private Runnable transition;

    enum Actions {
        IRC,
        SCR,
        TLD,
        SER,
        TLF,
        TLU,
        TLS,
        STA,
        STR,
        SCA,
        SCN,
        SCJ,
        ZRC
    }

    @Before
    public void setup() {
        stateMachine = new LcpStateMachine(stateActionListener, configChecker);
    }

    @Test
    public void testUp() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onLinkUp();
            }
        };

        checkTransition(INITIAL, CLOSED);
        checkTransition(STARTING, REQ_SENT, Actions.IRC, Actions.SCR);
    }

    @Test
    public void testDown() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onLinkDown();
            }
        };

        checkTransition(CLOSED, INITIAL);
        checkTransition(STOPPED, STARTING, Actions.TLS);
        checkTransition(CLOSING, INITIAL, Actions.TLF); // Extra TLF compared to RFC.
        checkTransition(STOPPING, STARTING);
        checkTransition(REQ_SENT, STARTING);
        checkTransition(ACK_RCVD, STARTING);
        checkTransition(ACK_SENT, STARTING);
        checkTransition(OPENED, STARTING, Actions.TLD);
    }

    @Test
    public void testOpen() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onOpen();
            }
        };

        checkTransition(INITIAL, STARTING, Actions.TLS);
        checkTransition(STARTING, STARTING);
        checkTransition(CLOSED, REQ_SENT, Actions.IRC, Actions.SCR, Actions.TLS); // Extra TLS compared to RFC
        checkTransition(STOPPED, STOPPED);
        checkTransition(CLOSING, STOPPING);
        checkTransition(STOPPING, STOPPING);
        checkTransition(REQ_SENT, REQ_SENT);
        checkTransition(ACK_RCVD, ACK_RCVD);
        checkTransition(ACK_SENT, ACK_SENT);
        checkTransition(OPENED, OPENED);
    }

    @Test
    public void testClose() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onClose();
            }
        };

        checkTransition(INITIAL, INITIAL);
        checkTransition(STARTING, INITIAL, Actions.TLF);
        checkTransition(CLOSED, CLOSED);
        checkTransition(STOPPED, CLOSED);
        checkTransition(CLOSING, CLOSING);
        checkTransition(STOPPING, CLOSING);
        checkTransition(REQ_SENT, CLOSING, Actions.IRC, Actions.STR);
        checkTransition(ACK_RCVD, CLOSING, Actions.IRC, Actions.STR);
        checkTransition(ACK_SENT, CLOSING, Actions.IRC, Actions.STR);
        checkTransition(OPENED, CLOSING, Actions.IRC, Actions.STR, Actions.TLD);
    }

    @Test
    public void testReceiveGoodConfigRequest() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onConfigureRequest((byte) 0, null);
            }
        };

        when(configChecker.isConfigAcceptable(any(List.class))).thenReturn(true);

        checkTransition(CLOSED, CLOSED, Actions.STA);
        checkTransition(STOPPED, ACK_SENT, Actions.IRC, Actions.SCR, Actions.SCA, Actions.TLS); // Extra TLS compared to RFC
        checkTransition(CLOSING, CLOSING);
        checkTransition(STOPPING, STOPPING);
        checkTransition(REQ_SENT, ACK_SENT, Actions.SCA);
        checkTransition(ACK_RCVD, OPENED, Actions.SCA, Actions.TLU);
        checkTransition(ACK_SENT, ACK_SENT, Actions.SCA);
        checkTransition(OPENED, ACK_SENT, Actions.TLD, Actions.SCR, Actions.SCA);
    }

    @Test
    public void testReceiveBadConfigRequest() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onConfigureRequest((byte)0, null);
            }
        };

        when(configChecker.isConfigAcceptable(any(List.class))).thenReturn(false);

        checkTransition(CLOSED, CLOSED, Actions.STA);
        checkTransition(STOPPED, REQ_SENT, Actions.IRC, Actions.SCR, Actions.SCN, Actions.TLS); // Extra TLS compared to RFC
        checkTransition(CLOSING, CLOSING);
        checkTransition(STOPPING, STOPPING);
        checkTransition(REQ_SENT, REQ_SENT, Actions.SCN);
        checkTransition(ACK_RCVD, ACK_RCVD, Actions.SCN);
        checkTransition(ACK_SENT, REQ_SENT, Actions.SCN);
        checkTransition(OPENED, REQ_SENT, Actions.TLD, Actions.SCR, Actions.SCN);
    }

    @Test
    public void testReceiveConfigAcknowledge() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onConfigureAck((byte) 0, null);
            }
        };

        checkTransition(CLOSED, CLOSED, Actions.STA);
        checkTransition(STOPPED, STOPPED, Actions.STA);
        checkTransition(CLOSING, CLOSING);
        checkTransition(STOPPING, STOPPING);
        checkTransition(REQ_SENT, ACK_RCVD, Actions.IRC);
        checkTransition(ACK_RCVD, REQ_SENT, Actions.SCR);
        checkTransition(ACK_SENT, OPENED, Actions.IRC, Actions.TLU);
        checkTransition(OPENED, REQ_SENT, Actions.TLD, Actions.SCR);
    }

    @Test
    public void testReceiveConfigNak() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onConfigureNak((byte) 0, null);
            }
        };

        checkTransition(CLOSED, CLOSED, Actions.STA);
        checkTransition(STOPPED, STOPPED, Actions.STA);
        checkTransition(CLOSING, CLOSING);
        checkTransition(STOPPING, STOPPING);
        checkTransition(REQ_SENT, REQ_SENT, Actions.IRC, Actions.SCR);
        checkTransition(ACK_RCVD, REQ_SENT, Actions.SCR);
        checkTransition(ACK_SENT, ACK_SENT, Actions.IRC, Actions.SCR);
        checkTransition(OPENED, REQ_SENT, Actions.TLD, Actions.SCR);
    }

    @Test
    public void testReceiveTerminateRequest() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onReceiveTerminateRequest((byte) 0, null);
            }
        };

        checkTransition(CLOSED, CLOSED, Actions.STA);
        checkTransition(STOPPED, STOPPED, Actions.STA);
        checkTransition(CLOSING, CLOSING, Actions.STA);
        checkTransition(STOPPING, STOPPING, Actions.STA);
        checkTransition(REQ_SENT, REQ_SENT, Actions.STA);
        checkTransition(ACK_RCVD, REQ_SENT, Actions.STA);
        checkTransition(ACK_SENT, REQ_SENT, Actions.STA);
        checkTransition(OPENED, STOPPING, Actions.TLD, Actions.ZRC, Actions.STA);
    }

    @Test
    public void testReceiveTerminateAcknowledge() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onReceiveTerminateAck((byte) 0, null);
            }
        };

        checkTransition(CLOSED, CLOSED);
        checkTransition(STOPPED, STOPPED);
        checkTransition(CLOSING, CLOSED, Actions.TLF);
        checkTransition(STOPPING, STOPPED, Actions.TLF);
        checkTransition(REQ_SENT, REQ_SENT);
        checkTransition(ACK_RCVD, REQ_SENT);
        checkTransition(ACK_SENT, ACK_SENT);
        checkTransition(OPENED, REQ_SENT, Actions.TLD, Actions.SCR);
    }

    @Test
    public void testReceiveUnknownCode() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onUnknownCode((byte) 0, (byte) 0, null);
            }
        };

        checkTransition(CLOSED, CLOSED, Actions.SCJ);
        checkTransition(STOPPED, STOPPED, Actions.SCJ);
        checkTransition(CLOSING, CLOSING, Actions.SCJ);
        checkTransition(STOPPING, STOPPING, Actions.SCJ);
        checkTransition(REQ_SENT, REQ_SENT, Actions.SCJ);
        checkTransition(ACK_RCVD, ACK_RCVD, Actions.SCJ);
        checkTransition(ACK_SENT, ACK_SENT, Actions.SCJ);
        checkTransition(OPENED, OPENED, Actions.SCJ);
    }

    @Test
    public void testPermittedCodeReject() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onCodeReject((byte) 0, null);
            }
        };

        testPermittedCodeOrProtocolReject();
    }

    @Test
    public void testPermittedProtocolReject() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onProtocolReject((byte) 0, null);
            }
        };

        testPermittedCodeOrProtocolReject();
    }

    private void testPermittedCodeOrProtocolReject() {
        when(configChecker.isRejectAcceptable(any(Buffer.class))).thenReturn(true);

        checkTransition(CLOSED, CLOSED);
        checkTransition(STOPPED, STOPPED);
        checkTransition(CLOSING, CLOSING);
        checkTransition(STOPPING, STOPPING);
        checkTransition(REQ_SENT, REQ_SENT);
        checkTransition(ACK_RCVD, REQ_SENT);
        checkTransition(ACK_SENT, ACK_SENT);
        checkTransition(OPENED, OPENED);
    }

    @Test
    public void testCatastrophicCodeReject() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onCodeReject((byte) 0, null);
            }
        };

        testCatastrophicCodeOrProtocolReject();
    }

    @Test
    public void testCatastrophicProtocolReject() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onCodeReject((byte) 0, null);
            }
        };

        testCatastrophicCodeOrProtocolReject();
    }

    private void testCatastrophicCodeOrProtocolReject() {
        when(configChecker.isRejectAcceptable(any(Buffer.class))).thenReturn(false);

        checkTransition(CLOSED, CLOSED); // No TLF, compared to RFC
        checkTransition(STOPPED, STOPPED); // No TLF, compared to RFC
        checkTransition(CLOSING, CLOSED, Actions.TLF);
        checkTransition(STOPPING, STOPPED, Actions.TLF);
        checkTransition(REQ_SENT, STOPPED, Actions.TLF);
        checkTransition(ACK_RCVD, STOPPED, Actions.TLF);
        checkTransition(ACK_SENT, STOPPED, Actions.TLF);
        checkTransition(OPENED, STOPPING, Actions.TLD, Actions.IRC, Actions.STR);
    }

    @Test
    public void testReceiveEchoRequest() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onEchoRequest((byte) 0, null);
            }
        };

        checkDoesNothingIfNotOpen();
        checkTransition(OPENED, OPENED, Actions.SER);
    }

    @Test
    public void testReceiveEchoReply() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onEchoReply((byte) 0, null);
            }
        };

        checkDoesNothingIfNotOpen();
        checkTransition(OPENED, OPENED);
    }

    @Test
    public void testReceiveDiscardRequest() {
        transition = new Runnable() {
            @Override
            public void run() {
                stateMachine.onDiscardRequest((byte) 0, null);
            }
        };

        checkDoesNothingIfNotOpen();
        checkTransition(OPENED, OPENED);
    }

    private void checkDoesNothingIfNotOpen() {
        checkTransition(CLOSED, CLOSED);
        checkTransition(STOPPED, STOPPED);
        checkTransition(CLOSING, CLOSING);
        checkTransition(STOPPING, STOPPING);
        checkTransition(REQ_SENT, REQ_SENT);
        checkTransition(ACK_RCVD, ACK_RCVD);
        checkTransition(ACK_SENT, ACK_SENT);
    }

    private void checkTransition(LcpStateMachine.State startState,
                                 LcpStateMachine.State endState,
                                 Actions... actions) {
        stateMachine.forceState(startState);
        this.actions.clear();
        transition.run();
        assertEquals(endState, stateMachine.getState());
        assertEquals(new HashSet<Actions>(Arrays.asList(actions)), this.actions);
    }

    private class ActionRecorder implements LcpStateActionListener {
        @Override
        public void onInitializeRestartCount() {
            add(Actions.IRC);
        }

        @Override
        public void onSendConfigureRequest() {
            add(Actions.SCR);
        }

        @Override
        public void onThisLayerDown() {
            add(Actions.TLD);
        }

        @Override
        public void onSendCodeReject() {
            add(Actions.SCJ);
        }

        @Override
        public void onSendEchoReply() {
            add(Actions.SER);
        }

        @Override
        public void onThisLayerFinished() {
            add(Actions.TLF);
        }

        @Override
        public void onThisLayerUp() {
            add(Actions.TLU);
        }

        @Override
        public void onThisLayerStarted() {
            add(Actions.TLS);
        }

        @Override
        public void onSendTerminateAcknowledge() {
            add(Actions.STA);
        }

        @Override
        public void onSendTerminateRequest() {
            add(Actions.STR);
        }

        @Override
        public void onSendConfigureAcknowledge() {
            add(Actions.SCA);
        }

        @Override
        public void onSendConfigureNak() {
            add(Actions.SCN);
        }

        @Override
        public void onZeroRestartCount() {
            add(Actions.ZRC);
        }

        private void add(Actions action) {
            assertFalse(actions.contains(action));
            actions.add(action);
        }
    }
}
