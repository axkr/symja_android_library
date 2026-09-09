package org.matheclipse.core.builtin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ProcessObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Starting other programs, and asking after the ones already started.
 *
 * <p>
 * A notebook uses these to look after the kernel it launched: it remembers the process id the child
 * reported, asks whether that process is still there, and ends it when the notebook is closed.
 */
public class ProcessFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.StartProcess.setEvaluator(new StartProcess());
        S.RunProcess.setEvaluator(new RunProcess());
        S.ProcessObject.setEvaluator(new ProcessObject());
        S.ProcessStatus.setEvaluator(new ProcessStatus());
        S.ProcessInformation.setEvaluator(new ProcessInformation());
        S.KillProcess.setEvaluator(new KillProcess());
        S.Run.setEvaluator(new Run());
      }
    }
  }

  private static IExpr sandbox(IAST ast, EvalEngine engine) {
    // The operation `1` is not allowed in sandbox mode.
    return Errors.printMessage(ast.topHead(), "sandbox", F.List(ast.topHead()), engine);
  }

  /** The command and its arguments, from a string or a list of them. */
  private static List<String> command(IExpr expr) {
    List<String> command = new ArrayList<String>();
    if (expr.isString()) {
      command.add(expr.toString());
      return command;
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      for (int i = 1; i < list.size(); i++) {
        if (!list.get(i).isString()) {
          return null;
        }
        command.add(list.get(i).toString());
      }
      return command.isEmpty() ? null : command;
    }
    return null;
  }

  /** Build a process, with the directory and environment the options ask for. */
  private static ProcessBuilder builder(List<String> command, IExpr directory, IExpr environment) {
    ProcessBuilder builder = new ProcessBuilder(command);
    if (directory != null && directory.isString()) {
      builder.directory(new File(directory.toString()));
    }
    if (environment != null && environment.isAssociation()) {
      Map<String, String> env = builder.environment();
      IAST association = (IAST) environment.normal(false);
      for (int i = 1; i < association.size(); i++) {
        IExpr rule = association.get(i);
        if (rule.isRuleAST() && rule.first().isString() && rule.second().isString()) {
          env.put(rule.first().toString(), rule.second().toString());
        }
      }
    }
    return builder;
  }

  /**
   * <code>StartProcess[{"cmd", "args"…}]</code>: start a program and let it run.
   *
   * <p>
   * The process's own output is drained by threads of its own, because a program whose output
   * nobody reads stops as soon as the pipe between them fills up.
   */
  private static class StartProcess extends AbstractFunctionOptionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      if (!Config.isOSAccessEnabled(engine)) {
        return sandbox(ast, engine);
      }
      List<String> command = command(ast.arg1());
      if (command == null) {
        return F.NIL;
      }
      try {
        ProcessBuilder builder = builder(command, options[0], options[1]);
        Process process = builder.start();
        drain(process.getInputStream());
        drain(process.getErrorStream());
        return ProcessObjectExpr.newInstance(process);
      } catch (IOException ioe) {
        return S.$Failed;
      }
    }

    /** Read a stream to its end and throw it away, so the process is never blocked writing. */
    private static void drain(InputStream stream) {
      Thread thread = new Thread(() -> {
        byte[] buffer = new byte[4096];
        try {
          while (stream.read(buffer) >= 0) {
            // the bytes are not wanted, only the room they take
          }
        } catch (IOException ioe) {
          //
        }
      }, "symja-process-drain");
      thread.setDaemon(true);
      thread.start();
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final org.matheclipse.core.interfaces.ISymbol newSymbol) {
      setOptions(newSymbol, //
          new org.matheclipse.core.interfaces.IBuiltInSymbol[] {S.ProcessDirectory,
              S.ProcessEnvironment}, //
          new IExpr[] {S.None, S.None});
    }
  }

  /**
   * <code>RunProcess[{"cmd", …}]</code>: run a program and wait for what it says.
   *
   * <p>
   * The result is an association of its exit code and its two outputs, or one of them when the
   * second argument names one.
   */
  private static class RunProcess extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return sandbox(ast, engine);
      }
      List<String> command = command(ast.arg1());
      if (command == null) {
        return F.NIL;
      }
      String what = ast.size() > 2 && ast.arg2().isString() ? ast.arg2().toString() : "All";
      String input = ast.size() > 3 && ast.arg3().isString() ? ast.arg3().toString() : null;
      try {
        Process process = new ProcessBuilder(command).start();
        if (input != null) {
          try (OutputStream stdin = process.getOutputStream()) {
            stdin.write(input.getBytes(StandardCharsets.UTF_8));
          }
        } else {
          process.getOutputStream().close();
        }
        String out = read(process.getInputStream());
        String err = read(process.getErrorStream());
        int code = process.waitFor();
        switch (what) {
          case "ExitCode":
            return F.ZZ(code);
          case "StandardOutput":
            return F.stringx(out);
          case "StandardError":
            return F.stringx(err);
          default:
            return F.assoc(F.List(//
                F.Rule(F.stringx("ExitCode"), F.ZZ(code)), //
                F.Rule(F.stringx("StandardOutput"), F.stringx(out)), //
                F.Rule(F.stringx("StandardError"), F.stringx(err))));
        }
      } catch (IOException ioe) {
        return S.$Failed;
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        return S.$Aborted;
      }
    }

    private static String read(InputStream stream) throws IOException {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      int read;
      while ((read = stream.read(buffer)) >= 0) {
        out.write(buffer, 0, read);
      }
      return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * <code>ProcessObject[pid]</code>: the process with this number.
   *
   * <p>
   * A number nothing is running under is a message and <code>$Failed</code>, which is what a
   * caller wrapping it in <code>Check</code> is looking for.
   */
  private static class ProcessObject extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1() instanceof ProcessObjectExpr) {
        return F.NIL;
      }
      if (!Config.isOSAccessEnabled(engine)) {
        return sandbox(ast, engine);
      }
      int pid = ast.arg1().toIntDefault();
      if (pid == Integer.MIN_VALUE) {
        return F.NIL;
      }
      Optional<ProcessHandle> handle = ProcessHandle.of(pid);
      if (!handle.isPresent()) {
        // No process with the process ID `1` was found.
        Errors.printMessage(S.ProcessObject, "nopid", F.List(ast.arg1()), engine);
        return S.$Failed;
      }
      return ProcessObjectExpr.newInstance(handle.get());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>ProcessStatus[process]</code>: <code>"Running"</code> or <code>"Finished"</code>. */
  private static class ProcessStatus extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ProcessObjectExpr)) {
        return F.NIL;
      }
      ProcessObjectExpr process = (ProcessObjectExpr) ast.arg1();
      String status = process.handle().isAlive() ? "Running" : "Finished";
      if (ast.isAST2()) {
        return F.booleSymbol(ast.arg2().toString().equals(status));
      }
      return F.stringx(status);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>ProcessInformation[process]</code>: what the operating system knows about it. */
  private static class ProcessInformation extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ProcessObjectExpr)) {
        return F.NIL;
      }
      ProcessHandle handle = ((ProcessObjectExpr) ast.arg1()).handle();
      ProcessHandle.Info info = handle.info();
      IASTAppendable rules = F.ListAlloc(4);
      rules.append(F.Rule(F.stringx("PID"), F.ZZ(handle.pid())));
      rules.append(F.Rule(F.stringx("Program"), F.stringx(info.command().orElse(""))));
      rules.append(F.Rule(F.stringx("Status"),
          F.stringx(handle.isAlive() ? "Running" : "Finished")));
      if (ast.isAST2() && ast.arg2().isString()) {
        for (int i = 1; i < rules.size(); i++) {
          if (rules.get(i).first().toString().equals(ast.arg2().toString())) {
            return rules.get(i).second();
          }
        }
        return F.Missing(F.stringx("KeyAbsent"), ast.arg2());
      }
      return F.assoc(rules);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>KillProcess[process]</code>: end it, and wait briefly for it to go. */
  private static class KillProcess extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof ProcessObjectExpr)) {
        return F.NIL;
      }
      ProcessHandle handle = ((ProcessObjectExpr) ast.arg1()).handle();
      handle.destroy();
      try {
        handle.onExit().get(2, TimeUnit.SECONDS);
      } catch (Exception e) {
        handle.destroyForcibly();
      }
      return S.Null;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>Run["command"]</code>: run a command line and answer its exit code. */
  private static class Run extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return sandbox(ast, engine);
      }
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      List<String> command = LinkFunctions.splitCommand(ast.arg1().toString());
      if (command.isEmpty()) {
        return F.NIL;
      }
      try {
        Process process = new ProcessBuilder(command).inheritIO().start();
        return F.ZZ(process.waitFor());
      } catch (IOException ioe) {
        return S.$Failed;
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        return S.$Aborted;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private ProcessFunctions() {}
}
