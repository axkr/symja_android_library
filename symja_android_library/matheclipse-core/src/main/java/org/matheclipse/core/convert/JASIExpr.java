package org.matheclipse.core.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.matheclipse.core.eval.exception.JASConversionException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.polynomials.ExpVectorLong;

import edu.jas.arith.BigRational;
import edu.jas.poly.Complex;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.poly.TermOrderByName;
import edu.jas.structure.RingFactory;

/**
 * Convert <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> objects from
 * and to MathEclipse <code>IExpr</code> objects
 * 
 * <b>Note:</b>: set the "no complex number" flag to <code>false</code> to allow
 * complex numbers on input in method <code>expr2IExprJAS(IExpr)</code>
 * 
 */
public class JASIExpr {
	private final RingFactory<IExpr> fRingFactory;
	private final TermOrder fTermOrder;
	private final GenPolynomialRing<IExpr> fPolyFactory;

	private final GenPolynomialRing<edu.jas.arith.BigInteger> fBigIntegerPolyFactory;

	/**
	 * The variables used in the polynomials.
	 */
	private final List<? extends IExpr> fVariables;

	/**
	 * "numeric function" flag to allow numeric functions on input in method
	 * <code>expr2IExprJAS(IExpr)</code>
	 * 
	 * @see {@link #expr2IExprJAS(IExpr)}
	 */
	private boolean fNumericFunction = false;

	public JASIExpr(IExpr variable, boolean numericFunction) {
		this(variable, new ExprRingFactory(), numericFunction);
	}

	public JASIExpr(IExpr variable, RingFactory<IExpr> ringFactory) {
		this(variable, ringFactory, false);
	}

	public JASIExpr(IExpr variable, RingFactory<IExpr> ringFactory, boolean numericFunction) {
		this.fNumericFunction = numericFunction;
		List<IExpr> varList = new ArrayList<IExpr>();
		varList.add(variable);
		this.fRingFactory = ringFactory;
		this.fVariables = varList;
		String[] vars = new String[fVariables.size()];
		for (int i = 0; i < fVariables.size(); i++) {
			vars[i] = fVariables.get(i).toString();
		}
		this.fTermOrder = TermOrderByName.Lexicographic;
		this.fPolyFactory = new GenPolynomialRing<IExpr>(fRingFactory, fVariables.size(), fTermOrder, vars);
		this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ZERO,
				fVariables.size(), fTermOrder, vars);
	}

	public JASIExpr(final List<? extends IExpr> variablesList) {
		this(variablesList, new ExprRingFactory(), TermOrderByName.Lexicographic, false);
	}

	public JASIExpr(final List<? extends IExpr> variablesList, boolean numericFunction) {
		this(variablesList, new ExprRingFactory(), TermOrderByName.Lexicographic, numericFunction);
	}

	public JASIExpr(final List<? extends IExpr> variablesList, RingFactory<IExpr> ringFactory) {
		this(variablesList, ringFactory, TermOrderByName.Lexicographic, false);
	}

	public JASIExpr(final List<? extends IExpr> variablesList, RingFactory<IExpr> ringFactory, TermOrder termOrder,
			boolean numericFunction) {
		this.fNumericFunction = numericFunction;
		this.fRingFactory = ringFactory;
		this.fVariables = variablesList;
		final int size = fVariables.size();
		String[] vars = new String[size];
		for (int i = 0; i < size; i++) {
			vars[i] = fVariables.get(i).toString();
		}
		this.fTermOrder = termOrder;
		this.fPolyFactory = new GenPolynomialRing<IExpr>(fRingFactory, fVariables.size(), fTermOrder, vars);
		this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(edu.jas.arith.BigInteger.ZERO,
				fVariables.size(), fTermOrder, vars);
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
			BigRational re = coeff.getRe();
			BigRational im = coeff.getIm();
			IAST monomTimes = F.Times(F.complex(F.fraction(re.numerator(), re.denominator()),
					F.fraction(im.numerator(), im.denominator())));
			long lExp;
			for (int i = 0; i < exp.length(); i++) {
				lExp = exp.getVal(i);
				if (lExp != 0) {
					monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
				}
			}
			if (monomTimes.size() == 2) {
				result.add(monomTimes.arg1());
			} else {
				result.add(monomTimes);
			}
		}
		if (result.size() == 2) {
			return result.arg1();
		} else {
			return result;
		}
	}

	/**
	 * Convert the given expression into a
	 * <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a> polynomial. Only
	 * symbolic numbers are converted (i.e. no <code>INum</code> or
	 * <code>IComplexNum</code> values are converted into the polynomial
	 * structure)
	 * 
	 * <b>Note:</b>: set the "no complex number" flag to <code>false</code> to
	 * allow complex numbers on input in method
	 * <code>expr2IExprJAS(IExpr)</code>
	 * 
	 * @param exprPoly
	 * @return
	 * @throws JASConversionException
	 * @see {@link #setNoComplexNumber(boolean)}
	 */
	public GenPolynomial<IExpr> expr2IExprJAS(final IExpr exprPoly) throws JASConversionException {
		try {
			return expr2IExprPoly(exprPoly);
		} catch (Exception ae) {
			// ae.printStackTrace();
			throw new JASConversionException();
		}
	}

	public GenPolynomial<IExpr> expr2IExprJAS(final org.matheclipse.core.polynomials.ExprPolynomial exprPoly) {
		GenPolynomial<IExpr> result = new GenPolynomial<IExpr>(fPolyFactory);// fPolyFactory.getZERO();
		SortedMap<org.matheclipse.core.polynomials.ExpVectorLong, IExpr> monoms = exprPoly.getMap();
		for (Entry<ExpVectorLong, IExpr> entry : monoms.entrySet()) {
			long[] arr = entry.getKey().getVal();
			result.doPutToMap(ExpVector.create(arr), monoms.get(entry.getKey()));
		}
		return result;
	}

	private GenPolynomial<IExpr> expr2IExprPoly(final IExpr exprPoly) throws ArithmeticException, ClassCastException {
		if (exprPoly instanceof IAST) {
			final IAST ast = (IAST) exprPoly;
			GenPolynomial<IExpr> result = fPolyFactory.getZERO();
			GenPolynomial<IExpr> p = fPolyFactory.getZERO();
			if (ast.isPlus()) {
				IExpr expr = ast.arg1();
				result = expr2IExprPoly(expr);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					p = expr2IExprPoly(expr);
					result = result.sum(p);
				}
				return result;
			} else if (ast.isTimes()) {
				IExpr expr = ast.arg1();
				result = expr2IExprPoly(expr);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					p = expr2IExprPoly(expr);
					result = result.multiply(p);
				}
				return result;
			} else if (ast.isPower()) {
				final IExpr expr = ast.arg1();
				if (expr instanceof ISymbol) {
					ExpVector leer = fPolyFactory.evzero;
					int ix = leer.indexVar(expr.toString(), fPolyFactory.getVars());
					if (ix >= 0) {
						int exponent = -1;
						try {
							exponent = Validate.checkPowerExponent(ast);
						} catch (WrongArgumentType e) {
							//
						}
						if (exponent < 0) {
							throw new ArithmeticException(
									"JASConvert:expr2Poly - invalid exponent: " + ast.arg2().toString());
						}
						ExpVector e = ExpVector.create(fVariables.size(), ix, exponent);
						return fPolyFactory.getONE().multiply(e);
					} 
				}
			} else if (fNumericFunction) {
				if (ast.isNumericFunction()) {
					return new GenPolynomial<IExpr>(fPolyFactory, ast);
				}
			}
		} else if (exprPoly instanceof ISymbol) {
			ExpVector leer = fPolyFactory.evzero;
			int ix = leer.indexVar(exprPoly.toString(), fPolyFactory.getVars());
			if (ix >= 0) {
				ExpVector e = ExpVector.create(fVariables.size(), ix, 1L);
				return fPolyFactory.getONE().multiply(e);
			}
			if (fNumericFunction) {
				if (exprPoly.isNumericFunction()) {
					return new GenPolynomial<IExpr>(fPolyFactory, exprPoly);
				}
				throw new ClassCastException(exprPoly.toString());
			} else {
				return new GenPolynomial<IExpr>(fPolyFactory, exprPoly);
			}
		} else if (exprPoly instanceof IInteger) {
			return new GenPolynomial<IExpr>(fPolyFactory, exprPoly);
		} else if (exprPoly instanceof IFraction) {
			return new GenPolynomial<IExpr>(fPolyFactory, exprPoly);
		}
		if (exprPoly.isFree(t -> fVariables.contains(t), true)) {
			return new GenPolynomial<IExpr>(fPolyFactory, exprPoly);
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
	 * Converts a <a href="http://krum.rz.uni-mannheim.de/jas/">JAS</a>
	 * polynomial to a MathEclipse AST with head <code>Plus</code>
	 * 
	 * @param poly
	 *            a JAS polynomial
	 * @return
	 * @throws ArithmeticException
	 * @throws ClassCastException
	 */
	public IExpr exprPoly2Expr(final GenPolynomial<IExpr> poly) {
		return exprPoly2Expr(poly, null);
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
	public IExpr exprPoly2Expr(final GenPolynomial<IExpr> poly, IExpr variable) {
		if (poly.length() == 0) {
			return F.C0;
		}

		IAST result = F.Plus();
		for (Monomial<IExpr> monomial : poly) {
			IExpr coeff = monomial.coefficient();
			ExpVector exp = monomial.exponent();
			IAST monomTimes = F.Times();
			monomialToExpr(coeff, exp, monomTimes); 
			result.add(monomTimes.getOneIdentity(F.C1));
		}
		return result.getOneIdentity(F.C0);
	}

	public boolean monomialToExpr(IExpr coeff, ExpVector exp, IAST monomTimes) {
		long lExp;
		ExpVector leer = fPolyFactory.evzero;
		if (!coeff.isOne()) {
			monomTimes.add(coeff);
		}
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
	public GenPolynomialRing<IExpr> getPolynomialRingFactory() {
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
			IInteger coeffValue = F.integer(coeff.getVal());
			IAST monomTimes = F.Times(coeffValue);
			long lExp;
			for (int i = 0; i < exp.length(); i++) {
				lExp = exp.getVal(i);
				if (lExp != 0) {
					monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
				}
			}
			if (monomTimes.size() == 2) {
				result.add(monomTimes.arg1());
			} else {
				result.add(monomTimes);
			}
		}
		if (result.size() == 2) {
			return result.arg1();
		} else {
			return result;
		}
	}

}
