package org.matheclipse.core.interfaces;

import java.math.BigInteger;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.UniformFlags;

/** An expression representing a big integer number */
public interface IInteger extends IRational {

  /** The BigInteger constant minus one. */
  BigInteger BI_MINUS_ONE = BigInteger.valueOf(-1L);

  /** The BigInteger constant 2. */
  BigInteger BI_TWO = BigInteger.valueOf(2L);

  /** The BigInteger constant 3. */
  BigInteger BI_THREE = BigInteger.valueOf(3L);

  /** The BigInteger constant 4. */
  BigInteger BI_FOUR = BigInteger.valueOf(4L);

  /** The BigInteger constant 7. */
  BigInteger BI_SEVEN = BigInteger.valueOf(7L);

  /** The BigInteger constant 8. */
  BigInteger BI_EIGHT = BigInteger.valueOf(8L);

  /** Certainty for the isProbablePrime() method */
  public static final int PRIME_CERTAINTY = 100;

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
   * <code>this + val</code>
   *
   * @param val
   * @return
   */
  @Override
  public IInteger add(int val);

  /**
   * Returns the number of bits in the minimal two's-complement representation of this IInteger,
   * <i>excluding</i> a sign bit. For positive IIntegers, this is equivalent to the number of bits
   * in the ordinary binary representation.
   *
   * @return number of bits in the minimal two's-complement representation of this IInteger,
   *         <i>excluding</i> a sign bit.
   */
  public long bitLength();

  /**
   * Converts this integer to <code>byte</code>; this method raises no exception, if this integer
   * cannot be represented by an <code>byte</code> type.
   *
   * @return the numeric value represented by this integer after conversion to type <code>byte
   *     </code>.
   */
  public byte byteValue();

  /**
   * @return <i>&#x03BB;</i>(<i>n</i>) where <i>n</i> is the number represented by this factors
   * @throws ArithmeticException if internal conversion to <code>long</code> is not possible.
   */
  public IInteger charmichaelLambda();

  @Override
  public void checkBitLength();

  @Override
  public IInteger dec();

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

  /**
   * Return the divisors of this integer number.
   *
   * <p>
   * divisors for <code>24</code> gives:
   *
   * <pre>
   * {1, 2, 3, 4, 6, 8, 12, 24}
   * </pre>
   *
   * @return a list of the divisors
   */
  public IAST divisors();

  /**
   * Euler phi function.
   *
   * <p>
   * See: <a href="http://en.wikipedia.org/wiki/Euler%27s_totient_function">Euler's totient
   * function</a>
   *
   * @return Euler's totient function
   * @throws ArithmeticException
   */
  public IInteger eulerPhi() throws ArithmeticException;

  public IInteger[] extendedGCD(IInteger that);

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
  @Override
  public IInteger factorial();

  /**
   * Returns the greatest common divisor of this large integer and the one specified.
   *
   * @param val an integer value
   * @return
   */
  public IInteger gcd(IInteger val);

  @Override
  public IInteger inc();

  /**
   * Number of digits in base <code>radix</code> implementation.
   *
   * @param radix
   * @return
   */
  public long integerLength(IInteger radix);

  /**
   * Converts this integer to <code>int</code>; unlike {@link #toInt} this method raises no
   * exception, if this integer cannot be represented by an <code>int</code> type.
   *
   * @return the numeric value represented by this integer after conversion to type <code>int</code>
   *         .
   */
  public int intValue();

  /**
   * &quot;integer quotientr&quot; of <code>this</code> divided by <code>that</code> also sometimes
   * called &quot;floor division&quot;
   * 
   * @param that
   * @return
   */
  public IInteger iquo(final IInteger that);

  /**
   * &quot;integer remainder&quot; of <code>this</code> divided by <code>that</code>
   * 
   * @param that
   * @return
   */
  public IInteger irem(final IInteger that);

  /**
   * Check if this IInteger is an even number.
   *
   * @return <code>true</code> if this IInteger is an even number.
   */
  @Override
  public boolean isEven();

  /**
   * Check if this integer is an integer multiple of the {@code other} integer.
   * <p>
   * Returns {@code true} if there exists an integer {@code k} such that {@code this == k * other}.
   * <p>
   * Special cases:
   * <ul>
   * <li>{@code 0.isMultipleOf(x)} is always {@code true} (since {@code 0 = 0 * x}).</li>
   * <li>{@code x.isMultipleOf(0)} is {@code true} if and only if {@code x} is also {@code 0} (since
   * {@code x = k * 0} implies {@code x = 0}).</li>
   * <li>{@code x.isMultipleOf(1)} and {@code x.isMultipleOf(-1)} are always {@code true}.</li>
   * </ul>
   *
   * @param other the integer to check divisibility by. Cannot be null.
   * @return {@code true} if this integer is a multiple of {@code other}, {@code false} otherwise.
   * @throws NullPointerException if {@code other} is null.
   */
  boolean isMultipleOf(IInteger other);

  /**
   * Check if this IInteger is an odd number.
   *
   * @return <code>true</code> if this IInteger is an odd number.
   */
  @Override
  public boolean isOdd();

  /**
   * Returns {@code true} if this IInteger is probably prime, {@code false} if it's definitely
   * composite. A negative integer p is prime, if <code>p.negate()</code> is a prime number
   *
   * @return {@code true} if this IInteger is probably prime, {@code false} if it's definitely
   *         composite.
   */
  public boolean isProbablePrime();

  /**
   * Returns {@code true} if this IInteger is probably prime, {@code false} if it's definitely
   * composite. If {@code certainty} is &le; 0, {@code true} is returned.
   *
   * @param certainty a measure of the uncertainty that the caller is willing to tolerate: if the
   *        call returns {@code true} the probability that this IInteger is prime exceeds (1 -
   *        1/2<sup> {@code certainty}</sup>). The execution time of this method is proportional to
   *        the value of this parameter.
   * @return {@code true} if this IInteger is probably prime, {@code false} if it's definitely
   *         composite.
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
   * Returns a pair with the n-th integer root as first element and {@link S#True} or
   * {@link S#False} as the second element to indicate if the root is exact.
   *
   *
   * @param n
   * @return
   * @throws IllegalArgumentException if {@code this < 0}
   * @throws ArithmeticException if this integer is negative and n is even.
   */
  public IPair nthRoot(int n);

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
   * <p>
   * returns the list of the primitive roots of <code>this</code> integer.
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

  /**
   * Returns the quotient of the division of this integer by the specified value.
   * 
   * @param that the divisor
   * @return
   */
  public IInteger quotient(final IInteger that);

  public IInteger shiftLeft(final int n);

  public IInteger shiftRight(final int n);

  /**
   * Returns an array of two IIntegers containing the integer square root {@code s} of {@code this}
   * and its remainder {@code this - s*s}, respectively.
   *
   * @return an array of two IIntegers with the integer square root at offset 0 and the remainder at
   *         offset 1 or <code>null</code> if <code>this</code> is negative.
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

  @Override
  default int uniformFlags() {
    return UniformFlags.INTEGER | UniformFlags.NUMBER | UniformFlags.ATOM;
  }
}
