package org.matheclipse.io.servlet;

import org.apache.commons.io.output.StringBuilderWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.steps.DialogCommand;
import org.matheclipse.core.eval.steps.DialogStep;
import org.matheclipse.core.eval.steps.DialogStepsListener;
import org.matheclipse.core.eval.steps.StepDialog;
import org.matheclipse.core.eval.steps.StepLevel;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * An evaluation which is being stepped through, one step at a time, by a reader in the browser.
 *
 * <p>
 * A request cannot hold an evaluation open across the pauses - it would be killed by the request
 * timeout long before the reader had finished looking - so the evaluation gets a thread of its own
 * and the two sides pass a step one way and a command the other. Each request wakes the evaluation
 * with what the reader pressed and comes back with the step it reaches next, so a request is never
 * open for longer than one step takes.
 *
 * <p>
 * A reader who closes the tab leaves an evaluation parked on a queue. It is not left there: the
 * wait has a deadline, and when it passes the evaluation gives up by itself and the dialog is
 * dropped.
 */
public class TraceDialogSession {

  /** How many dialogs one browser session may have open. */
  private static final int MAX_DIALOGS_PER_SESSION = 4;

  /** How long an evaluation waits for the reader before it gives up, in seconds. */
  private static final long READER_TIMEOUT_SECONDS = 600;

  /** How long a request waits for the evaluation to reach its next step, in seconds. */
  private static final long STEP_TIMEOUT_SECONDS = 30;

  /** What a request is told: either the step the evaluation stopped at, or that it is over. */
  static final class Frame {

    /** The step the evaluation is waiting at, or <code>null</code> when it has finished. */
    final DialogStep step;

    /** The whole derivation, once the evaluation is over. */
    final IAST traceForm;

    /** What an expression the reader asked about evaluates to. */
    final IExpr answer;

    Frame(DialogStep step, IAST traceForm, IExpr answer) {
      this.step = step;
      this.traceForm = traceForm;
      this.answer = answer;
    }

    boolean isFinished() {
      return step == null;
    }
  }

  /** One open dialog. */
  static final class Dialog implements StepDialog {

    final String id;

    /** Steps handed from the evaluation to whoever is waiting for one. */
    private final BlockingQueue<Frame> frames = new ArrayBlockingQueue<Frame>(1);

    /** Commands handed from a request to the waiting evaluation. */
    private final BlockingQueue<DialogCommand> commands = new ArrayBlockingQueue<DialogCommand>(1);

    /** What the reader asked to have evaluated at the current step, if anything. */
    private volatile IExpr question = F.NIL;

    /** The answer to that question, evaluated where the evaluation stands. */
    private volatile IExpr answer = F.NIL;

    private volatile boolean over = false;

    Dialog(String id) {
      this.id = id;
    }

    /** Called on the evaluating thread: hand the step over and wait for what to do with it. */
    @Override
    public DialogCommand atStep(DialogStep step) {
      try {
        answer = F.NIL;
        frames.put(new Frame(step, null, F.NIL));
        while (true) {
          DialogCommand command = commands.poll(READER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
          if (command == null) {
            // the reader is gone
            return DialogCommand.ABORT;
          }
          if (question.isPresent()) {
            // a question about the evaluation, not a command: answer it and keep waiting
            IExpr asked = question;
            question = F.NIL;
            answer = step.evaluate(asked);
            frames.put(new Frame(step, null, answer));
            continue;
          }
          return command;
        }
      } catch (InterruptedException iex) {
        Thread.currentThread().interrupt();
        return DialogCommand.ABORT;
      }
    }

    /** Called on a request thread: let the evaluation go on and wait for where it gets to. */
    Frame advance(DialogCommand command, IExpr ask) throws InterruptedException {
      if (over) {
        return null;
      }
      question = ask == null ? F.NIL : ask;
      commands.put(command);
      return frames.poll(STEP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /** Called on a request thread: wait for the first step. */
    Frame first() throws InterruptedException {
      return frames.poll(STEP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /** Called on the evaluating thread when there is nothing left to show. */
    void finished(IAST traceForm) {
      over = true;
      try {
        frames.put(new Frame(null, traceForm, F.NIL));
      } catch (InterruptedException iex) {
        Thread.currentThread().interrupt();
      }
    }

    boolean isOver() {
      return over;
    }
  }

  /** The open dialogs of each session, oldest first so the cache can drop from the front. */
  private static final Map<String, Map<String, Dialog>> SESSIONS =
      SessionRegistry.bySession("stepped-through evaluations");

  /** Counter for the dialog ids; an id only has to be unique inside one session. */
  private static long dialogCounter = 0L;

  private TraceDialogSession() {}

  /** Is this a call the notebook should step through rather than simply evaluate? */
  public static boolean isTraceDialog(IExpr expr) {
    return expr.isAST(S.TraceDialog) && ((IAST) expr).argSize() >= 1;
  }

  /**
   * Start stepping through a <code>TraceDialog(...)</code> and come back with its first step.
   *
   * <p>
   * The evaluation gets an engine of its own rather than the session's. A dialog stands still for
   * as long as the reader takes to press a button, and the session's engine is held by one
   * evaluation at a time - borrowing it would stop every other cell of the notebook for minutes.
   * The definitions the reader has made are still in force: those belong to the symbols, which one
   * session shares, not to the engine.
   *
   * @param ast the <code>TraceDialog(...)</code> as the reader wrote it
   */
  static synchronized Dialog start(String sessionID, boolean relaxedSyntax, IAST ast, int maxDepth,
      int stepLevel) {
    final String id = "tracedialog" + (++dialogCounter);
    final Dialog dialog = new Dialog(id);
    Thread thread = new Thread(() -> {
      IAST traceForm;
      EvalEngine engine = new EvalEngine(sessionID + "-" + id, Config.DEFAULT_RECURSION_LIMIT,
          Config.DEFAULT_ITERATION_LIMIT, System.out, System.err, relaxedSyntax);
      try {
        engine.setOutListDisabled(true, (short) 0);
        EvalEngine.set(engine);
        traceForm = DialogStepsListener.evaluate(engine, ast.arg1(), dialog, maxDepth, stepLevel);
      } catch (RuntimeException rex) {
        traceForm = F.TraceForm(F.HoldForm(S.$Failed), F.CEmptyList);
      } finally {
        EvalEngine.remove();
      }
      dialog.finished(traceForm);
    }, "symja-tracedialog-" + id);
    thread.setDaemon(true);

    Map<String, Dialog> dialogs = SESSIONS.get(sessionID);
    if (dialogs == null) {
      dialogs = new LinkedHashMap<String, Dialog>();
      SESSIONS.put(sessionID, dialogs);
    }
    while (dialogs.size() >= MAX_DIALOGS_PER_SESSION) {
      String oldest = dialogs.keySet().iterator().next();
      dialogs.remove(oldest);
    }
    dialogs.put(id, dialog);
    thread.start();
    return dialog;
  }

  /**
   * Start stepping through a <code>TraceDialog(...)</code> cell and answer the browser with its
   * first step.
   *
   * @param engine the session's engine, used only to read its settings and to write the answer
   * @param ast the <code>TraceDialog(...)</code> as the reader wrote it
   */
  public static String[] begin(EvalEngine engine, boolean relaxedSyntax, IAST ast) {
    int maxDepth = Integer.MAX_VALUE;
    int stepLevel = StepLevel.RULE;
    if (ast.size() > 2) {
      int depth = ast.arg2().isInfinity() ? Integer.MAX_VALUE : ast.arg2().toIntDefault(-1);
      if (depth < 0) {
        return JSONBuilder.createJSONError(
            "The depth of TraceDialog should be a non-negative integer or Infinity.");
      }
      maxDepth = depth;
    }
    if (ast.size() > 3) {
      stepLevel = StepLevel.parse(ast.arg3());
      if (stepLevel < 0) {
        return JSONBuilder.createJSONError(
            "The level of TraceDialog should be Rule, Algebra or Arithmetic.");
      }
    }
    Dialog dialog = start(engine.getSessionID(), relaxedSyntax, ast, maxDepth, stepLevel);
    try {
      Frame frame = dialog.first();
      if (frame == null) {
        dispose(engine.getSessionID(), dialog.id);
        return JSONBuilder.createJSONError("The evaluation did not reach a first step.");
      }
      if (frame.isFinished()) {
        // nothing to step through: show the derivation the way TraceForm does
        dispose(engine.getSessionID(), dialog.id);
        return JSONBuilder.createJSONSteps(engine, frame.traceForm, new StringBuilderWriter(),
            new StringBuilderWriter());
      }
      return JSONBuilder.createJSONTraceDialog(engine, dialog.id, frame.step, null, null);
    } catch (InterruptedException iex) {
      Thread.currentThread().interrupt();
      dispose(engine.getSessionID(), dialog.id);
      return JSONBuilder.createJSONError("The derivation was interrupted.");
    }
  }

  static synchronized Dialog lookup(String sessionID, String id) {
    Map<String, Dialog> dialogs = SESSIONS.get(sessionID);
    return dialogs == null ? null : dialogs.get(id);
  }

  static synchronized void dispose(String sessionID, String id) {
    Map<String, Dialog> dialogs = SESSIONS.get(sessionID);
    if (dialogs != null) {
      dialogs.remove(id);
    }
  }

  /** Drop everything of a browser session which has gone away. */
  public static synchronized void remove(String sessionID) {
    SESSIONS.remove(sessionID);
  }
}
