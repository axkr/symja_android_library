package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
// <XMP>
// Gaussian Integer factorization applet
//
// Written by Dario Alejandro Alpern (Buenos Aires - Argentina)
// Last updated May 31st, 2002, See http://www.alpertron.com.ar/GAUSSIAN.HTM
//
// No part of this code can be used for commercial purposes without
// the written consent from the author. Otherwise it can be used freely
// except that you have to write somewhere in the code this header.
//
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.ComplexSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBigNumber;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public final class GaussianInteger {

  private final static GaussianInteger ZERO = new GaussianInteger(F.C0, F.C0);
  private final static GaussianInteger ONE = new GaussianInteger(F.C1, F.C0);
  private final static GaussianInteger IMAG_UNIT = new GaussianInteger(F.C0, F.C1);
  private final static GaussianInteger NEGATIVE_ONE = new GaussianInteger(F.CN1, F.C0);
  private final static GaussianInteger NEGATIVE_IMAG_UNIT = new GaussianInteger(F.C0, F.CN1);

  /**
   * Powers of {@link S#I}
   * 
   * <p>
   * Ported from Sympy.
   */
  private final static GaussianInteger[] UNITS =
      new GaussianInteger[] {ONE, IMAG_UNIT, NEGATIVE_ONE, NEGATIVE_IMAG_UNIT};// powers of i

  private static class GaussianFactors {
    /**
     * Primes of the prime factorization of the norm
     */
    private BigInteger primes[];
    /**
     * Exponents of the prime factorization of the norm
     */
    private int exponents[];

    private BigInteger valA, valB;

    GaussianFactors() {}

    private void divideGaussian(BigInteger real, BigInteger imag,
        SortedMap<ComplexSym, Integer> complexMap) {
      real = real.abs();
      BigInteger temp;
      BigInteger norm = real.multiply(real).add(imag.multiply(imag));
      BigInteger realNum = valA.multiply(real).add(valB.multiply(imag));
      BigInteger imagNum = valB.multiply(real).subtract(valA.multiply(imag));
      if (realNum.mod(norm).signum() == 0 && imagNum.mod(norm).signum() == 0) {
        valA = realNum.divide(norm);
        valB = imagNum.divide(norm);
        if (real.signum() < 0) {
          real = real.negate();
          if (imag.signum() > 0) {
            temp = imag;
            imag = real;
            real = temp;
          } else {
            imag = imag.negate();
          }
        } else if (imag.signum() < 0) {
          imag = imag.negate();
          temp = imag;
          imag = real;
          real = temp;
        }
        ComplexSym c = ComplexSym.valueOf(F.ZZ(real), F.ZZ(imag));
        Integer value = complexMap.get(c);
        if (value == null) {
          complexMap.put(c, 1);
        } else {
          complexMap.put(c, value + 1);
        }
      }
    }

    private SortedMap<ComplexSym, Integer> factorize(BigInteger re, BigInteger im) {
      SortedMap<ComplexSym, Integer> resultMap = new TreeMap<ComplexSym, Integer>();
      valA = re;
      valB = im;
      BigInteger norm = valA.multiply(valA).add(valB.multiply(valB));
      if (norm.signum() == 0) {
        // Any gaussian prime divides this number
        return resultMap;
      }
      if (norm.compareTo(BigInteger.ONE) > 0) {
        SortedMap<BigInteger, Integer> bigMap = Config.PRIME_FACTORS.factorInteger(norm);
        primes = new BigInteger[bigMap.size()];
        exponents = new int[bigMap.size()];
        int i = 0;
        for (Map.Entry<BigInteger, Integer> entry : bigMap.entrySet()) {
          primes[i] = entry.getKey();
          exponents[i++] = entry.getValue();
        }
        final BigInteger TWO = BigInteger.valueOf(2L);
        for (int index = 0; index < primes.length; index++) {
          BigInteger p = primes[index];
          final int exp = exponents[index];
          if (p.equals(TWO)) {
            for (int index2 = 0; index2 < exp; index2++) {
              divideGaussian(BigInteger.ONE, BigInteger.ONE, resultMap); /* Divide by 1+i */
              divideGaussian(BigInteger.ONE, BigInteger.ONE.negate(),
                  resultMap); /* Divide by 1-i */
            }
          } else if (!p.testBit(1)) {
            /* if p = 1 (mod 4) */
            BigInteger q = p.subtract(BigInteger.ONE); /* q = p-1 */
            BigInteger k = BigInteger.ONE;
            BigInteger mult1;
            do { // Compute Mult1 = sqrt(-1) mod p
              k = k.add(BigInteger.ONE);
              mult1 = k.modPow(q.shiftRight(2), p);
            } while (mult1.equals(BigInteger.ONE) || mult1.equals(q));
            BigInteger mult2 = BigInteger.ONE;
            while (true) {
              k = mult1.multiply(mult1).add(mult2.multiply(mult2)).divide(p);
              if (k.equals(BigInteger.ONE)) {
                break;
              }
              BigInteger m1 = mult1.mod(k);
              BigInteger m2 = mult2.mod(k);
              if (m1.compareTo(k.shiftRight(1)) > 0) {
                m1 = m1.subtract(k);
              }
              if (m2.compareTo(k.shiftRight(1)) > 0) {
                m2 = m2.subtract(k);
              }
              BigInteger temp = mult1.multiply(m1).add(mult2.multiply(m2)).divide(k);
              mult2 = mult1.multiply(m2).subtract(mult2.multiply(m1)).divide(k);
              mult1 = temp;
            } /* end while */
            if (mult1.abs().compareTo(mult2.abs()) < 0) {
              BigInteger temp = mult1;
              mult1 = mult2;
              mult2 = temp;
            }
            for (int index2 = 0; index2 < exp; index2++) {
              divideGaussian(mult1, mult2, resultMap);
              divideGaussian(mult1, mult2.negate(), resultMap);
            }
            /* end p = 1 (mod 4) */
          } else {
            /* if p = 3 (mod 4) */
            for (int index2 = 0; index2 < exp; index2++) {
              divideGaussian(primes[index], BigInteger.ZERO, resultMap);
            } /* end p = 3 (mod 4) */
          }
        }
      }
      return resultMap;
    }
  }

  /**
   * Return quadrant index 0-3. 0 is included in quadrant 0.
   * 
   * <p>
   * Ported from Sympy.
   * 
   * @return
   */
  public int quadrant() {
    if (y.isPositive()) {
      return x.isPositive() ? 0 : 1;
    } else if (y.isNegative()) {
      return x.isNegative() ? 2 : 3;
    } else {
      return x.isNegative() ? 2 : 0;
    }
  }

  /**
   * Helper method for &quot;normalization&quot; of gaussian integers.
   * 
   * <p>
   * Ported from Sympy.
   * 
   * @return
   */
  private GaussianInteger canonicalUnit() {
    int quadrant = quadrant();
    if (quadrant > 0) {
      quadrant = 4 - quadrant; // - for inverse power
    }
    return UNITS[quadrant];
  }

  /**
   * &quot;normalization&quot; of gaussian integers.
   * 
   * <p>
   * Ported from Sympy.
   * 
   * @return
   */
  public GaussianInteger normalize() {
    GaussianInteger unit = canonicalUnit();
    GaussianInteger d = this.multiply(unit);
    // args = tuple(a*unit for a in args)
    // return (d,) + args if args else d
    return d;
  }

  /**
   * Return quadrant index <code>0-3</code>. <code>0</code> is included in quadrant <code>0</code>.
   * 
   * <p>
   * Ported from Sympy.
   * 
   * @param gaussianInt
   * @return
   */
  private static int quadrant(IInteger[] gaussianInt) {
    if (gaussianInt[1].isPositive()) {
      return gaussianInt[0].isPositive() ? 0 : 1;
    } else if (gaussianInt[1].isNegative()) {
      return gaussianInt[0].isNegative() ? 2 : 3;
    }
    return gaussianInt[0].isNegative() ? 2 : 0;
  }

  /**
   * Helper method for &quot;normalization&quot; of gaussian integers.
   * 
   * <p>
   * Ported from Sympy.
   * 
   * @param that
   * @return
   */
  private static IInteger[] canonicalUnit(IInteger[] that) {
    int quadrant = quadrant(that);
    if (quadrant > 0) {
      quadrant = 4 - quadrant; // `-` for inverse power
    }
    return UNITS[quadrant].getXY();
  }

  /**
   * &quot;normalization&quot; of gaussian integers.
   * 
   * <p>
   * Ported from Sympy.
   * 
   * @param gaussianInt
   * @return
   */
  private static IInteger[] normalize(IInteger[] gaussianInt) {
    IInteger[] unit = canonicalUnit(gaussianInt);
    IInteger[] d = multiply(gaussianInt, unit);
    // args = tuple(a*unit for a in args)
    // return (d,) + args if args else d
    return d;
  }

  private static IInteger[] add(final IInteger[] c1, final IInteger[] c2) {
    return new IInteger[] {c1[0].add(c2[0]), c1[1].add(c2[1])};
  }

  /**
   * Factor a gaussian integer number.
   * 
   * @param gaussianInteger gaussian integer represented by an {@link IBigNumber} instance (i.e.
   *        instance of {@link IComplex} or {@link IInteger})
   * @param realPart the real part of the gaussian integer
   * @param imaginaryPart the imaginary part of the gaussian integer
   * @return
   */
  public static IAST factorize(IBigNumber gaussianInteger, BigInteger realPart,
      BigInteger imaginaryPart) {
    GaussianFactors g = new GaussianFactors();
    SortedMap<ComplexSym, Integer> complexMap = g.factorize(realPart, imaginaryPart);
    IASTAppendable list = F.ListAlloc(complexMap.size() + 1);

    IASTAppendable ast = F.TimesAlloc(complexMap.size());
    for (Map.Entry<ComplexSym, Integer> e : complexMap.entrySet()) {
      if (e.getValue() == 1) {
        ast.append(e.getKey());
      } else {
        ast.append(F.Power(e.getKey(), F.ZZ(e.getValue())));
      }
    }

    IExpr factor = F.eval(F.Divide(gaussianInteger, ast));
    if (!factor.isOne()) {
      list.append(F.list(factor, F.C1));
    }
    for (Map.Entry<ComplexSym, Integer> e : complexMap.entrySet()) {
      list.append(F.list(e.getKey(), F.ZZ(e.getValue())));
    }
    return list;
  }

  /**
   * Greatest common divisor of the gaussian {@link IInteger} numbers <code>g1, g2</code>.
   *
   * @param g1
   * @param g2
   * @return
   */
  private static IInteger[] gcd(final IInteger[] g1, final IInteger[] g2) {
    final IInteger fReal = g1[0];
    final IInteger fImaginary = g1[1];
    if ((fReal.isZero() && fImaginary.isZero()) || (g2[0].isZero() && g2[1].isZero())) {
      return new IInteger[] {F.C0, F.C0};
    }

    IInteger[] integerAndRemainder;

    IInteger dividendRe = fReal;
    IInteger dividendIm = fImaginary;
    IInteger dividersRe = g2[0];
    IInteger dividersIm = g2[1];
    while (!dividersRe.isZero() || !dividersIm.isZero()) {
      integerAndRemainder = //
          GaussianInteger.quotientRemainder( //
              new IInteger[] {dividendRe, dividendIm}, //
              new IInteger[] {dividersRe, dividersIm});

      dividendRe = dividersRe;
      dividendIm = dividersIm;

      dividersRe = integerAndRemainder[2];
      dividersIm = integerAndRemainder[3];
    }

    if ((dividendRe.isMinusOne() && dividendIm.isZero())
        || (dividendRe.isZero() && dividendIm.isOne())
        || (dividendRe.isZero() && dividendIm.isMinusOne())) {
      return new IInteger[] {F.C1, F.C0};
    }

    dividendRe = dividendRe.isNegative() ? dividendRe.negate() : dividendRe;
    dividendIm = dividendIm.isNegative() ? dividendIm.negate() : dividendIm;
    return new IInteger[] {dividendRe, dividendIm};
  }

  /**
   * Test if the gaussian integer is square free.
   * 
   * @param gaussianInteger gaussian integer represented by an {@link IBigNumber} instance (i.e.
   *        instance of {@link IComplex} or {@link IInteger})
   * @param realPart the real part of the gaussian integer
   * @param imaginaryPart the imaginary part of the gaussian integer
   * 
   */
  public static boolean isSquareFree(IBigNumber gaussianInteger, IInteger realPart,
      IInteger imaginaryPart) {
    IAST factors =
        factorize(gaussianInteger, realPart.toBigNumerator(), imaginaryPart.toBigNumerator());
    if (!factors.isListOfLists()) {
      return false;
    }
    for (int i = 1; i < factors.size(); i++) {
      IAST subList = factors.getAST(i);
      if (!subList.isList2()) {
        return false;
      }
      if (subList.second().isInteger() && ((IInteger) subList.second()).isGE(F.C2)) {
        return false;
      }
    }
    return true;
  }

  private static IInteger[] lcm(IInteger[] g1, IInteger[] g2) {
    IInteger[] gcd = gcd(g1, g2);
    IInteger[] quotientRemainder = quotientRemainder(g1, gcd);
    IInteger[] product = multiply(quotientRemainder, g2);
    return normalize(product);
  }

  private static IInteger[] multiply(final IInteger[] c1, final IInteger[] c2) {
    final IInteger fReal = c1[0];
    final IInteger fImaginary = c1[1];
    final IInteger re = c2[0];
    final IInteger im = c2[1];
    IInteger newRe = fReal.multiply(re).subtract(fImaginary.multiply(im));
    IInteger newIm = fReal.multiply(im).add(re.multiply(fImaginary));
    return new IInteger[] {newRe, newIm};
  }

  /**
   * Return the quotient and remainder as an array <code>[quotient, remainder]</code> of the
   * division of the gaussian <code>IInteger</code> numbers <code>c1 / c2</code>.
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Gaussian_integer">Wikipedia - Gaussian integer</a>
   * <li><a href=
   * "http://fermatslasttheorem.blogspot.com/2005/06/division-algorithm-for-gaussian.html">Division
   * Algorithm for Gaussian Integers </a>
   * </ul>
   *
   * @param c1
   * @param c2
   * @return the quotient and remainder as a quadruple array
   *         <code>[quotient[0], quotient[1], remainder[0], remainder[1]]</code>
   */
  public static IInteger[] quotientRemainder(final IInteger[] c1, final IInteger[] c2) {
    final IInteger fReal = c1[0];
    final IInteger fImaginary = c1[1];
    final IInteger re = c2[0];
    final IInteger im = c2[1];

    if (re.isOne() && im.isZero()) {
      // c2 is one
      return new IInteger[] {fReal, fImaginary, F.C0, F.C0};
    }
    IInteger numeratorReal = fReal.multiply(re).add( //
        fImaginary.multiply(im));

    IInteger numeratorImaginary = fReal.multiply(im.negate()).add( //
        re.multiply(fImaginary));

    IInteger denominator = re.multiply(re).add( //
        im.multiply(im));

    if (denominator.isZero()) {
      throw new IllegalArgumentException("Denominator can not be zero.");
    }

    IInteger divisionReal = numeratorReal.divideBy(denominator).roundExpr();
    IInteger divisionImaginary = numeratorImaginary.divideBy(denominator).roundExpr();

    IInteger remainderReal =
        fReal.subtract(re.multiply(divisionReal)).add(im.multiply(divisionImaginary));
    IInteger remainderImaginary =
        fImaginary.subtract(re.multiply(divisionImaginary)).subtract(im.multiply(divisionReal));

    return new IInteger[] {divisionReal, divisionImaginary, remainderReal, remainderImaginary};
  }

  public static IInteger[] subtract(final IInteger[] c1, final IInteger[] c2) {
    return new IInteger[] {c1[0].subtract(c2[0]), c1[1].subtract(c2[1])};
  }

  private IInteger x;

  private IInteger y;

  public GaussianInteger(IInteger re, IInteger im) {
    this.x = re;
    this.y = im;
  }

  private GaussianInteger(IInteger[] g) {
    this.x = g[0];
    this.y = g[1];
  }

  public GaussianInteger add(final GaussianInteger that) {
    return new GaussianInteger(add(this.getXY(), that.getXY()));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (obj instanceof GaussianInteger) {
      GaussianInteger other = (GaussianInteger) obj;
      return x.equals(other.x) && y.equals(other.y);
    }
    return false;
  }

  /**
   * Greatest common divisor of the gaussian {@link IInteger} numbers.
   *
   * @param that
   */
  public GaussianInteger gcd(final GaussianInteger that) {
    return new GaussianInteger(gcd(this.getXY(), that.getXY()));
  }

  public IInteger[] getXY() {
    return new IInteger[] {x, y};
  }

  public IComplex getComplex() {
    return F.CC(x, y);
  }

  public IInteger re() {
    return x;
  }

  public IInteger im() {
    return y;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  /**
   * Least common multiple of the gaussian {@link IInteger} numbers.
   *
   * @param that
   */
  public GaussianInteger lcm(final GaussianInteger that) {
    return new GaussianInteger(lcm(this.getXY(), that.getXY()));
  }

  public GaussianInteger multiply(final GaussianInteger that) {
    return new GaussianInteger(multiply(this.getXY(), that.getXY()));
  }

  public GaussianInteger subtract(final GaussianInteger that) {
    return new GaussianInteger(subtract(this.getXY(), that.getXY()));
  }
}
