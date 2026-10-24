package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>ParametricRegion({f1, f2, ...}, {{u, umin, umax}, ...})</code> - the points
 * <code>{f1, f2, ...}</code> which the parameters sweep out.
 *
 * <p>
 * The region is brought into the canonical form
 * <code>ParametricRegion({{f1, f2, ...}, cond}, {u, ...})</code>, in which every parameter range
 * has become part of the condition <code>cond</code>:
 * <ul>
 * <li><code>{u, a, b}</code> gives <code>Inequality(a, LessEqual, u, LessEqual, b)</code>, an
 * infinite end drops the corresponding bound,</li>
 * <li>a bare parameter <code>u</code> adds no condition,</li>
 * <li>a range of a parameter which does not occur in the coordinates is dropped,</li>
 * <li>a condition which is already given comes first, the range conditions are appended to it,</li>
 * <li>without any condition the form is <code>ParametricRegion({f1, f2, ...}, {u, ...})</code>,
 * </li>
 * <li>a numeric range whose lower end exceeds its upper end gives <code>EmptyRegion(n)</code>.</li>
 * </ul>
 */
public class ParametricRegion extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.argSize() != 2 || !ast.arg2().isList()) {
      return F.NIL;
    }
    IAST functions = coordinateFunctions(ast);
    if (functions.isNIL()) {
      return F.NIL;
    }
    IExpr condition = hasCondition(ast) ? ast.arg1().second() : F.NIL;
    IAST ranges = (IAST) ast.arg2();
    if (ranges.forAll(x -> x.isVariable())) {
      // already canonical, apart from a trivially true condition
      if (condition.isTrue()) {
        return F.binaryAST2(S.ParametricRegion, functions, ranges);
      }
      return F.NIL;
    }

    IASTAppendable variables = F.ListAlloc(ranges.argSize());
    IASTAppendable conditions = F.ast(S.And, ranges.argSize() + 1);
    if (condition.isPresent() && !condition.isTrue()) {
      conditions.append(condition);
    }
    for (int i = 1; i <= ranges.argSize(); i++) {
      IExpr range = ranges.get(i);
      if (range.isVariable()) {
        variables.append(range);
        continue;
      }
      if (!range.isList3() || !range.first().isVariable()) {
        return F.NIL;
      }
      IExpr u = range.first();
      IExpr min = range.second();
      IExpr max = range.last();
      if (engine.evaluate(F.Greater(min, max)).isTrue()) {
        // no parameter value is admissible
        return F.unaryAST1(S.EmptyRegion, F.ZZ(functions.argSize()));
      }
      if (functions.isFree(u, true) && (condition.isNIL() || condition.isFree(u, true))) {
        // the parameter does not change the points
        continue;
      }
      variables.append(u);
      boolean noLowerBound = min.isNegativeInfinity();
      boolean noUpperBound = max.isInfinity();
      if (noLowerBound && noUpperBound) {
        continue;
      }
      if (noLowerBound) {
        conditions.append(F.LessEqual(u, max));
      } else if (noUpperBound) {
        conditions.append(F.GreaterEqual(u, min));
      } else {
        conditions.append(F.ast(new IExpr[] {min, S.LessEqual, u, S.LessEqual, max}, //
            S.Inequality));
      }
    }
    if (conditions.argSize() == 0) {
      return F.binaryAST2(S.ParametricRegion, functions, variables);
    }
    IExpr allConditions = conditions.argSize() == 1 ? conditions.arg1() : conditions;
    return F.binaryAST2(S.ParametricRegion, F.list(functions, allConditions), variables);
  }

  /** The first argument has the form <code>{{f1, f2, ...}, cond}</code>. */
  private static boolean hasCondition(IAST reg) {
    IExpr arg1 = reg.arg1();
    return arg1.isList2() && arg1.first().isList() && !arg1.second().isList();
  }

  /**
   * The coordinate functions <code>{f1, f2, ...}</code> of a <code>ParametricRegion</code>, with
   * or without a condition.
   *
   * @return {@link F#NIL} if <code>reg</code> has no vector of coordinate functions
   */
  public static IAST coordinateFunctions(IAST reg) {
    if (reg.argSize() < 1 || !reg.arg1().isList()) {
      return F.NIL;
    }
    IAST functions = hasCondition(reg) ? (IAST) reg.arg1().first() : (IAST) reg.arg1();
    if (functions.argSize() == 0 || functions.exists(x -> x.isList())) {
      return F.NIL;
    }
    return functions;
  }

  /**
   * The condition on the parameters of a <code>ParametricRegion</code> in canonical form.
   *
   * @return <code>True</code> if the region has no condition
   */
  public static IExpr parameterCondition(IAST reg) {
    return hasCondition(reg) ? reg.arg1().second() : S.True;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
