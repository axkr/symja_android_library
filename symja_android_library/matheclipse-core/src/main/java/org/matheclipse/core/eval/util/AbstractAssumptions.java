package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

public class AbstractAssumptions implements IAssumptions {

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

	public static boolean assumeNegative(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return ((ISignedNumber) expr).isNegative();
		}
		if (expr.isNumber()) {
			return false;
		}
		if (expr.isBuiltInSymbol()) {
			final IEvaluator module = ((IBuiltInSymbol) expr).getEvaluator();
			if (module instanceof ISignedNumberConstant) {
				return ((ISignedNumberConstant) module).evalReal() < 0.0;
			}
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isNegative(expr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean assumePositive(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return ((ISignedNumber) expr).isPositive();
		}
		if (expr.isNumber()) {
			return false;
		}
		if (expr.isBuiltInSymbol()) {
			final IEvaluator module = ((IBuiltInSymbol) expr).getEvaluator();
			if (module instanceof ISignedNumberConstant) {
				return ((ISignedNumberConstant) module).evalReal() > 0.0;
			}
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isPositive(expr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean assumeNonNegative(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return !((ISignedNumber) expr).isNegative();
		}
		if (expr.isNumber()) {
			return false;
		}
		if (expr.isBuiltInSymbol()) {
			final IEvaluator module = ((IBuiltInSymbol) expr).getEvaluator();
			if (module instanceof ISignedNumberConstant) {
				return ((ISignedNumberConstant) module).evalReal() >= 0.0;
			}
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

			// if (((ISymbol) expr).getEvaluator() instanceof ISignedNumberConstant) {
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

	public static ISymbol assumeComplex(final IExpr expr) {
		if (expr.isNumber()) {
			return F.True;
		}
		if (expr.isBuiltInSymbol()) {
			if (((IBuiltInSymbol) expr).getEvaluator() instanceof ISignedNumberConstant) {
				return F.True;
			}
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

	public static ISymbol assumePrime(final IExpr expr) {
		if (expr.isInteger() && ((IInteger) expr).isProbablePrime()) {
			return F.True;
		}
		if (expr.isNumber()) {
			return F.False;
		}
		// if (expr.isAST()) {
		// IAST ast = (IAST) expr;
		// if (ast.isPower() && ast.arg1().isRational() && ast.arg2().isRational()) {
		// IExpr arg2 = ast.arg2();
		// if (arg2.equals(F.C1D2)) {
		// return F.False;
		// }
		// }
		// }

		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isPrime(expr)) {
				return F.True;
			}
		}
		return null;
	}

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

	public static ISymbol assumeReal(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return F.True;
		}
		if (expr.isNumber()) {
			return F.False;
		}
		if (expr.isBuiltInSymbol()) {
			if (((IBuiltInSymbol) expr).getEvaluator() instanceof ISignedNumberConstant) {
				return F.True;
			}
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
