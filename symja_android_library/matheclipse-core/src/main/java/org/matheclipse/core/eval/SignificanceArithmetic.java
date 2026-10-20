package org.matheclipse.core.eval;

import org.apfloat.Apfloat;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.external.apfloat.ApfloatSignificance;
import org.matheclipse.external.apfloat.ApfloatSignificance.Function;

/**
 * The precision of a function value from the precision of its arguments: the relative error of each
 * argument times the condition number of the function in it, see {@link ApfloatSignificance}.
 * <code>Sqrt(1.5`20)</code> has the precision <code>20.301</code>, <code>Cos(1.5`20)</code> the
 * precision <code>18.6746</code>.
 *
 * <p>
 * Only a function value with an argument of a precision of its own is changed: a number written
 * with a precision, the result of <code>N(expr, p)</code> or of significance arithmetic. The
 * values which <code>N</code> calculates from exact numbers keep the working precision of
 * <code>N</code>.
 */
public final class SignificanceArithmetic {

  private SignificanceArithmetic() {}

  /**
   * The function value <code>result</code> of <code>ast</code> with the precision of significance
   * arithmetic.
   *
   * @param head the head of <code>ast</code>
   * @param ast the function with its arguments
   * @param result the value of the function
   * @return <code>result</code> with its precision, or <code>result</code> itself if it is not a
   *         real number of a function this knows
   */
  public static IExpr conditioned(final ISymbol head, final IAST ast, final IExpr result) {
    if (!(result instanceof ApfloatNum) || !(head instanceof IBuiltInSymbol)) {
      return result;
    }
    final Function function = functionOf(((IBuiltInSymbol) head).ordinal(), ast.argSize());
    if (function == null) {
      return result;
    }
    final ApfloatNum value = (ApfloatNum) result;
    if (value.isZero()) {
      return result;
    }
    // the arguments: real numbers, at least one of them of a precision of its own
    final int argSize = ast.argSize();
    final boolean sqrt = ((IBuiltInSymbol) head).ordinal() == ID.Sqrt;
    final Apfloat[] args = new Apfloat[sqrt ? 2 : argSize];
    final double[] precisions = new double[args.length];
    boolean tracked = false;
    for (int i = 1; i <= argSize; i++) {
      final IExpr arg = ast.get(i);
      if (!(arg instanceof IReal)) {
        return result;
      }
      if (arg instanceof ApfloatNum) {
        final ApfloatNum number = (ApfloatNum) arg;
        if (!Double.isNaN(number.zeroAccuracy10())) {
          // a zero of a given accuracy has no relative error
          return result;
        }
        tracked |= number.isSignificanceTracked();
        precisions[i - 1] = number.significancePrecision();
      } else {
        // exact numbers, and machine numbers as the binary numbers they are
        precisions[i - 1] = Double.POSITIVE_INFINITY;
      }
      args[i - 1] = ((IReal) arg).apfloatValue();
    }
    if (!tracked) {
      return result;
    }
    if (sqrt) {
      // Sqrt(x) is x^(1/2)
      args[1] = new Apfloat("0.5");
      precisions[1] = Double.POSITIVE_INFINITY;
    }
    final double[] conditionNumbers =
        ApfloatSignificance.conditionNumbers(function, args, value.apfloatValue());
    if (conditionNumbers == null) {
      return result;
    }
    final double precision = ApfloatSignificance.functionPrecision(precisions, conditionNumbers);
    if (Double.isNaN(precision) || Double.isInfinite(precision)) {
      return result;
    }
    if (precision <= 0.0) {
      // not one digit of the value is known
      return ApfloatNum
          .zero(precision - ApfloatSignificance.log10Abs(value.apfloatValue()));
    }
    Apfloat apfloat = value.apfloatValue();
    final long digits = Math.round(precision);
    if (apfloat.precision() < digits) {
      // more digits than the arguments have, like the square root: calculated with enough of them
      final Apfloat precise = ApfloatSignificance.evaluate(function, args, digits + 3);
      if (precise == null) {
        return result;
      }
      apfloat = precise;
    }
    return ApfloatNum.valueOf(apfloat, precision);
  }

  /**
   * The precision of a numeric function which has no condition numbers here, like
   * <code>LaguerreL</code>: its own arithmetic would add the errors of every intermediate result,
   * which overstates the error of the value by far. It is calculated as before significance
   * arithmetic, and the value gets the lowest precision of the arguments.
   *
   * @param head the head of <code>ast</code>
   * @param ast the function with its arguments
   * @return the lowest precision of the arguments of a precision of their own, <code>NaN</code> if
   *         the function is not such a function or no argument has a precision of its own
   */
  public static double opaquePrecision(final ISymbol head, final IAST ast) {
    if (!(head instanceof IBuiltInSymbol) || !head.isNumericFunctionAttribute()) {
      return Double.NaN;
    }
    final int id = ((IBuiltInSymbol) head).ordinal();
    if (id == ID.Plus || id == ID.Times || functionOf(id, ast.argSize()) != null) {
      return Double.NaN;
    }
    double precision = Double.NaN;
    for (int i = 1; i < ast.size(); i++) {
      final IExpr arg = ast.get(i);
      if (arg instanceof ApfloatNum && ((ApfloatNum) arg).isSignificanceTracked()) {
        final double p = ((ApfloatNum) arg).precision10();
        precision = Double.isNaN(precision) ? p : Math.min(precision, p);
      }
    }
    return precision;
  }

  /**
   * The numbers of <code>result</code> with the precision <code>precision</code>, or with their own
   * if that is lower, see {@link #opaquePrecision(ISymbol, IAST)}.
   */
  public static IExpr withPrecision(final IExpr result, final double precision) {
    if (result instanceof ApfloatNum) {
      return ((ApfloatNum) result).withMaximumPrecision(precision);
    }
    if (result.isAST()) {
      return result.replaceAll(x -> x instanceof ApfloatNum
          ? ((ApfloatNum) x).withMaximumPrecision(precision)
          : F.NIL).orElse(result);
    }
    return result;
  }

  /** The function of a built-in symbol with <code>argSize</code> arguments, or <code>null</code>. */
  private static Function functionOf(final int id, final int argSize) {
    if (argSize == 1) {
      switch (id) {
        case ID.Sqrt:
          return Function.POWER;
        case ID.Exp:
          return Function.EXP;
        case ID.Log:
          return Function.LOG;
        case ID.Sin:
          return Function.SIN;
        case ID.Cos:
          return Function.COS;
        case ID.Tan:
          return Function.TAN;
        case ID.Cot:
          return Function.COT;
        case ID.Sec:
          return Function.SEC;
        case ID.Csc:
          return Function.CSC;
        case ID.ArcSin:
          return Function.ARCSIN;
        case ID.ArcCos:
          return Function.ARCCOS;
        case ID.ArcTan:
          return Function.ARCTAN;
        case ID.ArcCot:
          return Function.ARCCOT;
        case ID.ArcSec:
          return Function.ARCSEC;
        case ID.ArcCsc:
          return Function.ARCCSC;
        default:
          return null;
      }
    }
    if (argSize == 2) {
      switch (id) {
        case ID.Power:
          return Function.POWER;
        case ID.Log:
          return Function.LOG_BASE;
        default:
          return null;
      }
    }
    return null;
  }
}
