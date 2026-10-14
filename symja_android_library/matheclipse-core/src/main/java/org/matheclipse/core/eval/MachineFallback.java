package org.matheclipse.core.eval;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;

/**
 * The switch from machine numbers to arbitrary precision numbers, where the machine arithmetic of
 * a numeric function fails.
 *
 * <p>
 * A function of machine numbers is calculated with doubles. That fails in two ways which are not
 * the function's: an intermediate result leaves the range of a double - <code>Coth(1000.0)</code>
 * as <code>Cosh/Sinh</code> is <code>Infinity/Infinity</code>, <code>Factorial(1000.0)</code> is
 * beyond the largest double - or a tolerance takes a tiny argument for the singular point next to
 * it, <code>Log(1.0*^-16)</code>. The result is an infinity, <code>Indeterminate</code> or an
 * exception then. Before that is returned, the function is evaluated once more with the same
 * arguments as arbitrary precision numbers: a number which that gives is the result - the machine
 * number it is, or the arbitrary precision number of machine precision if it is beyond the range
 * of a double.
 */
public final class MachineFallback {

  /** The digits of the second evaluation: those of a double and guard digits. */
  private static final long PRECISION = ParserConfig.MACHINE_PRECISION + 5;

  private MachineFallback() {}

  /**
   * A result which the machine arithmetic may have failed on: a number which is not finite, or
   * <code>Indeterminate</code>. An infinity as a symbol is the pole which a function reports
   * itself, also where its test for the pole has a tolerance for the rounding of its arguments -
   * <code>Hypergeometric2F1(1/3, 2/3, 1, 1.0)</code> - and is left alone.
   */
  public static boolean isSuspect(IExpr result) {
    if (result instanceof Num) {
      return !Double.isFinite(((Num) result).doubleValue());
    }
    if (result instanceof ComplexNum) {
      final Complex c = ((ComplexNum) result).complexValue();
      return !Double.isFinite(c.getReal()) || !Double.isFinite(c.getImaginary());
    }
    return result == S.Indeterminate;
  }

  /**
   * Evaluate a numeric function of machine numbers with arbitrary precision numbers.
   *
   * @param symbol the head of the function
   * @param ast the function with its evaluated arguments
   * @return the number which the function is, or {@link F#NIL} if this is no numeric function of
   *         machine numbers, or the arbitrary precision arithmetic gives no number either
   */
  public static IExpr evaluate(final ISymbol symbol, final IAST ast, final EvalEngine engine) {
    if (engine.fMachineFallback || !Attribute.NUMERICFUNCTION.isSetIn(symbol.getAttributes())
        || ast.argSize() < 1 || ast.argSize() > 6) {
      return F.NIL;
    }
    boolean machine = false;
    for (int i = 1; i < ast.size(); i++) {
      final IExpr arg = ast.get(i);
      if (arg instanceof Num) {
        if (!Double.isFinite(((Num) arg).doubleValue())) {
          return F.NIL;
        }
        machine = true;
      } else if (arg instanceof ComplexNum) {
        final Complex c = ((ComplexNum) arg).complexValue();
        if (!Double.isFinite(c.getReal()) || !Double.isFinite(c.getImaginary())) {
          return F.NIL;
        }
        machine = true;
      } else if (!arg.isNumber() || arg.isInexactNumber()) {
        // a symbolic argument, or an arbitrary precision number: nothing to switch
        return F.NIL;
      }
    }
    if (!machine) {
      return F.NIL;
    }
    final IASTMutable precise = ast.copy();
    for (int i = 1; i < ast.size(); i++) {
      final IExpr arg = ast.get(i);
      if (arg instanceof Num) {
        precise.set(i, ApfloatNum.valueOf(apfloat(((Num) arg).doubleValue())));
      } else if (arg instanceof ComplexNum) {
        final Complex c = ((ComplexNum) arg).complexValue();
        precise.set(i,
            ApcomplexNum.valueOf(new Apcomplex(apfloat(c.getReal()), apfloat(c.getImaginary()))));
      }
    }
    final boolean numericMode = engine.isNumericMode();
    final long precision = engine.getNumericPrecision();
    final int figures = engine.getSignificantFigures();
    final boolean quiet = engine.isQuietMode();
    engine.fMachineFallback = true;
    try {
      // the mode stays: exact arguments are exact for the second evaluation as well
      engine.setNumericMode(numericMode, PRECISION, figures);
      // the messages are those of the result which is returned
      engine.setQuietMode(true);
      final IExpr result = engine.evaluate(precise);
      if (result instanceof ApfloatNum) {
        return machine(((ApfloatNum) result).apfloatValue());
      }
      if (result instanceof ApcomplexNum) {
        final Apcomplex z = ((ApcomplexNum) result).apcomplexValue();
        if (z.imag().signum() == 0) {
          return machine(z.real());
        }
        final double re = z.real().doubleValue();
        final double im = z.imag().doubleValue();
        if (Double.isInfinite(re) || Double.isInfinite(im)) {
          return ApcomplexNum
              .valueOf(ApcomplexNum.precisionOfParts(z, ParserConfig.MACHINE_PRECISION));
        }
        return F.complexNum(re, im);
      }
      if (result.isAST(S.Overflow, 1)) {
        // beyond the range of an arbitrary precision number too
        return result;
      }
      return F.NIL;
    } catch (org.apfloat.OverflowException oe) {
      // beyond the range of an arbitrary precision number too
      return F.Overflow();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      engine.fMachineFallback = false;
      engine.setQuietMode(quiet);
      engine.setNumericMode(numericMode, precision, figures);
    }
  }

  /** The double as an arbitrary precision number. */
  private static Apfloat apfloat(double value) {
    return ApfloatNum.apfloatOf(value, PRECISION);
  }

  /**
   * The machine number of a result, or the arbitrary precision number of machine precision if it
   * is beyond the largest double. A result below the smallest double is the machine underflow it
   * is with doubles.
   */
  private static IExpr machine(Apfloat value) {
    final double d = value.doubleValue();
    if (Double.isInfinite(d)) {
      return ApfloatNum.valueOf(value.precision(ParserConfig.MACHINE_PRECISION));
    }
    return F.num(d);
  }
}
