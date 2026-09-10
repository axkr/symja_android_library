package org.matheclipse.core.expression.data;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>ProcessObject[…]</code>: a process this kernel started, or one it found by its number.
 *
 * <p>
 * Asked for a property it answers from the operating system, so <code>process["ExitCode"]</code> is
 * what the process has done by now rather than what it had done when the object was made.
 */
public class ProcessObjectExpr extends DataExpr<ProcessHandle> {

  private static final long serialVersionUID = 1L;

  /** The process a {@link Process} runs, kept so that its streams stay reachable. */
  private final transient Process process;

  public static ProcessObjectExpr newInstance(Process process) {
    return new ProcessObjectExpr(process.toHandle(), process);
  }

  public static ProcessObjectExpr newInstance(ProcessHandle handle) {
    return new ProcessObjectExpr(handle, null);
  }

  private ProcessObjectExpr(ProcessHandle handle, Process process) {
    super(S.ProcessObject, handle);
    this.process = process;
  }

  /** The process itself, when this kernel started it. */
  public Process process() {
    return process;
  }

  public ProcessHandle handle() {
    return fData;
  }

  @Override
  public IExpr evaluateHead(IAST ast, EvalEngine engine) {
    if (!ast.isAST1() || !ast.arg1().isString()) {
      return F.NIL;
    }
    switch (ast.arg1().toString()) {
      case "PID":
      case "ProcessID":
        return F.ZZ(fData.pid());
      case "Program":
        return F.stringx(fData.info().command().orElse(""));
      case "ExitCode":
        if (process == null || process.isAlive()) {
          return S.Null;
        }
        return F.ZZ(process.exitValue());
      case "Status":
        return F.stringx(fData.isAlive() ? "Running" : "Finished");
      case "Properties":
        return F.List(F.stringx("PID"), F.stringx("Program"), F.stringx("ExitCode"),
            F.stringx("Status"));
      default:
        return F.NIL;
    }
  }

  @Override
  public IAST fullForm() {
    return F.unaryAST1(S.ProcessObject, F.ZZ(fData.pid()));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    return obj instanceof ProcessObjectExpr && fData.pid() == ((ProcessObjectExpr) obj).fData.pid();
  }

  @Override
  public int hashCode() {
    return 223 + Long.hashCode(fData.pid());
  }

  @Override
  public IExpr copy() {
    return new ProcessObjectExpr(fData, process);
  }

  @Override
  public String toString() {
    return "ProcessObject[" + fData.pid() + ", " + (fData.isAlive() ? "Running" : "Finished") + "]";
  }
}
