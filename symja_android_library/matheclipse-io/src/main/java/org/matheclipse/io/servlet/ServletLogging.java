package org.matheclipse.io.servlet;

import java.io.PrintStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;
import org.matheclipse.logging.ThreadLocalNotifyingAppender;
import org.matheclipse.logging.ThreadLocalNotifyingAppender.ThreadLocalNotifierClosable;

/**
 * Routes log4j2 events of the evaluating thread into the streams of the request being answered.
 *
 * <p>
 * This used to live in {@link ServletServer}, which is also the standalone Undertow launcher. Every
 * evaluation calls it, so answering a single request loaded that launcher - and the JVM verifies a
 * class as a whole, so a deployment that does not ship Undertow (an App Engine war has its own
 * container and no use for a second one) could fail to link it. Undertow is referenced nowhere in
 * here, so this class links with nothing but log4j2 present.
 */
final class ServletLogging {

  private ServletLogging() {}

  /**
   * Forward log events at <code>ERROR</code> or above to the given streams, until the returned
   * handle is closed.
   */
  static ThreadLocalNotifierClosable setLogEventNotifier(PrintStream outs, PrintStream errors) {

    return ThreadLocalNotifyingAppender.addLogEventNotifier(e -> {
      if (e.getLevel().isMoreSpecificThan(Level.ERROR)) {
        StringBuilder msg = new StringBuilder();
        Message logMessage = e.getMessage();
        if (logMessage != null) {
          msg.append(logMessage.getFormattedMessage());
        }
        Throwable thrown = e.getThrown();
        if (thrown != null) {
          msg.append(": ").append(thrown.getMessage());
        }
        PrintStream stream = e.getLevel().isMoreSpecificThan(Level.ERROR) ? errors : outs;
        stream.println(msg.toString());
      }
    });
  }
}
