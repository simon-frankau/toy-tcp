/**
 * The LcpStateMachine handles the state transitions and events.
 *
 * It implements EventProcessor, triggering events on an ActionProcessor.
 *
 * It contains little core logic in itself, instead delegating configuration-checking and timer handling to helper
 * classes.
 */
package name.arbitrary.toytcp.ppp.lcp.statemachine;