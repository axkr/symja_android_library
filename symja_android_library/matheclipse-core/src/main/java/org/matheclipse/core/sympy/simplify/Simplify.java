package org.matheclipse.core.sympy.simplify;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
      IExpr[] parts = singleLogTermParts(term);
      // If a single-Log product was found and (it's positive OR force is true)
      if (parts != null && (force || parts[1].isPositiveResult())) {
        IASTAppendable args = groupMap.getOrDefault(parts[0], F.TimesAlloc(4));
        args.append(parts[1]);
        groupMap.put(parts[0], args);
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
      IExpr mergedLog = F.Log.of(F.Together(combinedArgs));
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
    return expr;
  }

  /**
   * Decompose an additive term into <code>{coefficient, logArgument}</code> such that
   * <code>term == coefficient * Log(logArgument)</code>, with an explicit numeric <code>-1</code>
   * moved into the argument (<code>-Log(x) -&gt; Log(1/x)</code>) so that subtraction merges into a
   * quotient.
   *
   * <p>
   * Returns <code>null</code> when the term is not a single-logarithm product: no <code>Log</code>
   * factor at all, or more than one. A product like <code>Log(a)*Log(b)</code> must never be split
   * - extracting either <code>Log</code> would silently drop the other factor and change the term's
   * value (this used to turn <code>Log(x)*Log(Log(x)) - Log(x)</code> into the wrong
   * <code>Log(Log(x)/x)</code>).
   *
   * <p>
   * Shared by this class and the Gruntz limit machinery
   * (<code>org.matheclipse.core.reflection.system</code>) so the term decomposition exists only
   * once; the two callers differ only in how they merge the grouped arguments
   * (<code>Together</code> here, <code>ExpandAll</code> there).
   */
  public static IExpr[] singleLogTermParts(IExpr term) {
    if (term.isLog()) {
      return new IExpr[] {F.C1, term.first()};
    }
    if (!term.isTimes()) {
      return null;
    }
    IAST times = (IAST) term;
    IASTAppendable coeffPart = F.TimesAlloc(times.size());
    IExpr sign = F.C1;
    IExpr logArg = F.NIL;
    for (IExpr factor : times) {
      if (factor.isLog()) {
        if (logArg.isPresent()) {
          // a second Log factor - not of the form coefficient*Log(argument)
          return null;
        }
        logArg = factor.first();
      } else if (factor.isNumber() && factor.isNegative()) {
        // Extract the negative sign to normalize the coefficient key
        sign = F.eval(F.Times(sign, factor));
      } else {
        coeffPart.append(factor);
      }
    }
    if (logArg.isNIL()) {
      return null;
    }
    IExpr coeff = F.eval(coeffPart.oneIdentity1());
    if (sign.isMinusOne()) {
      // Move the sign into the Log argument: -Log(x) -> Log(x^-1)
      logArg = F.Power(logArg, F.CN1);
    } else if (!sign.isOne()) {
      // Re-apply the sign if it wasn't a simple -1
      coeff = F.eval(F.Times(coeff, sign));
    }
    return new IExpr[] {coeff, logArg};
  }
}
