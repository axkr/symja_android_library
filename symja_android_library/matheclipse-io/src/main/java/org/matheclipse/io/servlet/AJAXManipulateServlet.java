package org.matheclipse.io.servlet;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.util.WriterOutputStream;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.manipulate.ManipulateSpec;
import org.matheclipse.logging.ThreadLocalNotifyingAppender.ThreadLocalNotifierClosable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.MoreExecutors;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Re-evaluates the body of a <code>Manipulate</code> widget.
 *
 * <p>
 * <code>POST /ajax/manipulate/</code> with
 *
 * <pre>
 * id       the widget id handed out when the Manipulate was first evaluated
 * bindings a JSON object of control name to the value the browser holds
 * button   optional, the index of a Button control that was pressed
 * bodyButton optional, the index of a Button the body itself produced
 * bodyControl optional, the index of a control the body itself drew
 * bodyValue   the value that control produced, in the shape a panel control sends
 * dispose  optional, release the widget and run its Deinitialization code
 * </pre>
 *
 * The answer is the same JSON a plain evaluation produces, so the browser puts the body of a widget
 * on screen through exactly the code that renders any other result. A button press additionally
 * returns the control values its action wrote, so a control the action moved follows along.
 */
public class AJAXManipulateServlet extends HttpServlet {

  private static final long serialVersionUID = -4287201858582532663L;

  private static final Logger LOGGER = LogManager.getLogger(AJAXManipulateServlet.class);

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json; charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-cache");
    PrintWriter out = res.getWriter();

    String id = req.getParameter("id");
    if (id == null) {
      out.println(JSONBuilder.createJSONErrorString("No widget id posted!"));
      return;
    }
    HttpSession session = req.getSession();
    ManipulateSpec spec = ManipulateSession.lookup(session.getId(), id);
    if (spec == null) {
      // the session was restarted, or the widget fell out of the per session cache
      out.println(JSONBuilder
          .createJSONErrorString("This interactive output has expired - evaluate the input again."));
      return;
    }

    // a cell that was deleted releases its widget, which runs its Deinitialization code
    if (req.getParameter("dispose") != null) {
      EvalEngine disposeEngine = AJAXQueryServlet.ENGINES.get(session.getId());
      if (disposeEngine == null) {
        ManipulateSession.dispose(null, session.getId(), id);
      } else {
        synchronized (AJAXQueryServlet.sessionLock(session.getId())) {
          try {
            EvalEngine.set(disposeEngine);
            ManipulateSession.dispose(disposeEngine, session.getId(), id);
          } finally {
            EvalEngine.remove();
          }
        }
      }
      out.println("{\"disposed\": true}");
      return;
    }

    JsonNode bindings;
    try {
      String bindingsParameter = req.getParameter("bindings");
      bindings = bindingsParameter == null ? JSONBuilder.JSON_OBJECT_MAPPER.createObjectNode()
          : JSONBuilder.JSON_OBJECT_MAPPER.readTree(bindingsParameter);
    } catch (Exception ex) {
      out.println(JSONBuilder.createJSONErrorString("Cannot read the control values: "
          + ex.getMessage()));
      return;
    }

    int buttonIndex = intParameter(req, "button");
    // a Button the body itself produced, identified by its position in the last rendering
    int bodyButtonIndex = intParameter(req, "bodyButton");
    // a control the body itself drew - Slider[Dynamic[x]] and its relatives - and the value the
    // user produced with it, in the same shape a panel control sends
    int bodyControlIndex = intParameter(req, "bodyControl");
    JsonNode bodyControlValue = null;
    if (bodyControlIndex >= 0) {
      try {
        String posted = req.getParameter("bodyValue");
        bodyControlValue = posted == null ? null : JSONBuilder.JSON_OBJECT_MAPPER.readTree(posted);
      } catch (Exception ex) {
        out.println(JSONBuilder
            .createJSONErrorString("Cannot read the control value: " + ex.getMessage()));
        return;
      }
    }

    final StringBuilderWriter outWriter = new StringBuilderWriter();
    WriterOutputStream wouts = new WriterOutputStream(outWriter);
    final StringBuilderWriter errorWriter = new StringBuilderWriter();
    WriterOutputStream werrors = new WriterOutputStream(errorWriter);
    try (PrintStream outs = new PrintStream(wouts);
        PrintStream errors = new PrintStream(werrors);
        ThreadLocalNotifierClosable c = ServletServer.setLogEventNotifier(outs, errors)) {

      EvalEngine engine = AJAXQueryServlet.ENGINES.get(session.getId());
      if (engine == null) {
        out.println(JSONBuilder.createJSONErrorString(
            "This interactive output has expired - evaluate the input again."));
        return;
      }
      engine.setOutPrintStream(outs);
      engine.setErrorPrintStream(errors);

      // see AJAXQueryServlet#sessionLock: one evaluation per session at a time, and never on the
      // engine's own monitor - a time budgeted evaluation copies the engine from its worker thread
      synchronized (AJAXQueryServlet.sessionLock(session.getId())) {
        out.println(evaluate(engine, spec, bindings, buttonIndex, bodyButtonIndex, bodyControlIndex,
            bodyControlValue, id, outWriter, errorWriter));
      }
    } finally {
      EvalEngine.remove();
    }
  }

  /**
   * Evaluate the body under a time limit, the same way the query servlet guards an evaluation: a
   * body that does not finish must not hold on to the request thread.
   */
  private static int intParameter(HttpServletRequest req, String name) {
    String value = req.getParameter(name);
    if (value == null) {
      return -1;
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException nfe) {
      return -1;
    }
  }

  private static String evaluate(EvalEngine engine, ManipulateSpec spec, JsonNode bindings,
      int buttonIndex, int bodyButtonIndex, int bodyControlIndex, JsonNode bodyControlValue,
      String widgetId, StringBuilderWriter outWriter, StringBuilderWriter errorWriter) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<String> task = executor.submit(() -> {
      try {
        EvalEngine.set(engine);
        ObjectNode updated = null;
        JsonNode effective = bindings;
        if (buttonIndex >= 0 || bodyButtonIndex >= 0) {
          updated = buttonIndex >= 0
              ? ManipulateSession.runButtonAction(engine, spec, bindings, buttonIndex)
              : ManipulateSession.runBodyButtonAction(engine, spec, bindings, widgetId,
                  bodyButtonIndex);
        } else if (bodyControlIndex >= 0) {
          updated = ManipulateSession.applyBodyControl(engine, spec, bindings, widgetId,
              bodyControlIndex, bodyControlValue);
        }
        if (updated != null && !updated.isEmpty()) {
          // the write may have moved panel controls; render for the values it left behind
          ObjectNode merged = bindings.deepCopy();
          merged.setAll(updated);
          effective = merged;
        }
        IExpr result = ManipulateSession.registerBodyInteractions(engine, spec, effective,
            widgetId, ManipulateSession.evaluateBody(engine, spec, effective));
        String[] rendered =
            AJAXQueryServlet.renderResult(engine, result, outWriter, errorWriter);
        return withExtras(rendered[1], updated,
            ManipulateSession.resolveEnabled(engine, spec, effective),
            ManipulateSession.resolveVisible(engine, spec, effective),
            ManipulateSession.renderDisplays(engine, spec, effective, outWriter, errorWriter),
            ManipulateSession.bodyControlsJSON(widgetId));
      } catch (AbortException ae) {
        String[] aborted =
            AJAXQueryServlet.renderResult(engine, S.$Aborted, outWriter, errorWriter);
        return aborted[1];
      } catch (RuntimeException rex) {
        LOGGER.debug("Manipulate re-evaluation failed", rex);
        return JSONBuilder.createJSONErrorString("Error: " + rex.getMessage());
      }
    });
    try {
      return task.get(Config.SERVER_REQUEST_TIMEOUT_SECONDS * 1000L, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      return JSONBuilder.createJSONErrorString("Timeout exceeded. Calculation aborted!");
    } finally {
      if (!task.isDone()) {
        task.cancel(true);
      }
      MoreExecutors.shutdownAndAwaitTermination(executor, 1, TimeUnit.SECONDS);
    }
  }

  /**
   * Add what the browser needs besides the rendering: the control values a button action wrote, the
   * resolved <code>Enabled</code> state of every control, and the re-rendered read-out rows.
   */
  private static String withExtras(String resultJSON, ObjectNode updated,
      com.fasterxml.jackson.databind.node.ArrayNode enabled,
      com.fasterxml.jackson.databind.node.ArrayNode visible, ObjectNode displays,
      com.fasterxml.jackson.databind.node.ArrayNode bodyControls) {
    boolean hasBindings = updated != null && !updated.isEmpty();
    if (!hasBindings && enabled == null && visible == null && displays == null
        && bodyControls == null) {
      return resultJSON;
    }
    try {
      JsonNode tree = JSONBuilder.JSON_OBJECT_MAPPER.readTree(resultJSON);
      if (tree instanceof ObjectNode) {
        if (hasBindings) {
          ((ObjectNode) tree).set("bindings", updated);
        }
        if (enabled != null) {
          ((ObjectNode) tree).set("enabled", enabled);
        }
        if (visible != null) {
          ((ObjectNode) tree).set("visible", visible);
        }
        if (displays != null) {
          ((ObjectNode) tree).set("displays", displays);
        }
        if (bodyControls != null) {
          ((ObjectNode) tree).set("bodyControls", bodyControls);
        }
        return tree.toString();
      }
    } catch (Exception ex) {
      // fall through and answer with the rendering alone
    }
    return resultJSON;
  }
}
