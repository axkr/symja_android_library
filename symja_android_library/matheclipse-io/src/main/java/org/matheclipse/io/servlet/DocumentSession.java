package org.matheclipse.io.servlet;

import java.io.IOException;
import java.util.UUID;
import org.matheclipse.core.manipulate.ManipulateSpec;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.ServletException;

/**
 * A session without a browser: the cells of a document are evaluated one after the other in an
 * engine of their own, and each answers with the JSON a notebook cell would have received.
 *
 * <p>
 * For a program that turns a document into something else than a live page - a static HTML file,
 * for one. It goes through the same code as the servlets, so a cell is read, evaluated, limited in
 * time and rendered exactly as it is in the notebook, and a <code>Manipulate</code> is kept as a
 * widget whose body can be evaluated again for other control values with
 * {@link #manipulateFrame(String, String)}, and a live <code>Dynamic</code> cell follows
 * {@link #dynamicControl(String, int, String)} and {@link #dynamicAction(String, int)}.
 *
 * <p>
 * Not thread safe. Close it to release the engine and the widgets.
 */
public final class DocumentSession implements AutoCloseable {

  private final AJAXQueryServlet cells;
  private final String sessionID;

  private DocumentSession(AJAXQueryServlet cells) {
    this.cells = cells;
    this.sessionID = "document-" + UUID.randomUUID();
  }

  /**
   * Start the kernel if this is the first use of it in this JVM, and open a session.
   *
   * @param relaxedSyntax <code>true</code> for the relaxed Symja syntax <code>sin(x)</code>,
   *        <code>false</code> for <code>Sin[x]</code>. The first session opened decides for the JVM,
   *        as the first servlet started does.
   */
  public static DocumentSession open(boolean relaxedSyntax) {
    AJAXQueryServlet cells = relaxedSyntax ? new AJAXQueryServlet() : new MMAAJAXQueryServlet();
    try {
      // the servlet is not deployed; its init() is what starts the kernel
      cells.init();
    } catch (ServletException ex) {
      throw new IllegalStateException(ex);
    }
    return new DocumentSession(cells);
  }

  /**
   * Evaluate the source of one cell.
   *
   * @param source one or more expressions, as a notebook cell holds them
   * @return the JSON a notebook cell would have received
   */
  public String evaluate(String source) {
    return cells.evaluate(null, sessionID, source == null ? "" : source.trim(), "", "");
  }

  /**
   * Evaluate the body of a <code>Manipulate</code> of this session for one set of control values.
   *
   * @param widgetId the <code>id</code> in the JSON the cell that built the widget answered with
   * @param bindingsJSON a JSON object of control name to value, in the shape the page of the
   *        notebook posts them: a number for a slider, the index of the choice for a setter, a
   *        boolean for a checkbox
   * @return the JSON of the rendered body
   */
  public String manipulateFrame(String widgetId, String bindingsJSON) {
    ManipulateSpec spec = ManipulateSession.lookup(sessionID, widgetId);
    if (spec == null) {
      return JSONBuilder.createJSONErrorString("No such widget in this session: " + widgetId);
    }
    try {
      JsonNode bindings = JSONBuilder.JSON_OBJECT_MAPPER.readTree(bindingsJSON);
      return AJAXManipulateServlet.evaluate(sessionID, spec, bindings, -1, -1, -1, null, widgetId);
    } catch (IOException ex) {
      return JSONBuilder
          .createJSONErrorString("Cannot read the control values: " + ex.getMessage());
    }
  }

  /**
   * Move a control that a live <code>Dynamic</code> cell of this session draws.
   *
   * @param cellId the <code>id</code> in the JSON the cell that built the live cell answered with
   * @param controlIndex the position of the control in the <code>controls</code> of that JSON
   * @param valueJSON the new value, in the shape the page of the notebook posts it
   * @return the JSON of every cell that has to be redrawn, by its id
   */
  public String dynamicControl(String cellId, int controlIndex, String valueJSON) {
    try {
      JsonNode value = JSONBuilder.JSON_OBJECT_MAPPER.readTree(valueJSON);
      return dynamicUpdate(cellId, controlIndex, value, -1);
    } catch (IOException ex) {
      return JSONBuilder.createJSONErrorString("Cannot read the control value: " + ex.getMessage());
    }
  }

  /**
   * Press a <code>Button</code> that a live cell of this session draws.
   *
   * @param actionIndex the <code>data-action</code> of the button in the rendering of the cell
   * @return the JSON of every live cell of the session, by its id
   */
  public String dynamicAction(String cellId, int actionIndex) {
    try {
      return dynamicUpdate(cellId, -1, null, actionIndex);
    } catch (IOException ex) {
      return JSONBuilder.createJSONErrorString("Error: " + ex.getMessage());
    }
  }

  private String dynamicUpdate(String cellId, int controlIndex, JsonNode value, int actionIndex)
      throws IOException {
    AJAXQueryServlet.SessionState state = AJAXQueryServlet.stateOf(sessionID);
    if (state == null) {
      return JSONBuilder.createJSONErrorString("Nothing has been evaluated in this session.");
    }
    return AJAXDynamicServlet.update(state, sessionID, cellId, controlIndex, value, actionIndex, 0L,
        false);
  }

  @Override
  public void close() {
    // the widgets are released first: their Deinitialization code still needs the engine
    ManipulateSession.remove(AJAXQueryServlet.engineOf(sessionID), sessionID);
    DynamicSession.remove(sessionID);
    AJAXQueryServlet.removeSession(sessionID);
    SessionSandbox.remove(sessionID);
  }
}
