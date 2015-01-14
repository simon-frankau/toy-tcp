package name.arbitrary.toytcp;

import java.io.IOException;

/**
 * Class that runs the underlying 'pppd' executable.
 *
 * TODO: pppd wants to talk to a terminal. We'll have to deal with pseudoterminals. In the meantime, assume
 * we run inside pppd with stdin and stdout redirected, and don't use this class.
 */
public class PppProcessRunner {
    Process start() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("sudo", "/usr/bin/pppd");
        return pb.start();
    }
}
