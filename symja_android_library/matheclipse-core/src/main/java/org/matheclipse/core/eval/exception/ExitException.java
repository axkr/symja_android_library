package org.matheclipse.core.eval.exception;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.S;

/**
 * Exception thrown by {@link S#Exit} and {@link S#Quit} when Symja owns the process it runs in
 * ({@link Config#PROCESS_MODE}), so that a script can end the interpreter with an exit code the way
 * <code>wolframscript</code> does.
 *
 * <p>
 * It unwinds the evaluation the way the other {@link FlowControlException}s do, and the console
 * turns it into {@link System#exit(int)}. Every <code>catch</code> that swallows a
 * <code>RuntimeException</code> around an evaluation has to let it through, otherwise
 * <code>Exit[]</code> inside a package or an event handler would be silently ignored.
 */
public class ExitException extends FlowControlException {

  private static final long serialVersionUID = 1L;

  private final int exitCode;

  public ExitException(final int exitCode) {
    this.exitCode = exitCode;
  }

  /** The status the process should end with. */
  public int getExitCode() {
    return exitCode;
  }
}
