package org.matheclipse.core.reflection.system;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.parser.client.ParserConfig;

/**
 * <code>SetPrecision(expr, n)</code> gives every number of <code>expr</code> the precision
 * <code>n</code>: exact numbers and constants are evaluated to <code>n</code> digits, a number
 * of a lower precision is filled up with zeros (binary zeros for a machine number), a number of
 * a higher precision is cut. <code>SetPrecision(expr, MachinePrecision)</code> gives machine
 * numbers and <code>SetPrecision(expr, Infinity)</code> the exact rational values.
 * <p>
 * Nothing of the conversion is kept anywhere: the numbers are built with the precision asked
 * for, and the numeric mode which evaluates the rest of the expression is the one of the
 * {@link EvalEngine} of the call, restored when the call returns. Two engines with different
 * precisions do not see each other.
 */
public class SetPrecision extends AbstractFunctionEvaluator {

  /** <code>precision</code> value for machine numbers */
  static final long MACHINE = -1L;

  /** <code>precision</code> value for exact numbers */
  static final long EXACT = -2L;

  /** How the precision of a single number is derived from the second argument. */
  @FunctionalInterface
  interface IPrecisionOf {
    /**
     * @param scale the number of digits before the decimal point of the number,
     *        <code>Floor(Log10(Abs(x)))+1</code>
     * @return the precision in digits; a value <code>&lt;= 0</code> means that no digit is left
     */
    long digits(long scale);
  }

  public SetPrecision() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg2 = ast.arg2();
    if (arg2.equals(S.MachinePrecision)) {
      return convert(ast.arg1(), MACHINE, scale -> MACHINE, engine);
    }
    if (arg2.isInfinity()) {
      return convert(ast.arg1(), EXACT, scale -> EXACT, engine);
    }
    final long precision = digitsArgument(ast, arg2, engine);
    if (precision == Long.MIN_VALUE) {
      return F.NIL;
    }
    if (precision <= 0) {
      // Requested precision `1` is smaller than `2`.
      return Errors.printMessage(ast.topHead(), "precsm", F.list(arg2, F.C1), engine);
    }
    return convert(ast.arg1(), precision, scale -> precision, engine);
  }

  /**
   * The number of digits in the second argument of <code>SetPrecision</code> and
   * <code>SetAccuracy</code>.
   *
   * @return <code>Long.MIN_VALUE</code> if the argument is not a real number, or if it is too
   *         large; a message is printed
   */
  static long digitsArgument(final IAST ast, IExpr arg2, EvalEngine engine) {
    double digits = arg2.isReal() || arg2.isNumericFunction(true) ? arg2.evalf() : Double.NaN;
    if (!Double.isFinite(digits)) {
      // The value `1` is not a real number.
      Errors.printMessage(ast.topHead(), "realx", F.list(arg2), engine);
      return Long.MIN_VALUE;
    }
    if (digits > Config.MAX_PRECISION_APFLOAT) {
      // Requested precision `1` is greater than `2`.
      Errors.printMessage(ast.topHead(), "precgt",
          F.list(arg2, F.ZZ(Config.MAX_PRECISION_APFLOAT)), engine);
      return Long.MIN_VALUE;
    }
    return (long) Math.ceil(Math.max(digits, -(double) Config.MAX_PRECISION_APFLOAT));
  }

  /**
   * Replace the numbers of <code>expr</code> and evaluate what is left with the precision of the
   * call.
   *
   * @param expr
   * @param evalPrecision the precision for the numeric evaluation of the constants and functions
   *        which are left after the numbers were replaced; {@link #MACHINE} or {@link #EXACT}
   * @param precisionOf the precision of a single number
   * @param engine the engine of the call; its numeric mode and precision are the same after the
   *        call as before
   */
  static IExpr convert(IExpr expr, long evalPrecision, IPrecisionOf precisionOf,
      EvalEngine engine) {
    IExpr result = replaceNumbers(expr, precisionOf);
    if (evalPrecision == EXACT || result.isNumber() || result.isFree(x -> x.isNumber(), false)
        && result.isFree(x -> x.isConstantAttribute(), false)) {
      return result;
    }
    if (evalPrecision == MACHINE) {
      return engine.evalN(result);
    }
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldPrecision = engine.getNumericPrecision();
    final int oldSignificantFigures = engine.getSignificantFigures();
    try {
      long precision = Math.max(evalPrecision, ParserConfig.MACHINE_PRECISION);
      engine.setNumericMode(true, precision,
          (int) Math.min(precision, Math.min(Config.MAX_OUTPUT_SIZE, Short.MAX_VALUE)));
      return engine.evalWithoutNumericReset(result);
    } finally {
      engine.setNumericMode(oldNumericMode, oldPrecision, oldSignificantFigures);
    }
  }

  private static IExpr replaceNumbers(IExpr expr, IPrecisionOf precisionOf) {
    if (expr.isNumber()) {
      return number((INumber) expr, precisionOf);
    }
    if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IASTMutable result = F.NIL;
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        IExpr temp = replaceNumbers(arg, precisionOf);
        if (temp != arg) {
          if (result.isNIL()) {
            result = ast.copy();
          }
          result.set(i, temp);
        }
      }
      return result.orElse(expr);
    }
    return expr;
  }

  private static IExpr number(INumber x, IPrecisionOf precisionOf) {
    if (x.isZero() && x.isExactNumber()) {
      // the exact zero has no digits to set
      return x;
    }
    if (x instanceof IComplex) {
      IComplex c = (IComplex) x;
      IExpr re = real(c.re(), null, precisionOf);
      IExpr im = real(c.im(), null, precisionOf);
      return complex(re, im);
    }
    if (x instanceof IComplexNum) {
      Apcomplex c = x.apcomplexValue();
      IComplexNum cn = (IComplexNum) x;
      boolean machine = !(x instanceof ApcomplexNum);
      IExpr re = real(null, machine ? exact(cn.reDoubleValue()) : c.real(), precisionOf);
      IExpr im = real(null, machine ? exact(cn.imDoubleValue()) : c.imag(), precisionOf);
      return complex(re, im);
    }
    if (x instanceof IRational) {
      return real((IRational) x, null, precisionOf);
    }
    if (x instanceof ApfloatNum) {
      return real(null, ((ApfloatNum) x).apfloatValue(), precisionOf);
    }
    if (x instanceof INum) {
      double d = ((INum) x).doubleValue();
      if (!Double.isFinite(d)) {
        return x;
      }
      return real(null, exact(d), precisionOf);
    }
    return x;
  }

  /** The binary value of a machine number, which <code>SetPrecision</code> fills up with zeros. */
  private static Apfloat exact(double d) {
    return Double.isFinite(d) ? new Apfloat(new BigDecimal(d)) : new Apfloat(d);
  }

  private static IExpr complex(IExpr re, IExpr im) {
    if (re instanceof IRational && im instanceof IRational) {
      return F.CC((IRational) re, (IRational) im);
    }
    if (re instanceof ApfloatNum && im instanceof ApfloatNum) {
      return F.complexNum(
          new Apcomplex(((ApfloatNum) re).apfloatValue(), ((ApfloatNum) im).apfloatValue()));
    }
    return F.complexNum(re.evalf(), im.evalf());
  }

  /**
   * One real number, given as the exact <code>rational</code> or as the <code>inexact</code>
   * value.
   */
  private static IExpr real(IRational rational, Apfloat inexact, IPrecisionOf precisionOf) {
    final long scale;
    if (rational != null) {
      if (rational.isZero()) {
        scale = 0;
      } else {
        // digits of the numerator minus digits of the denominator, within one digit
        scale = new Apfloat(rational.toBigNumerator(), 20L)
            .divide(new Apfloat(rational.toBigDenominator(), 20L)).scale();
      }
    } else {
      scale = inexact.signum() == 0 ? 0 : inexact.scale();
    }
    final long precision = precisionOf.digits(scale);
    if (precision == EXACT) {
      if (rational != null) {
        return rational;
      }
      BigDecimal value = new BigDecimal(inexact.toString(true));
      BigInteger unscaled = value.unscaledValue();
      return value.scale() >= 0 //
          ? F.QQ(unscaled, BigInteger.TEN.pow(value.scale())).normalize()
          : F.ZZ(unscaled.multiply(BigInteger.TEN.pow(-value.scale())));
    }
    if (precision == MACHINE) {
      return F.num(rational != null ? rational.doubleValue() : inexact.doubleValue());
    }
    if (precision <= 0) {
      // no digit is left of the number
      return F.num(new Apfloat(0L, 1L));
    }
    if (rational != null) {
      return F.num(new Apfloat(rational.toBigNumerator(), precision)
          .divide(new Apfloat(rational.toBigDenominator(), precision)));
    }
    return F.num(inexact.precision(precision));
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_2;
  }
}
