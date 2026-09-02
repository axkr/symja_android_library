package org.matheclipse.core.patternmatching.hash;

import org.hipparchus.util.ArithmeticUtils;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * Evaluate <code>Log(x) * Log(y)</code> combinations. For example evaluate <code>
 * Log(1000) / Log(10)</code> to <code>3</code>
 */
public class HashedPatternRulesLog extends HashedPatternRules {
  public HashedPatternRulesLog(IExpr lhsPattern1, IExpr lhsPattern2) {
    super(lhsPattern1, lhsPattern2, S.Null, false, null, true);
  }

  @Override
  public IExpr evalDownRule(IExpr arg1, IExpr num1, IExpr arg2, IExpr num2, EvalEngine engine) {
    if (num1.isOne() && num2.isMinusOne()) {
      IExpr temp = getRulesData().evalDownRule(F.list(arg1, arg2), engine);
      if (temp.isPresent()) {
        IExpr i1 = arg1.first();
        IExpr i2 = arg2.first();
        if (i1.isInteger() && i2.isInteger()) {
          IExpr result = AbstractIntegerSym.baseBLog((IInteger) i2, (IInteger) i1);
          if (result.isPresent()) {
            return result;
          }
          return rationalBaseBLog((IInteger) i2, (IInteger) i1);
        }
      }
    }
    return F.NIL;
  }

  /**
   * <code>Log(arg) / Log(b)</code> as an exact rational number <code>p/q</code>, for the cases
   * {@link AbstractIntegerSym#baseBLog(IInteger, IInteger)} cannot answer because neither integer
   * is an integer power of the other - for example <code>Log(8)/Log(4) == 3/2</code>, where both
   * are powers of the common base <code>2</code>.
   *
   * <p>
   * The candidate <code>p/q</code> is read off the numeric quotient and then verified exactly:
   * <code>Log(arg)/Log(b) == p/q</code> if and only if <code>arg^q == b^p</code>.
   *
   * @return the quotient, or {@link F#NIL} if it is not rational
   */
  private static IExpr rationalBaseBLog(final IInteger b, final IInteger arg) {
    try {
      long base = b.toLong();
      long x = arg.toLong();
      if (base > 1L && x > 1L && base != x) {
        double quotient = Math.log(x) / Math.log(base);
        // the exponents of a long fit into 62 bits, so a common base implies q <= 62
        for (long q = 2; q <= 62; q++) {
          double scaled = quotient * q;
          long p = Math.round(scaled);
          if (Math.abs(scaled - p) < 1.0e-8) {
            if (p > 0L && ArithmeticUtils.gcd(p, q) == 1L
                && arg.powerRational(q).equals(b.powerRational(p))) {
              // cross checked result
              return F.QQ(p, q);
            }
            break;
          }
        }
      }
    } catch (ArithmeticException ae) {
      // toLong() method failed
    }
    return F.NIL;
  }
}
