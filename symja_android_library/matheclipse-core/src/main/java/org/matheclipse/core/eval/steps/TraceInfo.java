package org.matheclipse.core.eval.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.matheclipse.core.eval.steps.output.JSONStepsTemplate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class TraceInfo {

  // Sub-steps
  public final List<TraceInfo> fTraceInfos;

  // What input is calculating
  /* package private */ IExpr input;

  // The result after calculate the input.
  /* package private */ IExpr result;

  // Details about this trace
  /* package private */ IAST info = F.List();

  // debug property
  protected Object stackMarker;
  // debug property
  protected StackTraceElement[] allocationStackTraceElements;

  public TraceInfo(List<TraceInfo> fInfo) {
    fTraceInfos = new ArrayList<>(fInfo);
  }

  public void append(IExpr input, IAST result, IExpr info) {
    TraceInfo traceInfo = new TraceInfo(new ArrayList<>());
    traceInfo.info = F.List(input, result, info);
    fTraceInfos.add(traceInfo);
  }

  public void append(TraceInfo traceList) {
    fTraceInfos.add(traceList);
  }

  /**
   * Create JSON output format from the math-steps trace. Math-steps tracing should be disabled
   * during JSON output generation.
   *
   * @param symbolFilter filter symbols like <code>D, Integrate, Plus, Power, Times ...</code> which
   *        should be included in the JSON output
   * @param ruleDescription
   * @return
   */
  public JSONStepsTemplate createJSONSteps(Predicate<ISymbol> symbolFilter,
      RuleDescription ruleDescription) {
    TraceInfoJSON ti = new TraceInfoJSON(ruleDescription);
    return ti.toJSONStepsListRecursive(symbolFilter, 0, this);
  }

  public int size() {
    return fTraceInfos.size();
  }

  /**
   * Create plain text output format from the math-steps trace. Math-steps tracing should be
   * disabled during output generation.
   *
   * @param symbolFilter
   * @return
   */
  public String toString(Predicate<ISymbol> symbolFilter) {
    StringBuilder buf = new StringBuilder();
    toTreeRecursive(buf, symbolFilter, 0, info);
    return buf.toString();
  }

  /**
   * Print the trace tree recursively with the indented level.
   *
   * @param output
   * @param symbolFilter
   * @param level
   * @param infoList
   */
  private void toTreeRecursive(StringBuilder output, Predicate<ISymbol> symbolFilter, int level,
      IAST infoList) {
    for (int i = 1; i < infoList.size(); i++) {
      IAST frameList = (IAST) infoList.get(i);
      if (frameList.argSize() > 1) {
        IExpr result = frameList.arg2();
        if (result.isAST(S.HoldForm)) {
          IExpr input = frameList.arg1();
          IAST info = (IAST) frameList.arg3();
          ISymbol symbol = (ISymbol) info.first();
          if (!symbolFilter.test(symbol)) {
            continue;
          }
          IExpr holdFormArg1 = result.first();
          if (holdFormArg1.isIndeterminate() //
              || holdFormArg1 == S.Null) {
            continue;
          }
          if (holdFormArg1.isCondition()) {
            // the `if-condition` in `Condition(rhs-result, if-condition)` is evaluated to true
            // here, otherwise no entry will be written in the trace.
            // For output only display `rhs-result`.
            // See: PatternMatcherAndEvaluator#checkRHSCondition()
            holdFormArg1 = holdFormArg1.first();
          }
          IExpr ruleID = info.second();
          output.append("\n");
          // indent the output by level
          output.append("  ".repeat(Math.max(0, level)));
          output.append(input.toString());
          output.append(" -> ");
          output.append(holdFormArg1.toString());
          output.append(" $$");

          output.append(symbol.toString());
          output.append(" -- ");
          output.append(ruleID.toString());

          for (int j = 3; j < info.size(); j++) {
            output.append(" -- ");
            output.append(info.get(j).toString());
          }

          output.append("$$");
        } else {
          toTreeRecursive(output, symbolFilter, level + 1, frameList);
        }
      } else {
        toTreeRecursive(output, symbolFilter, level + 1, frameList);
      }
    }
  }

  public void setResult(IExpr result) {
    this.result = result;
  }

  public void setInput(IExpr input) {
    this.input = input;
  }


  public IExpr getInput() {
    return input;
  }


  public IExpr getResult() {
    return result;
  }

  public int argSize() {
    return info.argSize();
  }

  @Override
  public String toString() {
    return "TraceInfo{" + "input=" + input + ", result=" + result + ", info=" + info
        + ", fTraceInfos=" + fTraceInfos + '}';
  }
}
