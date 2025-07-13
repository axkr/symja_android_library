package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
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
}
