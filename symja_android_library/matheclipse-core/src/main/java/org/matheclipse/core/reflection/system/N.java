package org.matheclipse.core.reflection.system;

import java.util.function.LongFunction;
import org.matheclipse.core.interfaces.INumber;
import org.apfloat.ApfloatMath;
import org.apfloat.Apfloat;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apcomplex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.PrecisionLoss;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;


/**
 *
 *
 * <pre>
 * N(expr)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives the numerical value of <code>expr</code>.<br>
 *
 * </blockquote>
 *
 * <pre>
 * N(expr, precision)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * evaluates <code>expr</code> numerically with a precision of <code>prec</code> digits.<br>
 *
 * </blockquote>
 *
 * <p>
 * <strong>Note</strong>: the upper case identifier <code>N</code> is different from the lower case
 * identifier <code>n</code>.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; N(Pi)
 * 3.141592653589793
 *
 * &gt;&gt; N(Pi, 50)
 * 3.1415926535897932384626433832795028841971693993751
 *
 * &gt;&gt; N(1/7)
 * 0.14285714285714285
 *
 * &gt;&gt; N(1/7, 5)
 * 1.4285714285714285714e-1
 * </pre>
 */
public final class N extends AbstractCoreFunctionEvaluator {

  public N() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    return numericEval(ast, engine);
  }

  @Override
  public IExpr numericEval(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isListOrAssociation() || arg1.isRuleAST()) {
      return ((IAST) arg1).mapThread(ast, 1);
    }
    if (ast.isAST1()) {
      return org.matheclipse.core.reflection.system.N.evalN1(arg1, engine);
    }
    IExpr arg2 = ast.arg2();
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldDigitPrecision = engine.getNumericPrecision();
    try {
      long nDigitPrecision = oldDigitPrecision;
      // arg2 = engine.evaluateNonNumeric(arg2);
      arg2 = engine.evalN(F.Ceiling(arg2));
      nDigitPrecision = arg2.toIntDefault();
      if (nDigitPrecision <= 0) {
        // Requested precision `1` is smaller than `2`.
        return Errors.printMessage(S.N, "precsm", F.list(arg2, F.C1), engine);
      }
      if (nDigitPrecision > Config.MAX_PRECISION_APFLOAT) {
        if (!Config.TRUNCATE_PRECISION_IN_N) {
          // Requested precision `1` is greater than `2`.
          return Errors.printMessage(S.N, "precgt",
              F.list(arg2, F.ZZ(Config.MAX_PRECISION_APFLOAT)), engine);
        }
        // truncate the requested precision to the maximum precision Symja supports
        nDigitPrecision = Config.MAX_PRECISION_APFLOAT;
      }
      return org.matheclipse.core.reflection.system.N.evalN2(arg1, nDigitPrecision, engine);
    } finally {
      engine.setNumericMode(oldNumericMode);
      engine.setNumericPrecision(oldDigitPrecision);
    }
  }


  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {}


  public static IExpr evalN1(IExpr expr, EvalEngine engine) {
    return N.evalN1(expr, Config.USE_EXTENDED_PRECISION_IN_N, engine);
  }

  public static IExpr evalN1(IExpr expr, boolean useExtendedPrecision, EvalEngine engine) {
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldDigitPrecision = engine.getNumericPrecision();
    final int oldSignificantFigures = engine.getSignificantFigures();
    try {
      long numericPrecision = oldDigitPrecision; // Config.MACHINE_PRECISION;
      // if (expr.isNumericFunction(true)) {
      // engine.setNumericMode(true, numericPrecision, oldSignificantFigures);
      // IExpr temp = engine.evalWithoutNumericReset(expr);
      // if (temp.isListOrAssociation() || temp.isRuleAST()) {
      // return ((IAST) temp).mapThread(F.N(F.Slot1), 1);
      // }
      // return temp;
      // }
      expr = evaluateExact(expr, engine);
      if (expr.isInexactNumber()) {
        return expr;
      }
      if (expr.isListOrAssociation() || expr.isRuleAST()) {
        return ((IAST) expr).mapThread(F.N(F.Slot1), 1);
      }
      if (useExtendedPrecision) {
        engine.setDeterminePrecision(expr, false);
        engine.setNumericMode(true);
      } else {
        engine.setNumericMode(true, numericPrecision, oldSignificantFigures);
      }
      if (expr.isAST()) {
        ISymbol topSymbol = expr.topHead();
        expr = engine.evalArgs((IAST) expr, topSymbol.getAttributes(), true).orElse(expr);
      }

      IExpr result = engine.evalWithoutNumericReset(expr);
      if (result instanceof ApfloatNum) {
        // out of the double range an arbitrary precision number of machine precision: also
        // below it, N((10^800+1)^(-1/2)) is no 0.0
        return ApfloatNum.machineOrPromoted(((ApfloatNum) result).apfloatValue());
      }
      if (result instanceof ApcomplexNum) {
        org.hipparchus.complex.Complex c = result.evalfc();
        if (Double.isInfinite(c.getReal()) || Double.isInfinite(c.getImaginary())) {
          return F.complexNum(ApcomplexNum.precisionOfParts(
              ((ApcomplexNum) result).apcomplexValue(), ParserConfig.MACHINE_PRECISION));
        }
        return F.complexNum(c);
      }
      return result;
    } finally {
      engine.setNumericMode(oldNumericMode);
      engine.setNumericPrecision(oldDigitPrecision);
    }
  }

  /**
   * The exact evaluation which comes before the numeric one. An exact value beyond a limit of the
   * engine - the integer <code>(10^6)!</code> - is not needed for the numeric one: the expression
   * stays as it is then, and its arguments become numbers first.
   */
  private static IExpr evaluateExact(IExpr expr, EvalEngine engine) {
    try {
      return engine.evaluate(expr);
    } catch (IterationLimitExceeded | BigIntegerLimitExceeded | ASTElementLimitExceeded le) {
      if (expr.isAST() && expr.topHead().isNumericFunctionAttribute()) {
        // the arguments are still evaluated exactly, where that is possible
        return ((IAST) expr).map(x -> evaluateExact(x, engine), 1);
      }
      return expr;
    }
  }

  /**
   * Evaluates an expression numerically with a specified precision.
   * <p>
   * This method first attempts to evaluate the given expression symbolically. If the expression is
   * a list, association, or rule, it applies the numeric evaluation to each element using the
   * specified precision. After symbolic evaluation, it performs numeric evaluation with the given
   * precision, ensuring that the precision is within valid bounds.
   *
   * @param expr the expression to be evaluated
   * @param nDigitPrecision the number of digits of precision for numeric evaluation
   * @param engine the evaluation engine used for processing the expression
   */
  public static IExpr evalN2(IExpr expr, final long nDigitPrecision, EvalEngine engine) {
    // first try symbolic evaluation
    final IExpr original = expr;
    // the digits which numbers lose in it, see below
    final long lostExactly;
    final long tracked = PrecisionLoss.start(engine);
    try {
      expr = evaluateExact(expr, engine);
      lostExactly = PrecisionLoss.lost(engine);
    } finally {
      PrecisionLoss.stop(engine, tracked);
    }
    if (expr.isListOrAssociation() || expr.isRuleAST()) {
      return ((IAST) expr).mapThread(x -> evalN2(x, nDigitPrecision, engine));
    }

    // after symbolic evaluation do numeric evaluation with n-digit precision
    long nPrecision = nDigitPrecision;
    final int maxSize =
        (Config.MAX_OUTPUT_SIZE > Short.MAX_VALUE) ? Short.MAX_VALUE : Config.MAX_OUTPUT_SIZE;
    int significantFigures = (nDigitPrecision > maxSize) ? maxSize : (int) nDigitPrecision;
    if (nDigitPrecision < ParserConfig.MACHINE_PRECISION) {
      nPrecision = ParserConfig.MACHINE_PRECISION;
    }
    engine.setNumericMode(true, nPrecision, significantFigures);
    final boolean adaptive = Config.MAX_EXTRA_PRECISION > 0 && original.isAST()
        && !original.has(x -> x.isInexactNumber(), true);
    if (expr.isNumber() && !(adaptive && expr.isInexactNumber()
        && lostExactly >= PrecisionLoss.THRESHOLD)) {
      if (nPrecision > ParserConfig.MACHINE_PRECISION && !expr.isInexactNumber()) {
        // an exact number like -3/7*I: converted with guard digits, which the result keeps
        engine.setNumericMode(true, nPrecision + GUARD_DIGITS, significantFigures);
        try {
          return toPrecision(expr.evaluate(engine).orElse(expr), nPrecision);
        } finally {
          engine.setNumericMode(true, nPrecision, significantFigures);
        }
      }
      return expr.evaluate(engine).orElse(expr);
    }
    if (!adaptive || !(expr.isAST() || expr.isNumber())) {
      if (nPrecision > ParserConfig.MACHINE_PRECISION && expr.isSymbol()
          && !original.has(x -> x.isInexactNumber(), true)) {
        // a constant like Pi: with guard digits, as in evalExact()
        engine.setNumericMode(true, nPrecision + GUARD_DIGITS, significantFigures);
        try {
          return toPrecision(engine.evalWithoutNumericReset(expr), nPrecision);
        } finally {
          engine.setNumericMode(true, nPrecision, significantFigures);
        }
      }
      // an inexact number in the input has the digits it has: more working digits add none
      return engine.evalWithoutNumericReset(expr);
    }
    if (expr.isNumber() || expr.has(x -> x.isInexactNumber(), true)) {
      // The exact evaluation has left numbers of the precision: the engine has the precision of
      // the input from its start, and some functions are numeric with that alone - BernoulliB
      // in Zeta(60), the elliptic integrals of exact numbers. More digits of the result need
      // more digits of these, which is the exact evaluation once more.
      final IExpr first = expr;
      final long firstPrecision = nPrecision;
      return evalExact(original, working -> {
        if (working == firstPrecision) {
          return first;
        }
        engine.setNumericMode(false);
        engine.setNumericPrecision(working);
        try {
          return evaluateExact(original, engine);
        } finally {
          engine.setNumericMode(true, working, significantFigures);
        }
      }, lostExactly, nPrecision, significantFigures, engine);
    }
    final IExpr exact = expr;
    return evalExact(expr, working -> exact, 0L, nPrecision, significantFigures, engine);
  }

  /** Working digits beyond the ones a reported loss asks for. */
  private static final long GUARD_DIGITS = 3;

  /**
   * The numeric value of an exact expression. Every operation is calculated with the digits of the
   * working precision and no more, so that a difference of nearly equal numbers, or a periodic
   * function of a large argument, has fewer right digits than was asked for. The operations
   * report that to {@link PrecisionLoss}; the expression is evaluated again then, with as many
   * digits more as were lost, up to {@link Config#MAX_EXTRA_PRECISION} of them.
   *
   * <p>
   * A reported loss need not reach the result: a function may subtract equal numbers on its way
   * and bring its own working digits. So the result of the second evaluation is compared with the
   * first one, which is returned as it is if both agree.
   *
   * @param expr an expression without inexact numbers
   * @param exactAt the expression which is evaluated with a working precision: <code>expr</code>,
   *        or the exact evaluation of the input with that precision of the engine
   * @param lostExactly the digits which the exact evaluation of the input has lost already
   * @param precision the precision of the result
   * @param significantFigures the digits which output forms display
   */
  private static IExpr evalExact(IExpr expr, LongFunction<IExpr> exactAt, final long lostExactly,
      final long precision, int significantFigures, EvalEngine engine) {
    final long maxPrecision = precision + Config.MAX_EXTRA_PRECISION;
    // A function calculated with the digits of the precision has no digits beyond them to round
    // its last digit with: the first evaluation has guard digits, which the result keeps beyond
    // its precision. At machine precision the evaluation stays one with doubles.
    long working =
        precision > ParserConfig.MACHINE_PRECISION ? precision + GUARD_DIGITS : precision;
    boolean first = true;
    if (working > precision) {
      engine.setNumericMode(true, working, significantFigures);
    }
    // the result of the evaluation before, in the digits of the precision
    IExpr previous = null;
    // a result which was zero, because every digit was lost
    IExpr zero = null;
    final long before = PrecisionLoss.start(engine);
    try {
      while (true) {
        PrecisionLoss.reset(engine);
        // Quiet: an evaluation which loses digits reports what the next one does not, the 1/0 of
        // a difference which is zero. The messages of the last evaluation are printed below.
        final boolean quiet = engine.isQuietMode();
        final String shortcut = engine.getMessageShortcut();
        boolean messages;
        IExpr result;
        engine.setMessageShortcut(null);
        engine.setQuietMode(true);
        try {
          result = engine.evalWithoutNumericReset(exactAt.apply(working));
          messages = engine.getMessageShortcut() != null;
        } finally {
          engine.setQuietMode(quiet);
          if (engine.getMessageShortcut() == null) {
            engine.setMessageShortcut(shortcut);
          }
        }
        final long lost =
            first ? Math.max(PrecisionLoss.lost(engine), lostExactly) : PrecisionLoss.lost(engine);
        first = false;
        long needed = working;
        if (lost >= PrecisionLoss.THRESHOLD) {
          // every digit lost: twice the digits; else the digits which were lost in addition
          needed =
              lost >= working ? 2 * working + GUARD_DIGITS : precision + lost + GUARD_DIGITS;
        }
        final IExpr rounded = working > precision ? toPrecision(result, precision) : result;
        if (zero == null && lost >= working && rounded.isZero()) {
          zero = rounded;
        }
        boolean limit = false;
        if (needed > working) {
          if (previous != null && !rounded.isZero() && agree(previous, rounded, precision)) {
            // the loss did not reach the result: the one of the evaluation before is as good
            result = previous;
          } else if (working < maxPrecision) {
            previous = rounded;
            working = Math.min(needed, maxPrecision);
            engine.setNumericMode(true, working, significantFigures);
            continue;
          } else {
            limit = true;
            // Still no digit which is right: what is left of a difference that was zero is the
            // noise of its operands, and the difference is zero as far as can be seen
            result = zero != null && lost >= working - GUARD_DIGITS ? zero : rounded;
          }
        } else {
          result = rounded;
        }
        if (working > precision) {
          engine.setNumericMode(true, precision, significantFigures);
        }
        if (messages && !quiet) {
          // the messages of the last evaluation are wanted: once more, aloud
          engine.evalWithoutNumericReset(exactAt.apply(precision));
        }
        if (limit && !PrecisionLoss.isLimitQuiet(engine)) {
          // Internal precision limit `1` reached while evaluating `2`.
          Errors.printMessage(S.N, "meprec",
              F.list(F.stringx("of " + Config.MAX_EXTRA_PRECISION + " extra digits"), expr),
              engine);
        }
        return result;
      }
    } finally {
      PrecisionLoss.stop(engine, before);
    }
  }

  /**
   * The numbers of a result which was calculated with more digits, with the precision. The digits
   * beyond the precision stay stored: the next operation uses them, and the printed number is
   * rounded with them.
   */
  private static IExpr toPrecision(IExpr result, final long precision) {
    if (result instanceof ApfloatNum) {
      return ApfloatNum.valueOf(round(((ApfloatNum) result).apfloatValue(), precision));
    }
    if (result instanceof ApcomplexNum) {
      final Apcomplex z = ((ApcomplexNum) result).apcomplexValue();
      // each part has the digits of the precision
      return ApcomplexNum
          .valueOf(new Apcomplex(round(z.real(), precision), round(z.imag(), precision)));
    }
    if (result.isAST()) {
      return result.replaceAll(x -> x instanceof ApfloatNum || x instanceof ApcomplexNum
          ? toPrecision(x, precision)
          : F.NIL).orElse(result);
    }
    return result;
  }

  private static Apfloat round(Apfloat x, long precision) {
    if (x.signum() == 0 || precision <= 0) {
      return precision <= 0 ? Apfloat.ZERO : x;
    }
    if (x.precision() <= precision) {
      return x;
    }
    return x.precision(precision);
  }

  /**
   * Whether two results are the same in the digits of the precision: numbers which differ by no
   * more than the last digit, in expressions which are equal otherwise.
   */
  private static boolean agree(IExpr a, IExpr b, final long precision) {
    if (a instanceof INumber && b instanceof INumber) {
      if (!a.isInexactNumber() || !b.isInexactNumber()) {
        return a.equals(b);
      }
      final Apcomplex x = ((INumber) a).apcomplexValue();
      final Apcomplex y = ((INumber) b).apcomplexValue();
      final Apcomplex difference = x.subtract(y);
      if (difference.isZero()) {
        return true;
      }
      // less than two units of the last digit of the larger number
      final long last = Math.max(x.scale(), y.scale()) - precision;
      final Apfloat size = ApcomplexMath.abs(difference.precision(10));
      return size.scale() < last + 1
          || (size.scale() == last + 1 && ApfloatMath.scale(size, -last).doubleValue() <= 2.0);
    }
    if (a.isAST() && b.isAST()) {
      final IAST x = (IAST) a;
      final IAST y = (IAST) b;
      if (x.size() != y.size()) {
        return false;
      }
      for (int i = 0; i < x.size(); i++) {
        if (!agree(x.get(i), y.get(i), precision)) {
          return false;
        }
      }
      return true;
    }
    return a.equals(b);
  }

}
