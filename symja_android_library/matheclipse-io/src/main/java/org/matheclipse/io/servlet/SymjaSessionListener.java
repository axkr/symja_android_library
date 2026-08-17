package org.matheclipse.io.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Releases everything a browser session holds on the server once that session ends.
 *
 * <p>
 * Three maps are keyed by the session id and would otherwise keep one entry per browser session for
 * as long as the server runs: the evaluation engine with the whole session state, the lock that
 * serializes its evaluations, and the <code>Manipulate</code> widgets with their held bodies.
 */
public class SymjaSessionListener implements HttpSessionListener {

  private static final Logger LOGGER = LogManager.getLogger(SymjaSessionListener.class);

  @Override
  public void sessionDestroyed(HttpSessionEvent event) {
    String sessionID = event.getSession().getId();
    LOGGER.debug("Session {} ended, releasing its engine and widgets", sessionID);
    // the widgets are released first: their Deinitialization code still needs the engine
    ManipulateSession.remove(AJAXQueryServlet.ENGINES.get(sessionID), sessionID);
    AJAXQueryServlet.removeSession(sessionID);
  }
}
