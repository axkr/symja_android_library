package org.matheclipse.core.sympy.simplify;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class Simplify {
  /**
   * Combines logarithms in a Plus expression: c*Log(a) + c*Log(b) -> c*Log(a*b)
   */
  public static IExpr combinePlusLogs(IAST plusAST, boolean force) {
    // Maps a coefficient (e.g., '1/z') to a list of logarithmic arguments to be merged
    Map<IExpr, IASTAppendable> groupMap = new HashMap<>();
    IASTAppendable remainingTerms = F.PlusAlloc(plusAST.size());

    for (int i = 1; i < plusAST.size(); i++) {
      IExpr term = plusAST.get(i);
      IExpr coeff = F.C1;
      IExpr logArg = F.NIL;

      if (term.isLog()) {
        logArg = term.first();
      } else if (term.isTimes()) {
        IAST times = (IAST) term;
        IASTAppendable coeffPart = F.TimesAlloc(times.size());
        IExpr sign = F.C1;

        for (IExpr factor : times) {
          if (factor.isLog()) {
            logArg = factor.first();
          } else if (factor.isNumber() && factor.isNegative()) {
            // Extract the negative sign to normalize the coefficient key
            sign = F.eval(F.Times(sign, factor));
          } else {
            coeffPart.append(factor);
          }
        }

        coeff = coeffPart.oneIdentity1();

        // If a negative sign was extracted, move it into the Log argument: -Log(x) -> Log(x^-1)
        if (sign.isMinusOne() && logArg.isPresent()) {
          logArg = F.Power(logArg, F.CN1);
        } else if (!sign.isOne()) {
          // Re-apply the sign if it wasn't a simple -1
          coeff = F.eval(F.Times(coeff, sign));
        }
      }

      // If a Log was found and (it's positive OR force is true)
      if (logArg.isPresent() && (force || logArg.isPositiveResult())) {
        IASTAppendable args = groupMap.getOrDefault(coeff, F.TimesAlloc(4));
        args.append(logArg);
        groupMap.put(coeff, args);
      } else {
        remainingTerms.append(term);
      }
    }

    if (groupMap.isEmpty()) {
      return plusAST;
    }

    for (Map.Entry<IExpr, IASTAppendable> entry : groupMap.entrySet()) {
      IExpr coeff = entry.getKey();
      IAST combinedArgs = entry.getValue();

      // Log(a) + Log(b^-1) -> Log(a/b)
      // Note: Use Together to simplify (1/2+z)/z into (1 + 1/(2z))
      IExpr mergedLog = F.Log(F.Together.of(combinedArgs));
      remainingTerms.append(F.Times(coeff, mergedLog));
    }

    return F.eval(remainingTerms);
  }

  public static IExpr logCombine(IExpr expr) {
    return logCombine(expr, false);
  }

  public static IExpr logCombine(IExpr expr, boolean force) {
    if (expr.isPlus()) {
      IAST plus = (IAST) expr;
      // Group terms that are Log or Coeff * Log
      // For z*Log(1/2+z) - z*Log(z):
      // 1. Identify coefficient 'z'
      // 2. Combine Log(1/2+z) - Log(z) into Log((1/2+z)/z)
      // 3. Return z * Log(1 + 1/(2z))
      return combinePlusLogs(plus, force);
    }
    if (expr.isTimes()) {
      IAST timesAST = (IAST) expr;
      int indexOf = timesAST.indexOf(IExpr::isLog);
      if (indexOf < 0) {
        return expr;
      }
      if (timesAST.get(indexOf).first().isPositiveResult()) {
        IASTMutable timesReduced = timesAST.removeAtCopy(indexOf);
        for (int i = 1; i < timesReduced.size(); i++) {
          IExpr arg = timesReduced.get(i);
          if (arg.isRealResult()) {

          }
        }
      }
    }
    return expr;
  }
}
