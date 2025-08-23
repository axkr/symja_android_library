package org.matheclipse.core.interfaces;

import java.math.BigInteger;
import org.hipparchus.fraction.BigFraction;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.expression.AbstractFractionSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.FractionSym;
import com.google.common.math.BigIntegerMath;
import edu.jas.arith.BigComplex;

/** Interface for "rational" numbers (i.e. numbers implementing IInteger or IFraction) */
public interface IRational extends IReal, IBigNumber {

  /** {@inheritDoc} */
  @Override
  public IRational abs();

  public IRational add(IRational parm1);

  default IRational add(int parm1) {
    return add(F.ZZ(parm1));
  }

  @Override
  public IInteger ceil();

  /**
   * Check if the bit length of the numerator or denominator is greater than
   * {@link Config#MAX_BIT_LENGTH}.
   * <p>
   * If <code>true</code> throw BigIntegerLimitExceeded
   */
  public void checkBitLength() throws BigIntegerLimitExceeded;

  public int compareInt(final int value);

  @Override
  public IRational dec();

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   */
  public IInteger denominator();

  public IRational divideBy(IRational parm1);

  default IRational divideBy(int parm1) {
    return divideBy(F.ZZ(parm1));
  }

  /**
   * Check if this number equals the given fraction <code>numerator/denominator</code> number.
   * <code>GCD(numerator, /denominator)</code> should be 1;
   *
   * @param numerator the numerator
   * @param denominator the denominator
   * @return
   */
  public boolean equalsFraction(final int numerator, final int denominator);

  /**
   * Return the prime factors paired with their exponents for integer and fractional numbers. For
   * factors of the denominator part of fractional numbers the exponents are negative.
   *
   * <pre>
   * factorInteger(-4) ==> {{-1,1},{2,2}}
   * </pre>
   *
   * @return the list of prime factors paired with their exponents
   */
  public IASTAppendable factorInteger();

  /**
   * Return the prime factors paired with their exponents for integer and fractional numbers. For
   * factors of the denominator part of fractional numbers the exponents are negative.
   *
   * <code>F.ZZ(-4).factorSmallPrimes(1,2) => Times((-1)^1,2^2)</code>
   * 
   * @param rootNumerator the numerator of the root
   * @param rootDenominator the denominator of the root
   * @return
   */
  public IAST factorSmallPrimes(int rootNumerator, int rootDenominator);

  @Override
  public IInteger floor();

  /**
   * Return the fractional part of this fraction
   *
   * @return
   */
  @Override
  public IRational fractionalPart();

  /**
   * Compute the gcd of two rationals. The gcd is the rational number, such that dividing this and
   * other with the gcd will yield two co-prime integers.
   *
   * @param that the second rational argument.
   * @return the gcd of this and other.
   */
  public IRational gcd(IRational that);

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   * @deprecated use {@link #denominator()}
   */
  @Deprecated
  default IInteger getDenominator() {
    return denominator();
  }

  /**
   * Returns this number as <code>BigFraction</code> number.
   *
   * @return <code>this</code> number s big fraction.
   * @deprecated use {@link #toBigFraction()}
   */
  @Deprecated
  default BigFraction getFraction() {
    return toBigFraction();
  }

  /**
   * Returns the numerator of this fraction.
   *
   * @return
   * @deprecated use {@link #numerator()}
   */
  @Deprecated
  default IInteger getNumerator() {
    return numerator();
  }

  @Override
  default IRational imRational() {
    return F.C0;
  }

  @Override
  public IRational inc();

  /** {@inheritDoc} */
  @Override
  public IRational inverse();

  default IRational max(IRational that) {
    return isGT(that) ? this : that;
  }

  default IRational min(IRational that) {
    return isLT(that) ? this : that;
  }

  /**
   * Returns <code>this mod m</code>, a non-negative value less than m. This differs from <code>
   * this % m</code>, which might be negative. For example:
   *
   * <pre>
   * mod(7, 4) == 3
   * mod(-7, 4) == 1
   * mod(-1, 4) == 3
   * mod(-8, 4) == 0
   * mod(8, 4) == 0
   * </pre>
   *
   * @param m
   * @return
   * @throws ArithmeticException - if m <= 0
   */
  default IRational mod(final IRational m) {
    return subtract(m.multiply(this.divideBy(m).floorFraction()));
  }

  @Override
  public IRational multiply(int n);

  public IRational multiply(IRational parm1);

  @Override
  public IRational negate();

  /**
   * Return the normalized form of this number (i.e. if the denominator part equals one, return the
   * numerator part as an integer number).
   *
   * @return
   */
  public IRational normalize();

  /**
   * Returns the numerator of this fraction.
   *
   * @return numerator
   */
  public IInteger numerator();

  /**
   * Returns this number raised at the specified exponent. See
   * <a href="https://en.wikipedia.org/wiki/Exponentiation_by_squaring">Wikipedia - Exponentiation
   * by squaring</a>
   *
   * @param exp the exponent.
   * @return <code>this<sup>exp</sup></code>
   * @throws ArithmeticException if {@code 0^0} is given.
   */
  public IRational powerRational(final long exp) throws ArithmeticException;

  @Override
  default IRational reRational() {
    return this;
  }

  @Override
  public IRational roundClosest(IReal factor);

  public IRational subtract(IRational parm1);

  default IRational subtract(int parm1) {
    return subtract(F.ZZ(parm1));
  }

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   */
  public BigInteger toBigDenominator();

  /**
   * Returns this number as {@link edu.jas.arith.BigRational} number.
   *
   * @return <code>this</code> number s big fraction.
   */
  public BigFraction toBigFraction();

  /**
   * Returns this number as {@link edu.jas.arith.BigComplex} number.
   *
   * @return <code>this</code> number s big complex representation.
   */
  @Override
  default BigComplex toBigComplex() {
    return new BigComplex(this.toBigRational());
  }

  /**
   * Returns this number as {@link edu.jas.arith.BigRational} number.
   *
   * @return <code>this</code> number s big fraction.
   */
  public edu.jas.arith.BigRational toBigRational();

  /**
   * Returns the numerator of this fraction.
   *
   * @return denominator
   */
  public BigInteger toBigNumerator();

  /**
   * Truncates the integer part in the "direction to 0" as <code>isNegative() ? ceil() : floor()
   * </code>..
   *
   * @return
   */
  default IInteger trunc() {
    return isNegative() ? ceil() : floor();
  }

  /**
   * Compute the Bernoulli number of the first kind.
   *
   * @param n
   * @return throws ArithmeticException if n is a negative int number
   */
  static IRational bernoulliNumber(int n) {
    if (n == 0) {
      return F.C1;
    } else if (n == 1) {
      return F.CN1D2;
    } else if (n < 0) {
      throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
    } else if (n % 2 != 0) {
      // http://fungrim.org/entry/a98234/
      return F.C0;
    }
    if (n > Config.MAX_AST_SIZE) {
      throw new ASTElementLimitExceeded(n);
    }
    IFraction[] bernoulli = new IFraction[n + 1];
    bernoulli[0] = FractionSym.ONE;
    bernoulli[1] = AbstractFractionSym.valueOf(-1L, 2L);
  
    int iterationLimit = EvalEngine.get().getIterationLimit();
    if (iterationLimit > 0 && iterationLimit < Integer.MAX_VALUE / 2) {
      iterationLimit *= 2;
    }
    int iterationCounter = 0;
    for (int k = 2; k <= n; k++) {
      bernoulli[k] = FractionSym.ZERO;
      for (int i = 0; i < k; i++) {
        if (!bernoulli[i].isZero()) {
          if (iterationLimit > 0 && iterationLimit <= iterationCounter++) {
            IterationLimitExceeded.throwIt(iterationCounter, F.BernoulliB(F.ZZ(n)));
          }
          IFraction bin = AbstractFractionSym.valueOf(BigIntegerMath.binomial(k + 1, k + 1 - i));
          bernoulli[k] = bernoulli[k].sub(bin.mul(bernoulli[i]));
        }
      }
      bernoulli[k] = bernoulli[k].div(AbstractFractionSym.valueOf(k + 1));
    }
    return bernoulli[n].normalize();
  }

  /**
   * Compute the Bernoulli number of the first kind.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>.
   * <br>
   * For better performing implementations see
   * <a href= "http://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers"
   * >ComputationAndAsymptoticsOfBernoulliNumbers</a>
   *
   * @param n
   * @return throws ArithmeticException if n is not an non-negative Java int number
   */
  static IRational bernoulliNumber(final IInteger n) {
    int bn = n.toIntDefault(-1);
    if (bn >= 0) {
      return IRational.bernoulliNumber(bn);
    }
    throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
  }
}
