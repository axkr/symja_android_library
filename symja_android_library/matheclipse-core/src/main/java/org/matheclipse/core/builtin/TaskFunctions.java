package org.matheclipse.core.builtin;

import java.util.List;
import java.util.UUID;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.tasks.EventLoop;
import org.matheclipse.core.eval.tasks.ScheduledTaskEntry;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Scheduled tasks: <code>SessionSubmit[ScheduledTask[expr, period]]</code> and what removes them
 * again.
 *
 * <p>
 * A task runs on the evaluating thread, when the {@link EventLoop} is next pumped - inside a
 * <code>Pause</code>, a blocking socket read, or between the top-level statements of a script. It
 * therefore sees the same definitions as the code that submitted it, and never runs beside it.
 */
public class TaskFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.SessionSubmit.setEvaluator(new SessionSubmit());
        S.TaskRemove.setEvaluator(new TaskRemove());
        S.TaskExecute.setEvaluator(new TaskExecute());
        S.Tasks.setEvaluator(new Tasks());
      }
    }
  }

  /**
   * <code>SessionSubmit[ScheduledTask[expr, spec]]</code> and <code>SessionSubmit[expr]</code>.
   *
   * <p>
   * The specification is a number of seconds, a <code>Quantity</code> of time, <code>{delay}</code>
   * for something that happens once, or <code>{delay, n}</code> for something that happens
   * <code>n</code> times.
   */
  private static final class SessionSubmit extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr held;
      long delayNanos = 0;
      long periodNanos = -1;
      int runs = -1;
      if (arg1.isAST(S.ScheduledTask, 3)) {
        held = F.Hold(arg1.first());
        IExpr spec = engine.evaluate(arg1.second());
        double period = seconds(spec, engine);
        if (spec.isList()) {
          IAST list = (IAST) spec;
          double delay = seconds(list.arg1(), engine);
          if (Double.isNaN(delay)) {
            return S.$Failed;
          }
          delayNanos = (long) (delay * 1_000_000_000L);
          if (list.argSize() >= 2) {
            runs = list.arg2().toIntDefault(1);
            periodNanos = delayNanos;
          }
        } else {
          if (Double.isNaN(period)) {
            return S.$Failed;
          }
          periodNanos = (long) (period * 1_000_000_000L);
          delayNanos = periodNanos;
        }
      } else {
        // an expression on its own runs once, as soon as the loop is pumped
        held = F.Hold(arg1);
      }
      ScheduledTaskEntry task = new ScheduledTaskEntry(UUID.randomUUID().toString(), held,
          delayNanos, periodNanos, runs);
      EventLoop.INSTANCE.schedule(task);
      return taskObject(task, "Running");
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>TaskRemove[task]</code>. */
  private static final class TaskRemove extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return F.mapList((IAST) arg1, x -> evaluate(F.unaryAST1(S.TaskRemove, x), engine));
      }
      String uuid = uuidOf(arg1);
      if (uuid == null) {
        return S.$Failed;
      }
      ScheduledTaskEntry task = EventLoop.INSTANCE.task(uuid);
      EventLoop.INSTANCE.remove(uuid);
      return task == null ? S.$Failed : taskObject(task, "Removed");
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>TaskExecute[task]</code>: run it now, without waiting for it to come due. */
  private static final class TaskExecute extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String uuid = uuidOf(ast.arg1());
      ScheduledTaskEntry task = uuid == null ? null : EventLoop.INSTANCE.task(uuid);
      if (task == null) {
        return S.$Failed;
      }
      task.run(engine);
      return taskObject(task, "Running");
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>Tasks[]</code>. */
  private static final class Tasks extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      List<ScheduledTaskEntry> tasks = EventLoop.INSTANCE.tasks();
      IASTAppendable result = F.ListAlloc(tasks.size());
      for (ScheduledTaskEntry task : tasks) {
        result.append(taskObject(task, "Running"));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }
  }

  /**
   * <code>TaskObject[&lt;|"TaskUUID" -> …|&gt;]</code>: a plain expression, because the patterns
   * that match it - <code>t_TaskObject</code> - want an ordinary head.
   */
  private static IExpr taskObject(ScheduledTaskEntry task, String status) {
    return F.unaryAST1(S.TaskObject,
        F.assoc(F.List(F.Rule(F.stringx("TaskUUID"), F.stringx(task.uuid())),
            F.Rule(F.stringx("TaskType"), F.stringx("ScheduledTask")),
            F.Rule(F.stringx("TaskStatus"), F.stringx(status)),
            F.Rule(F.stringx("Evaluation"), task.expression()))));
  }

  /** The name inside a <code>TaskObject</code>, however it was handed over. */
  private static String uuidOf(IExpr expr) {
    if (expr.isString()) {
      return expr.toString();
    }
    if (expr.isAST(S.TaskObject, 2)) {
      IExpr data = expr.first();
      if (data instanceof org.matheclipse.core.interfaces.IAssociation) {
        IExpr uuid =
            ((org.matheclipse.core.interfaces.IAssociation) data).getValue(F.stringx("TaskUUID"));
        if (uuid != null && uuid.isString()) {
          return uuid.toString();
        }
      }
      if (data.isString()) {
        return data.toString();
      }
    }
    return null;
  }

  /** For <code>DeleteObject</code> of a task. */
  static boolean removeTask(IExpr taskData) {
    String uuid = uuidOf(F.unaryAST1(S.TaskObject, taskData));
    return uuid != null && EventLoop.INSTANCE.remove(uuid);
  }

  /**
   * How many seconds a period is: a number, or a <code>Quantity</code> of time.
   *
   * @return <code>Double.NaN</code> when it is neither
   */
  private static double seconds(IExpr spec, EvalEngine engine) {
    if (spec.isList()) {
      return Double.NaN;
    }
    if (spec.isNumber()) {
      return spec.evalf();
    }
    if (spec.isAST(S.Quantity, 3)) {
      IExpr magnitude =
          engine.evaluate(F.QuantityMagnitude(F.UnitConvert(spec, F.stringx("Seconds"))));
      if (magnitude.isNumber()) {
        return magnitude.evalf();
      }
    }
    return Double.NaN;
  }

  public static void initialize() {
    Initializer.init();
  }

  private TaskFunctions() {}
}
