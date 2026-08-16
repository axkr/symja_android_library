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

    int buttonIndex = -1;
    String buttonParameter = req.getParameter("button");
    if (buttonParameter != null) {
      try {
        buttonIndex = Integer.parseInt(buttonParameter);
      } catch (NumberFormatException nfe) {
        buttonIndex = -1;
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
        out.println(evaluate(engine, spec, bindings, buttonIndex, outWriter, errorWriter));
      }
    } finally {
      EvalEngine.remove();
    }
  }

  /**
   * Evaluate the body under a time limit, the same way the query servlet guards an evaluation: a
   * body that does not finish must not hold on to the request thread.
   */
  private static String evaluate(EvalEngine engine, ManipulateSpec spec, JsonNode bindings,
      int buttonIndex, StringBuilderWriter outWriter, StringBuilderWriter errorWriter) {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    Future<String> task = executor.submit(() -> {
      try {
        EvalEngine.set(engine);
        ObjectNode updated = null;
        JsonNode effective = bindings;
        if (buttonIndex >= 0) {
          updated = ManipulateSession.runButtonAction(engine, spec, bindings, buttonIndex);
          if (updated != null) {
            // the action may have moved controls; render for the values it left behind
            ObjectNode merged = bindings.deepCopy();
            merged.setAll(updated);
            effective = merged;
          }
        }
        IExpr result = ManipulateSession.evaluateBody(engine, spec, effective);
        String[] rendered =
            AJAXQueryServlet.renderResult(engine, result, outWriter, errorWriter);
        return withBindings(rendered[1], updated);
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

  /** Add the control values a button action wrote to the result the browser gets back. */
  private static String withBindings(String resultJSON, ObjectNode updated) {
    if (updated == null || updated.isEmpty()) {
      return resultJSON;
    }
    try {
      JsonNode tree = JSONBuilder.JSON_OBJECT_MAPPER.readTree(resultJSON);
      if (tree instanceof ObjectNode) {
        ((ObjectNode) tree).set("bindings", updated);
        return tree.toString();
      }
    } catch (Exception ex) {
      // fall through and answer without the binding update
    }
    return resultJSON;
  }
}
