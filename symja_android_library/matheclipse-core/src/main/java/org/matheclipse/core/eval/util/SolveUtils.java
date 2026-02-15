package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

public class SolveUtils {

  /**
   * <code>result[0]</code> is the list of expressions <code>== 0</code> . <code>result[1]</code>are
   * the <code>Unequal, Less, LessEqual, Greater, GreaterEqual</code> expressions. If <code>
   * result[2].isPresent()</code> return the entry as solution.
   *
   * @param list
   * @param solution
   * @param isNumeric set isNumeric[0] = true, if an expression must be rationalized
   * @return
   */
  public static IASTMutable[] filterSolveLists(IAST list, IASTMutable solution,
      boolean[] isNumeric) {
    // if numeric is true we use NSolve instead of SOlve
    boolean numeric = isNumeric[0];
    IASTMutable[] result = new IASTMutable[3];
    IASTAppendable termsEqualZero = F.ListAlloc(list.size());
    IASTAppendable inequalityTerms = F.ListAlloc(list.size());
    result[0] = termsEqualZero;
    result[1] = inequalityTerms;
    result[2] = F.NIL;
    int i = 1;
    while (i < list.size()) {
      IExpr arg = list.get(i);
      if (arg.isTrue()) {
      } else if (arg.isFalse()) {
        // no solution possible
        result[2] = F.ListAlloc();
        return result;
      } else if (arg.isEqual()) {
        // arg must be Equal(_, 0)
        IExpr arg1 = arg.first();
        if (numeric) {
          // NSolve
          termsEqualZero.append(arg1);
        } else {
          // Solve
          IExpr temp = AbstractFractionSym.rationalize(arg1, false);
          if (temp.isPresent()) {
            isNumeric[0] = true;
            termsEqualZero.append(temp);
          } else {
            termsEqualZero.append(arg1);
          }
        }
      } else {
        inequalityTerms.append(arg);
      }
      i++;
    }
    EvalAttributes.sort(result[0]);
    EvalAttributes.sort(result[1]);
    if (result[0].isEmpty() && result[1].isEmpty()) {
      if (solution.isPresent()) {
        result[2] = solution;
      } else {
        result[2] = F.unary(S.List, F.List());
      }
      return result;
    }
    return result;
  }

  /**
   * Collect constant values from a conditional expression or a constant value that satisfy the
   * condition
   * <code>lower &quot;lowerSymbol&quot; constantValue &quot;upperSymbol&quot; upper</code>. *
   * 
   * @param valueExpr
   * @param lower
   * @param upper
   * @param lowerSymbol {@link S#Less} or {@link S#LessEqual}
   * @param upperSymbol {@link S#Less} or {@link S#LessEqual}
   * @param collector
   * @param engine
   */
  public static void collectConstants(IExpr valueExpr, IExpr lower, IExpr upper,
      IBuiltInSymbol lowerSymbol, IBuiltInSymbol upperSymbol, IASTAppendable collector,
      EvalEngine engine) {
    if (valueExpr.isConditionalExpression()) {
      IAST ast = (IAST) valueExpr;
      IExpr val = ast.arg1();
      IExpr condition = ast.arg2();
      IExpr integersDomainVariable = F.NIL;
      if (condition.isAST(S.Element, 3) && condition.second().equals(S.Integers)) {
        integersDomainVariable = condition.first();
      } else if (condition.isAnd()) {
        for (int i = 1; i < condition.size(); i++) {
          IExpr arg = condition.get(i);
          if (arg.isAST(S.Element, 3) && arg.second().equals(S.Integers)) {
            integersDomainVariable = arg.first();
            break;
          }
        }
      }

      if (integersDomainVariable.isPresent()) {
        IExpr[] coeffs = val.linear(integersDomainVariable);
        if (coeffs != null) {
          IExpr c0 = coeffs[0];
          IExpr c1 = coeffs[1];
          IExpr minK = F.NIL;
          IExpr maxK = F.NIL;
          if (engine.evalTrue(F.GreaterEqual(c1, F.C0))) {
            minK = F.Ceiling(F.Divide(F.Subtract(lower, c0), c1));
            maxK = F.Floor(F.Divide(F.Subtract(upper, c0), c1));
          } else if (engine.evalTrue(F.LessEqual(c1, F.C0))) {
            minK = F.Ceiling(F.Divide(F.Subtract(upper, c0), c1));
            maxK = F.Floor(F.Divide(F.Subtract(lower, c0), c1));
          } else if (c1.isZero()) {
            collectConstants(c0, lower, upper, lowerSymbol, upperSymbol, collector, engine);
            return;
          }

          if (minK.isPresent() && maxK.isPresent()) {
            IExpr lowerInt = engine.evaluate(minK);
            IExpr upperInt = engine.evaluate(maxK);
            long start = lowerInt.toLongDefault();
            long end = upperInt.toLongDefault();
            if (F.isPresent(start) && F.isPresent(end)) {
              for (long k = start; k <= end; k++) {
                IExpr valK = F.Plus(c0, F.Times(c1, F.ZZ(k)));
                valueExpr = engine.evaluate(valK);
                if (engine.evalTrue(F.And(//
                    F.binaryAST2(lowerSymbol, lower, valueExpr),
                    F.binaryAST2(upperSymbol, valueExpr, upper)))) {
                  collector.append(valueExpr);
                }
              }
            }
          }
        }
        return;
      } else if (engine.evalTrue(condition)) {
        collectConstants(val, lower, upper, lowerSymbol, upperSymbol, collector, engine);
        return;
      }
    }

    if (engine.evalTrue(F.And(//
        F.binaryAST2(lowerSymbol, lower, valueExpr),
        F.binaryAST2(upperSymbol, valueExpr, upper)))) {
      collector.append(valueExpr);
    }
  }
}
