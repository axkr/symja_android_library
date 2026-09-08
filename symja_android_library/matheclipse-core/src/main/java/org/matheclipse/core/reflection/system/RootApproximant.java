package org.matheclipse.core.reflection.system;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.LLL;
import org.matheclipse.parser.client.ParserConfig;

/**
 * RootApproximant(x) or RootApproximant(x, n)
 *
 * <p>
 * Convert the number <code>x</code> to one of the "nearby" algebraic numbers, of degree at most
 * <code>n</code>.
 *
 * <p>
 * The candidate polynomials are found with lattice basis reduction: for a degree <code>d</code> the
 * rows <code>(e_i | round(K * Re(x^i)) | round(K * Im(x^i)))</code>, <code>i = 0..d</code>, with
 * <code>K = 10^p</code> and <code>p</code> derived from the precision of <code>x</code>, span a
 * lattice whose short vectors are exactly the integer relations between the powers of
 * <code>x</code>, i.e. the coefficient vectors of polynomials which nearly vanish at
 * <code>x</code>.
 */
public class RootApproximant extends AbstractFunctionEvaluator {

  /** Number of decimal digits which are dropped from the precision of the input. */
  private static final int SCALE_GUARD = 2;

  /**
   * A candidate polynomial is only accepted if its coefficients are this many decimal digits
   * smaller than the ones a random (i.e. meaningless) short vector would have.
   */
  private static final int ACCEPT_MARGIN = 3;

  /**
   * A candidate polynomial is only accepted if its coefficients use at most this fraction of the
   * available digits. Without this relative bound a lattice of high dimension produces short
   * vectors which pass the additive {@link #ACCEPT_MARGIN} test although they carry no information
   * about the input value.
   */
  private static final double ACCEPT_FRACTION = 0.75;

  /** Upper bound for the degree which is searched if no degree was given. */
  private static final int MAX_DEFAULT_DEGREE = 12;

  /** Lower bound for the degree which is searched if no degree was given. */
  private static final int MIN_DEFAULT_DEGREE = 6;

  public RootApproximant() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr arg1 = ast.arg1();

    // exact rational and Gaussian rational numbers are already algebraic numbers
    if (arg1.isRational() || arg1.isComplex()) {
      return arg1;
    }
    if (!arg1.isNumericFunction()) {
      return F.NIL;
    }

    int maxDegree = -1;
    if (ast.isAST2()) {
      maxDegree = ast.arg2().toIntDefault();
      if (maxDegree <= 0) {
        // Positive integer (less equal 2147483647) expected at position `2` in `1`.
        return Errors.printMessage(S.RootApproximant, "intpm", F.list(ast, F.C2), engine);
      }
    }

    try {
      IExpr numericArg = engine.evalN(arg1);
      if (!numericArg.isNumber()) {
        return F.NIL;
      }
      INumber value = (INumber) numericArg;
      if (value.isZero()) {
        return F.C0;
      }

      int precision = precisionOf(value);
      int digits = Math.max(4, precision - SCALE_GUARD);
      if (maxDegree < 0) {
        maxDegree = precision <= ParserConfig.MACHINE_PRECISION ? MIN_DEFAULT_DEGREE
            : Math.min(MAX_DEFAULT_DEGREE, Math.max(MIN_DEFAULT_DEGREE, precision / 4));
      }
      if (maxDegree > Config.MAX_POLYNOMIAL_DEGREE) {
        maxDegree = Config.MAX_POLYNOMIAL_DEGREE;
      }

      final boolean complex = !value.im().isZero();
      if (complex) {
        // a Gaussian rational is found from the real and the imaginary part separately
        IExpr re = rationalReconstruct(value.re(), digits);
        if (re.isPresent()) {
          IExpr im = rationalReconstruct(value.im(), digits);
          if (im.isPresent()) {
            return F.Plus(re, F.Times(F.CI, im));
          }
        }
      }

      // the powers value^0, value^1, ..., value^maxDegree
      INumber[] powers = new INumber[maxDegree + 1];
      powers[0] = F.C1;
      for (int i = 1; i <= maxDegree; i++) {
        IExpr power = engine.evaluate(F.Times(powers[i - 1], value));
        if (!power.isNumber()) {
          return F.NIL;
        }
        powers[i] = (INumber) power;
      }

      for (int degree = 1; degree <= maxDegree; degree++) {
        BigInteger[] coefficients = shortestRelation(powers, degree, digits, complex);
        if (coefficients == null) {
          continue;
        }
        IExpr result = candidateToAlgebraicNumber(coefficients, value, digits, engine);
        if (result.isPresent()) {
          return result;
        }
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(S.RootApproximant, rex);
    }

    // no algebraic number found; leave the input unchanged
    return arg1;
  }

  /**
   * Reduce the lattice spanned by the powers <code>1, x, ..., x^degree</code> and return the
   * coefficient vector of the shortest vector found, with trailing zeros removed.
   *
   * @param powers the powers of the value
   * @param degree the degree of the candidate polynomial
   * @param digits the number of decimal digits which are used for the scaling
   * @param complex whether an imaginary part has to be taken into account
   * @return the coefficients indexed by their exponent, or <code>null</code> if the candidate has
   *         to be rejected
   */
  private static BigInteger[] shortestRelation(INumber[] powers, int degree, int digits,
      boolean complex) {
    final int columns = degree + 1 + (complex ? 2 : 1);
    BigInteger[][] lattice = new BigInteger[degree + 1][columns];
    for (int i = 0; i <= degree; i++) {
      for (int j = 0; j <= degree; j++) {
        lattice[i][j] = i == j ? BigInteger.ONE : BigInteger.ZERO;
      }
      lattice[i][degree + 1] = scaledRound(powers[i].re(), digits);
      if (complex) {
        lattice[i][degree + 2] = scaledRound(powers[i].im(), digits);
      }
    }

    BigInteger[][] reduced = LLL.reduce(lattice);
    if (reduced.length == 0) {
      return null;
    }
    BigInteger[] shortest = reduced[0];
    BigInteger shortestNorm = LLL.normSquared(shortest);
    for (int i = 1; i < reduced.length; i++) {
      BigInteger norm = LLL.normSquared(reduced[i]);
      if (norm.compareTo(shortestNorm) < 0) {
        shortest = reduced[i];
        shortestNorm = norm;
      }
    }

    // strip the scaled columns and the trailing zero coefficients
    int actualDegree = degree;
    while (actualDegree >= 0 && shortest[actualDegree].signum() == 0) {
      actualDegree--;
    }
    if (actualDegree < 1) {
      // the zero polynomial or a constant is not a candidate
      return null;
    }
    BigInteger[] coefficients = new BigInteger[actualDegree + 1];
    BigInteger height = BigInteger.ZERO;
    for (int i = 0; i <= actualDegree; i++) {
      coefficients[i] = shortest[i];
      BigInteger abs = coefficients[i].abs();
      if (abs.compareTo(height) > 0) {
        height = abs;
      }
    }

    // A random lattice of this shape has a shortest vector with coefficients of about
    // K^(1/(degree+1)) = 10^(digits/(degree+1)). Only accept a candidate whose coefficients are
    // clearly smaller than that, otherwise it carries no information about the input value.
    double heightDigits = height.signum() == 0 ? 0.0 : bitLengthToDigits(height);
    double usedDigits = (actualDegree + 1) * heightDigits;
    if (usedDigits + ACCEPT_MARGIN > digits || usedDigits > ACCEPT_FRACTION * digits) {
      return null;
    }
    return coefficients;
  }

  /**
   * Turn an accepted coefficient vector into an algebraic number.
   *
   * @return {@link F#NIL} if the polynomial doesn't vanish at <code>value</code> closely enough
   */
  private static IExpr candidateToAlgebraicNumber(BigInteger[] coefficients, INumber value,
      int digits, EvalEngine engine) {
    // normalize: divide by the content and make the leading coefficient positive
    BigInteger content = BigInteger.ZERO;
    for (int i = 0; i < coefficients.length; i++) {
      content = content.gcd(coefficients[i]);
    }
    if (content.signum() == 0) {
      return F.NIL;
    }
    boolean negate = coefficients[coefficients.length - 1].signum() < 0;
    ISymbol x = F.Dummy("x");
    IASTAppendable poly = F.PlusAlloc(coefficients.length);
    for (int i = 0; i < coefficients.length; i++) {
      BigInteger c = coefficients[i].divide(content);
      if (negate) {
        c = c.negate();
      }
      if (c.signum() != 0) {
        poly.append(i == 0 ? F.ZZ(c) : F.Times(F.ZZ(c), F.Power(x, F.ZZ(i))));
      }
    }
    IExpr polyInX = engine.evaluate(poly);

    // the candidate may be reducible; use the irreducible factor which vanishes at the value
    IExpr factorList = engine.evaluate(F.FactorList(polyInX));
    if (factorList.isList()) {
      IExpr bestFactor = F.NIL;
      IExpr bestValue = F.NIL;
      IAST list = (IAST) factorList;
      for (int i = 1; i < list.size(); i++) {
        IExpr entry = list.get(i);
        if (!entry.isList2()) {
          continue;
        }
        IExpr factor = entry.first();
        if (factor.isFree(x)) {
          continue;
        }
        IExpr residual = engine.evaluate(F.Abs(F.subst(factor, x, value)));
        if (!residual.isReal()) {
          continue;
        }
        if (bestValue.isNIL() || ((IReal) residual).isLT((IReal) bestValue)) {
          bestValue = residual;
          bestFactor = factor;
        }
      }
      if (bestFactor.isPresent()) {
        polyInX = bestFactor;
      }
    }

    IExpr degree = engine.evaluate(F.Exponent(polyInX, x));
    int degreeInt = degree.toIntDefault();
    if (degreeInt < 1) {
      return F.NIL;
    }

    // Verify that the polynomial really (nearly) vanishes at the value. The input carries an error
    // of about |value| * 10^-digits, which the evaluation of the polynomial amplifies by at most
    // the sum of the absolute values of its terms. Two orders of magnitude of slack are added
    // because the height criterion above is what actually rejects meaningless candidates.
    IExpr residual = engine.evaluate(F.Abs(F.subst(polyInX, x, value)));
    if (!residual.isReal()) {
      return F.NIL;
    }
    IExpr coefficientList = engine.evaluate(F.CoefficientList(polyInX, x));
    if (!coefficientList.isList()) {
      return F.NIL;
    }
    IAST coefficientAST = (IAST) coefficientList;
    IASTAppendable terms = F.PlusAlloc(coefficientAST.size());
    for (int i = 1; i < coefficientAST.size(); i++) {
      terms.append(F.Times(F.Abs(coefficientAST.get(i)), F.Power(F.Abs(value), F.ZZ(i - 1))));
    }
    IExpr bound = engine.evaluate(F.Times(F.ZZ(degreeInt + 1),
        F.Power(F.C10, F.ZZ(2 - digits)), F.Plus(F.C1, terms)));
    if (!bound.isReal() || !((IReal) residual).isLE((IReal) bound)) {
      return F.NIL;
    }

    if (degreeInt == 1) {
      IExpr c1 = S.Coefficient.funEval(engine, polyInX, x, F.C1);
      IExpr c0 = S.Coefficient.funEval(engine, polyInX, x, F.C0);
      return engine.evaluate(F.Divide(F.Negate(c0), c1));
    }

    double tolerance = digits <= ParserConfig.MACHINE_PRECISION ? 1e-6 : Math.pow(10.0, -digits / 2);
    return RootReduce.nearestRootObject(polyInX, x, F.NIL, value, tolerance, engine);
  }

  /**
   * Reconstruct a rational number from a real number by reducing the two-dimensional lattice
   * <code>{(1, 0, round(K*x)), (0, 1, -K)}</code>.
   *
   * @return {@link F#NIL} if no rational number with small enough numerator and denominator was
   *         found
   */
  private static IExpr rationalReconstruct(IReal value, int digits) {
    BigInteger scale = BigInteger.TEN.pow(digits);
    BigInteger[][] lattice = new BigInteger[][] { //
        {BigInteger.ONE, BigInteger.ZERO, scaledRound(value, digits)}, //
        {BigInteger.ZERO, BigInteger.ONE, scale.negate()}};
    BigInteger[][] reduced = LLL.reduce(lattice);
    if (reduced.length == 0) {
      return F.NIL;
    }
    BigInteger[] shortest = reduced[0];
    if (LLL.normSquared(reduced[1]).compareTo(LLL.normSquared(shortest)) < 0) {
      shortest = reduced[1];
    }
    BigInteger denominator = shortest[0];
    BigInteger numerator = shortest[1];
    if (denominator.signum() == 0) {
      return F.NIL;
    }
    double heightDigits =
        Math.max(bitLengthToDigits(denominator.abs()), bitLengthToDigits(numerator.abs()));
    if (2 * heightDigits + ACCEPT_MARGIN > digits
        || 2 * heightDigits > ACCEPT_FRACTION * digits) {
      return F.NIL;
    }
    return F.fraction(numerator, denominator);
  }

  /**
   * Round <code>value * 10^digits</code> to the nearest integer, exactly, without going through a
   * <code>double</code>.
   *
   * @param value
   * @param digits
   * @return
   */
  private static BigInteger scaledRound(IReal value, int digits) {
    if (value.isZero()) {
      return BigInteger.ZERO;
    }
    if (value.isRational()) {
      IRational rational = (IRational) value;
      return new BigDecimal(rational.toBigNumerator()) //
          .movePointRight(digits)
          .divide(new BigDecimal(rational.toBigDenominator()), 0, RoundingMode.HALF_EVEN)
          .toBigInteger();
    }
    if (value instanceof INum && ((INum) value).precision() <= ParserConfig.MACHINE_PRECISION) {
      // new BigDecimal(double) is the exact binary value of the double
      return new BigDecimal(value.doubleValue()).movePointRight(digits)
          .setScale(0, RoundingMode.HALF_EVEN).toBigInteger();
    }
    Apfloat scaled = ApfloatMath.scale(value.apfloatValue(), digits);
    Apint truncated = scaled.truncate();
    // compareTo on Apfloats is exact, adding 0.5 first would lose precision
    Apfloat fraction = ApfloatMath.abs(scaled.subtract(truncated));
    if (fraction.compareTo(new Apfloat("0.5")) >= 0) {
      truncated = scaled.signum() < 0 ? truncated.subtract(Apint.ONE) : truncated.add(Apint.ONE);
    }
    return truncated.toBigInteger();
  }

  /** The decimal logarithm of the absolute value of a {@link BigInteger}. */
  private static double bitLengthToDigits(BigInteger value) {
    if (value.signum() == 0) {
      return 0.0;
    }
    BigInteger abs = value.abs();
    int bits = abs.bitLength();
    if (bits < 1000) {
      double d = abs.doubleValue();
      if (d > 0.0 && !Double.isInfinite(d)) {
        return Math.log10(d);
      }
    }
    // an upper bound is good enough for numbers this large
    return bits * 0.30102999566398119521;
  }

  /** The number of correct decimal digits the given number carries. */
  private static int precisionOf(INumber value) {
    try {
      if (value.isInexactNumber()) {
        long precision = value.re() instanceof INum ? ((INum) value.re()).precision()
            : ParserConfig.MACHINE_PRECISION;
        if (precision > 0 && precision < Integer.MAX_VALUE) {
          return (int) precision;
        }
      }
    } catch (RuntimeException rex) {
      // fall through to the machine precision
    }
    return (int) ParserConfig.MACHINE_PRECISION;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.LISTABLE);
  }
}
