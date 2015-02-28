package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ISignedNumberConstant;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

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
		if (expr.isSymbol()) {
			final IEvaluator module = ((ISymbol) expr).getEvaluator();
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
		if (expr.isSymbol()) {
			final IEvaluator module = ((ISymbol) expr).getEvaluator();
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
		if (expr.isZero()) {
			return true;
		}
		return assumePositive(expr); 
	}

	public static boolean assumeAlgebraic(final IExpr expr) {
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isAlgebraic(expr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean assumeBoolean(final IExpr expr) {
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isBoolean(expr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean assumeComplex(final IExpr expr) {
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isComplex(expr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean assumeInteger(final IExpr expr) {
		if (expr.isInteger()) {
			return true;
		}
		if (expr.isNumber()) {
			return false;
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isInteger(expr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean assumePrime(final IExpr expr) {
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isPrime(expr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean assumeRational(final IExpr expr) {
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isRational(expr)) {
				return true;
			}
		}
		return false;
	}

	public static boolean assumeReal(final IExpr expr) {
		if (expr.isSignedNumber()) {
			return true;
		}
		if (expr.isNumber()) {
			return false;
		}
		if (expr.isSymbol()) {
			if (((ISymbol) expr).getEvaluator() instanceof ISignedNumberConstant) {
				return true;
			}
		}
		IAssumptions assumptions = EvalEngine.get().getAssumptions();
		if (assumptions != null) {
			if (assumptions.isReal(expr)) {
				return true;
			}
		}
		return false;
	}
}
