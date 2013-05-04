package org.matheclipse.core.convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.matheclipse.core.eval.exception.JASConversionException;
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

import com.google.common.base.Predicates;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
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
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;
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
		this.fTermOrder = new TermOrder(TermOrder.INVLEX);
		this.fPolyFactory = new GenPolynomialRing<C>(fRingFactory, fVariables.size(), fTermOrder, vars);
		this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ZERO, fVariables.size(),
				fTermOrder, vars);
	}

	public JASConvert(final List<? extends IExpr> variablesList, RingFactory<C> ringFactory) {
		this(variablesList, ringFactory, new TermOrder(TermOrder.INVLEX));
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
		this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ZERO, fVariables.size(),
				fTermOrder, vars);
	}

	public GenPolynomial<C> expr2JAS(final IExpr exprPoly) throws JASConversionException {
		try {
			return expr2Poly(exprPoly, false);
		} catch (Exception ae) {
			// ae.printStackTrace();
			throw new JASConversionException();
		}
	}

	/**
	 * Convert the given expression into a <a
	 * href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial.
	 * <code>INum</code> double values are internally converted to IFractions and
	 * converte into the pokynomial structure.
	 * 
	 * @param exprPoly
	 * @return
	 * @throws JASConversionException
	 */
	public GenPolynomial<C> numericExpr2JAS(final IExpr exprPoly) throws JASConversionException {
		try {
			return numericExpr2Poly(exprPoly);
		} catch (Exception ae) {
			ae.printStackTrace();
			throw new JASConversionException();
		}
	}

	/**
	 * Convert the given expression into a <a
	 * href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial. Only
	 * symbolic numbers are converted (i.e. no <code>INum</code> or
	 * <code>IComplexNum</code> values are converted into the polynomial
	 * structure)
	 * 
	 * @param exprPoly
	 * @return
	 * @throws JASConversionException
	 */
	public GenPolynomial<C> expr2IExprJAS(final IExpr exprPoly) throws JASConversionException {
		try {
			return expr2IExprPoly(exprPoly);
		} catch (Exception ae) {
			// ae.printStackTrace();
			throw new JASConversionException();
		}
	}

	/**
	 * Convert the given expression into a <a
	 * href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial.
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
	 * Convert the given expression into a <a
	 * href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial
	 * 
	 * @param exprPoly
	 * @param numeric2Rational
	 *          if <code>true</code>, <code>INum</code> double values are
	 *          converted to <code>BigRational</code> internally
	 * 
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	private GenPolynomial<C> expr2Poly(final IExpr exprPoly, boolean numeric2Rational) throws ArithmeticException, ClassCastException {
		if (exprPoly instanceof IAST) {
			final IAST ast = (IAST) exprPoly;
			GenPolynomial<C> result = fPolyFactory.getZERO();
			GenPolynomial<C> p = fPolyFactory.getZERO();
			if (ast.isPlus()) {
				IExpr expr = ast.get(1);
				result = expr2Poly(expr, numeric2Rational);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					p = expr2Poly(expr, numeric2Rational);
					result = result.sum(p);
				}
				return result;
			} else if (ast.isTimes()) {
				IExpr expr = ast.get(1);
				result = expr2Poly(expr, numeric2Rational);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					p = expr2Poly(expr, numeric2Rational);
					result = result.multiply(p);
				}
				return result;
			} else if (ast.isPower()) {
				final IExpr expr = ast.get(1);
				for (int i = 0; i < fVariables.size(); i++) {
					if (fVariables.get(i).equals(expr)) {
						int exponent = getExponent(ast);
						if (exponent < 0) {
							throw new ArithmeticException("JASConvert:expr2Poly - invalid exponent: " + ast.get(2).toString());
						}
						ExpVector e = ExpVector.create(fVariables.size(), i, exponent);
						return fPolyFactory.valueOf(e);
						// return fPolyFactory.getONE().multiply(e);
					}
				}
			}
		} else if (exprPoly instanceof ISymbol) {
			for (int i = 0; i < fVariables.size(); i++) {
				if (fVariables.get(i).equals(exprPoly)) {
					ExpVector e = ExpVector.create(fVariables.size(), i, 1L);
					return fPolyFactory.getONE().multiply(e);
				}
			}
			// class cast exception
		} else if (exprPoly instanceof IInteger) {
			return fPolyFactory.fromInteger((java.math.BigInteger) ((IInteger) exprPoly).asType(java.math.BigInteger.class));
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
		BigInteger n = ((IFraction) exprPoly).getBigNumerator();// .toJavaBigInteger();
		BigInteger d = ((IFraction) exprPoly).getBigDenominator();// .toJavaBigInteger();
		BigRational nr = new BigRational(n);
		BigRational dr = new BigRational(d);
		BigRational r = nr.divide(dr);
		if (fRingFactory instanceof ComplexRing) {
			ComplexRing ring = (ComplexRing)fRingFactory;
			Complex<BigRational> c = new Complex<BigRational>(ring, r);
			return new GenPolynomial(fPolyFactory, c);
		} else {
			return new GenPolynomial(fPolyFactory, r);
		}
	}

	private GenPolynomial<C> expr2IExprPoly(final IExpr exprPoly) throws ArithmeticException, ClassCastException {
		if (exprPoly instanceof IAST) {
			final IAST ast = (IAST) exprPoly;
			GenPolynomial<C> result = fPolyFactory.getZERO();
			GenPolynomial<C> p = fPolyFactory.getZERO();
			if (ast.isPlus()) {
				IExpr expr = ast.get(1);
				result = expr2IExprPoly(expr);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					p = expr2IExprPoly(expr);
					result = result.sum(p);
				}
				return result;
			} else if (ast.isTimes()) {
				IExpr expr = ast.get(1);
				result = expr2IExprPoly(expr);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					p = expr2IExprPoly(expr);
					result = result.multiply(p);
				}
				return result;
			} else if (ast.isPower()) {
				final IExpr expr = ast.get(1);
				for (int i = 0; i < fVariables.size(); i++) {
					if (fVariables.get(i).equals(expr)) {
						int exponent = getExponent(ast);
						if (exponent < 0) {
							throw new ArithmeticException("JASConvert:expr2Poly - invalid exponent: " + ast.get(2).toString());
						}
						ExpVector e = ExpVector.create(fVariables.size(), i, exponent);
						return fPolyFactory.getONE().multiply(e);
					}
				}
			}
		} else if (exprPoly instanceof ISymbol) {
			for (int i = 0; i < fVariables.size(); i++) {
				if (fVariables.get(i).equals(exprPoly)) {
					ExpVector e = ExpVector.create(fVariables.size(), i, 1L);
					return fPolyFactory.getONE().multiply(e);
				}
			}
			return new GenPolynomial(fPolyFactory, exprPoly);
		} else if (exprPoly instanceof IInteger) {
			return fPolyFactory.fromInteger((java.math.BigInteger) ((IInteger) exprPoly).asType(java.math.BigInteger.class));
		} else if (exprPoly instanceof IFraction) {
			return fraction2Poly((IFraction) exprPoly);
		}
		if (exprPoly.isFree(Predicates.in(fVariables), true)) {
			return new GenPolynomial(fPolyFactory, exprPoly);
		} else {
			for (int i = 0; i < fVariables.size(); i++) {
				if (fVariables.get(i).equals(exprPoly)) {
					ExpVector e = ExpVector.create(fVariables.size(), i, 1L);
					return fPolyFactory.getONE().multiply(e);
				}
			}
		}
		throw new ClassCastException(exprPoly.toString());
	}

	/**
	 * Get the positive exponent <code>int</code> value of the
	 * <code>Power[x,exp]</code> expression. Return a negative exponent value if
	 * the exponent isn't valid.
	 * 
	 * @param powerAST
	 * @return the positive exponent <code>int</code> value of the
	 *         <code>Power[x,exp]</code> expression. Return a negative exponent
	 *         value if the exponent isn't valid.
	 */
	public static int getExponent(final IAST powerAST) {
		int exponent = -1;
		try {
			// the following may throw ClassCastExcepion or ArithmeticException
			if (powerAST.get(2) instanceof INum) {
				exponent = ((INum) powerAST.get(2)).toInt();
				if (exponent < 0) {
					return -1;
				}
			} else if (powerAST.get(2) instanceof IInteger) {
				if (((IInteger) powerAST.get(2)).isNegative()) {
					return -1;
					// throw new
					// ArithmeticException("JASConvert:expr2Poly - negative exponent: " +
					// powerAST.get(2).toString());
				}
				exponent = ((IInteger) powerAST.get(2)).toInt();
			}
		} catch (ArithmeticException ae) {

		}
		return exponent;
	}

	/**
	 * BigInteger from BigRational coefficients. Represent as polynomial with
	 * BigInteger coefficients by multiplication with the gcd of the numerators
	 * and the lcm of the denominators of the BigRational coefficients.
	 * 
	 * @param A
	 *          polynomial with BigRational coefficients to be converted.
	 * @return Object[] with 3 entries: [0]->gcd [1]->lcm and [2]->polynomial with
	 *         BigInteger coefficients.
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
	 * BigInteger coefficients by multiplication with the lcm of the numerators of
	 * the BigRational coefficients.
	 * 
	 * @param A
	 *          polynomial with BigRational coefficients to be converted.
	 * @return polynomial with BigInteger coefficients.
	 */
	public GenPolynomial<edu.jas.arith.BigInteger> integerFromRationalCoefficients(GenPolynomial<BigRational> A) {
		return PolyUtil.integerFromRationalCoefficients(fBigIntegerPolyFactory, A);
	}

	public IAST integerPoly2Expr(final GenPolynomial<edu.jas.arith.BigInteger> poly) throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.Plus(F.C0);
		}
		IAST result = F.Plus();
		for (Monomial<edu.jas.arith.BigInteger> monomial : poly) {
			edu.jas.arith.BigInteger coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			IInteger coeffValue = F.integer(coeff.getVal());
			IAST monomTimes = F.Times(coeffValue);
			long lExp;
			for (int i = 0; i < exp.length(); i++) {
				lExp = exp.getVal(i);
				if (lExp != 0) {
					monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
				}
			}
			result.add(monomTimes);
		}
		return result;
	}

	/**
	 * Convert a jas <code>Integral</code> into a matheclipse expression
	 * 
	 * @param integral
	 *          the JAS Integral
	 * @return
	 */
	public IAST integral2Expr(Integral<BigRational> integral) {
		IAST sum = F.Plus();
		GenPolynomial<BigRational> pol = integral.pol;
		List<GenPolynomial<BigRational>> rational = integral.rational;
		List<LogIntegral<BigRational>> logarithm = integral.logarithm;

		if (!pol.isZERO()) {
			sum.add(poly2Expr(pol, null));
		}
		if (rational.size() != 0) {
			int i = 0;
			while (i < rational.size()) {
				sum.add(F.Times(poly2Expr(rational.get(i++), null), F.Power(poly2Expr(rational.get(i++), null), F.CN1)));
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
	 *          the JAS LogIntegral
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
				plus.add(F.Times(F.fraction(cp.numerator(), cp.denominator()), F.Log(poly2Expr(p, null))));
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

				AlgebraicNumber<BigRational> arGen = ar.getGenerator();
				// sb.append(" ## over " + ap.factory() + "\n");

				if (p.degree(0) < ar.modul.degree(0) && ar.modul.degree(0) > 2) {
					IAST rootOf = F.ast(F.RootOf);
					rootOf.add(poly2Expr(ar.modul, null));
					// rootOf.add(variable);
					// sb.append("sum_(" + ar.getGenerator() + " in ");
					// sb.append("rootOf(" + ar.modul + ") ) ");
					times.add(rootOf);

					throw new UnsupportedOperationException("JASConvert#logIntegral2Expr()");
				}

				times.add(poly2Expr(v, null));
				times.add(F.Log(polyAlgebraicNumber2Expr(p)));
				plus.add(times);
			}

		}
		return plus;
	}

	public IAST modIntegerPoly2Expr(final GenPolynomial<ModInteger> poly) throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.Plus(F.C0);
		}
		IAST result = F.Plus();
		for (Monomial<ModInteger> monomial : poly) {
			ModInteger coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			IInteger coeffValue = F.integer(coeff.getVal());
			IAST monomTimes = F.Times(coeffValue);
			long lExp;
			for (int i = 0; i < exp.length(); i++) {
				lExp = exp.getVal(i);
				if (lExp != 0) {
					monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
				}
			}
			result.add(monomTimes);
		}
		return result;
	}

	/**
	 * Converts a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial
	 * to a MathEclipse AST with head <code>Plus</code>
	 * 
	 * @param poly
	 *          a JAS polynomial
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	public IAST poly2Expr(final GenPolynomial<BigRational> poly) throws ArithmeticException, ClassCastException {
		return poly2Expr(poly, null);
	}

	/**
	 * Converts a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial
	 * to a MathEclipse AST with head <code>Plus</code>
	 * 
	 * @param poly
	 *          a JAS polynomial
	 * @param variable
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	public IAST poly2Expr(final GenPolynomial<BigRational> poly, IExpr variable) throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.Plus(F.C0);
		}

		boolean getVar = variable == null;
		IAST result = F.Plus();
		for (Monomial<BigRational> monomial : poly) {
			BigRational coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			IFraction coeffValue = F.fraction(coeff.numerator(), coeff.denominator());
			IAST monomTimes = F.Times(coeffValue);
			long lExp;
			for (int i = 0; i < exp.length(); i++) {
				lExp = exp.getVal(i);
				if (lExp != 0) {
					if (getVar) {
						variable = fVariables.get(i);
					}
					monomTimes.add(F.Power(variable, F.integer(lExp)));
				}
			}
			result.add(monomTimes);
		}
		return result;
	}

	/**
	 * Converts a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial
	 * to a MathEclipse AST with head <code>Plus</code>
	 * 
	 * @param poly
	 *          a JAS polynomial
	 * @param variable
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	public IAST exprPoly2Expr(final GenPolynomial<IExpr> poly, IExpr variable) throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.Plus(F.C0);
		}

		boolean getVar = variable == null;
		IAST result = F.Plus();
		for (Monomial<IExpr> monomial : poly) {
			IExpr coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			// IFraction coeffValue = F.fraction(coeff.numerator(),
			// coeff.denominator());
			IAST monomTimes = F.Times(coeff);
			long lExp;
			for (int i = 0; i < exp.length(); i++) {
				lExp = exp.getVal(i);
				if (lExp != 0) {
					if (getVar) {
						variable = fVariables.get(i);
					}
					monomTimes.add(F.Power(variable, F.integer(lExp)));
				}
			}
			result.add(monomTimes);
		}
		return result;
	}

	public IAST polyAlgebraicNumber2Expr(final GenPolynomial<AlgebraicNumber<BigRational>> poly) throws ArithmeticException,
			ClassCastException {
		if (poly.length() == 0) {
			return F.Plus(F.C0);
		}

		SortedMap<ExpVector, AlgebraicNumber<BigRational>> val = poly.getMap();
		if (val.size() == 0) {
			return F.Plus(F.C0);
		} else {
			IAST result = F.Plus();
			String symbolName = poly.factory().getVars()[0];
			IExpr sym = F.$s(symbolName);
			for (Map.Entry<ExpVector, AlgebraicNumber<BigRational>> m : val.entrySet()) {
				AlgebraicNumber<BigRational> c = m.getValue();

				IAST times = F.Times();
				ExpVector e = m.getKey();
				if (!c.isONE() || e.isZERO()) {
					times.add(algebraicNumber2Expr(c, sym));
				}
				if (e != null && sym != null) {
					long lExp = e.getVal(0);
					if (lExp != 0) {
						times.add(F.Power(sym, F.integer(lExp)));
					} else {
						times.add(F.Power(sym, F.integer(lExp)));
					}
				}
				if (times.size() > 1) {
					result.add(times);
				}

			}
			return result;
		}

	}

	public IAST algebraicNumber2Expr(final AlgebraicNumber<BigRational> coeff, IExpr variable) throws ArithmeticException,
			ClassCastException {
		GenPolynomial<BigRational> val = coeff.val;
		return poly2Expr(val, variable);
	}

	/**
	 * Convert a jas <code>Integral</code> into a matheclipse expression
	 * 
	 * @param integral
	 *          the JAS Integral
	 * @return
	 */
	public IAST quotIntegral2Expr(QuotIntegral<BigRational> integral) {
		IAST sum = F.Plus();
		List<Quotient<BigRational>> rational = integral.rational;
		List<LogIntegral<BigRational>> logarithm = integral.logarithm;

		// StringBuffer sb = new StringBuffer();
		// sb.append("integral( " + quot.toString() + " )" );
		// sb.append(" =\n");
		// boolean first = true;
		if (rational.size() != 0) {
			Quotient<BigRational> qTemp;
			GenPolynomial<BigRational> qNum;
			GenPolynomial<BigRational> qDen;

			for (int i = 0; i < rational.size(); i++) {
				qTemp = rational.get(i);
				qNum = qTemp.num;
				qDen = qTemp.den;
				sum.add(F.Times(poly2Expr(qNum, null), F.Power(poly2Expr(qDen, null), F.CN1)));
			}
		}
		if (logarithm.size() != 0) {
			for (LogIntegral<BigRational> pf : logarithm) {
				sum.add(logIntegral2Expr(pf));
			}
		}

		return sum;
	}

	public IAST rationalPoly2Expr(final GenPolynomial<BigRational> poly) throws ArithmeticException, ClassCastException {
		if (poly.length() == 0) {
			return F.Plus(F.C0);
		}
		IAST result = F.Plus();
		for (Monomial<BigRational> monomial : poly) {
			BigRational coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			IFraction coeffValue = F.fraction(coeff.numerator(), coeff.denominator());
			IAST monomTimes = F.Times(coeffValue);
			long lExp;
			for (int i = 0; i < exp.length(); i++) {
				lExp = exp.getVal(i);
				if (lExp != 0) {
					monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
				}
			}
			result.add(monomTimes);
		}
		return result;
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
		if (F.isZero(imd, epsilon)) {
			return F.num(red);
		}
		return F.complexNum(red, imd);
	}
}
