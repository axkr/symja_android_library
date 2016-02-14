package org.matheclipse.core.convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISignedNumber;
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
 * Convert <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> objects from
 * and to MathEclipse objects
 * 
 * 
 * @param <C>
 */
public class JASConvert<C extends RingElem<C>> {
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
		this.fTermOrder = TermOrderByName.Lexicographic;
		this.fPolyFactory = new GenPolynomialRing<C>(fRingFactory, fVariables.size(), fTermOrder, vars);
		this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ZERO,
				fVariables.size(), fTermOrder, vars);
	}

	public JASConvert(final List<? extends IExpr> variablesList, RingFactory<C> ringFactory) {
		this(variablesList, ringFactory, TermOrderByName.Lexicographic);
	}

	public JASConvert(final List<? extends IExpr> variablesList, RingFactory<C> ringFactory, TermOrder termOrder) {
		this.fRingFactory = ringFactory;
		this.fVariables = variablesList;
		String[] vars = new String[fVariables.size()];
		for (int i = 0; i < fVariables.size(); i++) {
			vars[i] = fVariables.get(i).toString();
		}
		this.fTermOrder = termOrder;
		this.fPolyFactory = new GenPolynomialRing<C>(fRingFactory, fVariables.size(), fTermOrder, vars);
		this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ZERO,
				fVariables.size(), fTermOrder, vars);
	}

	public GenPolynomial<C> expr2JAS(final IExpr exprPoly, boolean numeric2Rational) throws JASConversionException {
		try {
			return expr2Poly(exprPoly, numeric2Rational);
		} catch (Exception ae) {
			// ae.printStackTrace();
			throw new JASConversionException();
		}
	}

	/**
	 * Convert the given expression into a
	 * <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial.
	 * <code>INum</code> double values are internally converted to IFractions
	 * and converte into the pokynomial structure.
	 * 
	 * @param exprPoly
	 * @return
	 * @throws JASConversionException
	 */
	public GenPolynomial<C> numericExpr2JAS(final IExpr exprPoly) throws JASConversionException {
		try {
			return numericExpr2Poly(exprPoly);
		} catch (Exception ae) {
			// ae.printStackTrace();
			throw new JASConversionException();
		}
	}

	/**
	 * Convert the given expression into a
	 * <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial.
	 * <code>INum</code> values are internally converted to IFractions and
	 * <code>expr2Poly</code> was called for the expression
	 * 
	 * @param exprPoly
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	private GenPolynomial<C> numericExpr2Poly(final IExpr exprPoly) throws ArithmeticException, ClassCastException {
		return expr2Poly(exprPoly, true);
	}

	/**
	 * Convert the given expression into a
	 * <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial
	 * 
	 * @param exprPoly
	 * @param numeric2Rational
	 *            if <code>true</code>, <code>INum</code> double values are
	 *            converted to <code>BigRational</code> internally
	 * 
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	private GenPolynomial<C> expr2Poly(final IExpr exprPoly, boolean numeric2Rational)
			throws ArithmeticException, ClassCastException {
		if (exprPoly instanceof IAST) {
			final IAST ast = (IAST) exprPoly;
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
			} else if (ast.isPower() && ast.arg1().isSymbol()) {
				final ISymbol expr = (ISymbol) ast.arg1();
				int exponent = -1;
				try {
					exponent = Validate.checkPowerExponent(ast);
				} catch (WrongArgumentType e) {
				}
				if (exponent < 0) {
					throw new ArithmeticException("JASConvert:expr2Poly - invalid exponent: " + ast.arg2().toString());
				}
				try {
					return fPolyFactory.univariate(expr.getSymbolName(), exponent);
				} catch (IllegalArgumentException iae) {
					// fall through
				}
			}
		} else if (exprPoly instanceof ISymbol) {
			try {
				return fPolyFactory.univariate(((ISymbol) exprPoly).getSymbolName(), 1L);
			} catch (IllegalArgumentException iae) {
				// fall through
			}
		} else if (exprPoly instanceof IInteger) {
			return fPolyFactory
					.fromInteger((java.math.BigInteger) ((IInteger) exprPoly).asType(java.math.BigInteger.class));
		} else if (exprPoly instanceof IFraction) {
			return fraction2Poly((IFraction) exprPoly);
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
		throw new ClassCastException(exprPoly.toString());
	}

	private GenPolynomial<C> fraction2Poly(final IFraction exprPoly) {
		BigInteger n = exprPoly.getBigNumerator();// .toJavaBigInteger();
		BigInteger d = exprPoly.getBigDenominator();// .toJavaBigInteger();
		BigRational nr = new BigRational(n);
		BigRational dr = new BigRational(d);
		BigRational r = nr.divide(dr);
		if (fRingFactory instanceof ComplexRing) {
			ComplexRing ring = (ComplexRing) fRingFactory;
			Complex<BigRational> c = new Complex<BigRational>(ring, r);
			return new GenPolynomial(fPolyFactory, c);
		} else {
			return new GenPolynomial(fPolyFactory, r);
		}
	}

	/**
	 * BigInteger from BigRational coefficients. Represent as polynomial with
	 * BigInteger coefficients by multiplication with the gcd of the numerators
	 * and the lcm of the denominators of the BigRational coefficients.
	 * 
	 * @param A
	 *            polynomial with BigRational coefficients to be converted.
	 * @return Object[] with 3 entries: [0]->gcd [1]->lcm and [2]->polynomial
	 *         with BigInteger coefficients.
	 */
	public Object[] factorTerms(GenPolynomial<BigRational> A) {
		return PolyUtil.integerFromRationalCoefficientsFactor(fBigIntegerPolyFactory, A);
	}

	/**
	 * @return the fPolyFactory
	 */
	public GenPolynomialRing<C> getPolynomialRingFactory() {
		return fPolyFactory;
	}

	/**
	 * BigInteger from BigRational coefficients. Represent as polynomial with
	 * BigInteger coefficients by multiplication with the lcm of the numerators
	 * of the BigRational coefficients.
	 * 
	 * @param A
	 *            polynomial with BigRational coefficients to be converted.
	 * @return polynomial with BigInteger coefficients.
	 */
	public GenPolynomial<edu.jas.arith.BigInteger> integerFromRationalCoefficients(GenPolynomial<BigRational> A) {
		return PolyUtil.integerFromRationalCoefficients(fBigIntegerPolyFactory, A);
	}

	/**
	 * Convert a JAS integer polynomial to <code>IExpr</code>.
	 * 
	 * @param poly
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	public IExpr integerPoly2Expr(final GenPolynomial<edu.jas.arith.BigInteger> poly)
			throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.C0;
		}
		IAST result = F.Plus();
		for (Monomial<edu.jas.arith.BigInteger> monomial : poly) {
			edu.jas.arith.BigInteger coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			IAST monomTimes = F.Times();
			monomialToExpr(coeff, exp, monomTimes);
			result.add(monomTimes.getOneIdentity(F.C1));
		}
		return result.getOneIdentity(F.C0);
	}

	public boolean monomialToExpr(edu.jas.arith.BigInteger coeff, ExpVector exp, IAST monomTimes) {
		if (!coeff.isONE()) {
			IInteger coeffValue = F.integer(coeff.getVal());
			monomTimes.add(coeffValue);
		}
		return expVectorToExpr(exp, monomTimes);
	}

	private boolean expVectorToExpr(ExpVector exp, IAST monomTimes) {
		long lExp;
		ExpVector leer = fPolyFactory.evzero;
		for (int i = 0; i < exp.length(); i++) {
			lExp = exp.getVal(i);
			if (lExp != 0) {
				int ix = leer.varIndex(i);
				if (ix >= 0) {
					if (lExp == 1L) {
						monomTimes.add(fVariables.get(ix));
					} else {
						monomTimes.add(F.Power(fVariables.get(ix), F.integer(lExp)));
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Convert a JAS complex polynomial to <code>IExpr</code>.
	 * 
	 * @param poly
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	public IExpr complexPoly2Expr(final GenPolynomial<Complex<BigRational>> poly)
			throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.C0;
		}
		IAST result = F.Plus();
		for (Monomial<Complex<BigRational>> monomial : poly) {
			Complex<BigRational> coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			IAST monomTimes = F.Times();
			monomialToExpr(coeff, exp, monomTimes);
			result.add(monomTimes.getOneIdentity(F.C1));
		}
		return result.getOneIdentity(F.C0);
	}

	public boolean monomialToExpr(Complex<BigRational> coeff, ExpVector exp, IAST monomTimes) {
		BigRational re = coeff.getRe();
		BigRational im = coeff.getIm();
		monomTimes.add(
				F.complex(F.fraction(re.numerator(), re.denominator()), F.fraction(im.numerator(), im.denominator())));
		return expVectorToExpr(exp, monomTimes);
	}

	/**
	 * Convert a jas <code>Integral</code> into a matheclipse expression
	 * 
	 * @param integral
	 *            the JAS Integral
	 * @return
	 */
	public IAST integral2Expr(Integral<BigRational> integral) {
		IAST sum = F.Plus();
		GenPolynomial<BigRational> pol = integral.pol;
		List<GenPolynomial<BigRational>> rational = integral.rational;
		List<LogIntegral<BigRational>> logarithm = integral.logarithm;

		if (!pol.isZERO()) {
			sum.add(rationalPoly2Expr(pol));
		}
		if (rational.size() != 0) {
			int i = 0;
			while (i < rational.size()) {
				sum.add(F.Times(rationalPoly2Expr(rational.get(i++)),
						F.Power(rationalPoly2Expr(rational.get(i++)), F.CN1)));
			}
		}
		if (logarithm.size() != 0) {
			for (LogIntegral<BigRational> pf : logarithm) {
				sum.add(logIntegral2Expr(pf));
			}
		}
		return sum;
	}

	/**
	 * Convert a jas <code>LogIntegral</code> into a matheclipse expression
	 * 
	 * @param logIntegral
	 *            the JAS LogIntegral
	 * @return
	 */

	public IAST logIntegral2Expr(LogIntegral<BigRational> logIntegral) {
		IAST plus = F.Plus();

		List<BigRational> cfactors = logIntegral.cfactors;

		List<GenPolynomial<BigRational>> cdenom = logIntegral.cdenom;

		List<AlgebraicNumber<BigRational>> afactors = logIntegral.afactors;

		List<GenPolynomial<AlgebraicNumber<BigRational>>> adenom = logIntegral.adenom;

		if (cfactors.size() > 0) {
			for (int i = 0; i < cfactors.size(); i++) {
				BigRational cp = cfactors.get(i);
				GenPolynomial<BigRational> p = cdenom.get(i);
				plus.add(F.Times(F.fraction(cp.numerator(), cp.denominator()), F.Log(rationalPoly2Expr(p))));
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
				IAST times = F.Times();

				if (p.degree(0) < ar.modul.degree(0) && ar.modul.degree(0) > 2) {
					IAST rootOf = F.ast(F.RootOf);
					rootOf.add(rationalPoly2Expr(ar.modul));
					times.add(rootOf);

					throw new UnsupportedOperationException("JASConvert#logIntegral2Expr()");
				}

				times.add(rationalPoly2Expr(v));
				times.add(F.Log(polyAlgebraicNumber2Expr(p)));
				plus.add(times);
			}

		}
		return plus;
	}

	/**
	 * Converts a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
	 * polynomial to a MathEclipse AST with head <code>Plus</code>
	 * 
	 * @param poly
	 *            a JAS polynomial
	 * @param variable
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	public IAST rationalPoly2Expr(final GenPolynomial<BigRational> poly)
			throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.Plus(F.C0);
		}

		IAST result = F.Plus();
		for (Monomial<BigRational> monomial : poly) {
			BigRational coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			IAST monomTimes = F.Times();
			monomialToExpr(coeff, exp, monomTimes);
			result.add(monomTimes.getOneIdentity(F.C1));
		}
		return result;
	}

	public boolean monomialToExpr(BigRational coeff, ExpVector exp, IAST monomTimes) {
		if (!coeff.isONE()) {
			IFraction coeffValue = F.fraction(coeff.numerator(), coeff.denominator());
			monomTimes.add(coeffValue);
		}
		return expVectorToExpr(exp, monomTimes);
	}

	public IAST polyAlgebraicNumber2Expr(final GenPolynomial<AlgebraicNumber<BigRational>> poly)
			throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.Plus(F.C0);
		}

		SortedMap<ExpVector, AlgebraicNumber<BigRational>> val = poly.getMap();
		if (val.size() == 0) {
			return F.Plus(F.C0);
		} else {
			IAST result = F.Plus();
			for (Map.Entry<ExpVector, AlgebraicNumber<BigRational>> m : val.entrySet()) {
				AlgebraicNumber<BigRational> coeff = m.getValue();
				IAST monomTimes = F.Times();
				ExpVector exp = m.getKey();
				monomialToExpr(coeff, exp, monomTimes);
				result.add(monomTimes.getOneIdentity(F.C1)); 
			}
			return result;
		}

	}

	public boolean monomialToExpr(AlgebraicNumber<BigRational> coeff, ExpVector exp, IAST monomTimes) {
		if (!coeff.isONE()) {
			monomTimes.add(algebraicNumber2Expr(coeff));
		}
		return expVectorToExpr(exp, monomTimes); 
	}

	public IAST algebraicNumber2Expr(final AlgebraicNumber<BigRational> coeff) 
			throws ArithmeticException, ClassCastException {
		GenPolynomial<BigRational> val = coeff.val;
		return rationalPoly2Expr(val); // , variable);
	}

	/**
	 * Convert a jas <code>Integral</code> into a matheclipse expression
	 * 
	 * @param integral
	 *            the JAS Integral
	 * @return
	 */
	public IAST quotIntegral2Expr(QuotIntegral<BigRational> integral) {
		IAST sum = F.Plus();
		List<Quotient<BigRational>> rational = integral.rational;
		List<LogIntegral<BigRational>> logarithm = integral.logarithm;

		if (rational.size() != 0) {
			Quotient<BigRational> qTemp;
			GenPolynomial<BigRational> qNum;
			GenPolynomial<BigRational> qDen;

			for (int i = 0; i < rational.size(); i++) {
				qTemp = rational.get(i);
				qNum = qTemp.num;
				qDen = qTemp.den;
				sum.add(F.Times(rationalPoly2Expr(qNum), F.Power(rationalPoly2Expr(qDen), F.CN1)));
			}
		}
		if (logarithm.size() != 0) {
			for (LogIntegral<BigRational> pf : logarithm) {
				sum.add(logIntegral2Expr(pf));
			}
		}

		return sum;
	}

	public static ModIntegerRing option2ModIntegerRing(ISignedNumber option) {
		// TODO convert to long value
		long longValue = option.toLong();
		final BigInteger value = BigInteger.valueOf(longValue);
		return new ModIntegerRing(longValue, value.isProbablePrime(32));
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

	public static INumber jas2Numeric(org.apache.commons.math4.complex.Complex c, double epsilon) {
		double red = c.getReal();
		double imd = c.getImaginary();
		return F.chopNumber(F.complexNum(red, imd), epsilon);
	}

	/**
	 *  
	 * Conversion of BigRational to BigInteger. result =
	 * (num/gcd)*(lcm/denom).
	 */
	static class RatToRatFactor implements UnaryFunctor<BigRational, BigRational> {

		final java.math.BigInteger lcm;

		final java.math.BigInteger gcd;

		public RatToRatFactor(java.math.BigInteger gcd, java.math.BigInteger lcm) {
			this.gcd = gcd;
			this.lcm = lcm;  
		}

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

	/**
	 * BigRational from BigRational coefficients. Represent as polynomial with
	 * BigInteger coefficients by multiplication with the gcd of the numerators
	 * and the lcm of the denominators of the BigRational coefficients. <br />
	 * 
	 * @param fac
	 *            result polynomial factory.
	 * @param A
	 *            polynomial with BigRational coefficients to be converted.
	 * @return Object[] with 3 entries: [0]->gcd [1]->lcm and [2]->polynomial
	 *         with BigInteger coefficients.
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
		result[2] = PolyUtil.<BigRational, BigRational> map(fac, A, new RatToRatFactor(gcd, lcm));
		return result;
	}
}
