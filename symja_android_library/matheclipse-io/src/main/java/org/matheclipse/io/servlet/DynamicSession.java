package org.matheclipse.io.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.output.StringBuilderWriter;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.manipulate.Dynamics;
import org.matheclipse.core.manipulate.Interactions;
import org.matheclipse.core.manipulate.ManipulateControl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The live <code>Dynamic</code> cells of a browser session.
 *
 * <p>
 * This is what makes a <code>Dynamic</code> outside a <code>Manipulate</code> mean anything. The
 * two halves of the canonical example are separate cells:
 *
 * <pre>
 * In[1] := x = 0.5; Slider[Dynamic[x]]
 * In[2] := Dynamic[x]
 * </pre>
 *
 * and moving the slider in the first has to change what the second shows. Nothing about the second
 * cell knows the first one exists; they are related only in that both mention <code>x</code>, and
 * <code>x</code> is one symbol of one session because a session is one engine.
 *
 * <p>
 * So that is what is tracked here. Every cell that mentions a <code>Dynamic</code> is remembered
 * with the symbols it watches. A write from any cell bumps a counter and names the symbols it
 * touched, and every cell watching one of them is evaluated again and sent back. The browser is
 * told which cells changed, and replaces exactly those.
 *
 * <p>
 * Unlike a <code>Manipulate</code>, whose variables live in a <code>Block</code> for the length of
 * one frame, a dynamic cell reads and writes the session's own symbols - which is precisely why the
 * effect crosses cells, and why it outlives the cell that caused it.
 */
public class DynamicSession {

  /** How many cells one browser session keeps before the oldest is dropped. */
  private static final int MAX_CELLS_PER_SESSION = 64;

  /** One live cell: the expression it shows and the controls it draws. */
  static final class Cell {

    /** The expression as it was evaluated, with its <code>Dynamic</code> wrappers intact. */
    final IExpr expression;

    /** The symbols whose change makes this cell show something else. */
    final Set<String> tracked;

    /** How often the cell asks to be looked at again even when nothing changed, in seconds. */
    final double updateInterval;

    /** The controls of the last rendering, by the position the browser knows them under. */
    List<ManipulateControl> controls = new ArrayList<ManipulateControl>();

    /** The held actions of the <code>Button</code>s of the last rendering. */
    List<IExpr> actions = new ArrayList<IExpr>();

    Cell(IExpr expression, Set<String> tracked, double updateInterval) {
      this.expression = expression;
      this.tracked = tracked;
      this.updateInterval = updateInterval;
    }

    boolean watches(Set<String> changed) {
      for (String name : changed) {
        if (tracked.contains(name)) {
          return true;
        }
      }
      return false;
    }
  }

  /** The cells of each session, oldest first so the cache can drop from the front. */
  private static final Map<String, Map<String, Cell>> SESSIONS =
      new LinkedHashMap<String, Map<String, Cell>>();

  /**
   * How many writes a session has seen.
   *
   * <p>
   * A cell that has been off screen - a browser tab that was in the background, a request that was
   * lost - needs to know whether it missed anything. It sends the count it last saw and is told
   * everything that changed since.
   */
  private static final Map<String, Long> GENERATIONS = new LinkedHashMap<String, Long>();

  /** Counter for the cell ids; an id only has to be unique inside one session. */
  private static long cellCounter = 0L;

  private DynamicSession() {}

  // ---------------------------------------------------------------- registry

  /**
   * Whether a result is worth keeping as a live cell: it either shows something that changes, or
   * offers a control that changes it.
   */
  static boolean isDynamicResult(IExpr expr) {
    return Dynamics.containsDynamic(expr);
  }

  static synchronized String store(String sessionID, Cell cell) {
    Map<String, Cell> cells = SESSIONS.get(sessionID);
    if (cells == null) {
      cells = new LinkedHashMap<String, Cell>(16, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Cell> eldest) {
          return size() > MAX_CELLS_PER_SESSION;
        }
      };
      SESSIONS.put(sessionID, cells);
    }
    String id = "dy" + (++cellCounter);
    cells.put(id, cell);
    return id;
  }

  static synchronized Cell lookup(String sessionID, String id) {
    Map<String, Cell> cells = SESSIONS.get(sessionID);
    return cells == null ? null : cells.get(id);
  }

  static synchronized boolean dispose(String sessionID, String id) {
    Map<String, Cell> cells = SESSIONS.get(sessionID);
    return cells != null && cells.remove(id) != null;
  }

  /** Forget every cell of a session that has ended. */
  public static synchronized void remove(String sessionID) {
    SESSIONS.remove(sessionID);
    GENERATIONS.remove(sessionID);
  }

  static synchronized long generation(String sessionID) {
    Long generation = GENERATIONS.get(sessionID);
    return generation == null ? 0L : generation.longValue();
  }

  private static synchronized long bumpGeneration(String sessionID) {
    long next = generation(sessionID) + 1L;
    GENERATIONS.put(sessionID, Long.valueOf(next));
    return next;
  }

  /** The cells of a session, as a snapshot that the caller may walk without holding the lock. */
  private static synchronized List<Map.Entry<String, Cell>> cellsOf(String sessionID) {
    Map<String, Cell> cells = SESSIONS.get(sessionID);
    return cells == null ? new ArrayList<Map.Entry<String, Cell>>()
        : new ArrayList<Map.Entry<String, Cell>>(cells.entrySet());
  }

  // ---------------------------------------------------------------- creating

  /**
   * Register a cell for a result that carries a <code>Dynamic</code> and render it for the first
   * time.
   *
   * @return the JSON result for the browser, in the <code>String[]</code> shape the servlet uses
   */
  static String[] create(EvalEngine engine, IExpr expr, StringBuilderWriter outWriter,
      StringBuilderWriter errorWriter) throws java.io.IOException {
    Set<String> tracked = new LinkedHashSet<String>();
    collectTracking(expr, tracked);
    Cell cell = new Cell(expr, tracked, smallestInterval(expr));
    String id = store(engine.getSessionID(), cell);
    ObjectNode rendering = render(engine, cell, outWriter, errorWriter);
    return JSONBuilder.createJSONDynamic(id, rendering, cell.updateInterval,
        generation(engine.getSessionID()));
  }

  /**
   * Evaluate a cell as it stands now and describe everything the browser has to put on screen: the
   * rendering itself and the controls that go into it.
   */
  static ObjectNode render(EvalEngine engine, Cell cell, StringBuilderWriter outWriter,
      StringBuilderWriter errorWriter) throws java.io.IOException {
    IExpr value;
    try {
      // the wrappers come off before the evaluation, so that whatever reads around them - a plot
      // option, a graphics primitive - sees the value; one the evaluation itself produced is
      // resolved afterwards
      value = Dynamics.resolve(engine.evaluate(Dynamics.releaseAll(cell.expression)), engine);
    } catch (RuntimeException rex) {
      value = F.stringx("Error: " + rex.getMessage());
    }
    Interactions interactions = new Interactions(engine);
    IExpr rewritten = interactions.rewrite(value);
    synchronized (DynamicSession.class) {
      cell.controls = interactions.getControls();
      cell.actions = interactions.getActions();
    }

    String[] rendered = AJAXQueryServlet.renderResult(engine, rewritten, outWriter, errorWriter);
    ObjectNode node = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    node.set("body", JSONBuilder.JSON_OBJECT_MAPPER.readTree(rendered[1]));
    ArrayNode controls = JSONBuilder.JSON_OBJECT_MAPPER.createArrayNode();
    for (ManipulateControl control : cell.controls) {
      controls.add(control.toJSON(JSONBuilder.JSON_OBJECT_MAPPER));
    }
    node.set("controls", controls);
    return node;
  }

  // ---------------------------------------------------------------- updating

  /**
   * Write the value a control produced, then re-render every cell that was watching what it wrote
   * to.
   *
   * @param id the cell the control belongs to
   * @param controlIndex the control's position in that cell's last rendering
   * @param value the value the browser produced, in the shape a control of that kind sends
   * @return the answer for the browser: the new generation and the cells that changed
   */
  static ObjectNode applyControl(EvalEngine engine, String sessionID, String id, int controlIndex,
      JsonNode value, StringBuilderWriter outWriter, StringBuilderWriter errorWriter)
      throws java.io.IOException {
    Cell cell = lookup(sessionID, id);
    ManipulateControl control = null;
    if (cell != null) {
      synchronized (DynamicSession.class) {
        if (controlIndex >= 0 && controlIndex < cell.controls.size()) {
          control = cell.controls.get(controlIndex);
        }
      }
    }
    if (control == null || control.isReadOnly() || !control.getDynamic().isAST()) {
      return changedSince(engine, sessionID, generation(sessionID), outWriter, errorWriter);
    }

    IAST dynamic = (IAST) control.getDynamic();
    IExpr written = ManipulateSession.valueOf(control, value, engine);
    if (!written.isPresent()) {
      return changedSince(engine, sessionID, generation(sessionID), outWriter, errorWriter);
    }
    Dynamics.assign(dynamic, written, engine);

    // whatever the setter touched, the symbols the target mentions are the ones that moved
    Set<String> changed = new LinkedHashSet<String>();
    Dynamics.collectSymbols(Dynamics.target(dynamic), changed);
    bumpGeneration(sessionID);
    return rerender(engine, sessionID, changed, outWriter, errorWriter);
  }

  /**
   * Run the action of a <code>Button</code> a cell drew, then re-render everything.
   *
   * <p>
   * An action is arbitrary code - <code>x = 0</code>, or a whole simulation step - so there is no
   * telling in advance what it touched. Every cell is looked at again rather than guessing.
   */
  static ObjectNode applyAction(EvalEngine engine, String sessionID, String id, int actionIndex,
      StringBuilderWriter outWriter, StringBuilderWriter errorWriter) throws java.io.IOException {
    Cell cell = lookup(sessionID, id);
    IExpr action = null;
    if (cell != null) {
      synchronized (DynamicSession.class) {
        if (actionIndex >= 0 && actionIndex < cell.actions.size()) {
          action = cell.actions.get(actionIndex);
        }
      }
    }
    if (action != null) {
      try {
        engine.evaluate(action);
      } catch (RuntimeException rex) {
        // an action that fails must not stop the cells from being refreshed
      }
      bumpGeneration(sessionID);
    }
    return rerenderAll(engine, sessionID, outWriter, errorWriter);
  }

  /**
   * The cells that have to be redrawn because the session moved on since the browser last heard,
   * for a cell asking on a timer or after a lost request.
   */
  static ObjectNode changedSince(EvalEngine engine, String sessionID, long since,
      StringBuilderWriter outWriter, StringBuilderWriter errorWriter) throws java.io.IOException {
    if (since >= generation(sessionID)) {
      ObjectNode answer = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
      answer.put("generation", generation(sessionID));
      answer.set("cells", JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode());
      return answer;
    }
    return rerenderAll(engine, sessionID, outWriter, errorWriter);
  }

  /**
   * Re-render a cell on its own, for an <code>UpdateInterval</code> timer: the cell asked to be
   * looked at again whether or not anything it tracks has changed.
   */
  static ObjectNode refresh(EvalEngine engine, String sessionID, String id,
      StringBuilderWriter outWriter, StringBuilderWriter errorWriter) throws java.io.IOException {
    ObjectNode cells = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    Cell cell = lookup(sessionID, id);
    if (cell != null) {
      cells.set(id, render(engine, cell, outWriter, errorWriter));
    }
    ObjectNode answer = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    answer.put("generation", generation(sessionID));
    answer.set("cells", cells);
    return answer;
  }

  private static ObjectNode rerender(EvalEngine engine, String sessionID, Set<String> changed,
      StringBuilderWriter outWriter, StringBuilderWriter errorWriter) throws java.io.IOException {
    ObjectNode cells = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    for (Map.Entry<String, Cell> entry : cellsOf(sessionID)) {
      if (entry.getValue().watches(changed)) {
        cells.set(entry.getKey(), render(engine, entry.getValue(), outWriter, errorWriter));
      }
    }
    ObjectNode answer = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    answer.put("generation", generation(sessionID));
    answer.set("cells", cells);
    return answer;
  }

  private static ObjectNode rerenderAll(EvalEngine engine, String sessionID,
      StringBuilderWriter outWriter, StringBuilderWriter errorWriter) throws java.io.IOException {
    ObjectNode cells = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    for (Map.Entry<String, Cell> entry : cellsOf(sessionID)) {
      cells.set(entry.getKey(), render(engine, entry.getValue(), outWriter, errorWriter));
    }
    ObjectNode answer = JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode();
    answer.put("generation", generation(sessionID));
    answer.set("cells", cells);
    return answer;
  }

  // ---------------------------------------------------------------- tracking

  /**
   * The symbols every <code>Dynamic</code> of an expression watches, gathered into one set.
   *
   * <p>
   * A cell is redrawn as a whole, so it is enough to know that something it mentions has moved;
   * which of its dynamics it was makes no difference to what has to happen next.
   */
  private static void collectTracking(IExpr expr, Set<String> tracked) {
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    if (Dynamics.isDynamic(ast)) {
      tracked.addAll(Dynamics.trackedSymbols(ast));
      // a control writes to its target, so the target is watched even when it is not displayed
      Dynamics.collectSymbols(Dynamics.target(ast), tracked);
      return;
    }
    for (int i = 0; i < ast.size(); i++) {
      collectTracking(ast.get(i), tracked);
    }
  }

  /** The shortest <code>UpdateInterval</code> any <code>Dynamic</code> of the cell asked for. */
  private static double smallestInterval(IExpr expr) {
    if (!expr.isAST()) {
      return Double.POSITIVE_INFINITY;
    }
    IAST ast = (IAST) expr;
    if (Dynamics.isDynamic(ast)) {
      return Dynamics.updateInterval(ast);
    }
    double smallest = Double.POSITIVE_INFINITY;
    for (int i = 0; i < ast.size(); i++) {
      smallest = Math.min(smallest, smallestInterval(ast.get(i)));
    }
    return smallest;
  }

  /** Visible for tests: the symbols a cell would watch. */
  public static Collection<String> trackedSymbolsOf(IExpr expr) {
    Set<String> tracked = new LinkedHashSet<String>();
    collectTracking(expr, tracked);
    return tracked;
  }
}
