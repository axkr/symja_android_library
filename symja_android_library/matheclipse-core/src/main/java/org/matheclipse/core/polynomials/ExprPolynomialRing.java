package org.matheclipse.core.polynomials;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.expression.ExprRingFactory;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.kern.PreemptStatus;
import edu.jas.kern.PrettyPrint;
import edu.jas.kern.Scripting;
import edu.jas.util.CartesianProduct;
import edu.jas.util.CartesianProductInfinite;
import edu.jas.util.LongIterable;

/**
 * GenPolynomialRing generic polynomial factory implementing ExprRingFactory;
 * Factory for n-variate ordered polynomials over C. Almost immutable object,
 * except variable names.
 * 
 * @param <C>
 *            coefficient type
 * @author Heinz Kredel
 */

public class ExprPolynomialRing {

	/**
	 * The factory for the coefficients.
	 */
	public final ExprRingFactory coFac;

	/**
	 * The number of variables.
	 */
	public final int nvar;

	/**
	 * The term order.
	 */
	public final ExprTermOrder tord;

	/**
	 * True for partially reversed variables.
	 */
	protected boolean partial;

	/**
	 * The names of the variables. This value can be modified.
	 */
	// protected String[] vars;
	protected IAST vars;

	/**
	 * The names of all known variables.
	 */
	private static Set<IExpr> knownVars = new HashSet<IExpr>();

	/**
	 * The constant polynomial 0 for this ring.
	 */
	public final ExprPolynomial ZERO;

	/**
	 * The constant polynomial 1 for this ring.
	 */
	public final ExprPolynomial ONE;

	/**
	 * The constant exponent vector 0 for this ring.
	 */
	public final ExpVectorLong evzero;

	/**
	 * A default random sequence generator.
	 */
	protected final static Random random = new Random();

	/**
	 * Indicator if this ring is a field.
	 */
	protected int isField = -1; // initially unknown

	/**
	 * Log4j logger object.
	 */
	private static final Logger logger = Logger.getLogger(ExprPolynomialRing.class);

	/**
	 * Count for number of polynomial creations.
	 */
	public static int creations = 0;

	/**
	 * Flag to enable if preemptive interrrupt is checked.
	 */
	final boolean checkPreempt = PreemptStatus.isAllowed();

	final boolean numericFunction;

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param cf
	 *            factory for coefficients of type C.
	 * @param v
	 *            names for the variables.
	 */
	public ExprPolynomialRing(ExprRingFactory cf, IAST v) {
		this(cf, v, v.size() - 1);
	}

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param listOfVariables
	 *            names for the variables.
	 */
	public ExprPolynomialRing(IAST listOfVariables) {
		this(ExprRingFactory.CONST, listOfVariables, listOfVariables.size() - 1, ExprTermOrderByName.Lexicographic);
	}

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param listOfVariables
	 *            names for the variables.
	 * @param t
	 *            a term order.
	 */
	public ExprPolynomialRing(IAST listOfVariables, ExprTermOrder t) {
		this(ExprRingFactory.CONST, listOfVariables, listOfVariables.size() - 1, t);
	}

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param symbol
	 *            name of a variable.
	 */
	public ExprPolynomialRing(ISymbol symbol) {
		this(ExprRingFactory.CONST, F.List(symbol), 1, ExprTermOrderByName.Lexicographic);
	}

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param symbol
	 *            name of a variable.
	 * @param t
	 *            a term order.
	 */
	public ExprPolynomialRing(ISymbol symbol, ExprTermOrder t) {
		this(ExprRingFactory.CONST, F.List(symbol), 1, t);
	}

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param cf
	 *            factory for coefficients of type C.
	 * @param listOfVariables
	 *            names for the variables.
	 * @param n
	 *            number of variables.
	 */
	public ExprPolynomialRing(ExprRingFactory cf, IAST listOfVariables, int n) {
		this(cf, listOfVariables, n, ExprTermOrderByName.Lexicographic);
	}

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param cf
	 *            factory for coefficients of type C.
	 * @param listOfVariables
	 *            names for the variables.
	 * @param t
	 *            a term order.
	 */
	public ExprPolynomialRing(ExprRingFactory cf, IAST listOfVariables, ExprTermOrder t) {
		this(cf, listOfVariables, listOfVariables.size() - 1, t);
	}

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param cf
	 *            factory for coefficients of type C.
	 * @param listOfVariables
	 *            names for the variables.
	 * @param n
	 *            number of variables.
	 * @param t
	 *            a term order.
	 */
	public ExprPolynomialRing(ExprRingFactory cf, IAST listOfVariables, int n, ExprTermOrder t) {
		this(cf, listOfVariables, n, t, false);
	}

	/**
	 * The constructor creates a polynomial factory object.
	 * 
	 * @param cf
	 *            factory for coefficients of type C.
	 * @param v
	 *            names for the variables.
	 * @param n
	 *            number of variables.
	 * @param t
	 *            a term order.
	 */
	public ExprPolynomialRing(ExprRingFactory cf, IAST listOfVariables, int n, ExprTermOrder t,
			boolean numericFunction) {
		coFac = cf;
		nvar = n;
		tord = t;
		partial = false;
		// if (v == null) {
		// vars = null;
		// } else {
		vars = listOfVariables.clone();// Arrays.copyOf(v, v.length); // >
										// Java-5
		// }
		ZERO = new ExprPolynomial(this);
		IExpr coeff = coFac.getONE();
		evzero = new ExpVectorLong(nvar);
		this.numericFunction = numericFunction;
		ONE = new ExprPolynomial(this, coeff, evzero);
		// if (vars == null) {
		// if (PrettyPrint.isTrue()) {
		// vars = newVars("x", nvar);
		// }
		// } else {
		if (vars.size() - 1 != nvar) {
			throw new IllegalArgumentException("incompatible variable size " + vars.size() + ", " + nvar);
		}
		addVars(vars);
		// }
	}

	/**
	 * The constructor creates a polynomial factory object with the the same
	 * term order, number of variables and variable names as the given
	 * polynomial factory, only the coefficient factories differ.
	 * 
	 * @param cf
	 *            factory for coefficients of type C.
	 * @param o
	 *            other polynomial ring.
	 */
	public ExprPolynomialRing(ExprRingFactory cf, ExprPolynomialRing o) {
		this(cf, o.vars, o.nvar, o.tord);
	}

	/**
	 * The constructor creates a polynomial factory object with the the same
	 * coefficient factory, number of variables and variable names as the given
	 * polynomial factory, only the term order differs.
	 * 
	 * @param to
	 *            term order.
	 * @param o
	 *            other polynomial ring.
	 */
	public ExprPolynomialRing(ExprPolynomialRing o, ExprTermOrder to) {
		this(o.coFac, o.vars, o.nvar, to);
	}

	/**
	 * Copy this factory.
	 * 
	 * @return a clone of this.
	 */
	public ExprPolynomialRing copy() {
		return new ExprPolynomialRing(coFac, this);
	}

	/**
	 * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
	 * 
	 * @param exprPoly
	 *            the polynomial expression
	 * @return
	 */
	public ExprPolynomial create(final IExpr exprPoly) throws ArithmeticException, ClassCastException {
		return create(exprPoly, false, true);
	}

	/**
	 * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
	 * 
	 * @param exprPoly
	 *            the polynomial expression
	 * @param coefficient
	 *            set to <code>true</code> if called by the
	 *            <code>Coefficient()</code> function
	 * @param checkNegativeExponents
	 *            if <code>true</code> don't allow negative exponents
	 * @return
	 */
	public ExprPolynomial create(final IExpr exprPoly, boolean coefficient, boolean checkNegativeExponents)
			throws ArithmeticException, ClassCastException {
		int ix = evzero.indexVar(exprPoly, getVars());
		if (ix >= 0) {
			ExpVectorLong e = new ExpVectorLong(vars.size() - 1, ix, 1L);
			return getOne().multiply(e);
		}
		if (exprPoly instanceof IAST) {
			final IAST ast = (IAST) exprPoly;
			ExprPolynomial result = getZero();
			ExprPolynomial p = getZero();
			if (ast.isPlus()) {
				IExpr expr = ast.arg1();
				result = create(expr, coefficient, checkNegativeExponents);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					p = create(expr, coefficient, checkNegativeExponents);
					result = result.sum(p);
				}
				return result;
			} else if (ast.isTimes()) {
				IExpr expr = ast.arg1();
				result = create(expr, coefficient, checkNegativeExponents);
				for (int i = 2; i < ast.size(); i++) {
					expr = ast.get(i);
					p = create(expr, coefficient, checkNegativeExponents);
					result = result.multiply(p);
				}
				return result;
			} else if (ast.isPower()) {
				final IExpr expr = ast.arg1();
				ix = ExpVectorLong.indexVar(expr, getVars());
				if (ix >= 0) {
					int exponent = -1;
					try {
						exponent = Validate.checkPowerExponent(ast);
					} catch (WrongArgumentType e) {
						//
					}
					if (checkNegativeExponents && exponent < 0) {
						throw new ArithmeticException(
								"JASConvert:expr2Poly - invalid exponent: " + ast.arg2().toString());
					}
					ExpVectorLong e = new ExpVectorLong(vars.size() - 1, ix, exponent);
					return getOne().multiply(e);
				}

				// for (int i = 1; i < vars.size(); i++) {
				// if (vars.get(i).equals(expr)) {
				// int exponent = -1;
				// try {
				// exponent = Validate.checkPowerExponent(ast);
				// } catch (WrongArgumentType e) {
				// //
				// }
				// if (exponent < 0) {
				// throw new ArithmeticException("JASConvert:expr2Poly - invalid
				// exponent: " + ast.arg2().toString());
				// }
				// ExpVectorLong e = new ExpVectorLong(vars.size() - 1, i - 1,
				// exponent);
				// return getOne().multiply(e);
				// }
				// }
			}
			if (coefficient) {
				return new ExprPolynomial(this, ast);
			}
			if (numericFunction) {
				if (ast.isNumericFunction()) {
					return new ExprPolynomial(this, ast);
				}
			}
		} else if (exprPoly instanceof ISymbol) {
			if (coefficient) {
				return new ExprPolynomial(this, exprPoly);
			}
			if (numericFunction) {
				if (exprPoly.isNumericFunction()) {
					return new ExprPolynomial(this, exprPoly);
				}
				throw new ClassCastException(exprPoly.toString());
			} else {
				return new ExprPolynomial(this, exprPoly);
			}
		} else if (exprPoly.isNumber()) {
			return new ExprPolynomial(this, exprPoly);
		}
		if (exprPoly.isFree(Predicates.in(vars), true)) {
			return new ExprPolynomial(this, exprPoly);
		}
		throw new ClassCastException(exprPoly.toString());
	}

	/**
	 * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
	 * 
	 * @param expression
	 *            the expression which should be checked if it's a polynomial
	 * @param coefficient
	 *            set to <code>true</code> if called by the
	 *            <code>Coefficient()</code> function
	 * @return <code>true</code> if the given expression is a polynomial
	 */
	public boolean isPolynomial(final IExpr expression) throws ArithmeticException, ClassCastException {
		return isPolynomial(expression, false);
	}

	/**
	 * Create a <code>Polynomial</code> from the given <code>exprPoly</code>.
	 * 
	 * @param expression
	 *            the expression which should be checked if it's a polynomial
	 * @param coefficient
	 *            set to <code>true</code> if called by the
	 *            <code>Coefficient()</code> function
	 * @return <code>true</code> if the given expression is a polynomial
	 */
	public boolean isPolynomial(final IExpr expression, boolean coefficient)
			throws ArithmeticException, ClassCastException {
		for (int i = 1; i < vars.size(); i++) {
			if (vars.get(i).equals(expression)) {
				return true;
			}
		}
		if (expression instanceof IAST) {
			final IAST ast = (IAST) expression;
			if (ast.isPlus()) {
				for (int i = 1; i < ast.size(); i++) {
					if (!isPolynomial(ast.get(i), coefficient)) {
						return false;
					}
				}
				return true;
			} else if (ast.isTimes()) {
				for (int i = 1; i < ast.size(); i++) {
					if (!isPolynomial(ast.get(i), coefficient)) {
						return false;
					}
				}
				return true;
			} else if (ast.isPower()) {
				for (int i = 1; i < vars.size(); i++) {
					if (vars.get(i).equals(ast.arg1())) {
						int exponent = -1;
						try {
							exponent = Validate.checkPowerExponent(ast);
						} catch (WrongArgumentType e) {
							return false;
						}
						if (exponent < 0) {
							return false;
						}
						return true;
					}
				}
			}
			if (coefficient) {
				return true;
			}
			if (numericFunction) {
				if (ast.isNumericFunction()) {
					return true;
				}
			}
		} else if (expression instanceof ISymbol) {
			if (coefficient) {
				return true;
			}
			if (numericFunction) {
				if (expression.isNumericFunction()) {
					return true;
				}
				return false;
			} else {
				return true;
			}
		} else if (expression.isNumber()) {
			return true;
		}
		if (expression.isFree(Predicates.in(vars), true)) {
			return true;
		}
		return false;
	}

	/**
	 * Get the position of the <code>expr</code> in the variables list.
	 * 
	 * @param expr
	 * @return <code>-1</code> if the expression isn't found in the variable
	 *         list.
	 */
	private int isVariable(final IExpr expr) {
		for (int i = 1; i < vars.size(); i++) {
			if (vars.get(i).equals(expr)) {
				return i - 1;
			}
		}
		return -1;
	}

	/**
	 * Get the String representation.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@SuppressWarnings("cast")
	@Override
	public String toString() {
		String res = null;
		if (PrettyPrint.isTrue()) { // wrong: && coFac != null
			String scf = coFac.getClass().getSimpleName();
			// if (coFac instanceof AlgebraicNumberRing) {
			// AlgebraicNumberRing an = (AlgebraicNumberRing) coFac;
			// // String[] v = an.ring.vars;
			// res = "AN[ (" + an.ring.varsToString() + ") (" + an.toString() +
			// ") ]";
			// }
			// if (coFac instanceof GenPolynomialRing) {
			// GenPolynomialRing rf = (GenPolynomialRing) coFac;
			// // String[] v = rf.vars;
			// // ExprRingFactory cf = rf.coFac;
			// // String cs;
			// // if (cf instanceof ModIntegerRing) {
			// // cs = cf.toString();
			// // } else {
			// // cs = " " + cf.getClass().getSimpleName();
			// // }
			// // res = "IntFunc" + "{" + cs + "( " + rf.varsToString() + " )" +
			// " } ";
			// res = "IntFunc" + "( " + rf.toString() + " )";
			// }
			// if (((Object) coFac) instanceof ModIntegerRing) {
			// ModIntegerRing mn = (ModIntegerRing) ((Object) coFac);
			// res = "Mod " + mn.getModul() + " ";
			// }
			if (res == null) {
				res = coFac.toString();
				if (res.matches("[0-9].*")) {
					res = scf;
				}
			}
			res += "( " + varsToString() + " ) " + tord.toString() + " ";
		} else {
			res = this.getClass().getSimpleName() + "[ " + coFac.toString() + " ";
			// + coFac.getClass().getSimpleName();
			// if (coFac instanceof AlgebraicNumberRing) {
			// AlgebraicNumberRing an = (AlgebraicNumberRing) coFac;
			// res = "AN[ (" + an.ring.varsToString() + ") (" + an.modul + ")
			// ]";
			// }
			// if (coFac instanceof GenPolynomialRing) {
			// GenPolynomialRing rf = (GenPolynomialRing) coFac;
			// // String[] v = rf.vars;
			// // ExprRingFactory cf = rf.coFac;
			// // String cs;
			// // if (cf instanceof ModIntegerRing) {
			// // cs = cf.toString();
			// // } else {
			// // cs = " " + cf.getClass().getSimpleName();
			// // }
			// // res = "IntFunc{ " + cs + "( " + rf.varsToString() + " )" + " }
			// ";
			// res = "IntFunc" + "( " + rf.toString() + " )";
			// }
			// if (((Object) coFac) instanceof ModIntegerRing) {
			// ModIntegerRing mn = (ModIntegerRing) ((Object) coFac);
			// res = "Mod " + mn.getModul() + " ";
			// }
			// res += ", " + nvar + ", " + tord.toString() + ", " +
			// varsToString() + ", " + partial + " ]";
			res += "( " + varsToString() + " ) " + tord.toString() + " ]";
		}
		return res;
	}

	/**
	 * Get a scripting compatible string representation.
	 * 
	 * @return script compatible representation for this Element.
	 * @see edu.jas.structure.Element#toScript()
	 */
	public String toScript() {
		StringBuffer s = new StringBuffer();
		switch (Scripting.getLang()) {
		case Ruby:
			s.append("PolyRing.new(");
			break;
		case Python:
		default:
			s.append("PolyRing(");
		}
		// if (coFac instanceof RingElem) {
		// s.append(coFac.toScriptFactory());
		// } else {
		s.append(coFac.toScript().trim());
		// }
		s.append(",\"" + varsToString() + "\"");
		String to = tord.toString();
		if (tord.getEvord() == ExprTermOrder.INVLEX) {
			to = ",PolyRing.lex";
		}
		if (tord.getEvord() == ExprTermOrder.IGRLEX) {
			to = ",PolyRing.grad";
		}
		s.append(to);
		s.append(")");
		return s.toString();
	}

	/**
	 * Get a scripting compatible string representation of an ExpVectorLong of
	 * this ring.
	 * 
	 * @param e
	 *            exponent vector
	 * @return script compatible representation for the ExpVectorLong.
	 */
	public String toScript(ExpVectorLong e) {
		if (vars != null) {
			return e.toScript(vars);
		}
		return e.toScript();
	}

	/**
	 * Comparison with any other object.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (!(other instanceof ExprPolynomialRing)) {
			return false;
		}
		ExprPolynomialRing oring = (ExprPolynomialRing) other;
		if (nvar != oring.nvar) {
			return false;
		}
		if (!coFac.equals(oring.coFac)) {
			return false;
		}
		if (!tord.equals(oring.tord)) {
			return false;
		}
		// same variables required ?
		if (!vars.equals(oring.vars)) {
			return false;
		}
		return true;
	}

	/**
	 * Hash code for this polynomial ring.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int h;
		h = (nvar << 27);
		h += (coFac.hashCode() << 11);
		h += tord.hashCode();
		return h;
	}

	/**
	 * Get the number of polynomial creations.
	 * 
	 * @return creations.
	 */
	public int getCreations() {
		return creations;
	}

	/**
	 * Get the variable names.
	 * 
	 * @return vars.
	 */
	public IAST getVars() {
		return vars.clone();
	}

	/**
	 * Set the variable names.
	 * 
	 * @return old vars.
	 */
	public IAST setVars(IAST v) {
		if (v.size() - 1 != nvar) {
			throw new IllegalArgumentException(
					"v not matching number of variables: " + v.toString() + ", nvar " + nvar);
		}
		IAST t = vars;
		vars = v.clone();// Arrays.copyOf(v, v.length); // > Java-5
		return t;
	}

	/**
	 * Get a String representation of the variable names.
	 * 
	 * @return names seperated by commas.
	 */
	public String varsToString() {
		if (vars == null) {
			return "#" + nvar;
		}
		// return Arrays.toString(vars);
		return ExpVectorLong.varsToString(vars);
	}

	/**
	 * Get the zero element from the coefficients.
	 * 
	 * @return 0 as C.
	 */
	public IExpr getZEROCoefficient() {
		return coFac.getZERO();
	}

	/**
	 * Get the one element from the coefficients.
	 * 
	 * @return 1 as C.
	 */
	public IExpr getONECoefficient() {
		return coFac.getONE();
	}

	/**
	 * Get the zero element.
	 * 
	 * @return 0 as GenPolynomial.
	 */
	public ExprPolynomial getZero() {
		return ZERO;
	}

	/**
	 * Get the one element.
	 * 
	 * @return 1 as GenPolynomial.
	 */
	public ExprPolynomial getOne() {
		return ONE;
	}

	/**
	 * Query if this ring is commutative.
	 * 
	 * @return true if this ring is commutative, else false.
	 */
	public boolean isCommutative() {
		return coFac.isCommutative();
	}

	/**
	 * Query if this ring is associative.
	 * 
	 * @return true if this ring is associative, else false.
	 */
	public boolean isAssociative() {
		return coFac.isAssociative();
	}

	/**
	 * Query if this ring is a field.
	 * 
	 * @return false.
	 */
	public boolean isField() {
		if (isField > 0) {
			return true;
		}
		if (isField == 0) {
			return false;
		}
		if (coFac.isField() && nvar == 0) {
			isField = 1;
			return true;
		}
		isField = 0;
		return false;
	}

	/**
	 * Characteristic of this ring.
	 * 
	 * @return characteristic of this ring.
	 */
	public java.math.BigInteger characteristic() {
		return coFac.characteristic();
	}

	/**
	 * Get a (constant) GenPolynomial&lt;C&gt; element from a coefficient value.
	 * 
	 * @param a
	 *            coefficient.
	 * @return a GenPolynomial&lt;C&gt;.
	 */
	public ExprPolynomial valueOf(IExpr a) {
		return new ExprPolynomial(this, a);
	}

	/**
	 * Get a GenPolynomial&lt;C&gt; element from an exponent vector.
	 * 
	 * @param e
	 *            exponent vector.
	 * @return a GenPolynomial&lt;C&gt;.
	 */
	public ExprPolynomial valueOf(ExpVectorLong e) {
		return new ExprPolynomial(this, coFac.getONE(), e);
	}

	/**
	 * Get a GenPolynomial&lt;C&gt; element from a coeffcient and an exponent
	 * vector.
	 * 
	 * @param a
	 *            coefficient.
	 * @param e
	 *            exponent vector.
	 * @return a GenPolynomial&lt;C&gt;.
	 */
	public ExprPolynomial valueOf(IExpr a, ExpVectorLong e) {
		return new ExprPolynomial(this, a, e);
	}

	/**
	 * Get a (constant) GenPolynomial&lt;C&gt; element from a long value.
	 * 
	 * @param a
	 *            long.
	 * @return a GenPolynomial&lt;C&gt;.
	 */
	public ExprPolynomial fromInteger(long a) {
		return new ExprPolynomial(this, coFac.fromInteger(a), evzero);
	}

	/**
	 * Get a (constant) GenPolynomial&lt;C&gt; element from a BigInteger value.
	 * 
	 * @param a
	 *            BigInteger.
	 * @return a GenPolynomial&lt;C&gt;.
	 */
	public ExprPolynomial fromInteger(BigInteger a) {
		return new ExprPolynomial(this, coFac.fromInteger(a), evzero);
	}

	/**
	 * Copy polynomial c.
	 * 
	 * @param c
	 * @return a copy of c.
	 */
	public ExprPolynomial copy(ExprPolynomial c) {
		// System.out.println("GP copy = " + this);
		return new ExprPolynomial(this, c.val);
	}

	/**
	 * Generate univariate polynomial in a given variable.
	 * 
	 * @param i
	 *            the index of the variable.
	 * @return X_i as univariate polynomial.
	 */
	public ExprPolynomial univariate(int i) {
		return univariate(0, i, 1L);
	}

	/**
	 * Generate univariate polynomial in a given variable with given exponent.
	 * 
	 * @param i
	 *            the index of the variable.
	 * @param e
	 *            the exponent of the variable.
	 * @return X_i^e as univariate polynomial.
	 */
	public ExprPolynomial univariate(int i, long e) {
		return univariate(0, i, e);
	}

	/**
	 * Generate univariate polynomial in a given variable with given exponent.
	 * 
	 * @param modv
	 *            number of module variables.
	 * @param i
	 *            the index of the variable.
	 * @param e
	 *            the exponent of the variable.
	 * @return X_i^e as univariate polynomial.
	 */
	public ExprPolynomial univariate(int modv, int i, long e) {
		ExprPolynomial p = getZero();
		int r = nvar - modv;
		if (0 <= i && i < r) {
			IExpr one = coFac.getONE();
			ExpVectorLong f = new ExpVectorLong(r, i, e);
			if (modv > 0) {
				f = f.extend(modv, 0, 0l);
			}
			p = p.sum(one, f);
		}
		return p;
	}

	/**
	 * Get the generating elements excluding the generators for the coefficient
	 * ring.
	 * 
	 * @return a list of generating elements for this ring.
	 */
	public List<ExprPolynomial> getGenerators() {
		List<? extends ExprPolynomial> univs = univariateList();
		List<ExprPolynomial> gens = new ArrayList<ExprPolynomial>(univs.size() + 1);
		gens.add(getOne());
		gens.addAll(univs);
		return gens;
	}

	/**
	 * Get a list of the generating elements.
	 * 
	 * @return list of generators for the algebraic structure.
	 * @see edu.jas.structure.ElemFactory#generators()
	 */
	public List<ExprPolynomial> generators() {
		List<? extends IExpr> cogens = coFac.generators();
		List<? extends ExprPolynomial> univs = univariateList();
		List<ExprPolynomial> gens = new ArrayList<ExprPolynomial>(univs.size() + cogens.size());
		for (IExpr c : cogens) {
			gens.add(getOne().multiply(c));
		}
		gens.addAll(univs);
		return gens;
	}

	/**
	 * Get a list of the generating elements excluding the module variables.
	 * 
	 * @param modv
	 *            number of module variables
	 * @return list of generators for the polynomial ring.
	 */
	public List<ExprPolynomial> generators(int modv) {
		List<? extends IExpr> cogens = coFac.generators();
		List<? extends ExprPolynomial> univs = univariateList(modv);
		List<ExprPolynomial> gens = new ArrayList<ExprPolynomial>(univs.size() + cogens.size());
		for (IExpr c : cogens) {
			gens.add(getOne().multiply(c));
		}
		gens.addAll(univs);
		return gens;
	}

	/**
	 * Is this structure finite or infinite.
	 * 
	 * @return true if this structure is finite, else false.
	 * @see edu.jas.structure.ElemFactory#isFinite()
	 */
	public boolean isFinite() {
		return (nvar == 0) && coFac.isFinite();
	}

	/**
	 * Generate list of univariate polynomials in all variables.
	 * 
	 * @return List(X_1,...,X_n) a list of univariate polynomials.
	 */
	public List<? extends ExprPolynomial> univariateList() {
		return univariateList(0, 1L);
	}

	/**
	 * Generate list of univariate polynomials in all variables.
	 * 
	 * @param modv
	 *            number of module variables.
	 * @return List(X_1,...,X_n) a list of univariate polynomials.
	 */
	public List<? extends ExprPolynomial> univariateList(int modv) {
		return univariateList(modv, 1L);
	}

	/**
	 * Generate list of univariate polynomials in all variables with given
	 * exponent.
	 * 
	 * @param modv
	 *            number of module variables.
	 * @param e
	 *            the exponent of the variables.
	 * @return List(X_1^e,...,X_n^e) a list of univariate polynomials.
	 */
	public List<? extends ExprPolynomial> univariateList(int modv, long e) {
		List<ExprPolynomial> pols = new ArrayList<ExprPolynomial>(nvar);
		int nm = nvar - modv;
		for (int i = 0; i < nm; i++) {
			ExprPolynomial p = univariate(modv, nm - 1 - i, e);
			pols.add(p);
		}
		return pols;
	}

	/**
	 * Extend variables. Used e.g. in module embedding. Extend number of
	 * variables by i.
	 * 
	 * @param i
	 *            number of variables to extend.
	 * @return extended polynomial ring factory.
	 */
	// public ExprPolynomialRing extend(int i) {
	// // add module variable names
	// IAST v = newVars("e", i);
	// return extend(v);
	// }

	/**
	 * Extend variables. Used e.g. in module embedding. Extend number of
	 * variables by length(vn).
	 * 
	 * @param vn
	 *            names for extended variables.
	 * @return extended polynomial ring factory.
	 */
	public ExprPolynomialRing extend(IAST vn) {
		if (vn == null || vars == null) {
			throw new IllegalArgumentException("vn and vars may not be null");
		}
		int i = vn.size() - 1;
		IAST v = vars.clone();
		v.addArgs(vn);
		// for (int k = 0; k < vars.length; k++) {
		// v[k] = vars[k];
		// }
		// for (int k = 0; k < vn.length; k++) {
		// v[vars.length + k] = vn[k];
		// }
		ExprTermOrder to = tord.extend(nvar, i);
		ExprPolynomialRing pfac = new ExprPolynomialRing(coFac, v, nvar + i, to);
		return pfac;
	}

	/**
	 * Extend lower variables. Extend number of variables by i.
	 * 
	 * @param i
	 *            number of variables to extend.
	 * @return extended polynomial ring factory.
	 */
	// public ExprPolynomialRing extendLower(int i) {
	// IAST v = newVars("e", i);
	// return extendLower(v);
	// }

	/**
	 * Extend lower variables. Extend number of variables by length(vn).
	 * 
	 * @param vn
	 *            names for extended lower variables.
	 * @return extended polynomial ring factory.
	 */
	public ExprPolynomialRing extendLower(IAST vn) {
		if (vn == null || vars == null) {
			throw new IllegalArgumentException("vn and vars may not be null");
		}
		int i = vn.size() - 1;
		IAST v = vn.clone();
		vn.addArgs(vars);
		// String[] v = new String[vars.length + i];
		// for (int k = 0; k < vn.length; k++) {
		// v[k] = vn[k];
		// }
		// for (int k = 0; k < vars.length; k++) {
		// v[vn.length + k] = vars[k];
		// }
		ExprTermOrder to = tord.extendLower(nvar, i);
		ExprPolynomialRing pfac = new ExprPolynomialRing(coFac, v, nvar + i, to);
		return pfac;
	}

	/**
	 * Contract variables. Used e.g. in module embedding. Contract number of
	 * variables by i.
	 * 
	 * @param i
	 *            number of variables to remove.
	 * @return contracted polynomial ring factory.
	 */
	// public GenPolynomialRing contract(int i) {
	// String[] v = null;
	// if (vars != null) {
	// v = new String[vars.length - i];
	// for (int j = 0; j < vars.length - i; j++) {
	// v[j] = vars[j];
	// }
	// }
	// TermOrder to = tord.contract(i, nvar - i);
	// GenPolynomialRing pfac = new GenPolynomialRing(coFac, nvar - i, to, v);
	// return pfac;
	// }

	/**
	 * Distributive representation as polynomial with all main variables.
	 * 
	 * @return distributive polynomial ring factory.
	 */
	@SuppressWarnings("cast")
	public ExprPolynomialRing distribute() {
		// if (!(coFac instanceof GenPolynomialRing)) {
		return this;
		// }
		// ExprRingFactory cf = coFac;
		// ExprRingFactory<GenPolynomial> cfp = (ExprRingFactory<GenPolynomial>)
		// cf;
		// GenPolynomialRing cr = (GenPolynomialRing) cfp;
		// GenPolynomialRing pfac;
		// if (cr.vars != null) {
		// pfac = extend(cr.vars);
		// } else {
		// pfac = extend(cr.nvar);
		// }
		// return pfac;
	}

	/**
	 * Reverse variables. Used e.g. in opposite rings.
	 * 
	 * @return polynomial ring factory with reversed variables.
	 */
	// public GenPolynomialRing reverse() {
	// return reverse(false);
	// }

	/**
	 * Reverse variables. Used e.g. in opposite rings.
	 * 
	 * @param partial
	 *            true for partialy reversed term orders.
	 * @return polynomial ring factory with reversed variables.
	 */
	// public GenPolynomialRing reverse(boolean partial) {
	// String[] v = null;
	// if (vars != null) { // vars are not inversed
	// v = new String[vars.length];
	// int k = tord.getSplit();
	// if (partial && k < vars.length) {
	// // copy upper
	// for (int j = 0; j < k; j++) {
	// // v[vars.length - k + j] = vars[vars.length - 1 - j]; // reverse upper
	// v[vars.length - k + j] = vars[vars.length - k + j];
	// }
	// // reverse lower
	// for (int j = 0; j < vars.length - k; j++) {
	// // v[j] = vars[j]; // copy upper
	// v[j] = vars[vars.length - k - j - 1];
	// }
	// } else {
	// for (int j = 0; j < vars.length; j++) {
	// v[j] = vars[vars.length - 1 - j];
	// }
	// }
	// // System.out.println("vars = " + Arrays.toString(vars));
	// // System.out.println("v = " + Arrays.toString(v));
	// }
	// TermOrder to = tord.reverse(partial);
	// GenPolynomialRing pfac = new GenPolynomialRing(coFac, nvar, to, v);
	// pfac.partial = partial;
	// return pfac;
	// }

	/**
	 * Get PolynomialComparator.
	 * 
	 * @return polynomial comparator.
	 */
	public ExprPolynomialComparator getComparator() {
		return new ExprPolynomialComparator(tord, false);
	}

	/**
	 * Get PolynomialComparator.
	 * 
	 * @param rev
	 *            for reverse comparator.
	 * @return polynomial comparator.
	 */
	public ExprPolynomialComparator getComparator(boolean rev) {
		return new ExprPolynomialComparator(tord, rev);
	}

	/**
	 * New variable names. Generate new names for variables,
	 * 
	 * @param prefix
	 *            name prefix.
	 * @param n
	 *            number of variables.
	 * @return new variable names.
	 */
	// public static IAST newVars(String prefix, int n) {
	// IAST vars = F.List();
	// synchronized (knownVars) {
	// int m = knownVars.size();
	// String name = prefix + m;
	// for (int i = 0; i < n; i++) {
	// while (knownVars.contains(name)) {
	// m++;
	// name = prefix + m;
	// }
	// ISymbol sym = F.$s(name);
	// vars.add(sym);
	// // System.out.println("new variable: " + name);
	// knownVars.add(sym);
	// m++;
	// name = prefix + m;
	// }
	// }
	// return vars;
	// }

	/**
	 * New variable names. Generate new names for variables,
	 * 
	 * @param prefix
	 *            name prefix.
	 * @return new variable names.
	 */
	// public IAST newVars(String prefix) {
	// return newVars(prefix, nvar);
	// }

	/**
	 * New variable names. Generate new names for variables,
	 * 
	 * @param n
	 *            number of variables.
	 * @return new variable names.
	 */
	// public static IAST newVars(int n) {
	// return newVars("x", n);
	// }

	/**
	 * New variable names. Generate new names for variables,
	 * 
	 * @return new variable names.
	 */
	// public IAST newVars() {
	// return newVars(nvar);
	// }

	/**
	 * Add variable names.
	 * 
	 * @param vars
	 *            variable names to be recorded.
	 */
	public static void addVars(IAST vars) {
		if (vars == null) {
			return;
		}
		synchronized (knownVars) {
			for (int i = 1; i < vars.size(); i++) {
				knownVars.add(vars.get(i)); // eventualy names 'overwritten'
			}
		}
	}

	/**
	 * Permute variable names.
	 * 
	 * @param vars
	 *            variable names.
	 * @param P
	 *            permutation.
	 * @return P(vars).
	 */
	public static String[] permuteVars(List<Integer> P, String[] vars) {
		if (vars == null || vars.length <= 1) {
			return vars;
		}
		String[] b = new String[vars.length];
		int j = 0;
		for (Integer i : P) {
			b[j++] = vars[i];
		}
		return b;
	}

	/**
	 * Permutation of polynomial ring variables.
	 * 
	 * @param P
	 *            permutation.
	 * @return P(this).
	 */
	// public GenPolynomialRing permutation(List<Integer> P) {
	// if (nvar <= 1) {
	// return this;
	// }
	// TermOrder tp = tord.permutation(P);
	// if (vars == null) {
	// return new GenPolynomialRing(coFac, nvar, tp);
	// }
	// IExpr[] v1 = new IExpr[vars.size() - 1];
	// for (int i = 0; i < v1.length; i++) {
	// v1[i] = vars.get(v1.length - i);
	// }
	// String[] vp = permuteVars(P, v1);
	// String[] v2 = new String[vp.length];
	// for (int i = 0; i < vp.length; i++) {
	// v2[i] = vp[vp.length - 1 - i];
	// }
	// return new GenPolynomialRing(coFac, nvar, tp, v2);
	// }

	/**
	 * Get a GenPolynomial iterator.
	 * 
	 * @return an iterator over all polynomials.
	 */
	public Iterator<ExprPolynomial> iterator() {
		if (coFac.isFinite()) {
			return new GenPolynomialIterator(this);
		}
		logger.warn("ring of coefficients " + coFac + " is infinite, constructing iterator only over monomials");
		return new GenPolynomialMonomialIterator(this);
		// throw new IllegalArgumentException("only for finite iterable
		// coefficients implemented");
	}

}

/**
 * Polynomial iterator.
 * 
 * @author Heinz Kredel
 */
class GenPolynomialIterator implements Iterator<ExprPolynomial> {

	/**
	 * data structure.
	 */
	final ExprPolynomialRing ring;

	final Iterator<List<Long>> eviter;

	final List<ExpVectorLong> powers;

	final List<Iterable<IExpr>> coeffiter;

	Iterator<List<IExpr>> itercoeff;

	ExprPolynomial current;

	/**
	 * Polynomial iterator constructor.
	 */
	@SuppressWarnings("unchecked")
	public GenPolynomialIterator(ExprPolynomialRing fac) {
		ring = fac;
		LongIterable li = new LongIterable();
		li.setNonNegativeIterator();
		List<Iterable<Long>> tlist = new ArrayList<Iterable<Long>>(ring.nvar);
		for (int i = 0; i < ring.nvar; i++) {
			tlist.add(li);
		}
		CartesianProductInfinite<Long> ei = new CartesianProductInfinite<Long>(tlist);
		eviter = ei.iterator();
		ExprRingFactory cf = ring.coFac;
		coeffiter = new ArrayList<Iterable<IExpr>>();
		if (cf instanceof Iterable && cf.isFinite()) {
			Iterable<IExpr> cfi = (Iterable<IExpr>) cf;
			coeffiter.add(cfi);
		} else {
			throw new IllegalArgumentException("only for finite iterable coefficients implemented");
		}
		CartesianProduct<IExpr> tuples = new CartesianProduct<IExpr>(coeffiter);
		itercoeff = tuples.iterator();
		powers = new ArrayList<ExpVectorLong>();
		ExpVectorLong e = ExpVectorLong.create(eviter.next());
		powers.add(e);
		// System.out.println("new e = " + e);
		// System.out.println("powers = " + powers);
		List<IExpr> c = itercoeff.next();
		// System.out.println("coeffs = " + c);
		current = new ExprPolynomial(ring, c.get(0), e);
	}

	/**
	 * Test for availability of a next element.
	 * 
	 * @return true if the iteration has more elements, else false.
	 */
	public boolean hasNext() {
		return true;
	}

	/**
	 * Get next polynomial.
	 * 
	 * @return next polynomial.
	 */
	public synchronized ExprPolynomial next() {
		ExprPolynomial res = current;
		if (!itercoeff.hasNext()) {
			ExpVectorLong e = ExpVectorLong.create(eviter.next());
			powers.add(0, e); // add new ev at beginning
			// System.out.println("new e = " + e);
			// System.out.println("powers = " + powers);
			if (coeffiter.size() == 1) { // shorten frist iterator by one
											// element
				coeffiter.add(coeffiter.get(0));
				Iterable<IExpr> it = coeffiter.get(0);
				List<IExpr> elms = new ArrayList<IExpr>();
				for (IExpr elm : it) {
					elms.add(elm);
				}
				elms.remove(0);
				coeffiter.set(0, elms);
			} else {
				coeffiter.add(coeffiter.get(1));
			}
			CartesianProduct<IExpr> tuples = new CartesianProduct<IExpr>(coeffiter);
			itercoeff = tuples.iterator();
		}
		List<IExpr> coeffs = itercoeff.next();
		// while ( coeffs.get(0).isZERO() ) {
		// System.out.println(" skip zero ");
		// coeffs = itercoeff.next(); // skip tuples with zero in first
		// component
		// }
		// System.out.println("coeffs = " + coeffs);
		ExprPolynomial pol = ring.getZero().copy();
		int i = 0;
		for (ExpVectorLong f : powers) {
			IExpr c = coeffs.get(i++);
			if (c.isZero()) {
				continue;
			}
			if (pol.val.get(f) != null) {
				System.out.println("error f in pol = " + f + ", " + pol.getMap().get(f));
				throw new RuntimeException("error in iterator");
			}
			pol.doPutToMap(f, c);
		}
		current = pol;
		return res;
	}

	/**
	 * Remove an element if allowed.
	 */
	public void remove() {
		throw new UnsupportedOperationException("cannnot remove elements");
	}
}

/**
 * Polynomial monomial iterator.
 * 
 * @author Heinz Kredel
 */
class GenPolynomialMonomialIterator implements Iterator<ExprPolynomial> {

	/**
	 * data structure.
	 */
	final ExprPolynomialRing ring;

	final Iterator<List> iter;

	ExprPolynomial current;

	/**
	 * Polynomial iterator constructor.
	 */
	@SuppressWarnings("unchecked")
	public GenPolynomialMonomialIterator(ExprPolynomialRing fac) {
		ring = fac;
		LongIterable li = new LongIterable();
		li.setNonNegativeIterator();
		List<Iterable<Long>> tlist = new ArrayList<Iterable<Long>>(ring.nvar);
		for (int i = 0; i < ring.nvar; i++) {
			tlist.add(li);
		}
		CartesianProductInfinite<Long> ei = new CartesianProductInfinite<Long>(tlist);
		// Iterator<List<Long>> eviter = ei.iterator();

		ExprRingFactory cf = ring.coFac;
		Iterable<IExpr> coeffiter;
		if (cf instanceof Iterable && !cf.isFinite()) {
			Iterable<IExpr> cfi = (Iterable<IExpr>) cf;
			coeffiter = cfi;
		} else {
			throw new IllegalArgumentException("only for infinite iterable coefficients implemented");
		}

		// Cantor iterator for exponents and coeffcients
		List<Iterable> eci = new ArrayList<Iterable>(2); // no type parameter
		eci.add(ei);
		eci.add(coeffiter);
		CartesianProductInfinite ecp = new CartesianProductInfinite(eci);
		iter = ecp.iterator();

		List ec = iter.next();
		List<Long> ecl = (List<Long>) ec.get(0);
		IExpr c = (IExpr) ec.get(1); // zero
		ExpVectorLong e = ExpVectorLong.create(ecl);
		// System.out.println("exp = " + e);
		// System.out.println("coeffs = " + c);
		current = new ExprPolynomial(ring, c, e);
	}

	/**
	 * Test for availability of a next element.
	 * 
	 * @return true if the iteration has more elements, else false.
	 */
	public boolean hasNext() {
		return true;
	}

	/**
	 * Get next polynomial.
	 * 
	 * @return next polynomial.
	 */
	@SuppressWarnings("unchecked")
	public synchronized ExprPolynomial next() {
		ExprPolynomial res = current;

		List ec = iter.next();
		IExpr c = (IExpr) ec.get(1);
		while (c.isZero()) { // zero already done in first next
			ec = iter.next();
			c = (IExpr) ec.get(1);
		}
		List<Long> ecl = (List<Long>) ec.get(0);
		ExpVectorLong e = ExpVectorLong.create(ecl);
		// System.out.println("exp = " + e);
		// System.out.println("coeffs = " + c);
		current = new ExprPolynomial(ring, c, e);

		return res;
	}

	/**
	 * Remove an element if allowed.
	 */
	public void remove() {
		throw new UnsupportedOperationException("cannnot remove elements");
	}
}
