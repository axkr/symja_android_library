package org.matheclipse.core.eval;

import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.external.apfloat.ApfloatSignificance;
import org.matheclipse.external.apfloat.ApfloatSignificance.Function;

/**
 * The precision of a function value from the precision of its arguments: the relative error of each
 * argument times the condition number of the function in it, see {@link ApfloatSignificance}.
 * <code>Sqrt(1.5`20)</code> has the precision <code>20.301</code>, <code>Cos(1.5`20)</code> the
 * precision <code>18.6746</code>. Where the condition number doesn't exist, at a zero argument or
 * value and at the end of the domain, the error is how much the function changes within the error
 * of the argument. A function with poles is <code>ComplexInfinity</code> where not one digit of
 * its value is known, and the error of a bounded function is bounded too.
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
    if (!(head instanceof IBuiltInSymbol) || !(result instanceof INumber)) {
      return result;
    }
    final Function function = functionOf(((IBuiltInSymbol) head).ordinal(), ast.argSize());
    if (function == null) {
      return result;
    }
    final int argSize = ast.argSize();
    final boolean sqrt = ((IBuiltInSymbol) head).ordinal() == ID.Sqrt;
    if (function == Function.LOG_BASE) {
      final IExpr special = logSpecialPoint(ast.arg1(), ast.arg2());
      if (special.isPresent()) {
        return special;
      }
    }
    if (ast.arg1() instanceof ApfloatNum
        && !Double.isNaN(((ApfloatNum) ast.arg1()).zeroAccuracy10())) {
      final double zeroAccuracy = ((ApfloatNum) ast.arg1()).zeroAccuracy10();
      if (function == Function.POWER) {
        return powerOfZero(zeroAccuracy, sqrt ? F.C1D2 : ast.arg2(), result);
      }
      return argSize == 1 ? ofZeroArgument(head, function, zeroAccuracy, result) : result;
    }
    if (result instanceof ApcomplexNum) {
      return complexValueAtReal(function, ast, sqrt, (ApcomplexNum) result);
    }
    if (!(result instanceof ApfloatNum)) {
      return result;
    }
    final ApfloatNum value = (ApfloatNum) result;
    // the arguments: real numbers, at least one of them of a precision of its own
    final Apfloat[] args = new Apfloat[sqrt ? 2 : argSize];
    final double[] precisions = new double[args.length];
    boolean tracked = false;
    boolean extended = false;
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
        extended |= number.hasExtendedValue();
        precisions[i - 1] = number.significancePrecision();
        args[i - 1] = number.extendedValue();
      } else {
        // exact numbers, and machine numbers as the binary numbers they are
        precisions[i - 1] = Double.POSITIVE_INFINITY;
        args[i - 1] = ((IReal) arg).apfloatValue();
      }
    }
    if (!tracked) {
      return result;
    }
    if (sqrt) {
      // Sqrt(x) is x^(1/2)
      args[1] = new Apfloat("0.5");
      precisions[1] = Double.POSITIVE_INFINITY;
    }
    final double[] conditionNumbers = value.isZero() ? null
        : ApfloatSignificance.conditionNumbers(function, args, value.apfloatValue());
    if (conditionNumbers == null) {
      // a zero value, or the end of the domain: how much the function changes within the error
      if (args.length > 1) {
        if (!value.isZero()) {
          return result;
        }
        final Apfloat error = changeWithinArguments(function, args, precisions);
        return error == null ? result : valueWithError(function, Apfloat.ZERO, error);
      }
      if (precisions[0] < 1.0) {
        return result;
      }
      final Apfloat x = args[0];
      final double accuracy = precisions[0] - ApfloatSignificance.log10Abs(x);
      final Apfloat delta = ApfloatMath.abs(x).multiply(
          ApfloatSignificance.tenToTheMinus(precisions[0], Math.max(x.precision(), 20L)));
      final Apfloat[] change = ApfloatSignificance.changeWithin(function, x, delta, accuracy);
      return change == null ? result : valueWithError(function, change[0], change[1]);
    }
    final double precision = ApfloatSignificance.functionPrecision(precisions, conditionNumbers);
    if (Double.isNaN(precision) || Double.isInfinite(precision)) {
      return result;
    }
    if (precision <= 0.0 && ApfloatSignificance.hasPoles(function)) {
      // not one digit is known: the value may be next to a pole
      return F.CComplexInfinity;
    }
    Apfloat apfloat = value.apfloatValue();
    if (precision <= 0.0) {
      // not one digit of the value is known; the error of a bounded function is bounded too
      double accuracy = precision - ApfloatSignificance.log10Abs(apfloat);
      final double bound = ApfloatSignificance.bound(function);
      if (!Double.isNaN(bound)) {
        accuracy = Math.max(accuracy, -Math.log10(bound));
      }
      return ApfloatNum.zero(accuracy);
    }
    final long digits = Math.round(precision);
    if (apfloat.precision() < digits || extended) {
      // more digits than the arguments have, like the square root, or the digits of arguments
      // which are stored beyond their precision: calculated with enough of them
      final Apfloat precise = ApfloatSignificance.evaluate(function, args,
          Math.max(digits, ApfloatNum.MIN_STORED_DIGITS) + 3);
      if (precise != null) {
        apfloat = precise;
      } else if (apfloat.precision() < digits) {
        return result;
      }
    }
    return ApfloatNum.valueOf(apfloat, precision);
  }

  /**
   * A complex value of a function at real arguments, one of them of a precision of its own, as
   * <code>ArcSin(2.`20)</code>, <code>Log(-1.`20)</code> or <code>(-8.`20)^(1/3)</code>: its error
   * is how much the function changes within the error of that argument.
   */
  private static IExpr complexValueAtReal(final Function function, final IAST ast,
      final boolean sqrt, final ApcomplexNum result) {
    final int argSize = ast.argSize();
    final Apfloat[] args = new Apfloat[sqrt ? 2 : argSize];
    int position = -1;
    for (int i = 1; i <= argSize; i++) {
      final IExpr arg = ast.get(i);
      if (arg instanceof ApfloatNum && ((ApfloatNum) arg).isSignificanceTracked()) {
        if (position >= 0 || arg.isZero()) {
          // two arguments with errors, or an inexact zero
          return result;
        }
        position = i - 1;
        args[i - 1] = ((ApfloatNum) arg).extendedValue();
      } else if (arg.isRational()
          || (arg instanceof ApfloatNum && ((ApfloatNum) arg).isExactValue())) {
        args[i - 1] = ((IReal) arg).apfloatValue();
      } else {
        return result;
      }
    }
    if (position < 0) {
      return result;
    }
    if (sqrt) {
      args[1] = new Apfloat("0.5");
    }
    final double precision = ((ApfloatNum) ast.get(position + 1)).precision10();
    if (precision < 1.0) {
      return result;
    }
    final int k = position;
    final ApfloatSignificance.ArgumentFunction f = (t, digits) -> {
      final Apfloat[] values = args.clone();
      values[k] = t;
      return ApfloatSignificance.evaluateComplex(function, values, digits);
    };
    final Apfloat x = args[k];
    final Apfloat delta = ApfloatMath.abs(x)
        .multiply(ApfloatSignificance.tenToTheMinus(precision, Math.max(x.precision(), 20L)));
    final Apcomplex[] change = ApfloatSignificance.changeWithin(f, x, delta,
        precision - ApfloatSignificance.log10Abs(x));
    if (change == null) {
      return result;
    }
    return ApcomplexNum.valueOf(result.apcomplexValue(),
        -ApfloatSignificance.log10Abs(change[1].real()));
  }

  /**
   * How much a function of several arguments changes within the errors of its arguments: the
   * change within the error of each argument of a precision of its own, the other arguments held
   * fixed, added up. <code>Log(2, 1.`20)</code> is <code>0``19.84</code>, since <code>Log(2, x)</code>
   * changes by <code>10^-20/Log(2)</code> at <code>x=1</code>, and <code>Log(b, 1)</code> not at all.
   *
   * @return <code>null</code> if no argument changes the function
   */
  private static Apfloat changeWithinArguments(final Function function, final Apfloat[] args,
      final double[] precisions) {
    Apfloat total = null;
    for (int k = 0; k < args.length; k++) {
      if (Double.isInfinite(precisions[k])) {
        continue;
      }
      if (precisions[k] < 1.0 || args[k].signum() == 0) {
        return null;
      }
      final int position = k;
      final ApfloatSignificance.ArgumentFunction partial = (t, precision) -> {
        final Apfloat[] values = new Apfloat[args.length];
        for (int i = 0; i < values.length; i++) {
          values[i] = i == position ? t : args[i];
        }
        return ApfloatSignificance.evaluate(function, values, precision);
      };
      final Apfloat x = args[k];
      final Apfloat delta = ApfloatMath.abs(x).multiply(
          ApfloatSignificance.tenToTheMinus(precisions[k], Math.max(x.precision(), 20L)));
      final Apcomplex[] change = ApfloatSignificance.changeWithin(partial, x, delta,
          precisions[k] - ApfloatSignificance.log10Abs(x));
      if (change != null) {
        total = total == null ? change[1].real() : total.add(change[1].real());
      }
    }
    return total == null || total.signum() == 0 ? null : total;
  }

  /**
   * The special points of <code>Log(b, x)</code> with an inexact argument: an inexact zero gives
   * <code>Indeterminate</code>, a base which is <code>1</code> or whose error covers
   * <code>1</code> gives <code>ComplexInfinity</code>, or <code>Indeterminate</code> if
   * <code>x</code> is or covers <code>1</code> too, and an exact <code>x=1</code> with any other
   * base gives the exact <code>0</code>.
   *
   * @return {@link F#NIL} if the arguments are no special point, or no argument is an
   *         arbitrary-precision number
   */
  public static IExpr logSpecialPoint(final IExpr base, final IExpr x) {
    if (!(base instanceof ApfloatNum) && !(x instanceof ApfloatNum)) {
      return F.NIL;
    }
    if (isInexactZero(base) || isInexactZero(x)) {
      return S.Indeterminate;
    }
    if (isOrCoversOne(base)) {
      return isOrCoversOne(x) ? S.Indeterminate : F.CComplexInfinity;
    }
    if (x.isOne() && (x.isExactNumber() || ((ApfloatNum) x).isExactValue())) {
      return F.C0;
    }
    return F.NIL;
  }

  private static boolean isInexactZero(final IExpr number) {
    return number instanceof ApfloatNum && number.isZero()
        && !((ApfloatNum) number).isExactValue();
  }

  /** Test if <code>number</code> is <code>1</code>, or a number whose error covers <code>1</code>. */
  private static boolean isOrCoversOne(final IExpr number) {
    if (number.isOne()) {
      return true;
    }
    if (number instanceof ApfloatNum && ((ApfloatNum) number).isSignificanceTracked()
        && !number.isZero()) {
      final ApfloatNum real = (ApfloatNum) number;
      final Apfloat value = real.extendedValue();
      final Apfloat distance = ApfloatMath.abs(value.subtract(new Apfloat(1)));
      if (distance.signum() == 0) {
        return true;
      }
      // the error |x|*10^-p reaches 1
      return ApfloatSignificance.log10Abs(distance) <= ApfloatSignificance.log10Abs(value)
          - real.precision10();
    }
    return false;
  }

  /**
   * The value of a function at a zero of the accuracy <code>accuracy</code>, which has no relative
   * error: the error of the value is how much the function changes within the error of the zero.
   */
  private static IExpr ofZeroArgument(final ISymbol head, final Function function,
      final double accuracy, final IExpr result) {
    if (accuracy < 1.0) {
      return result;
    }
    final Apfloat delta = ApfloatSignificance.tenToTheMinus(accuracy, 20L);
    final Apfloat[] change =
        ApfloatSignificance.changeWithin(function, Apfloat.ZERO, delta, accuracy);
    if (change != null) {
      return valueWithError(function, change[0], change[1]);
    }
    // a complex value, as ArcCosh(0``20)
    final Apcomplex[] complexChange = ApfloatSignificance.changeWithin(
        (t, digits) -> ApfloatSignificance.evaluateComplex(function, new Apfloat[] {t}, digits),
        Apfloat.ZERO, delta, accuracy);
    if (complexChange == null || complexChange[0].imag().signum() == 0) {
      return result;
    }
    // the value on the branch of the function itself: its value at the exact 0
    final IExpr value = EvalEngine.get().evalN(F.unaryAST1(head, F.C0),
        (long) Math.ceil(Math.max(accuracy, 1.0)) + 10L);
    if (!(value instanceof INumber) || value.isReal()) {
      return result;
    }
    return ApcomplexNum.valueOf(((INumber) value).apcomplexValue(),
        -ApfloatSignificance.log10Abs(complexChange[1].real()));
  }

  /**
   * The value of a function at a zero of a given accuracy, which the function itself cannot
   * calculate, as <code>ArcCosh(0``20)</code>: the zero has no digits to calculate with.
   *
   * @param head the head of <code>ast</code>
   * @param ast the function with its arguments
   * @return {@link F#NIL} if <code>ast</code> is no such function of such a zero
   */
  public static IExpr unevaluatedAtZero(final ISymbol head, final IAST ast) {
    if (!(head instanceof IBuiltInSymbol) || !ast.isAST1() || !(ast.arg1() instanceof ApfloatNum)) {
      return F.NIL;
    }
    final double zeroAccuracy = ((ApfloatNum) ast.arg1()).zeroAccuracy10();
    if (Double.isNaN(zeroAccuracy)) {
      return F.NIL;
    }
    final Function function = functionOf(((IBuiltInSymbol) head).ordinal(), 1);
    if (function == null || function == Function.POWER) {
      return F.NIL;
    }
    return ofZeroArgument(head, function, zeroAccuracy, F.NIL);
  }

  /**
   * A zero of the accuracy <code>accuracy</code> to the exact positive power <code>exponent</code>:
   * its absolute value is below <code>10^-accuracy</code>, so the power is below
   * <code>10^(-accuracy*exponent)</code>. <code>Sqrt(0``20)</code> is <code>0``10</code>.
   */
  private static IExpr powerOfZero(final double accuracy, final IExpr exponent,
      final IExpr result) {
    final boolean exact = exponent.isRational()
        || (exponent instanceof ApfloatNum && ((ApfloatNum) exponent).isExactValue());
    if (!exact || !exponent.isPositive()) {
      return result;
    }
    return ApfloatNum.zero(accuracy * exponent.evalf());
  }

  /** <code>value</code> with the absolute error <code>error</code>. */
  private static IExpr valueWithError(final Function function, final Apfloat value,
      final Apfloat error) {
    double accuracy = -ApfloatSignificance.log10Abs(error);
    final double bound = ApfloatSignificance.bound(function);
    if (!Double.isNaN(bound)) {
      accuracy = Math.max(accuracy, -Math.log10(bound));
    }
    if (value.signum() == 0) {
      return ApfloatNum.zero(accuracy);
    }
    final double precision = ApfloatSignificance.log10Abs(value) + accuracy;
    if (precision <= 0.0) {
      if (ApfloatSignificance.hasPoles(function)) {
        return F.CComplexInfinity;
      }
      return ApfloatNum.zero(accuracy);
    }
    return ApfloatNum.valueOf(value, precision);
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
        case ID.Sinh:
          return Function.SINH;
        case ID.Cosh:
          return Function.COSH;
        case ID.Tanh:
          return Function.TANH;
        case ID.Coth:
          return Function.COTH;
        case ID.Sech:
          return Function.SECH;
        case ID.Csch:
          return Function.CSCH;
        case ID.ArcSinh:
          return Function.ARCSINH;
        case ID.ArcCosh:
          return Function.ARCCOSH;
        case ID.ArcTanh:
          return Function.ARCTANH;
        case ID.ArcCoth:
          return Function.ARCCOTH;
        case ID.ArcSech:
          return Function.ARCSECH;
        case ID.ArcCsch:
          return Function.ARCCSCH;
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
