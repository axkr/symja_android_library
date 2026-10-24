package org.matheclipse.core.builtin;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.tasks.EventLoop;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.io.link.LinkEntry;
import org.matheclipse.core.io.link.LinkRegistry;

/**
 * Parallel kernels: separate processes of this interpreter, started with <code>-wstp</code> and
 * driven over a link, as a notebook drives its evaluation kernel.
 *
 * <ul>
 * <li><code>LaunchKernels[n]</code> starts <code>n</code> kernels and gives their
 * <code>KernelObject[id, "local"]</code>; <code>Kernels[]</code> lists the running ones and
 * <code>CloseKernels[]</code> ends them.</li>
 * <li><code>ParallelSubmit[expr]</code> gives an <code>EvaluationObject[id, HoldForm[expr]]</code>
 * without evaluating <code>expr</code>; <code>ParallelSubmit[{x, y}, expr]</code> puts the current
 * values of <code>x</code> and <code>y</code> into it first.</li>
 * <li><code>WaitAll[e]</code> evaluates every evaluation object in <code>e</code> on the kernels,
 * spread over them in turn, and replaces each by its result.</li>
 * </ul>
 *
 * A kernel is a fresh interpreter: it doesn't see the definitions of the kernel which started it,
 * only the expression it is sent. Without any kernel, an evaluation object is evaluated by the
 * kernel which waits for it.
 */
public class ParallelKernelFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.LaunchKernels.setEvaluator(new LaunchKernels());
        S.Kernels.setEvaluator(new Kernels());
        S.CloseKernels.setEvaluator(new CloseKernels());
        S.ParallelSubmit.setEvaluator(new ParallelSubmit());
        S.WaitAll.setEvaluator(new WaitAll());
      }
    }
  }

  /** How long one wait for a kernel's answer lasts before the loop looks again. */
  private static final long POLL_MILLIS = 5L;

  /** The kernels <code>LaunchKernels[]</code> starts when it is not told how many. */
  private static final int DEFAULT_KERNELS = 4;

  /** A running parallel kernel. */
  private static final class Kernel {
    final int id;
    final LinkEntry link;

    Kernel(int id, LinkEntry link) {
      this.id = id;
      this.link = link;
    }

    IAST object() {
      return F.binaryAST2(S.KernelObject, F.ZZ(id), F.stringx("local"));
    }
  }

  /** A submitted evaluation and, once it has come back, its result. */
  private static final class Job {
    final IExpr expr;
    IExpr result;

    Job(IExpr expr) {
      this.expr = expr;
    }
  }

  private static final List<Kernel> KERNELS = new ArrayList<Kernel>();
  private static final Map<Integer, Job> JOBS = new HashMap<Integer, Job>();
  private static int nextKernelId = 1;
  private static int nextJobId = 1;
  /** The kernel the next evaluation goes to, by its position in {@link #KERNELS}. */
  private static int nextKernel = 0;

  private static synchronized List<Kernel> runningKernels() {
    KERNELS.removeIf(k -> k.link.isClosed());
    return new ArrayList<Kernel>(KERNELS);
  }

  private static IExpr sandbox(IAST ast, EvalEngine engine) {
    // The operation `1` is not allowed in sandbox mode.
    return Errors.printMessage(ast.topHead(), "sandbox", F.List(ast.topHead()), engine);
  }

  /** <code>LaunchKernels[]</code>, <code>LaunchKernels[n]</code>. */
  private static final class LaunchKernels extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return sandbox(ast, engine);
      }
      int count = DEFAULT_KERNELS;
      if (ast.isAST1()) {
        count = ast.arg1().toIntDefault();
        if (count < 0) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(S.LaunchKernels, "intpm", F.list(ast, F.C1), engine);
        }
      }
      List<String> relaunch = Config.RELAUNCH_COMMAND;
      if (relaunch == null || relaunch.isEmpty()) {
        // no command which starts another copy of this interpreter: Symja runs embedded
        return Errors.printMessage(S.LaunchKernels,
            "This interpreter cannot start other kernels.");
      }
      IASTAppendable launched = F.ListAlloc(count);
      for (int i = 0; i < count; i++) {
        List<String> command = new ArrayList<String>(relaunch);
        command.add("-wstp");
        LinkEntry link = LinkRegistry.launch(command);
        if (link == null) {
          break;
        }
        Kernel kernel;
        synchronized (ParallelKernelFunctions.class) {
          kernel = new Kernel(nextKernelId++, link);
          KERNELS.add(kernel);
        }
        launched.append(kernel.object());
      }
      return launched;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /** <code>Kernels[]</code>: the running parallel kernels. */
  private static final class Kernels extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      List<Kernel> running = runningKernels();
      IASTAppendable list = F.ListAlloc(running.size());
      running.forEach(kernel -> list.append(kernel.object()));
      return list;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }
  }

  /** <code>CloseKernels[]</code>, <code>CloseKernels[kernel]</code>, <code>CloseKernels[{...}]</code>. */
  private static final class CloseKernels extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      List<Kernel> running = runningKernels();
      List<Kernel> toClose = new ArrayList<Kernel>();
      if (ast.isAST0()) {
        toClose.addAll(running);
      } else {
        IExpr arg1 = ast.arg1();
        IAST objects = arg1.isList() ? (IAST) arg1 : F.list(arg1);
        for (IExpr object : objects) {
          for (Kernel kernel : running) {
            if (object.equals(kernel.object()) || object.equals(F.ZZ(kernel.id))) {
              toClose.add(kernel);
            }
          }
        }
      }
      IASTAppendable closed = F.ListAlloc(toClose.size());
      for (Kernel kernel : toClose) {
        kernel.link.close();
        synchronized (ParallelKernelFunctions.class) {
          KERNELS.remove(kernel);
        }
        closed.append(kernel.object());
      }
      return closed;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /** <code>ParallelSubmit[expr]</code>, <code>ParallelSubmit[{x, ...}, expr]</code>. */
  private static final class ParallelSubmit extends AbstractCoreFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr expr = ast.arg1();
      if (ast.isAST2()) {
        if (!ast.arg1().isList()) {
          return F.NIL;
        }
        // the current values of the variables, so the kernel needn't know them
        Map<IExpr, IExpr> values = new HashMap<IExpr, IExpr>();
        for (IExpr variable : (IAST) ast.arg1()) {
          if (variable.isSymbol()) {
            values.put(variable, engine.evaluate(variable));
          }
        }
        expr = ast.arg2().replaceAll(x -> values.containsKey(x) ? values.get(x) : F.NIL)
            .orElse(ast.arg2());
      }
      int id;
      synchronized (ParallelKernelFunctions.class) {
        id = nextJobId++;
        JOBS.put(id, new Job(expr));
      }
      return F.binaryAST2(S.EvaluationObject, F.ZZ(id), F.HoldForm(expr));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   * <code>WaitAll[e]</code>: the results of the evaluation objects in <code>e</code>. An expression
   * with no evaluation object in it is left to other definitions of <code>WaitAll</code>, as
   * packages make them for their own objects.
   */
  private static final class WaitAll extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.isAST1()) {
        return F.NIL;
      }
      IExpr arg1 = ast.arg1();
      List<Integer> ids = new ArrayList<Integer>();
      collectJobs(arg1, ids);
      if (ids.isEmpty()) {
        return F.NIL;
      }
      run(ids, engine);
      return arg1.replaceAll(x -> {
        if (isEvaluationObject(x)) {
          Job job;
          synchronized (ParallelKernelFunctions.class) {
            job = JOBS.get(x.first().toIntDefault());
          }
          return job != null && job.result != null ? job.result : S.$Failed;
        }
        return F.NIL;
      }).orElse(arg1);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static boolean isEvaluationObject(IExpr x) {
    return x.isAST(S.EvaluationObject, 3) && x.first().isInteger();
  }

  private static void collectJobs(IExpr expr, List<Integer> ids) {
    if (isEvaluationObject(expr)) {
      ids.add(expr.first().toIntDefault());
      return;
    }
    if (expr.isASTOrAssociation()) {
      for (IExpr arg : (IAST) expr) {
        collectJobs(arg, ids);
      }
    }
  }

  /**
   * Evaluate the jobs which have no result yet. Every kernel is sent its share first, in turn, so
   * that they work at the same time; then the answers are read, a kernel's in the order it was
   * sent them.
   */
  private static void run(List<Integer> ids, EvalEngine engine) {
    List<Kernel> kernels = runningKernels();
    Map<Kernel, Deque<Job>> sent = new HashMap<Kernel, Deque<Job>>();
    for (int id : ids) {
      Job job;
      synchronized (ParallelKernelFunctions.class) {
        job = JOBS.get(id);
      }
      if (job == null || job.result != null || containsJob(sent, job)) {
        continue;
      }
      if (kernels.isEmpty()) {
        job.result = engine.evaluate(job.expr);
        continue;
      }
      Kernel kernel;
      synchronized (ParallelKernelFunctions.class) {
        kernel = kernels.get(nextKernel++ % kernels.size());
      }
      try {
        kernel.link.write(F.unaryAST1(S.EvaluatePacket, job.expr));
        sent.computeIfAbsent(kernel, k -> new ArrayDeque<Job>()).add(job);
      } catch (IOException ioe) {
        job.result = S.$Failed;
      }
    }
    for (Map.Entry<Kernel, Deque<Job>> entry : sent.entrySet()) {
      Kernel kernel = entry.getKey();
      for (Job job : entry.getValue()) {
        job.result = readResult(kernel, engine);
      }
    }
  }

  private static boolean containsJob(Map<Kernel, Deque<Job>> sent, Job job) {
    for (Deque<Job> jobs : sent.values()) {
      if (jobs.contains(job)) {
        return true;
      }
    }
    return false;
  }

  /** The next answer of a kernel; what it printed meanwhile is printed here. */
  private static IExpr readResult(Kernel kernel, EvalEngine engine) {
    while (true) {
      IExpr packet = kernel.link.poll();
      if (packet != null) {
        if (packet.isAST(S.ReturnPacket, 2)) {
          return packet.first();
        }
        if (packet.isAST(S.TextPacket, 2)) {
          String text = packet.first().toString();
          if (text.endsWith("\n")) {
            engine.getOutPrintStream().print(text);
          } else {
            engine.getOutPrintStream().println(text);
          }
        }
        // a MessagePacket names a message whose text came as a TextPacket already
        continue;
      }
      if (kernel.link.isClosed()) {
        return S.$Failed;
      }
      engine.checkTimeLimit();
      EventLoop.INSTANCE.pump(engine);
      EventLoop.INSTANCE.awaitEvent(POLL_MILLIS);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ParallelKernelFunctions() {}
}
