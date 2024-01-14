package org.matheclipse.core.eval;

import java.util.Stack;
import java.util.function.Predicate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.AbstractEvalStepListener;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

import javax.annotation.Nullable;

public final class TraceStack extends AbstractEvalStepListener {

  final Stack<IASTAppendable> fStack = new Stack<IASTAppendable>();
  final Predicate<IExpr> fMatcher;
  final IAST fList;
  IASTAppendable fTraceList;

  public TraceStack(Predicate<IExpr> matcher) {
    super();
    fMatcher = matcher;
    fList = F.ListAlloc(7);
    pushList();
  }

  public void pushList() {
    fTraceList = fList.copyAppendable(7);
    fStack.push(fTraceList);
  }

  public void popList(boolean commitTraceFrame) {
    IAST traceList = fTraceList;
    fStack.pop();
    fTraceList = fStack.peek();
    if (traceList.size() > 1 && commitTraceFrame) {
      fTraceList.append(traceList);
    }
  }

  public IASTAppendable getList() {
    return fTraceList;
  }

  /**
   * Add the expression to the internal trace list, if the trace matcher returns <code>true</code>.
   *
   * @param expr an expression
   */
  public void add(IExpr expr) {
    if (fMatcher != null) {
      if (fMatcher.test(expr.head())) {
        fTraceList.append(F.HoldForm(expr));
      }
    } else {
      fTraceList.append(F.HoldForm(expr));
    }
  }

  /**
   * Add the expression to the internal trace list, if the trace matcher returns <code>true</code>
   * and the trace lit is empty.
   *
   * @param expr an expression
   */
  public void addIfEmpty(IExpr expr) {
    if (fTraceList.isAST0()) {
      add(expr);
    }
  }

  public void setList(IASTAppendable list) {
    fTraceList = list;
    fStack.pop();
    fStack.push(list);
  }

  @Override
  public void setUp(IExpr inputExpr, int recursionDepth, @Nullable Object stackMarker) {
    pushList();
  }

  @Override
  public void tearDown(@Nullable IExpr result, int recursionDepth, boolean commitTraceFrame, @Nullable Object stackMarker) {
    popList(commitTraceFrame);
  }

  @Override
  public void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter,
      IExpr hint) {
    if (iterationCounter <= 0L) {
      addIfEmpty(inputExpr);
      add(resultExpr);
    } else {
      add(resultExpr);
    }
  }
}
