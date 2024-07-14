package org.matheclipse.core.eval.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;
import org.matheclipse.core.eval.steps.output.JSONStepsTemplate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.AbstractEvalStepListener;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public final class TraceStackSteps extends AbstractEvalStepListener {


  // private static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  private final Stack<TraceInfo> fStack = new Stack<TraceInfo>();

  private final Predicate<IExpr> fMatcher;

  private final List<TraceInfo> fInfo;
  private TraceInfo fTraceList;

  public TraceStackSteps() {
    this(null);
  }

  public TraceStackSteps(Predicate<IExpr> matcher) {
    super();
    fMatcher = matcher;
    // fList = F.ListAlloc(7);
    fInfo = new ArrayList<>();
    pushList(null);
  }

  /**
   * Add the expression to the internal trace list, if the trace matcher returns <code>true</code>.
   *
   * @param result an expression
   */
  private void add(IExpr input, IExpr result, IExpr hint) {
    if (fMatcher != null) {
      if (fMatcher.test(result.head())) {
        fTraceList.append(input, F.HoldForm(result), hint);
      }
    } else {
      fTraceList.append(input, F.HoldForm(result), hint);
    }
  }

  @Override
  public void add(IExpr inputExpr, IExpr resultExpr, int recursionDepth, long iterationCounter,
      IExpr hint) {
    if (iterationCounter < 0L) {
      add(inputExpr, resultExpr, hint);
    }
  }


  /**
   * Create JSON output format from the math-steps trace.
   *
   * @param symbolFilter filter symbols like <code>D, Integrate, Plus, Power, Times ...</code> which
   *        should be included in the JSON output
   * @param ruleDescription
   * @return
   */
  public JSONStepsTemplate createJSONSteps(Predicate<ISymbol> symbolFilter,
      RuleDescription ruleDescription) {
    JSONStepsTemplate templateSteps = fTraceList.createJSONSteps(symbolFilter, ruleDescription);
    return templateSteps;
  }

  public void popList(IExpr result, IAST hints, boolean commitTraceFrame) {
    TraceInfo traceList = fTraceList;
    traceList.setResult(result);
    if (hints != null) {
      traceList.info = F.List(traceList.input, F.HoldForm(result), hints);
    }

    fStack.pop();
    fTraceList = fStack.peek();
    if ((traceList.size() > 0 || hints != null) && commitTraceFrame) {
      fTraceList.append(traceList);
    }

  }

  public void pushList(IExpr inputExpr) {
    fTraceList = new TraceInfo(fInfo);
    fTraceList.setInput(inputExpr);
    fTraceList.allocationStackTraceElements = new Exception().getStackTrace();
    fStack.push(fTraceList);
  }

  @Override
  public void setUp(IExpr inputExpr, int recursionDepth, Object stackMarker) {
    pushList(inputExpr);

    fTraceList.stackMarker = stackMarker;
  }

  @Override
  public void tearDown(IExpr result, int recursionDepth, boolean commitTraceFrame,
      Object stackMarker) {
    if (result == null) {
      // LOGGER.error("Its recommend to specify the result for {}", this.fTraceList.getInput());
    }

    if (stackMarker != null) {
      if (this.fTraceList.stackMarker != stackMarker) {
        throw new RuntimeException("System implementation error. "
            + "The stackMarker should match the previous one in the #setUp. "
            + "Check the fTraceList#allocationStackTraceElements for more details");
      }
    }

    popList(result, null, commitTraceFrame);
  }

  @Override
  public void tearDown(IExpr result, IAST hints, int recursionDepth, boolean commitTraceFrame,
      Object stackMarker) {
    popList(result, hints, commitTraceFrame);
  }

  public String toString(Predicate<ISymbol> filter) {
    return fTraceList.toString(filter);
  }

}
