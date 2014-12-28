package org.matheclipse.core.polynomials;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Represent a multivariable polynomial.
 * 
 */
public class Polynomial {
	private static enum polyType {
		Undefined, Integer, Rational, Complex, Numeric, ComplexNumeric, Expr
	};

	private polyType fPolynomialType = polyType.Undefined;

	SortedMap<ExponentArray, Monomial> fMonomials;

	final IAST fVariables;
	IExpr fExpr = null;
	final int fLength;
	boolean fIsPolynomial = true;

	public Polynomial(final IExpr polynomialExpr, final IAST variables, Comparator<ExponentArray> comparator) {
		this(polynomialExpr, variables, comparator, true);
	}

	/**
	 * Create a Polynomial.
	 * 
	 * @param polynomialExpr
	 * @param variables
	 * @param comparator
	 * @param convertPolynomial
	 *            convert to polynomial form
	 */
	public Polynomial(final IExpr polynomialExpr, final IAST variables, Comparator<ExponentArray> comparator,
			boolean convertPolynomial) {
		this.fVariables = variables;
		this.fExpr = polynomialExpr;
		fLength = variables.size() - 1;
		if (comparator == null) {
			fMonomials = new TreeMap<ExponentArray, Monomial>();
		} else {
			fMonomials = new TreeMap<ExponentArray, Monomial>(comparator);
		}
		if (convertPolynomial) {
			fIsPolynomial = createPolynomial(polynomialExpr);
		}
	}

	public Polynomial(final IExpr polynomialExpr, final IExpr variable) {
		this(polynomialExpr, variable, true);
	}

	public Polynomial(final IExpr polynomialExpr, final IAST variables) {
		this(polynomialExpr, variables, true);
	}

	public Polynomial(final IExpr polynomialExpr, final VariablesSet variables) {
		this(polynomialExpr, variables.getVarList(), true);
	}

	public Polynomial(final IExpr polynomialExpr, final IExpr variable, boolean convertPolynomial) {
		this(polynomialExpr, F.List(variable), convertPolynomial);
	}

	public Polynomial(final IExpr polynomialExpr, final IAST variables, boolean convertPolynomial) {
		this.fVariables = variables;
		this.fExpr = polynomialExpr;
		fLength = fVariables.size() - 1;
		fMonomials = new TreeMap<ExponentArray, Monomial>();
		if (convertPolynomial) {
			fIsPolynomial = createPolynomial(polynomialExpr);
		}
	}

	/**
	 * Add a new Monomial to this polynomial.
	 * 
	 * @param m
	 */
	private void addMonomial(Monomial m) {
		ExponentArray a1 = m.getExponents();
		Monomial monom = fMonomials.get(m.getExponents());
		if (monom != null) {
			monom.setCoefficient(monom.getCoefficient().plus(m.getCoefficient()));
			return;
		}
		fMonomials.put(a1, m);
	}

	/**
	 * Add a new Monomial to this polynomial.
	 * 
	 * @param coefficient
	 * @param expArray
	 *            array of exponents
	 */
	private void addMonomial(IExpr coefficient, ExponentArray expArray) {
		Monomial monom = fMonomials.get(expArray);
		if (monom != null) {
			monom.setCoefficient(monom.getCoefficient().plus(coefficient));
			return;
		}
		fMonomials.put(expArray, new Monomial(coefficient, expArray));
	}

	/**
	 * Create a <code>Polynomial</code> from the given <code>polynomialExpr</code>.
	 * 
	 * @param polynomialExpr
	 *            the polynomial expresion
	 * @return
	 */
	public boolean createPolynomial(final IExpr polynomialExpr) {
		return createPolynomial(polynomialExpr, false, false);
	}

	/**
	 * Create a <code>Polynomial</code> from the given <code>polynomialExpr</code>.
	 * 
	 * @param polynomialExpr
	 *            the polynomial expresion
	 * @param isFree
	 *            the coefficients must be free of variables
	 * @param numericFunction
	 *            the coefficients must be numeric functions
	 * @return
	 */
	public boolean createPolynomial(final IExpr polynomialExpr, boolean isFree, boolean numericFunction) {
		if (polynomialExpr.isZero()) {
			// 0 polynomial
			return true;
		}
		int exp = isVariable(polynomialExpr);
		if (exp >= 0) {
			addMonomial(F.C1, new ExponentArray(fLength, exp));
			return true;
		}
		if (polynomialExpr.isAST()) {
			try {
				final IAST ast = (IAST) polynomialExpr;
				if (ast.isPlus()) {
					for (int i = 1; i < ast.size(); i++) {
						IExpr temp = ast.get(i);
						exp = isVariable(temp);
						if (exp >= 0) {
							addMonomial(F.C1, new ExponentArray(fLength, exp));
							continue;
						} else if (isCoefficient(temp, numericFunction)) {
							addMonomial(temp, new ExponentArray(fLength));
							continue;
						} else if (temp.isTimes()) {
							Monomial monom = createMonomial((IAST) temp, isFree, numericFunction);
							if (monom != null) {
								addMonomial(monom);
								continue;
							}
						} else if (temp.isPower()) {
							Monomial monom = createPowerExponent((IAST) temp, isFree, numericFunction);
							if (monom != null) {
								addMonomial(monom);
								continue;
							}
						}
						return false;
					}
					return true;
				} else if (ast.isTimes()) {
					Monomial monom = createMonomial(ast, isFree, numericFunction);
					if (monom != null) {
						addMonomial(monom);
						return true;
					}
					return false;
				} else if (ast.isPower()) {
					Monomial monom = createPowerExponent(ast, isFree, numericFunction);
					if (monom == null) {
						return false;
					}
					addMonomial(monom);
					return true;
				}
			} catch (WrongArgumentType e) {
				return false;
			}
		}
		if (isCoefficient(polynomialExpr, numericFunction)) {
			addMonomial(polynomialExpr, new ExponentArray(fLength));
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param timesAST
	 * @param isFree
	 *            all coefficients must be free of variables
	 * @param numericFunction
	 * @return
	 * @throws WrongArgumentType
	 */
	private Monomial createMonomial(final IAST timesAST, boolean isFree, boolean numericFunction) throws WrongArgumentType {
		ExponentArray exponents = new ExponentArray(fLength);
		Monomial result = new Monomial(F.C1, exponents);
		for (int i = 1; i < timesAST.size(); i++) {
			IExpr temp = timesAST.get(i);
			int exp = isVariable(temp);
			if (exp >= 0) {
				result.timesByMonomial(exp);
				continue;
			}
			if (isCoefficient(temp, numericFunction)) {
				result.timesByMonomial(temp);
				continue;
			}
			if (temp.isPower()) {
				Monomial monom = createPowerExponent((IAST) temp, isFree, numericFunction);
				if (monom != null) {
					result.timesByMonomial(monom);
					continue;
				}
				return null;
			}
			if (isFree) {
				return null;
			}
			result.timesByMonomial(temp);
		}
		return result;
	}

	private Monomial createPowerExponent(final IAST powerAST, boolean isFree, boolean numericFunction) throws WrongArgumentType {
		IExpr arg1 = powerAST.arg1();
		int position = isVariable(arg1);
		if (position >= 0) {
			long exponent = Validate.checkLongPowerExponent(powerAST);
			if (exponent < 0) {
				return new Monomial(powerAST, new ExponentArray(fLength));
			}
			return new Monomial(F.C1, new ExponentArray(fLength, position, exponent));
		}
		if (isFree) {
			if (isCoefficient(powerAST, numericFunction)) {
				return new Monomial(powerAST, new ExponentArray(fLength));
			}
			return null;
		}
		return new Monomial(powerAST, new ExponentArray(fLength));
	}

	/**
	 * Check if the expression is a polynomial.
	 * 
	 * @param polynomialExpr
	 * @return
	 */
	public boolean isPolynomial(final IExpr polynomialExpr) {
		if (isVariable(polynomialExpr) >= 0) {
			return true;
		}
		if (polynomialExpr.isAST()) {
			final IAST ast = (IAST) polynomialExpr;
			if (ast.isPlus()) {
				for (int i = 1; i < ast.size(); i++) {
					IExpr temp = ast.get(i);
					if (isVariable(temp) >= 0) {
						continue;
					} else if (isCoefficient(temp, false)) {
						continue;
					} else if (temp.isTimes()) {
						if (isMonomial((IAST) temp)) {
							continue;
						}
					} else if (temp.isPower() && isPowerExponent((IAST) temp)) {
						continue;
					}
					return false;
				}
				return true;
			} else if (ast.isTimes()) {
				if (isMonomial(ast)) {
					return true;
				}
			} else if (ast.isPower()) {
				return isPowerExponent(ast);
			}
		}
		return isCoefficient(polynomialExpr, false);
	}

	public boolean isPolynomial() {
		return fIsPolynomial;
	}

	private boolean isPowerExponent(final IAST powerAST) {
		IExpr expr = powerAST.get(1);
		if (isVariable(expr) >= 0) {
			long exponent = -1;
			try {
				exponent = Validate.checkLongPowerExponent(powerAST);
			} catch (WrongArgumentType e) {
				return false;
			}
			if (exponent < 0) {
				return false;
			}
			return true;
		}
		return isCoefficient(powerAST, false);
	}

	private boolean isMonomial(final IAST timesAST) {
		for (int i = 1; i < timesAST.size(); i++) {
			IExpr temp = timesAST.get(i);
			if (isVariable(temp) >= 0) {
				continue;
			}
			if (isCoefficient(temp, false)) {
				continue;
			}
			if (temp.isPower() && isPowerExponent((IAST) temp)) {
				continue;
			}
			return false;
		}
		return true;
	}

	public boolean isCoefficient(final IExpr polynomialExpr, boolean numericFunction) {
		if (polynomialExpr.isInteger()) {
			if (fPolynomialType.compareTo(polyType.Integer) < 0) {
				fPolynomialType = polyType.Integer;
			}
			return true;
		} else if (polynomialExpr.isFraction()) {
			if (fPolynomialType.compareTo(polyType.Rational) < 0) {
				fPolynomialType = polyType.Rational;
			}
			return true;
		} else if (polynomialExpr.isComplex()) {
			if (fPolynomialType.compareTo(polyType.Complex) < 0) {
				fPolynomialType = polyType.Complex;
			}
			return true;
		} else if (polynomialExpr.isNumeric()) {
			if (polynomialExpr instanceof IComplexNum) {
				if (fPolynomialType.compareTo(polyType.ComplexNumeric) < 0) {
					fPolynomialType = polyType.ComplexNumeric;
				}
			} else {
				if (fPolynomialType.compareTo(polyType.Numeric) < 0) {
					fPolynomialType = polyType.Numeric;
				}
			}
			return true;
		} else if (polynomialExpr.isComplexNumeric()) {
			if (fPolynomialType.compareTo(polyType.ComplexNumeric) < 0) {
				fPolynomialType = polyType.ComplexNumeric;
			}
			return true;
		}
		if (polynomialExpr.isFree(Predicates.in(fVariables), true)) {
			if (numericFunction) {
				if (!polynomialExpr.isNumericFunction()) {
					return false;
				}
			}
			// if (polynomialExpr.isIndeterminate() || polynomialExpr.equals(F.Null)) {
			// return false;
			// }
			if (fPolynomialType.compareTo(polyType.Expr) < 0) {
				fPolynomialType = polyType.Expr;
			}
			return true;
		}
		return false;
	}

	/**
	 * Get the position of the <code>expr</code> in the variables list.
	 * 
	 * @param expr
	 * @return <code>-1</code> if the expression isn't found in the variable list.
	 */
	private int isVariable(final IExpr expr) {
		for (int i = 1; i < fVariables.size(); i++) {
			if (fVariables.get(i).equals(expr)) {
				return i - 1;
			}
		}
		return -1;
	}

	/**
	 * The maximum degree for all variables.
	 * 
	 * @return the maximum degree for all variables
	 */
	public long maximumDegree() {
		if (fMonomials.size() == 0) {
			return 0;
		}
		long maximum = 0;
		for (ExponentArray monom : fMonomials.keySet()) {
			long monomialDegree = monom.maximumDegree();
			if (monomialDegree > maximum) {
				maximum = monomialDegree;
			}
		}
		return maximum;
	}

	/**
	 * Get the coefficient of a univariate polynomial for the given <code>exponent</code>
	 * 
	 * @return the coefficient of a univariate polynomial for the given <code>exponent</code>
	 */
	public IExpr coefficient(long exponent) {
		ExponentArray expArray = new ExponentArray(1, 0, exponent);
		Monomial monom = fMonomials.get(expArray);
		if (monom != null) {
			return monom.getCoefficient();
		}
		return F.C0;
	}

	/**
	 * Get the coefficient of a univariate polynomial for the given <code>exponent</code>
	 * 
	 * @return the coefficient of a univariate polynomial for the given <code>exponent</code>
	 */
	public IExpr coefficient(long[] exponents) {
		ExponentArray expArray = new ExponentArray(exponents);
		Monomial monom = fMonomials.get(expArray);
		if (monom != null) {
			return monom.getCoefficient();
		}
		return F.C0;
	}

	/**
	 * Get the coefficients of a univariate polynomial up to n degree
	 * 
	 * @return the coefficients of a univariate polynomial up to n degree
	 */
	public IAST coefficientList() {
		IAST result = F.List();
		long lastDegree = 0L;
		long exp;
		Monomial monom;
		for (ExponentArray expArray : fMonomials.keySet()) {
			exp = expArray.getExponent(0);
			while (lastDegree < exp) {
				result.add(F.C0);
				lastDegree++;
			}
			if (lastDegree == exp) {
				monom = fMonomials.get(expArray);
				result.add(monom.getCoefficient());
				lastDegree++;
			}
		}
		return result;
	}

	/**
	 * Derivative of a polynomial. This method assumes that the polynomial is univariate.
	 * 
	 * @return <code>null</code> if the polynomial isn't univariate
	 */
	public Polynomial derivative() {
		Polynomial result = new Polynomial(null, fVariables, null, false);
		Validate.checkSize(fVariables, 2);

		Monomial clone;
		for (Monomial monom : fMonomials.values()) {
			clone = monom.clone();
			long exp = clone.fExpArray.fExponents[0];
			if (exp != 0) {
				clone.fExpArray.fExponents[0] = exp - 1;
				clone.fCoefficient = clone.fCoefficient.times(F.integer(exp));
				result.fMonomials.put(clone.fExpArray, clone);
			}
		}
		return result;
	}

	/**
	 * Get the monomials of a polynomial
	 * 
	 * @return the monomials of a polynomial
	 */
	public IAST monomialList() {
		IAST result = F.List();
		Monomial monom;
		for (ExponentArray expArray : fMonomials.keySet()) {
			monom = fMonomials.get(expArray);
			IAST temp = F.Times();
			monom.appendToExpr(temp, fVariables);
			result.add(temp);
		}
		return result;
	}

	/**
	 * Test if this expression equals <code>0</code> in symbolic or numeric mode.
	 * 
	 */
	public boolean isZero() {
		return fMonomials.size() == 0;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		Monomial monom;
		boolean initialized = false;
		for (ExponentArray expArray : fMonomials.keySet()) {
			monom = fMonomials.get(expArray);
			if (initialized) {
				buf.append("+");
			}
			monom.appendToString(buf, fVariables);
			initialized = true;
		}
		return buf.toString();
	}

	/**
	 * Convert this polynomial to an expression.
	 * 
	 * @return
	 */
	public IExpr getExpr() {
		if (fExpr == null) {
			IAST result = F.Plus();
			for (Monomial monom : fMonomials.values()) {
				IExpr coeff = monom.getCoefficient();
				if (!coeff.isZero()) {
					IAST times = F.Times(coeff);
					long[] exponents = monom.fExpArray.fExponents;
					for (int i = 0; i < exponents.length; i++) {
						if (exponents[i] != 0L) {
							times.add(F.Power(fVariables.get(i + 1), exponents[i]));
						}
					}
					result.add(times);
				}
			}
			fExpr = result.getOneIdentity(F.C0);
		}
		return fExpr;
	}

	public SortedMap<ExponentArray, Monomial> getMonomials() {
		return fMonomials;
	}

}
