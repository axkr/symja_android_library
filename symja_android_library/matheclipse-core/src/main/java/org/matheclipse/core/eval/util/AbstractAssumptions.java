package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

public abstract class AbstractAssumptions implements IAssumptions {

	@Override
	public boolean isNegative(IExpr expr) {
		return false;
	}

	@Override
	public boolean isPositive(IExpr expr) {
		return false;
	}

	@Override
	public boolean isNonNegative(IExpr expr) {
		return false;
	}

	@Override
	public boolean isAlgebraic(IExpr expr) {
		return false;
	}

	@Override
	public boolean isBoolean(IExpr expr) {
		return false;
	}

	@Override
	public boolean isComplex(IExpr expr) {
		return false;
	}

	@Override
	public boolean isInteger(IExpr expr) {
		return false;
	}

	@Override
	public boolean isPrime(IExpr expr) {
		return false;
	}

	@Override
	public boolean isRational(IExpr expr) {
		return false;
	}

	@Override
	public boolean isReal(IExpr expr) {
		return false;
	}

	/**
	 * Test if <code>expr</code> is assumed to be an negative number.
	 * 
	 * @param expr
	 * @return <code>true</code> if <code>expr</code> is assumed to be a negative number. Return <code>false</code> in
	 *         all other cases.
	 */
	public static boolean assumeNegative(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return ((ISignedNumber) expr).isNegative();
		}
		if (expr.isNumber()) {
			return false;
		}
		if (expr.isSignedNumberConstant()) {
			return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal() < 0.0;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isNegative(expr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test if <code>expr</code> is assumed to be an positive number.
	 * 
	 * @param expr
	 * @return <code>true</code> if <code>expr</code> is assumed to be a positive number. Return <code>false</code> in
	 *         all other cases.
	 */
	public static boolean assumePositive(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return ((ISignedNumber) expr).isPositive();
		}
		if (expr.isNumber()) {
			return false;
		}
		if (expr.isSignedNumberConstant()) {
			return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal() > 0.0;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isPositive(expr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test if <code>expr</code> is assumed to be an non negative number.
	 * 
	 * @param expr
	 * @return <code>true</code> if <code>expr</code> is assumed to be a non negative number. Return <code>false</code>
	 *         in all other cases.
	 */
	public static boolean assumeNonNegative(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return !((ISignedNumber) expr).isNegative();
		}
		if (expr.isNumber()) {
			return false;
		}
		if (expr.isSignedNumberConstant()) {
			return ((ISignedNumberConstant) ((IBuiltInSymbol) expr).getEvaluator()).evalReal() >= 0.0;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isNonNegative(expr)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * TODO implement algebraic number conditions.
	 * 
	 * @param expr
	 * @return
	 */
	public static ISymbol assumeAlgebraic(final IExpr expr) {
		if (expr.isRational()) {
			return F.True;
		}
		if (expr.isNumber()) {
			return F.True;
		}
		if (expr.equals(F.CComplexInfinity)) {
			return F.False;
		}
		if (expr.isSymbol()) {
			if (expr.equals(F.Degree)) {
				return F.False;
			}
			if (expr.equals(F.Pi)) {
				return F.False;
			}
			if (expr.equals(F.E)) {
				return F.False;
			}

			// if (((ISymbol) expr).getEvaluator() instanceof
			// ISignedNumberConstant) {
			// return F.True;
			// }
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isAlgebraic(expr)) {
				return F.True;
			}
			if (assumptions.isRational(expr)) {
				return F.True;
			}
			if (assumptions.isInteger(expr)) {
				return F.True;
			}
			if (assumptions.isPrime(expr)) {
				return F.True;
			}
		}
		return null;
	}

	/**
	 * Test if <code>expr</code> is assumed to be an boolean value.
	 * 
	 * @param expr
	 * @return <code>F.True</code> or <code>F.False</code> if <code>expr</code> is assumed to be a boolean value or no
	 *         boolean value. In all other cases return <code>null</code>.
	 */
	public static ISymbol assumeBoolean(final IExpr expr) {
		if (expr.isTrue() || expr.isFalse()) {
			return F.True;
		}
		if (expr.isNumber()) {
			return F.False;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isBoolean(expr)) {
				return F.True;
			}
		}
		return null;
	}

	/**
	 * Test if <code>expr</code> is assumed to be a complex number.
	 * 
	 * @param expr
	 * @return <code>F.True</code> or <code>F.False</code> if <code>expr</code> is assumed to be a complex number or no
	 *         complex number. In all other cases return <code>null</code>.
	 */
	public static ISymbol assumeComplex(final IExpr expr) {
		if (expr.isNumber()) {
			return F.True;
		}
		if (expr.isSignedNumberConstant()) {
			return F.True;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isComplex(expr)) {
				return F.True;
			}
			if (assumptions.isRational(expr)) {
				return F.True;
			}
			if (assumptions.isInteger(expr)) {
				return F.True;
			}
			if (assumptions.isPrime(expr)) {
				return F.True;
			}
			if (assumptions.isReal(expr)) {
				return F.True;
			}
			if (assumptions.isAlgebraic(expr)) {
				return F.True;
			}
		}
		return null;
	}

	/**
	 * Test if <code>expr</code> is assumed to be an integer.
	 * 
	 * @param expr
	 * @return <code>F.True</code> or <code>F.False</code> if <code>expr</code> is assumed to be an integer or no
	 *         integer. In all other cases return <code>null</code>.
	 */
	public static ISymbol assumeInteger(final IExpr expr) {
		if (expr.isInteger()) {
			return F.True;
		}
		if (expr.isNumber()) {
			return F.False;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isInteger(expr)) {
				return F.True;
			}
			if (assumptions.isPrime(expr)) {
				return F.True;
			}
		}
		return null;
	}

	/**
	 * Test if <code>expr</code> is assumed to be an prime number.
	 * 
	 * @param expr
	 * @return <code>F.True</code> or <code>F.False</code> if <code>expr</code> is assumed to be a prime number or no
	 *         prime number. In all other cases return <code>null</code>.
	 */
	public static ISymbol assumePrime(final IExpr expr) {
		if (expr.isInteger() && ((IInteger) expr).isProbablePrime()) {
			return F.True;
		}
		if (expr.isNumber()) {
			return F.False;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isPrime(expr)) {
				return F.True;
			}
		}
		return null;
	}

	/**
	 * Test if <code>expr</code> is assumed to be a rational number.
	 * 
	 * @param expr
	 * @return <code>F.True</code> or <code>F.False</code> if <code>expr</code> is assumed to be a rational number or no
	 *         rational number. In all other cases return <code>null</code>.
	 */
	public static ISymbol assumeRational(final IExpr expr) {
		if (expr.isRational()) {
			return F.True;
		}
		if (expr.isNumber()) {
			return F.False;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isRational(expr)) {
				return F.True;
			}
			if (assumptions.isInteger(expr)) {
				return F.True;
			}
			if (assumptions.isPrime(expr)) {
				return F.True;
			}
		}
		return null;
	}

	/**
	 * Test if <code>expr</code> is assumed to be a real number.
	 * 
	 * @param expr
	 * @return <code>F.True</code> or <code>F.False</code> if <code>expr</code> is assumed to be a real number or no
	 *         real number. In all other cases return <code>null</code>.
	 */
	public static ISymbol assumeReal(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return F.True;
		}
		if (expr.isNumber()) {
			return F.False;
		}
		if (expr.isSignedNumberConstant()) {
			return F.True;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isReal(expr)) {
				return F.True;
			}
			if (assumptions.isInteger(expr)) {
				return F.True;
			}
			if (assumptions.isPrime(expr)) {
				return F.True;
			}
			if (assumptions.isRational(expr)) {
				return F.True;
			}
		}
		return null;
	}
}
