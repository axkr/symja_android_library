package org.matheclipse.core.convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModIntegerRing;
import edu.jas.integrate.Integral;
import edu.jas.integrate.LogIntegral;
import edu.jas.integrate.QuotIntegral;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.Complex;
import edu.jas.poly.ComplexRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
import edu.jas.structure.UnaryFunctor;
import edu.jas.ufd.Quotient;

/**
 * Convert <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> objects from and to Symja objects.
 *
 * @param <C>
 */
public class JASConvert<C extends RingElem<C>> {
  private static final Logger LOGGER = LogManager.getLogger();

  /**   Conversion of BigRational to BigInteger. result = (num/gcd)*(lcm/denom). */
  static class RatToRatFactor implements UnaryFunctor<BigRational, BigRational> {

    final java.math.BigInteger lcm;

    final java.math.BigInteger gcd;

    public RatToRatFactor(java.math.BigInteger gcd, java.math.BigInteger lcm) {
      this.gcd = gcd;
      this.lcm = lcm;
    }

    @Override
    public BigRational eval(BigRational c) {
      if (c == null) {
        return BigRational.ZERO;
      }
      if (gcd.equals(java.math.BigInteger.ONE)) {
        // p = num*(lcm/denom)
        java.math.BigInteger b = lcm.divide(c.denominator());
        return BigRational.valueOf(c.numerator().multiply(b));
      }
      // p = (num/gcd)*(lcm/denom)
      java.math.BigInteger a = c.numerator().divide(gcd);
      java.math.BigInteger b = lcm.divide(c.denominator());
      return BigRational.valueOf(a.multiply(b));
    }
  }

  public static IComplex jas2Complex(edu.jas.poly.Complex<BigRational> c) {
    IFraction re = F.fraction(c.getRe().numerator(), c.getRe().denominator());
    IFraction im = F.fraction(c.getIm().numerator(), c.getIm().denominator());
    return F.complex(re, im);
  }

  public static INumber jas2Numeric(edu.jas.poly.Complex<BigRational> c, double epsilon) {
    IFraction re = F.fraction(c.getRe().numerator(), c.getRe().denominator());
    double red = re.doubleValue();
    IFraction im = F.fraction(c.getIm().numerator(), c.getIm().denominator());
    double imd = im.doubleValue();
    return F.chopNumber(F.complexNum(red, imd), epsilon);
  }

  public static INumber jas2Numeric(org.hipparchus.complex.Complex c, double epsilon) {
    double red = c.getReal();
    double imd = c.getImaginary();
    return F.chopNumber(F.complexNum(red, imd), epsilon);
  }

  public static ModIntegerRing option2ModIntegerRing(IReal option) {
    // TODO convert to long value
    long longValue = option.toLong();
    final BigInteger value = BigInteger.valueOf(longValue);
    return new ModIntegerRing(longValue, value.isProbablePrime(32));
  }

  /**
   * BigRational from BigRational coefficients. Represent as polynomial with BigInteger coefficients
   * by multiplication with the gcd of the numerators and the lcm of the denominators of the
   * BigRational coefficients. <br>
   *
   * @param fac result polynomial factory.
   * @param A polynomial with BigRational coefficients to be converted.
   * @return Object[] with 3 entries: [0]->gcd [1]->lcm and [2]->polynomial with BigInteger
   *         coefficients.
   */
  public static Object[] rationalFromRationalCoefficientsFactor(GenPolynomialRing<BigRational> fac,
      GenPolynomial<BigRational> A) {
    Object[] result = new Object[3];
    if (A == null || A.isZERO()) {
      result[0] = java.math.BigInteger.ONE;
      result[1] = java.math.BigInteger.ZERO;
      result[2] = fac.getZERO();
      return result;
    }
    java.math.BigInteger gcd = null;
    java.math.BigInteger lcm = null;
    int sLCM = 0;
    int sGCD = 0;
    // lcm of denominators
    Iterator<BigRational> iter = A.coefficientIterator();
    while (iter.hasNext()) {
      BigRational y = iter.next();
      java.math.BigInteger numerator = y.numerator();
      java.math.BigInteger denominator = y.denominator();
      // lcm = lcm(lcm,x)
      if (lcm == null) {
        lcm = denominator;
        sLCM = denominator.signum();
      } else {
        java.math.BigInteger d = lcm.gcd(denominator);
        lcm = lcm.multiply(denominator.divide(d));
      }
      // gcd = gcd(gcd,x)
      if (gcd == null) {
        gcd = numerator;
        sGCD = numerator.signum();
      } else {
        gcd = gcd.gcd(numerator);
      }
    }
    if (sLCM < 0) {
      lcm = lcm.negate();
    }
    if (sGCD < 0) {
      gcd = gcd.negate();
    }
    result[0] = gcd;
    result[1] = lcm;
    result[2] = PolyUtil.<BigRational, BigRational>map(fac, A, new RatToRatFactor(gcd, lcm));
    return result;
  }

  private final RingFactory<C> fRingFactory;

  private final TermOrder fTermOrder;

  private final GenPolynomialRing<C> fPolyFactory;

  private final GenPolynomialRing<edu.jas.arith.BigInteger> fBigIntegerPolyFactory;

  private final List<? extends IExpr> fVariables;

  public JASConvert(IExpr variable, RingFactory<C> ringFactory) {
    List<IExpr> varList = new ArrayList<IExpr>();
    varList.add(variable);
    this.fRingFactory = ringFactory;
    this.fVariables = varList;
    String[] vars = new String[fVariables.size()];
    for (int i = 0; i < fVariables.size(); i++) {
      vars[i] = fVariables.get(i).toString();
    }
    this.fTermOrder = TermOrderByName.INVLEX;
    this.fPolyFactory = new GenPolynomialRing<C>(fRingFactory, fVariables.size(), fTermOrder, vars);
    this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(
        edu.jas.arith.BigInteger.ZERO, fVariables.size(), fTermOrder, vars);
  }

  public JASConvert(final List<? extends IExpr> variablesList, RingFactory<C> ringFactory) {
    this(variablesList, ringFactory, TermOrderByName.INVLEX);
  }

  public JASConvert(final List<? extends IExpr> variablesList, RingFactory<C> ringFactory,
      TermOrder termOrder) {
    this.fRingFactory = ringFactory;
    this.fVariables = variablesList;
    String[] vars = new String[fVariables.size()];
    for (int i = 0; i < fVariables.size(); i++) {
      vars[i] = fVariables.get(i).toString();
    }
    this.fTermOrder = termOrder;
    this.fPolyFactory = new GenPolynomialRing<C>(fRingFactory, fVariables.size(), fTermOrder, vars);
    this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(
        edu.jas.arith.BigInteger.ZERO, fVariables.size(), fTermOrder, vars);
  }

  public IAST algebraicNumber2Expr(final AlgebraicNumber<BigRational> coeff)
      throws ArithmeticException {
    GenPolynomial<BigRational> val = coeff.val;
    return rationalPoly2Expr(val, false); // , variable);
  }

  /**
   * Convert a complex number into a jas polynomial. If the conversion isn't possible this method
   * throws a <code>JASConversionException</code>.
   *
   * @param complexValue the complex value containing a rational real and imaginary part
   * @return a jas polynomial
   * @throws JASConversionException
   */
  private GenPolynomial<C> complex2Poly(final IComplex complexValue) throws JASConversionException {
    IRational reRational = complexValue.reRational();
    IRational imRational = complexValue.imRational();
    BigRational nre = new BigRational(reRational.toBigNumerator());
    BigRational dre = new BigRational(reRational.toBigDenominator());
    BigRational re = nre.divide(dre);
    BigRational nim = new BigRational(imRational.toBigNumerator());
    BigRational dim = new BigRational(imRational.toBigDenominator());
    BigRational im = nim.divide(dim);
    if (fRingFactory instanceof ComplexRing) {
      ComplexRing ring = (ComplexRing) fRingFactory;
      Complex<BigRational> c = new Complex<BigRational>(ring, re, im);
      return new GenPolynomial(fPolyFactory, c);
    } else {
      // "ComplexRing expected"
      throw new JASConversionException();
    }
  }

  public IExpr complexIntegerPoly2Expr(final GenPolynomial<Complex<edu.jas.arith.BigInteger>> poly)
      throws ArithmeticException, JASConversionException {
    if (poly.length() == 0) {
      return F.C0;
    }
    IASTAppendable result = F.PlusAlloc(poly.length());
    for (Monomial<Complex<edu.jas.arith.BigInteger>> monomial : poly) {
      Complex<edu.jas.arith.BigInteger> coeff = monomial.coefficient();
      ExpVector exp = monomial.exponent();
      IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
      monomialIntegerToExpr(coeff, exp, monomTimes);
      result.append(monomTimes.oneIdentity1());
    }
    return result.oneIdentity0();
  }

  /**
   * Convert a JAS complex-rational polynomial to <code>IExpr</code>.
   *
   * @param poly
   * @return
   * @throws ArithmeticException
   * @throws JASConversionException
   */
  public IExpr complexPoly2Expr(final GenPolynomial<Complex<BigRational>> poly)
      throws ArithmeticException, JASConversionException {
    if (poly.length() == 0) {
      return F.C0;
    }
    IASTAppendable result = F.PlusAlloc(poly.length());
    for (Monomial<Complex<BigRational>> monomial : poly) {
      Complex<BigRational> coeff = monomial.coefficient();
      ExpVector exp = monomial.exponent();
      IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
      monomialToExpr(coeff, exp, monomTimes);
      result.append(monomTimes.oneIdentity1());
    }
    return result.oneIdentity0();
  }

  public GenPolynomial<C> expr2JAS(final IExpr exprPoly, boolean numeric2Rational)
      throws JASConversionException {
    try {
      return expr2Poly(exprPoly, numeric2Rational);
    } catch (JASConversionException jce) {
      throw jce;
    } catch (RuntimeException rex) {
      LOGGER.debug("JASConvert.expr2JAS() failed", rex);
      throw new JASConversionException();
    }
  }

  /**
   * Convert the given expression into a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
   * polynomial
   *
   * @param exprPoly
   * @param numeric2Rational if <code>true</code>, <code>INum</code> double values are converted to
   *        <code>BigRational</code> internally
   * @return
   * @throws ArithmeticException
   * @throws JASConversionException
   */
  private GenPolynomial<C> expr2Poly(final IExpr exprPoly, boolean numeric2Rational)
      throws ArithmeticException, JASConversionException {
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      if (ast.isSlot()) {
        try {
          return fPolyFactory.univariate(ast.toString(), 1L);
        } catch (IllegalArgumentException iae) {
          // fall through
        }
      } else {
        GenPolynomial<C> result = fPolyFactory.getZERO();
        GenPolynomial<C> p = fPolyFactory.getZERO();
        if (ast.isPlus()) {
          IExpr expr = ast.arg1();
          result = expr2Poly(expr, numeric2Rational);
          for (int i = 2; i < ast.size(); i++) {
            expr = ast.get(i);
            p = expr2Poly(expr, numeric2Rational);
            result = result.sum(p);
          }
          return result;
        } else if (ast.isTimes()) {
          IExpr expr = ast.arg1();
          result = expr2Poly(expr, numeric2Rational);
          for (int i = 2; i < ast.size(); i++) {
            expr = ast.get(i);
            p = expr2Poly(expr, numeric2Rational);
            result = result.multiply(p);
          }
          return result;
        } else if (ast.isPower() && ast.base().isSymbol()) {
          final ISymbol base = (ISymbol) ast.base();
          int exponent = ast.exponent().toIntDefault();
          if (exponent < 0) {
            throw new JASConversionException();
            // "JASConvert:expr2Poly - invalid exponent: " + ast.arg2().toString());
          }
          try {
            return fPolyFactory.univariate(base.getSymbolName(), exponent);
          } catch (IllegalArgumentException iae) {
            // fall through
          }
        } else if (ast.isPower() && ast.arg1().isSlot()) {
          final IAST base = (IAST) ast.arg1();
          int exponent = ast.exponent().toIntDefault();
          if (exponent < 0) {
            throw new JASConversionException();
            // throw new ArithmeticException(
            // "JASConvert:expr2Poly - invalid exponent: " + ast.arg2().toString());
          }
          try {
            return fPolyFactory.univariate(base.toString(), exponent);
          } catch (IllegalArgumentException iae) {
            // fall through
          }
        }
      }
    } else if (exprPoly instanceof ISymbol) {
      try {
        if (exprPoly.isIndeterminate()) {
          throw new JASConversionException();
        }
        return fPolyFactory.univariate(((ISymbol) exprPoly).getSymbolName(), 1L);
      } catch (IllegalArgumentException iae) {
        // java.lang.IllegalArgumentException: variable 'XXX' not defined in polynomial ring
      }
    } else if (exprPoly instanceof IInteger) {
      return fPolyFactory.fromInteger(
          (java.math.BigInteger) ((IInteger) exprPoly).asType(java.math.BigInteger.class));
    } else if (exprPoly instanceof IFraction) {
      return fraction2Poly((IFraction) exprPoly);
    } else if (exprPoly instanceof IComplex) {
      // may throw JASConversionException
      return complex2Poly((IComplex) exprPoly);
    } else if (exprPoly instanceof INum && numeric2Rational) {
      IFraction frac = F.fraction(((INum) exprPoly).getRealPart());
      return fraction2Poly(frac);
    } else if (exprPoly instanceof IComplexNum && numeric2Rational) {
      if (F.isZero(((IComplexNum) exprPoly).getImaginaryPart())) {
        // the imaginary part is zero
        IFraction frac = F.fraction(((INum) exprPoly).getRealPart());
        return fraction2Poly(frac);
      }
    }
    throw new JASConversionException();
  }

  private boolean expVectorToExpr(ExpVector exp, IASTAppendable monomTimes) {
    long lExp;
    ExpVector leer = fPolyFactory.evzero;
    for (int i = 0; i < exp.length(); i++) {
      lExp = exp.getVal(i);
      if (lExp != 0) {
        int ix = leer.varIndex(i);
        if (ix >= 0) {
          if (lExp == 1L) {
            monomTimes.append(fVariables.get(ix));
          } else {
            monomTimes.append(F.Power(fVariables.get(ix), F.ZZ(lExp)));
          }
        } else {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Pull out overall numerical factor in poly (BigInteger from BigRational coefficients). Represent
   * as polynomial with BigInteger coefficients by multiplication with the gcd of the numerators and
   * the lcm of the denominators of the BigRational coefficients.
   *
   * @param poly polynomial with BigRational coefficients to be converted.
   * @return Object[] with 3 entries: [0]->gcd (java.math.BigInteger) [1]->lcm
   *         (java.math.BigInteger) and [2]->polynomial (GenPolynomial<edu.jas.arith.BigInteger>)
   *         with BigInteger coefficients.
   */
  public Object[] factorTerms(GenPolynomial<BigRational> poly) {
    return PolyUtil.integerFromRationalCoefficientsFactor(fBigIntegerPolyFactory, poly);
  }

  /**
   * Convert a fractional number into a jas polynomial.
   *
   * @param exprPoly
   * @return a jas polynomial
   */
  private GenPolynomial<C> fraction2Poly(final IFraction exprPoly) {
    BigInteger n = exprPoly.toBigNumerator(); // .toJavaBigInteger();
    BigInteger d = exprPoly.toBigDenominator(); // .toJavaBigInteger();
    BigRational nr = new BigRational(n);
    BigRational dr = new BigRational(d);
    BigRational r = nr.divide(dr);
    if (fRingFactory instanceof ComplexRing) {
      ComplexRing<BigRational> ring = (ComplexRing<BigRational>) fRingFactory;
      Complex<BigRational> c = new Complex<BigRational>(ring, r);
      return new GenPolynomial(fPolyFactory, c);
    } else {
      return new GenPolynomial(fPolyFactory, r);
    }
  }

  public RingFactory<C> getCoefficientRingFactory() {
    return fRingFactory;
  }

  /** @return the fPolyFactory */
  public GenPolynomialRing<C> getPolynomialRingFactory() {
    return fPolyFactory;
  }

  /**
   * BigInteger from BigRational coefficients. Represent as polynomial with BigInteger coefficients
   * by multiplication with the lcm of the numerators of the BigRational coefficients.
   *
   * @param A polynomial with BigRational coefficients to be converted.
   * @return polynomial with BigInteger coefficients.
   */
  public GenPolynomial<edu.jas.arith.BigInteger> integerFromRationalCoefficients(
      GenPolynomial<BigRational> A) {
    return PolyUtil.integerFromRationalCoefficients(fBigIntegerPolyFactory, A);
  }

  /**
   * Convert a JAS integer polynomial to <code>IExpr</code>.
   *
   * @param poly
   * @return
   * @throws ArithmeticException
   * @throws JASConversionException
   */
  public IExpr integerPoly2Expr(final GenPolynomial<edu.jas.arith.BigInteger> poly)
      throws ArithmeticException, JASConversionException {
    if (poly.length() == 0) {
      return F.C0;
    }
    IASTAppendable result = F.PlusAlloc(poly.length());
    for (Monomial<edu.jas.arith.BigInteger> monomial : poly) {
      edu.jas.arith.BigInteger coeff = monomial.coefficient();
      ExpVector exp = monomial.exponent();
      IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
      monomialToExpr(coeff, exp, monomTimes);
      result.append(monomTimes.oneIdentity1());
    }
    return result.oneIdentity0();
  }

  /**
   * Convert a jas <code>Integral</code> into a matheclipse expression
   *
   * @param integral the JAS Integral
   * @return
   */
  public IAST integral2Expr(Integral<BigRational> integral) {

    GenPolynomial<BigRational> pol = integral.pol;
    List<GenPolynomial<BigRational>> rational = integral.rational;
    List<LogIntegral<BigRational>> logarithm = integral.logarithm;

    IASTAppendable sum = F.PlusAlloc(rational.size() + logarithm.size());
    if (!pol.isZERO()) {
      sum.append(rationalPoly2Expr(pol, false));
    }
    if (rational.size() != 0) {
      int i = 0;
      while (i < rational.size()) {
        sum.append(F.Times(rationalPoly2Expr(rational.get(i++), false),
            F.Power(rationalPoly2Expr(rational.get(i++), false), F.CN1)));
      }
    }
    if (logarithm.size() != 0) {
      for (LogIntegral<BigRational> pf : logarithm) {
        sum.append(logIntegral2Expr(pf));
      }
    }
    return sum;
  }

  /**
   * Convert a jas <code>LogIntegral</code> into a matheclipse expression
   *
   * @param logIntegral the JAS LogIntegral
   * @return
   */
  public IAST logIntegral2Expr(LogIntegral<BigRational> logIntegral) {
    List<BigRational> cfactors = logIntegral.cfactors;

    List<GenPolynomial<BigRational>> cdenom = logIntegral.cdenom;

    List<AlgebraicNumber<BigRational>> afactors = logIntegral.afactors;

    List<GenPolynomial<AlgebraicNumber<BigRational>>> adenom = logIntegral.adenom;

    IASTAppendable plus = F.PlusAlloc(cfactors.size() + afactors.size());
    if (cfactors.size() > 0) {
      for (int i = 0; i < cfactors.size(); i++) {
        BigRational cp = cfactors.get(i);
        GenPolynomial<BigRational> p = cdenom.get(i);
        plus.append(F.Times(F.fraction(cp.numerator(), cp.denominator()),
            F.Log(rationalPoly2Expr(p, false))));
      }
    }

    // TODO implement this conversion for AlgebraicNumbers...
    if (afactors.size() > 0) {
      for (int i = 0; i < afactors.size(); i++) {

        AlgebraicNumber<BigRational> ap = afactors.get(i);
        AlgebraicNumberRing<BigRational> ar = ap.factory();
        GenPolynomial<AlgebraicNumber<BigRational>> p = adenom.get(i);
        if (p.degree(0) < ar.modul.degree(0) && ar.modul.degree(0) > 2) {
        }

        GenPolynomial<BigRational> v = ap.getVal();
        IASTAppendable times = F.TimesAlloc(2);

        if (p.degree(0) < ar.modul.degree(0) && ar.modul.degree(0) > 2) {
          IASTAppendable rootOf = F.ast(S.RootOf);
          rootOf.append(rationalPoly2Expr(ar.modul, false));
          times.append(rootOf);

          throw new UnsupportedOperationException("JASConvert#logIntegral2Expr()");
        }

        times.append(rationalPoly2Expr(v, false));
        times.append(F.Log(polyAlgebraicNumber2Expr(p)));
        plus.append(times);
      }
    }
    return plus;
  }

  public boolean monomialIntegerToExpr(Complex<edu.jas.arith.BigInteger> coeff, ExpVector exp,
      IASTAppendable monomTimes) {
    edu.jas.arith.BigInteger re = coeff.getRe();
    edu.jas.arith.BigInteger im = coeff.getIm();
    monomTimes.append(F.complex(F.integer(re.getVal()), F.integer(im.getVal())));
    return expVectorToExpr(exp, monomTimes);
  }

  public boolean monomialToExpr(AlgebraicNumber<BigRational> coeff, ExpVector exp,
      IASTAppendable monomTimes) {
    if (!coeff.isONE()) {
      monomTimes.append(algebraicNumber2Expr(coeff));
    }
    return expVectorToExpr(exp, monomTimes);
  }

  public boolean monomialToExpr(BigRational coeff, ExpVector exp, IASTAppendable monomTimes) {
    if (!coeff.isONE()) {
      IFraction coeffValue = F.fraction(coeff.numerator(), coeff.denominator());
      monomTimes.append(coeffValue);
    }
    return expVectorToExpr(exp, monomTimes);
  }

  public boolean monomialToExpr(Complex<BigRational> coeff, ExpVector exp,
      IASTAppendable monomTimes) {
    BigRational re = coeff.getRe();
    BigRational im = coeff.getIm();
    monomTimes.append(F.complex(F.fraction(re.numerator(), re.denominator()).normalize(),
        F.fraction(im.numerator(), im.denominator()).normalize()));
    return expVectorToExpr(exp, monomTimes);
  }

  public boolean monomialToExpr(edu.jas.arith.BigInteger coeff, ExpVector exp,
      IASTAppendable monomTimes) {
    if (!coeff.isONE()) {
      IInteger coeffValue = F.integer(coeff.getVal());
      monomTimes.append(coeffValue);
    }
    return expVectorToExpr(exp, monomTimes);
  }

  /**
   * Convert the given expression into a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
   * polynomial. <code>INum</code> double values are internally converted to IFractions and converte
   * into the pokynomial structure.
   *
   * @param exprPoly
   * @return
   * @throws JASConversionException
   */
  public GenPolynomial<C> numericExpr2JAS(final IExpr exprPoly) throws JASConversionException {
    try {
      return numericExpr2Poly(exprPoly);
    } catch (RuntimeException rex) {
      throw new JASConversionException();
    }
  }

  /**
   * Convert the given expression into a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
   * polynomial. <code>INum</code> values are internally converted to IFractions and <code>expr2Poly
   * </code> was called for the expression
   *
   * @param exprPoly
   * @return
   * @throws ArithmeticException
   * @throws JASConversionException
   */
  private GenPolynomial<C> numericExpr2Poly(final IExpr exprPoly)
      throws ArithmeticException, JASConversionException {
    return expr2Poly(exprPoly, true);
  }

  public IAST polyAlgebraicNumber2Expr(final GenPolynomial<AlgebraicNumber<BigRational>> poly)
      throws ArithmeticException, JASConversionException {
    if (poly.length() == 0) {
      return F.Plus(F.C0);
    }

    SortedMap<ExpVector, AlgebraicNumber<BigRational>> val = poly.getMap();
    if (val.size() == 0) {
      return F.Plus(F.C0);
    } else {
      IASTAppendable result = F.PlusAlloc(val.size());
      for (Map.Entry<ExpVector, AlgebraicNumber<BigRational>> m : val.entrySet()) {
        AlgebraicNumber<BigRational> coeff = m.getValue();
        ExpVector exp = m.getKey();
        IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
        monomialToExpr(coeff, exp, monomTimes);
        result.append(monomTimes.oneIdentity1());
      }
      return result;
    }
  }

  /**
   * Convert a jas <code>Integral</code> into a matheclipse expression
   *
   * @param integral the JAS Integral
   * @return
   */
  public IAST quotIntegral2Expr(QuotIntegral<BigRational> integral) {

    List<Quotient<BigRational>> rational = integral.rational;
    List<LogIntegral<BigRational>> logarithm = integral.logarithm;

    if (rational.size() != 0) {
      Quotient<BigRational> qTemp;
      GenPolynomial<BigRational> qNum;
      GenPolynomial<BigRational> qDen;
      IASTAppendable sum = F.PlusAlloc(rational.size());
      for (int i = 0; i < rational.size(); i++) {
        qTemp = rational.get(i);
        qNum = qTemp.num;
        qDen = qTemp.den;
        sum.append(F.Times(rationalPoly2Expr(qNum, false),
            F.Power(rationalPoly2Expr(qDen, false), F.CN1)));
      }
      return sum;
    }
    if (logarithm.size() != 0) {
      IASTAppendable sum = F.PlusAlloc(logarithm.size());
      for (LogIntegral<BigRational> pf : logarithm) {
        sum.append(logIntegral2Expr(pf));
      }
      return sum;
    }

    return F.Plus();
  }

  /**
   * Converts a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial to a Symja AST with
   * head <code>Plus</code>
   *
   * @param poly a JAS polynomial
   * @param factorTerms
   * @return
   * @throws ArithmeticException
   * @throws JASConversionException
   */
  public IAST rationalPoly2Expr(final GenPolynomial<BigRational> poly, boolean factorTerms)
      throws ArithmeticException, JASConversionException {
    if (poly.length() == 0) {
      return F.Plus(F.C0);
    }
    if (factorTerms) {
      // pull out overall numerical factor in poly.
      Object[] objects = factorTerms(poly);
      GenPolynomial<edu.jas.arith.BigInteger> p2 =
          (GenPolynomial<edu.jas.arith.BigInteger>) objects[2];
      java.math.BigInteger gcd = (java.math.BigInteger) objects[0];
      java.math.BigInteger lcm = (java.math.BigInteger) objects[1];
      IRational factor = F.fraction(gcd, lcm).normalize();
      IASTAppendable result = F.PlusAlloc(p2.length());
      for (Monomial<edu.jas.arith.BigInteger> monomial : p2) {
        edu.jas.arith.BigInteger coeff = monomial.coefficient();
        ExpVector exp = monomial.exponent();
        IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
        monomialToExpr(coeff, exp, monomTimes);
        result.append(monomTimes.oneIdentity1());
      }
      if (factor.isOne()) {
        return result;
      }
      return F.Times(F.fraction(gcd, lcm), result);
    }

    IASTAppendable result = F.PlusAlloc(poly.length());
    for (Monomial<BigRational> monomial : poly) {
      BigRational coeff = monomial.coefficient();
      ExpVector exp = monomial.exponent();
      IASTAppendable monomTimes = F.TimesAlloc(exp.length() + 1);
      monomialToExpr(coeff, exp, monomTimes);
      result.append(monomTimes.oneIdentity1());
    }
    return result;
  }

  /**
   * Check if the polynomial has maximum degree 2 in 1 variable and return the coefficients.
   *
   * @param poly
   * @return <code>false</code> if the polynomials degree > 2 and number of variables <> 1
   */
  private static boolean isQuadratic(GenPolynomial<BigRational> poly, BigRational[] result) {
    if (poly.degree() <= 2 && poly.numberOfVariables() == 1) {
      result[0] = BigRational.ZERO;
      result[1] = BigRational.ZERO;
      result[2] = BigRational.ZERO;
      for (Monomial<BigRational> monomial : poly) {
        BigRational coeff = monomial.coefficient();
        ExpVector exp = monomial.exponent();
        for (int i = 0; i < exp.length(); i++) {
          result[(int) exp.getVal(i)] = coeff;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Check if the polynomial has maximum degree 2 in 1 variable and return the coefficients.
   *
   * @param poly
   * @return <code>false</code> if the polynomials degree > 2 and number of variables <> 1
   */
  private static boolean isQuadratic(GenPolynomial<edu.jas.arith.BigInteger> poly,
      edu.jas.arith.BigInteger[] result) {
    if (poly.degree() <= 2 && poly.numberOfVariables() == 1) {
      result[0] = edu.jas.arith.BigInteger.ZERO;
      result[1] = edu.jas.arith.BigInteger.ZERO;
      result[2] = edu.jas.arith.BigInteger.ZERO;
      for (Monomial<edu.jas.arith.BigInteger> monomial : poly) {
        edu.jas.arith.BigInteger coeff = monomial.coefficient();
        ExpVector exp = monomial.exponent();
        for (int i = 0; i < exp.length(); i++) {
          result[(int) exp.getVal(i)] = coeff;
        }
      }
      return true;
    }
    return false;
  }
}
