package name.arbitrary.toytcp.ppp.lcp;

import name.arbitrary.toytcp.ppp.lcp.options.Option;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigCheckerTest {
    private DefaultConfigChecker checker;

    @Mock
    private Option ackOption;
    @Mock
    private Option nakOption;
    @Mock
    private Option rejectOption;

    @Before
    public void setup() {
        checker = new DefaultConfigChecker();

        when(ackOption.getResponseType()).thenReturn(Option.ResponseType.ACCEPT);
        when(nakOption.getResponseType()).thenReturn(Option.ResponseType.NAK);
        when(rejectOption.getResponseType()).thenReturn(Option.ResponseType.REJECT);
    }

    @Test
    public void testEmptyConfigIsAcceptable() {
        assertEquals(Option.ResponseType.ACCEPT, checker.processIncomingConfigRequest(Collections.EMPTY_LIST));
    }

    @Test
    public void testAllRejectsAreCatastrophic() {
        assertFalse(checker.isRejectAcceptable(null));
    }

    @Test
    public void testAckOnlyConfigIsAcceptable() {
        assertEquals(Option.ResponseType.ACCEPT,
                     checker.processIncomingConfigRequest(Collections.singletonList(ackOption)));
    }

    @Test
    public void testNakUnacceptable() {
        List<Option> options = new ArrayList<Option>();
        options.add(ackOption);
        options.add(nakOption);
        assertEquals(Option.ResponseType.NAK,
                     checker.processIncomingConfigRequest(options));
    }

    @Test
    public void testRejectUnacceptable() {
        List<Option> options = new ArrayList<Option>();
        options.add(ackOption);
        options.add(nakOption);
        options.add(rejectOption);
        assertEquals(Option.ResponseType.REJECT,
                     checker.processIncomingConfigRequest(options));
    }
}