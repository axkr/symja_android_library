package org.matheclipse.io.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.steps.DialogCommand;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.io.servlet.TraceDialogSession.Dialog;
import org.matheclipse.io.servlet.TraceDialogSession.Frame;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Drives an evaluation which is being stepped through, one request per step.
 *
 * <p>
 * The reader presses a button, this lets the waiting evaluation go on, and the answer is the step
 * it reaches next - or the whole derivation, when there was nothing left to do. A request is
 * therefore never open for longer than one step takes, however long the reader spends looking at
 * the one before.
 *
 * <p>
 * <code>action</code> says what was pressed: <code>continue</code> for the next step,
 * <code>finish</code> to let it run to the end, <code>abort</code> to give up, and
 * <code>eval</code> to ask what an expression comes to where the evaluation stands.
 */
public class AJAXTraceDialogServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json; charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-cache");
    PrintWriter out = res.getWriter();

    HttpSession session = req.getSession();
    String sessionID = session.getId();
    String id = req.getParameter("id");
    if (id == null) {
      out.println(JSONBuilder.createJSONErrorString("No dialog id posted!"));
      return;
    }
    Dialog dialog = TraceDialogSession.lookup(sessionID, id);
    if (dialog == null) {
      out.println(JSONBuilder.createJSONErrorString(
          "This derivation has expired - evaluate the input again."));
      return;
    }
    if (req.getParameter("dispose") != null) {
      TraceDialogSession.dispose(sessionID, id);
      out.println("{\"disposed\": true}");
      return;
    }

    EvalEngine engine = AJAXQueryServlet.engineOf(sessionID);
    if (engine == null) {
      out.println(JSONBuilder.createJSONErrorString(
          "This derivation has expired - evaluate the input again."));
      return;
    }

    String action = req.getParameter("action");
    IExpr question = null;
    DialogCommand command = DialogCommand.CONTINUE;
    if ("finish".equals(action)) {
      command = DialogCommand.FINISH;
    } else if ("abort".equals(action)) {
      command = DialogCommand.ABORT;
    } else if ("eval".equals(action)) {
      String expression = req.getParameter("expr");
      if (expression == null || expression.trim().isEmpty()) {
        out.println(JSONBuilder.createJSONErrorString("Nothing to evaluate."));
        return;
      }
      try {
        question = engine.parse(expression);
      } catch (RuntimeException rex) {
        out.println(JSONBuilder.createJSONSyntaxErrorString(rex.getMessage()));
        return;
      }
    }

    try {
      Frame frame = dialog.advance(command, question);
      if (frame == null) {
        // the evaluation did not reach a next step in time; the reader can ask again
        out.println(JSONBuilder
            .createJSONErrorString("The evaluation is still working on the next step."));
        return;
      }
      if (frame.isFinished()) {
        TraceDialogSession.dispose(sessionID, id);
      }
      String[] result = JSONBuilder.createJSONTraceDialog(engine, id, frame.step, frame.traceForm,
          frame.answer);
      out.println(result[1]);
    } catch (InterruptedException iex) {
      Thread.currentThread().interrupt();
      out.println(JSONBuilder.createJSONErrorString("The derivation was interrupted."));
    } catch (RuntimeException rex) {
      out.println(JSONBuilder.createJSONErrorString(
          "Cannot step through the evaluation: " + rex.getMessage()));
    }
  }

  /** So the browser can start a dialog again after a reload without posting a body. */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    doPost(req, res);
  }
}
