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
import java.util.SortedMap;
import java.util.TreeMap;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.ComplexSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBigNumber;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public final class GaussianInteger {

  private BigInteger Primes[];
  private int Exponents[];
  private BigInteger ValA, ValB;
  private int Ind;

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
    GaussianInteger g = new GaussianInteger();
    SortedMap<ComplexSym, Integer> complexMap = new TreeMap<ComplexSym, Integer>();
    g.gaussianFactorization2(realPart, imaginaryPart, complexMap);
    IASTAppendable list = F.ListAlloc(complexMap.size() + 1);
    IExpr factor = F.C1;
    IASTAppendable ast = F.TimesAlloc(complexMap.size());
    for (Map.Entry<ComplexSym, Integer> entry : complexMap.entrySet()) {
      ComplexSym key = entry.getKey();
      int i = entry.getValue();
      if (i == 1) {
        ast.append(key);
      } else {
        IInteger is = F.ZZ(i);
        ast.append(F.Power(key, is));
      }
    }
    factor = F.eval(F.Divide(gaussianInteger, ast));
    if (!factor.isOne()) {
      list.append(F.list(factor, F.C1));
    }
    for (Map.Entry<ComplexSym, Integer> entry : complexMap.entrySet()) {
      ComplexSym key = entry.getKey();
      IInteger is = F.ZZ(entry.getValue());
      list.append(F.list(key, is));
    }
    return list;
  }

  /**
   * Test if the gaussian integer is square free.
   * 
   * @param gaussianInteger gaussian integer represented by an {@link IBigNumber} instance (i.e.
   *        instance of {@link IComplex} or {@link IInteger})
   * @param realPart the real part of the gaussian integer
   * @param imaginaryPart the imaginary part of the gaussian integer
   * 
   * @return
   */
  public static boolean isSquareFree(IBigNumber gaussianInteger, IInteger realPart,
      IInteger imaginaryPart) {
    IAST factors =
        factorize(gaussianInteger, realPart.toBigNumerator(), imaginaryPart.toBigNumerator());
    if (factors.isListOfLists()) {
      for (int i = 1; i < factors.size(); i++) {
        IAST subList = factors.getAST(i);
        if (!subList.isList2()) {
          return false;
        }
        if (subList.second().isInteger()) {
          IInteger exponent = (IInteger) subList.second();
          if (exponent.isGE(F.C2)) {
            return false;
          }
        }
      }
      return true;
    }
    return false;
  }

  void gaussianFactorization2(BigInteger re, BigInteger im,
      SortedMap<ComplexSym, Integer> complexMap) {
    BigInteger BigInt2;
    BigInt2 = BigInteger.valueOf(2L);
    BigInteger K, Mult1, Mult2, p, q, M1, M2, Tmp;
    int index, index2;
    ValA = re;
    ValB = im;
    BigInteger norm = ValA.multiply(ValA).add(ValB.multiply(ValB));
    Ind = 0;
    if (norm.signum() == 0) {
      // Any gaussian prime divides this number
      return;
    }
    if (norm.compareTo(BigInteger.ONE) > 0) {
      SortedMap<BigInteger, Integer> bigMap = Config.PRIME_FACTORS.factorInteger(norm);
      Ind = bigMap.size();
      Primes = new BigInteger[Ind];
      Exponents = new int[Ind];
      int i = 0;
      for (Map.Entry<BigInteger, Integer> entry : bigMap.entrySet()) {
        Primes[i] = entry.getKey();
        Exponents[i++] = entry.getValue();
      }
      for (index = 0; index < Ind; index++) {
        p = Primes[index];
        if (p.equals(BigInt2)) {
          for (index2 = 0; index2 < Exponents[index]; index2++) {
            divideGaussian(BigInteger.ONE, BigInteger.ONE, complexMap); /* Divide by 1+i */
            divideGaussian(BigInteger.ONE, BigInteger.ONE.negate(), complexMap); /* Divide by 1-i */
          }
        } else if (p.testBit(1) == false) {
          /* if p = 1 (mod 4) */
          q = p.subtract(BigInteger.ONE); /* q = p-1 */
          K = BigInteger.ONE;
          do { // Compute Mult1 = sqrt(-1) mod p
            K = K.add(BigInteger.ONE);
            Mult1 = K.modPow(q.shiftRight(2), p);
          } while (Mult1.equals(BigInteger.ONE) || Mult1.equals(q));
          Mult2 = BigInteger.ONE;
          while (true) {
            K = Mult1.multiply(Mult1).add(Mult2.multiply(Mult2)).divide(p);
            if (K.equals(BigInteger.ONE)) {
              break;
            }
            M1 = Mult1.mod(K);
            M2 = Mult2.mod(K);
            if (M1.compareTo(K.shiftRight(1)) > 0) {
              M1 = M1.subtract(K);
            }
            if (M2.compareTo(K.shiftRight(1)) > 0) {
              M2 = M2.subtract(K);
            }
            Tmp = Mult1.multiply(M1).add(Mult2.multiply(M2)).divide(K);
            Mult2 = Mult1.multiply(M2).subtract(Mult2.multiply(M1)).divide(K);
            Mult1 = Tmp;
          } /* end while */
          if (Mult1.abs().compareTo(Mult2.abs()) < 0) {
            Tmp = Mult1;
            Mult1 = Mult2;
            Mult2 = Tmp;
          }
          for (index2 = 0; index2 < Exponents[index]; index2++) {
            divideGaussian(Mult1, Mult2, complexMap);
            divideGaussian(Mult1, Mult2.negate(), complexMap);
          }
          /* end p = 1 (mod 4) */
        } else {
          /* if p = 3 (mod 4) */
          for (index2 = 0; index2 < Exponents[index]; index2++) {
            divideGaussian(Primes[index], BigInteger.ZERO, complexMap);
          } /* end p = 3 (mod 4) */
        }
      }
    }
  }

  private void divideGaussian(BigInteger real, BigInteger imag,
      SortedMap<ComplexSym, Integer> complexMap) {
    real = real.abs();
    BigInteger temp;
    BigInteger norm = real.multiply(real).add(imag.multiply(imag));
    BigInteger realNum = ValA.multiply(real).add(ValB.multiply(imag));
    BigInteger imagNum = ValB.multiply(real).subtract(ValA.multiply(imag));
    if (realNum.mod(norm).signum() == 0 && imagNum.mod(norm).signum() == 0) {
      ValA = realNum.divide(norm);
      ValB = imagNum.divide(norm);
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
   * @return the quotient and remainder as an array <code>[quotient, remainder]</code>
   */
  public static IInteger[] quotientRemainder(final IInteger[] c1, final IInteger[] c2) {
    final IInteger fReal = c1[0];
    final IInteger fImaginary = c1[1];
    final IInteger re = c2[0];
    final IInteger im = c2[1];
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

  /**
   * Greatest common divisor of the gaussian {@link IInteger} numbers <code>g1, g2</code>.
   *
   * @param g1
   * @param g2
   * @return
   */
  public static IInteger[] gcd(final IInteger[] g1, final IInteger[] g2) {
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

    return new IInteger[] {dividendRe, dividendIm};
  }
}
