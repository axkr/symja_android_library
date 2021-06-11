package org.matheclipse.core.interfaces;

import java.math.BigInteger;
import org.matheclipse.core.expression.F;

/** An expression representing a big integer number */
public interface IInteger extends IRational {

  @Override
  public void checkBitLength();

  /** Certainty for the isProbablePrime() method */
  public static final int PRIME_CERTAINTY = 32;

  /** {@inheritDoc} */
  @Override
  public IInteger abs();

  /**
   * <code>this + val</code>
   *
   * @param val
   * @return
   */
  public IInteger add(IInteger val);

  /**
   * Returns the number of bits in the minimal two's-complement representation of this IInteger,
   * <i>excluding</i> a sign bit. For positive IIntegers, this is equivalent to the number of bits
   * in the ordinary binary representation.
   *
   * @return number of bits in the minimal two's-complement representation of this IInteger,
   *     <i>excluding</i> a sign bit.
   */
  public long bitLength();

  /**
   * @return <i>&#x03BB;</i>(<i>n</i>) where <i>n</i> is the number represented by this factors
   * @throws ArithmeticException if internal conversion to <code>long</code> is not possible.
   */
  public IInteger charmichaelLambda();

  @Override
  public IInteger dec();

  @Override
  public IInteger inc();

  /**
   * Returns an IInteger whose value is <code>(this / that)</code>.
   *
   * @param that value by which this IInteger is to be divided.
   * @return <code>(this / that)</code>
   * @throws ArithmeticException if <code>(that)</code> is zero.
   */
  public IInteger div(final IInteger that);

  default IInteger div(final int that) {
    if (that == 1) {
      return this;
    }
    return div(F.ZZ(that));
  }

  /**
   * Returns an array of two IIntegers containing (this / that) followed by (this % that).
   *
   * @param that
   * @return
   */
  public IInteger[] divideAndRemainder(final IInteger that);

  public IInteger iquo(final IInteger that);

  public IInteger irem(final IInteger that);

  /**
   * Return the divisors of this integer number.
   *
   * <p>divisors for <code>24</code> gives:
   *
   * <pre>
   * { 1, 2, 3, 4, 6, 8, 12, 24 }
   * </pre>
   *
   * @return a list of the divisors
   */
  public IAST divisors();

  /**
   * Euler phi function.
   *
   * <p>See: <a href="http://en.wikipedia.org/wiki/Euler%27s_totient_function">Euler's totient
   * function</a>
   *
   * @return Euler's totient function
   * @throws ArithmeticException
   */
  public IInteger eulerPhi() throws ArithmeticException;

  /**
   * Get the highest exponent of <code>base</code> that divides <code>this</code>
   *
   * @param base an integer greater than 1
   * @return the exponent
   */
  public IExpr exponent(IInteger base);

  /**
   * The factorial number.
   *
   * @return
   */
  public IInteger factorial();

  /**
   * Returns the greatest common divisor of this large integer and the one specified.
   *
   * @param val an integer value
   * @return
   */
  public IInteger gcd(IInteger val);

  /**
   * Number of digits in base <code>radix</code> implementation.
   *
   * @param radix
   * @return
   */
  public long integerLength(IInteger radix);

  /**
   * Converts this integer to <code>byte</code>; this method raises no exception, if this integer
   * cannot be represented by an <code>byte</code> type.
   *
   * @return the numeric value represented by this integer after conversion to type <code>byte
   *     </code>.
   */
  public byte byteValue();

  /**
   * Converts this integer to <code>int</code>; unlike {@link #toInt} this method raises no
   * exception, if this integer cannot be represented by an <code>int</code> type.
   *
   * @return the numeric value represented by this integer after conversion to type <code>int</code>
   *     .
   */
  public int intValue();

  /**
   * Check if this IInteger is an even number.
   *
   * @return <code>true</code> if this IInteger is an even number.
   */
  public boolean isEven();

  /**
   * Check if this IInteger is an odd number.
   *
   * @return <code>true</code> if this IInteger is an odd number.
   */
  public boolean isOdd();

  /**
   * Returns {@code true} if this IInteger is probably prime, {@code false} if it's definitely
   * composite. A negative integer p is prime, if <code>p.negate()</code> is a prime number
   *
   * @return {@code true} if this IInteger is probably prime, {@code false} if it's definitely
   *     composite.
   */
  public boolean isProbablePrime();

  /**
   * Returns {@code true} if this IInteger is probably prime, {@code false} if it's definitely
   * composite. If {@code certainty} is &le; 0, {@code true} is returned.
   *
   * @param certainty a measure of the uncertainty that the caller is willing to tolerate: if the
   *     call returns {@code true} the probability that this IInteger is prime exceeds (1 - 1/2<sup>
   *     {@code certainty}</sup>). The execution time of this method is proportional to the value of
   *     this parameter.
   * @return {@code true} if this IInteger is probably prime, {@code false} if it's definitely
   *     composite.
   */
  public boolean isProbablePrime(int certainty);

  /**
   * See: <a href="http://en.wikipedia.org/wiki/Jacobi_symbol">Wikipedia - Jacobi symbol</a><br>
   * Book: Algorithmen Arbeitsbuch - D.Herrmann page 160
   *
   * @param b
   * @return
   */
  public IInteger jacobiSymbol(IInteger b);

  public IInteger jacobiSymbolF();

  public IInteger jacobiSymbolG(IInteger b);

  /**
   * Returns the least common multiple of this integer and the one specified.
   *
   * @param val
   * @return
   */
  public IInteger lcm(IInteger val);

  /**
   * Converts this IInteger to a {@code long}. This conversion is analogous to a <i>narrowing
   * primitive conversion</i> from {@code long} to {@code int} as defined in section 5.1.3 of
   * <cite>The Java&trade; Language Specification</cite>: if this IInteger is too big to fit in a
   * {@code long}, only the low-order 64 bits are returned. Note that this conversion can lose
   * information about the overall magnitude of the IInteger value as well as return a result with
   * the opposite sign.
   *
   * @return this IInteger converted to a {@code long}.
   */
  public long longValue();

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
  public IInteger mod(final IInteger m);

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
   * @param that
   * @return
   * @throws ArithmeticException - if m <= 0
   */
  default IInteger mod(final int that) {
    if (that == 1) {
      return F.C0;
    }
    return mod(F.ZZ(that));
  }

  public IInteger modInverse(final IInteger m);

  public IInteger modPow(final IInteger exp, final IInteger m);

  public IInteger moebiusMu();

  /**
   * Multiply this integer with value
   *
   * @param value
   * @return
   */
  public IInteger multiply(IInteger value);

  /**
   * Multiply this integer with value
   *
   * @param value
   * @return
   */
  @Override
  public IInteger multiply(int value);

  /**
   * Returns an <code>IInteger</code> whose value is <code>(-1) * this</code>.
   *
   * @return
   */
  @Override
  public IInteger negate();

  /**
   * Returns the n-th integer root
   *
   * @param n
   * @return
   * @throws ArithmeticException
   */
  public IExpr nthRoot(int n);

  /**
   * Split this integer into the nth-root (with prime factors less equal 1021) and the &quot;rest
   * factor&quot;
   *
   * @param n nth-root
   * @return <code>{nth-root, rest factor}</code>
   */
  public IInteger[] nthRootSplit(int n);

  @Override
  public IInteger powerRational(final long exp) throws ArithmeticException;

  /**
   *
   *
   * <pre>
   * primitiveRootList()
   * </pre>
   *
   * <blockquote>
   *
   * <p>returns the list of the primitive roots of <code>this</code> integer.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; PrimitiveRootList(37)
   * {2,5,13,15,17,18,19,20,22,24,32,35}
   *
   * &gt;&gt; PrimitiveRootList(127)
   * {3,6,7,12,14,23,29,39,43,45,46,48,53,55,56,57,58,65,67,78,83,85,86,91,92,93,96,97,101,106,109,110,112,114,116,118}
   * </pre>
   *
   * @return
   * @throws ArithmeticException
   */
  public IInteger[] primitiveRootList() throws ArithmeticException;

  public IInteger quotient(final IInteger that);

  public IInteger shiftLeft(final int n);

  public IInteger shiftRight(final int n);

  /**
   * Returns an array of two IIntegers containing the integer square root {@code s} of {@code this}
   * and its remainder {@code this - s*s}, respectively.
   *
   * @return an array of two IIntegers with the integer square root at offset 0 and the remainder at
   *     offset 1 or <code>null</code> if <code>this</code> is negative.
   */
  public IInteger[] sqrtAndRemainder();

  public IInteger subtract(IInteger value);

  /**
   * Returns the numerator of this Rational.
   *
   * @return numerator
   */
  @Override
  public BigInteger toBigNumerator();

  public byte[] toByteArray();
}
